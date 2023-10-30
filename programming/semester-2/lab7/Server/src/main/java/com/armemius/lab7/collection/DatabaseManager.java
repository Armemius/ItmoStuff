package com.armemius.lab7.collection;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.ZoneId;
import java.util.Objects;

import com.armemius.lab7.collection.data.*;
import com.armemius.lab7.collection.exceptions.CollectionRuntimeException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.log4j.Logger;

public class DatabaseManager {
    private DatabaseManager() {}

    private final static Logger logger = Logger.getLogger(DatabaseManager.class);
    private static Connection connection = null;
    private static Session session = null;
    private static final int L_PORT = 1337;
    private static final int R_PORT = 5432;
    private static int assignedPort = L_PORT;
    private static final String URL = "jdbc:postgresql://localhost:" + L_PORT + "/studs";
    private static final String HOST = "helios.se.ifmo.ru";
    private static final String DB_HOST = "pg";
    private static final int PORT = 2222;
    private static final String USER = "s368849";
    public static final String ADMIN_LOGIN = "admin";
    public static final String ADMIN_PASS = "s3cr3kt_pa$$w0rD";

    public static void connect() throws SQLException, JSchException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String heliosPass = System.getenv("HELIOS_PASS");
        String dbPass = System.getenv("DB_PASS");
        if (heliosPass == null || dbPass == null) {
            throw new SQLException("HELIOS_PASS or DB_PASS is not set");
        }
        JSch jsch = new JSch();
        session = jsch.getSession(USER, HOST, PORT);
        session.setPassword(heliosPass);
        session.setConfig("StrictHostKeyChecking", "no");
        logger.info("Establishing SSH connection");
        session.connect();
        logger.info("SSH connection established");
        assignedPort = session.setPortForwardingL(L_PORT, DB_HOST, R_PORT);
        logger.info("Port successfully forwarded (" + "localhost:" + assignedPort + " -> " + HOST + ":" + R_PORT + ")");
        logger.info("Establishing connection to database");
        connection = DriverManager.getConnection(URL, USER, dbPass);
        logger.info("Connection to database established");
        try {
            prepareDB();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void prepareDB() throws SQLException {
        var statement = connection.createStatement();
        final String CREATE_USERS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " +
                "Users (login varchar NOT NULL PRIMARY KEY, password varchar NOT NULL)";
        statement.executeUpdate(CREATE_USERS_TABLE_QUERY);
        register(ADMIN_LOGIN, ADMIN_PASS);
        final String CREATE_COORDINATES_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " +
                "Coordinates (id serial NOT NULL PRIMARY KEY, x int NOT NULL, y bigint NOT NULL, CHECK(y > "
                + Coordinates.MIN_Y + "))";
        statement.executeUpdate(CREATE_COORDINATES_TABLE_QUERY);
        final String CREATE_LOCATION_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS Location (id serial PRIMARY KEY NOT NULL, x bigint NOT NULL, " +
                "y real NOT NULL, z bigint NOT NULL)";
        statement.executeUpdate(CREATE_LOCATION_TABLE_QUERY);
        final String CREATE_PERSON_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS Person (id serial PRIMARY KEY NOT NULL, name varchar NOT NULL, " +
                "height float NOT NULL, eye_color varchar NOT NULL, hair_color varchar NOT NULL, " +
                "nationality varchar NOT NULL, location_id serial, CONSTRAINT fk_location FOREIGN KEY (location_id) REFERENCES Location(id))";
        statement.executeUpdate(CREATE_PERSON_TABLE_QUERY);
        final String CREATE_STUDY_GROUP_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS Study_group (id serial NOT NULL PRIMARY KEY, name varchar NOT NULL, " +
                "coord_id serial NOT NULL, creation_date timestamp NOT NULL, students_count bigint NOT NULL, expelled_students_count int NOT NULL, average_mark " +
                "real NOT NULL, semester varchar, admin_id serial NOT NULL, user_login varchar NOT NULL, CONSTRAINT fk_coord FOREIGN KEY (coord_id) " +
                "REFERENCES Coordinates(id), CONSTRAINT fk_user FOREIGN KEY (user_login) REFERENCES Users(login), CONSTRAINT fk_person FOREIGN KEY (admin_id) " +
                "REFERENCES Person(id))";
        statement.executeUpdate(CREATE_STUDY_GROUP_TABLE_QUERY);
    }

    private static Coordinates getCoordinates(int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM Coordinates WHERE id = ?");
        statement.setInt(1, id);
        var res = statement.executeQuery();
        if (!res.next()) {
            throw new SQLException("Unable to get row with such id");
        }
        return new Coordinates(res.getInt(2), res.getLong(3));
    }

    private static Location getLocation(int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM Location WHERE id = ?");
        statement.setInt(1, id);
        var res = statement.executeQuery();
        if (!res.next()) {
            throw new SQLException("Unable to get row with such id");
        }
        return new Location(res.getInt(2), res.getDouble(3), res.getLong(4));
    }

    private static Person getPerson(int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM Person WHERE id = ?");
        statement.setInt(1, id);
        var res = statement.executeQuery();
        if (!res.next()) {
            throw new SQLException("Unable to get row with such id");
        }
        return new Person(res.getString(2),
                res.getFloat(3),
                EyeColor.valueOf(res.getString(4)),
                HairColor.valueOf(res.getString(5)),
                Country.valueOf(res.getString(6)),
                getLocation(res.getInt(7)));
    }

    public static void loadGroups() throws SQLException {
        var counterStatement = connection.createStatement();
        var counter = counterStatement.executeQuery("SELECT COUNT(*) FROM Study_Group");
        counter.next();
        int maxAmount = counter.getInt(1);
        var statement = connection.prepareStatement("SELECT * FROM Study_Group");
        var res = statement.executeQuery();
        int amount = 0;
        logger.info("Loading groups from database");
        while (res.next()) {
            try {
                CollectionManager.add(new StudyGroup(res.getInt(1),
                        res.getString(2),
                        getCoordinates(res.getInt(3)),
                        res.getTimestamp(4).toLocalDateTime().atZone(ZoneId.systemDefault()),
                        res.getLong(5),
                        res.getInt(6),
                        res.getDouble(7),
                        Semester.valueOf(res.getString(8)),
                        getPerson(res.getInt(9)),
                        res.getString(10)
                ), res.getInt(1));
                System.out.print("Loaded " + ++amount + "/" + maxAmount + " groups (" + amount * 100 / maxAmount + "%)\r");
            } catch (CollectionRuntimeException ex) {
                logger.error("Row with incorrect data (" + ex.getMessage() + ")");
            }
        }
        logger.info("Load complete");
    }

    private static int putCoordinates(Coordinates coordinates) throws SQLException {
        final var x = coordinates.getX();
        final var y = coordinates.getY();
        var statement = connection.prepareStatement("INSERT INTO Coordinates VALUES (DEFAULT, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, x);
        statement.setLong(2, y);
        statement.executeUpdate();
        var generated = statement.getGeneratedKeys();
        generated.next();
        return generated.getInt(1);
    }

    private static int putLocation(Location location) throws SQLException {
        final var x = location.getX();
        final var y = location.getY();
        final var z = location.getZ();
        var statement = connection.prepareStatement("INSERT INTO Location VALUES (DEFAULT, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setLong(1, x);
        statement.setDouble(2, y);
        statement.setLong(3, z);
        statement.executeUpdate();
        var generated = statement.getGeneratedKeys();
        generated.next();
        return generated.getInt(1);
    }

    private static int putPerson(Person person) throws SQLException {
        final var name = person.getName();
        final var height = person.getHeight();
        final var eyeColor = person.getEyeColor();
        final var hairColor = person.getHairColor();
        final var nationality = person.getNationality();
        final var location = person.getLocation();
        var statement = connection.prepareStatement("INSERT INTO Person VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, name);
        statement.setFloat(2, height);
        statement.setString(3, eyeColor.toString());
        statement.setString(4, hairColor.toString());
        statement.setString(5, nationality.toString());
        statement.setInt(6, putLocation(location));
        statement.executeUpdate();
        var generated = statement.getGeneratedKeys();
        generated.next();
        return generated.getInt(1);
    }

    public static int putStudyGroup(StudyGroup studyGroup) throws SQLException {
        final var id = studyGroup.getId();
        final var name = studyGroup.getName();
        final var coordinates = studyGroup.getCoordinates();
        final var creationDate = studyGroup.getCreationDate();
        final var studentsCount = studyGroup.getStudentsCount();
        final var expelledCount = studyGroup.getExpelledStudents();
        final var averageMark = studyGroup.getAverageMark();
        final var semester = studyGroup.getSemesterEnum();
        final var admin = studyGroup.getGroupAdmin();
        final var login = studyGroup.getAuthorLogin();
        var statement = connection.prepareStatement("INSERT INTO Study_group VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, id);
        statement.setString(2, name);
        statement.setInt(3, putCoordinates(coordinates));
        statement.setTimestamp(4, Timestamp.valueOf(creationDate.toLocalDateTime()));
        statement.setLong(5, studentsCount);
        statement.setInt(6, expelledCount);
        statement.setDouble(7, averageMark);
        statement.setString(8, semester.toString());
        statement.setInt(9, putPerson(admin));
        statement.setString(10, login);
        statement.executeUpdate();
        var generated = statement.getGeneratedKeys();
        generated.next();
        return generated.getInt(1);
    }

    public static void truncate() throws SQLException {
        var statement = connection.createStatement();
        statement.executeUpdate("TRUNCATE TABLE STUDY_GROUP CASCADE");
    }

    public static boolean auth(String login, String password) {
        if (login == null || password == null) {
            return false;
        }
        try {
            final String GET_USER_QUERY = "SELECT * FROM Users WHERE login = ? AND password = ?";
            final String passHash = genHash(password);
            var statement = connection.prepareStatement(GET_USER_QUERY);
            statement.setString(1, login);
            statement.setString(2, passHash);
            var res = statement.executeQuery();
            return res.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean register(String login, String password) {
        if (login == null || password == null) {
            return false;
        }
        try {
            final String INSERT_NEW_USER_QUERY = "INSERT INTO Users VALUES(?, ?)";
            final String passHash = genHash(password);
            var statement = connection.prepareStatement(INSERT_NEW_USER_QUERY);
            statement.setString(1, login);
            statement.setString(2, passHash);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static String genHash(String password) {
        try {
            var md = MessageDigest.getInstance("md5");
            md.reset();
            md.update(password.getBytes());
            byte[] digest = md.digest();
            var hash = new BigInteger(1,digest);
            return hash.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close() {
        try {
            if (connection != null) {
                logger.info("Closing connection to database");
                connection.close();
                logger.info("Connection to database closed");
            }
            if (session != null) {
                logger.info("Disconnecting from SSH");
                session.disconnect();
                logger.info("Disconnected from SSH");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
