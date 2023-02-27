package com.armemius.lab5.tasks;

import com.armemius.lab5.collection.CollectionManager;
import com.armemius.lab5.collection.data.*;
import com.armemius.lab5.commands.CommandContext;
import com.armemius.lab5.commands.exceptions.CommandRuntimeException;
import com.armemius.lab5.io.InputHandler;
import com.armemius.lab5.io.OutputHandler;

/**
 * <b>Request</b> class represents tasks for commands with data input (e.g. {@link StudyGroup})
 */
public abstract class RequestTask extends InputTask {
    /**
     * Auxiliary function, contains logic
     * for inputting and creating new {@link StudyGroup}
     */
    protected StudyGroup requestGroup(InputHandler inputHandler, OutputHandler outputHandler) {
        Integer id = null;
        String groupName = null;
        Integer groupX = null;
        Long groupY = null;
        Long students = null;
        Integer expelled = null;
        Double mark = null;
        Semester semester = null;
        while (true) {
            try {
                if (id == null) {
                    id = CollectionManager.genId();
                }
                if (groupName == null) {
                    outputHandler.hold("Input group name (String): ");
                    groupName = getString(inputHandler, false);
                }
                if (groupX == null) {
                    outputHandler.hold("Input group x coordinate (Integer): ");
                    groupX = getNumber(Integer.class, inputHandler, false);
                }
                if (groupY == null) {
                    outputHandler.hold("Input group y coordinate (Long): ");
                    groupY = getNumber(Long.class, inputHandler, false);
                    if (groupY <= -266) {
                        groupY = null;
                        throw new CommandRuntimeException("Incorrect value");
                    }
                }
                Coordinates coordinates = new Coordinates(groupX, groupY);
                if (students == null) {
                    outputHandler.hold("Input students count (Long): ");
                    students = getNumber(Long.class, inputHandler, false);
                    if (students < 0) {
                        students = null;
                        throw new CommandRuntimeException("Incorrect value");
                    }
                }
                if (expelled == null) {
                    outputHandler.hold("Input expelled students count (Int): ");
                    expelled = getNumber(Integer.class, inputHandler, false);
                    if (expelled < 0) {
                        expelled = null;
                        throw new CommandRuntimeException("Incorrect value");
                    }
                }
                if (mark == null) {
                    outputHandler.hold("Input average mark value (Double): ");
                    mark = getNumber(Double.class, inputHandler, false);
                    if (mark < 0) {
                        mark = null;
                        throw new CommandRuntimeException("Incorrect value");
                    }
                }
                outputHandler.hold("Input current semester (Variants: SECOND/THIRD/SEVENTH/EIGHT, value can be empty): ");
                semester = getEnumField(Semester.class, inputHandler, true);
                Person admin = requestAdmin(inputHandler, outputHandler);
                return new StudyGroup(id, groupName, coordinates, students, expelled, mark, semester, admin);
            } catch (CommandRuntimeException ex) {
                outputHandler.put("Incorrect value, try again");
            }
        }
    }

    /**
     * Auxiliary function, contains logic
     * for inputting and creating new {@link StudyGroup}
     */
    protected Person requestAdmin(InputHandler inputHandler, OutputHandler outputHandler) {
        String adminName = null;
        Float adminHeight = null;
        EyeColor eyeColor = null;
        HairColor hairColor = null;
        Country nation = null;
        Long adminX = null;
        Double adminY = null;
        Long adminZ = null;

        while (true) {
            try {
                if (adminName == null) {
                    outputHandler.hold("Input group admin name (String): ");
                    adminName = getString(inputHandler, false);
                }
                if (adminHeight == null) {
                    outputHandler.hold("Input group admin height (Float): ");
                    adminHeight = getNumber(Float.class, inputHandler, false);
                    if (adminHeight < 0) {
                        adminHeight = null;
                        throw new CommandRuntimeException("Incorrect value");
                    }
                }
                if (eyeColor == null) {
                    outputHandler.hold("Input admin eye color (Variants: GREEN/YELLOW/WHITE): ");
                    eyeColor = getEnumField(EyeColor.class, inputHandler, false);
                }
                if (hairColor == null) {
                    outputHandler.hold("Input admin hair color (Variants: GREEN/RED/BLACK/ORANGE/WHITE): ");
                    hairColor = getEnumField(HairColor.class, inputHandler, false);
                }
                if (nation == null) {
                    outputHandler.hold("Input admin nationality (Variants: UNITED_KINGDOM/CHINA/VATICAN/SOUTH_KOREA): ");
                    nation = getEnumField(Country.class, inputHandler, false);
                }
                if (adminX == null) {
                    outputHandler.hold("Input admin x coordinate (Long): ");
                    adminX = getNumber(Long.class, inputHandler, false);
                }
                if (adminY == null) {
                    outputHandler.hold("Input admin y coordinate (Double): ");
                    adminY = getNumber(Double.class, inputHandler, false);
                }
                if (adminZ == null) {
                    outputHandler.hold("Input admin z coordinate (Long): ");
                    adminZ = getNumber(Long.class, inputHandler, false);
                }
                return new Person(adminName, adminHeight, eyeColor, hairColor, nation, new Location(adminX, adminY, adminZ));
            } catch (CommandRuntimeException ex) {
                outputHandler.put("Incorrect value, try again");
            }
        }
    }
}
