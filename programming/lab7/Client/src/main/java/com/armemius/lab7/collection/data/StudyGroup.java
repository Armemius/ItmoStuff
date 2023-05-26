package com.armemius.lab7.collection.data;

import com.armemius.lab7.collection.exceptions.CollectionRuntimeException;

import java.io.Serial;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@SuppressWarnings("unused")
public class StudyGroup implements Serializable {
    public StudyGroup() {}

    public final int MIN_ID = 0;
    public static final int MIN_STUDENTS_COUNT = 0;
    public static final int MIN_EXPELLED_STUDENTS_COUNT = 0;
    public static final double MIN_AVG_MARK = 0;

    public StudyGroup(Integer id,
                      String name,
                      Coordinates coordinates,
                      long studentsCount,
                      int expelledStudents,
                      double averageMark,
                      Semester semesterEnum,
                      Person groupAdmin,
                      String authorLogin) {
        creationDate = java.time.ZonedDateTime.now();
        this.id = id;
        setName(name);
        setCoordinates(coordinates);
        setStudentsCount(studentsCount);
        setExpelledStudents(expelledStudents);
        setAverageMark(averageMark);
        setSemesterEnum(semesterEnum);
        setGroupAdmin(groupAdmin);
        setAuthorLogin(authorLogin);
    }

    @Serial
    private static final long serialVersionUID = -903541336;
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long studentsCount; //Значение поля должно быть больше 0
    private int expelledStudents; //Значение поля должно быть больше 0
    private double averageMark; //Значение поля должно быть больше 0
    private Semester semesterEnum; //Поле может быть null
    private Person groupAdmin; //Поле не может быть null
    private String authorLogin;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        final int MIN_ID = 0;
        if (id == null || id <= MIN_ID) {
            throw new CollectionRuntimeException("Incorrect parameters for StudyGroup");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new CollectionRuntimeException("Incorrect parameters for StudyGroup");
        }
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new CollectionRuntimeException("Incorrect parameters for StudyGroup");
        }
        this.coordinates = coordinates;
    }

    public java.time.ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.time.ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public long getStudentsCount() {
        return studentsCount;
    }

    public void setStudentsCount(long studentsCount) {
        if (studentsCount <= MIN_STUDENTS_COUNT) {
            throw new CollectionRuntimeException("Incorrect parameters for StudyGroup");
        }
        this.studentsCount = studentsCount;
    }

    public int getExpelledStudents() {
        return expelledStudents;
    }

    public void setExpelledStudents(int expelledStudents) {
        if (expelledStudents <= MIN_EXPELLED_STUDENTS_COUNT) {
            throw new CollectionRuntimeException("Incorrect parameters for StudyGroup");
        }
        this.expelledStudents = expelledStudents;
    }

    public double getAverageMark() {
        return averageMark;
    }

    public void setAverageMark(double averageMark) {
        if (averageMark <= MIN_AVG_MARK) {
            throw new CollectionRuntimeException("Incorrect parameters for StudyGroup");
        }
        this.averageMark = averageMark;
    }

    public Semester getSemesterEnum() {
        return semesterEnum;
    }

    public void setSemesterEnum(Semester semesterEnum) {
        this.semesterEnum = semesterEnum;
    }

    public Person getGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupAdmin(Person groupAdmin) {
        if (groupAdmin == null) {
            throw new CollectionRuntimeException("Incorrect parameters for StudyGroup");
        }
        this.groupAdmin = groupAdmin;
    }

    public String getAuthorLogin() {
        return authorLogin;
    }

    public void setAuthorLogin(String authorLogin) {
        if (authorLogin == null) {
            throw new CollectionRuntimeException("Incorrect authorLogin parameter for StudyGroup");
        }
        this.authorLogin = authorLogin;
    }

    @Override
    public String toString() {
        return "StudyGroup(" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate.format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")) +
                ", studentsCount=" + studentsCount +
                ", expelledStudents=" + expelledStudents +
                ", averageMark=" + averageMark +
                ", semesterEnum=" + semesterEnum +
                ", groupAdmin=" + groupAdmin +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof StudyGroup group)) {
            return false;
        }
        return studentsCount == group.studentsCount
                && expelledStudents == group.expelledStudents
                && Double.compare(group.averageMark, averageMark) == 0
                && id.equals(group.id) && name.equals(group.name)
                && coordinates.equals(group.coordinates)
                && creationDate.equals(group.creationDate)
                && semesterEnum == group.semesterEnum
                && groupAdmin.equals(group.groupAdmin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                name,
                coordinates,
                creationDate,
                studentsCount,
                expelledStudents,
                averageMark,
                semesterEnum,
                groupAdmin);
    }
}