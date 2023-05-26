package com.armemius.lab7.collection;

import com.armemius.lab7.collection.data.Person;
import com.armemius.lab7.collection.data.StudyGroup;
import com.armemius.lab7.commands.exceptions.CommandArgumentException;
import com.armemius.lab7.commands.exceptions.CommandRuntimeException;
import org.apache.log4j.Logger;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <b>CollectionManager</b> is singleton class that handles
 * operations with collection and its data
 * <p>
 * On initialization, it sets up serializer
 * and gets environment variable '<i>LAB_5_PATH</i>' that points to
 * file with data
 */
public class CollectionManager {
    private CollectionManager() {
    }

    private static final ConcurrentSkipListMap<Integer, StudyGroup> storage;
    private static final ZonedDateTime creationTime;

    private final static Logger logger = Logger.getLogger(CollectionManager.class);

    static {
        storage = new ConcurrentSkipListMap<>();
        creationTime = ZonedDateTime.now();
    }

    /**
     * Method that checks if collection contains specified id
     *
     * @param id Id to check
     * @return Returns True if element is in the collection, otherwise returns False
     */
    public static boolean checkId(int id) {
        return storage.containsKey(id);
    }

    /**
     * Method that returns collection's type
     *
     * @return Collection's class type
     */
    public static String getCollectionType() {
        return storage.getClass().toString();
    }

    /**
     * Method that returns collection initialization time
     *
     * @return Collection initialization time
     */
    public static ZonedDateTime getCreationTime() {
        return creationTime;
    }

    /**
     * Method that returns number of elements inside collection
     *
     * @return Number of elements inside collection
     */
    public static long getElementsCount() {
        return storage.size();
    }

    /**
     * Loads collection from file
     */
    public static void load() {
        try {
            clear(DatabaseManager.ADMIN_LOGIN);
            DatabaseManager.loadGroups();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves collection to DB
     *
     */
    public static void save()  {
        logger.info("Saving groups to database");
        final int[] counter = {0};
        try {
            DatabaseManager.truncate();
            var groups = getAll();
            groups.parallelStream().forEach(group -> {
                try {
                    DatabaseManager.putStudyGroup(group);
                } catch (SQLException ex) {
                    logger.error("Error while saving group to database");
                }
                System.out.print("Saved " + ++counter[0] + "/" + groups.size()
                        + " groups (" + counter[0] * 100 / groups.size() + "%)\r");
            });
        } catch (SQLException ex) {
            logger.error("Database is inaccessible");
        }
        logger.info("Collection is saved to database");
    }

    /**
     * Method that outputs storage as list of <b>StudyGroup</b>
     *
     * @return List of <b>StudyGroup</b>
     */
    public static List<StudyGroup> getAll() {
        final List<StudyGroup> groups = new ArrayList<>();
        storage.forEach((id, group) -> groups.add(group));
        return groups;
    }

    /**
     * Method that adds new <b>StudyGroup</b> to collection
     *
     * @param group <b>StudyGroup</b> to add
     */
    public static void add(StudyGroup group) {
        storage.put(genId(), group);
    }

    public static void add(StudyGroup group, int id) {
        storage.put(id, group);
    }

    /**
     * Method that updates <b>StudyGroup</b> in collection
     *
     * @param id    Id of the <b>StudyGroup</b>
     * @param group New <b>StudyGroup</b>
     */
    public static void update(int id, StudyGroup group, String login) {
        if (!storage.containsKey(id)) {
            throw new CommandRuntimeException("Can't find the element with id " + id);
        }
        if (!checkOwnership(storage.get(id), login)) {
            throw new CommandRuntimeException("Access for user " + login + " for element with id " + id + " denied");
        }
        storage.replace(id, group);
    }

    /**
     * Clears the collection
     */
    public static void clear(String login) {
        if (login.equals(DatabaseManager.ADMIN_LOGIN)) {
            storage.clear();
            StudyGroup.getUsedIds().clear();
        } else {
            throw new CommandRuntimeException("User " + login + " do not have enough privileges to execute this command");
        }
    }

    /**
     * Method that removes elements from collection
     *
     * @param comparator Predicate that returns true if StudyGroup should be removed
     * @return Number of removals
     */
    public static int remove(Predicate<StudyGroup> comparator, String login) {
        final int[] removals = {0};
        CollectionManager.getAll()
                .stream()
                .filter(group -> checkOwnership(group, login))
                .forEach(group -> {
            if (comparator.test(group)) {
                storage.remove(group.getId());
                StudyGroup.getUsedIds().remove(group.getId());
                removals[0]++;
            }
        });

        return removals[0];
    }


    /**
     * Method that replaces element from collection to another
     *
     * @param comparator Predicate that returns true if StudyGroup should be replaced
     * @param id         Id of the group to be replaced
     * @param group      New group
     * @return True if replacement was successful, otherwise returns false
     */
    public static boolean replace(Predicate<StudyGroup> comparator, int id, StudyGroup group, String login) {
        if (!storage.containsKey(id))
            throw new CommandRuntimeException("Can't find the element with id " + id);
        if (comparator.test(storage.get(id))) {
            if (!checkOwnership(storage.get(id), login)) {
                throw new CommandRuntimeException("Access for user " + login + " for element with id " + id + " denied");
            }
            storage.replace(id, group);
            return true;
        }
        return false;
    }

    /**
     * Removes first match with specified <i>groupAdmin</i>
     *
     * @param admin Group to be removed with corresponding <i>groupAdmin</i>
     * @return True if there was removal, otherwise returns false
     */
    public static boolean removeAnyByGroupAdmin(Person admin, String login) {
        for (var it : getAll()) {
            if (it.getGroupAdmin().equals(admin)) {
                if (!checkOwnership(it, login)) {
                    throw new CommandRuntimeException("Access for user " + login + " for element with id " + it.getId() + " denied");
                }
                storage.remove(it.getId());
                StudyGroup.getUsedIds().remove(it.getId());
                return true;
            }
        }
        return false;
    }

    /**
     * Counts how many element are equal to specified <i>averageMark</i>
     *
     * @param avgMark Mark to compare with
     * @return Number of elements that are equal to specified <i>averageMark</i>
     */
    public static int countAvgMark(double avgMark) {
        final int[] count = {0};
        storage.forEach((id, group) -> {
            if (avgMark == group.getAverageMark()) {
                count[0]++;
            }
        });
        return count[0];
    }

    /**
     * Counts how many element are equal to specified <i>averageMark</i> within specified delta
     *
     * @param avgMark Mark to compare with
     * @param delta   Maximal deviation for mark
     * @return Number of elements that are equal to specified <i>averageMark</i> within delta
     */
    public static int countAvgMarkDelta(double avgMark, double delta) {
        final int[] count = {0};
        storage.forEach((id, group) -> {
            if (Math.abs(avgMark - group.getAverageMark()) < delta) {
                count[0]++;
            }
        });
        return count[0];
    }

    /**
     * Finds all the elements that has specified substring in their name
     *
     * @param substring Substring to match with
     * @return List of all matched elements
     */
    public static List<StudyGroup> filterContent(String substring) {
        final List<StudyGroup> result = new ArrayList<>();
        storage.forEach((id, group) -> {
            if (group.getName().contains(substring)) {
                result.add(group);
            }
        });
        return result;
    }

    /**
     * Finds all the elements that matches regex pattern
     *
     * @param regex Regex to match with
     * @return List of all matched elements
     */
    public static List<StudyGroup> filterContentRegex(String regex) {
        try {
            final List<StudyGroup> result = new ArrayList<>();
            storage.forEach((id, group) -> {
                if (Pattern.matches(regex, group.getName())) {
                    result.add(group);
                }
            });
            return result;
        } catch (PatternSyntaxException ex) {
            throw new CommandArgumentException("Incorrect regex pattern");
        }

    }

    /**
     * Generates free id for group
     *
     * @return Free id
     */
    public static int genId() {
        int id = 1;
        while (storage.containsKey(id)) {
            id++;
        }
        return id;
    }

    public static boolean checkOwnership(StudyGroup group, String login) {
        return Objects.equals(login, DatabaseManager.ADMIN_LOGIN)
                || Objects.equals(group.getAuthorLogin(), login);
    }

    public static boolean checkOwnership(int id, String login) {
        if (!storage.containsKey(id)) {
            return false;
        }
        return Objects.equals(login, DatabaseManager.ADMIN_LOGIN)
                || Objects.equals(storage.get(id).getAuthorLogin(), login);
    }
}
