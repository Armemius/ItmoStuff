package com.armemius.lab6.tasks;

import com.armemius.lab6.collection.CollectionManager;
import com.armemius.lab6.collection.data.*;
import com.armemius.lab6.commands.CommandContext;
import com.armemius.lab6.commands.params.Param;
import com.armemius.lab6.commands.params.Parametrized;
import com.armemius.lab6.io.InputHandler;
import com.armemius.lab6.io.OutputHandler;

@Parametrized(
        params = {
                @Param(letter = "h", name = "help"),
                @Param(letter = "r", name = "random")
        }
)
public class InsertTask extends RequestTask implements RemoteExecutable {
    /**
     * Action for <b>insert</b> command
     * Doesn't receive arguments
     * @param context Context of executing command
     */
    @Override
    public void execute(CommandContext context) {
        OutputHandler outputHandler = context.outputHandler();
        InputHandler inputHandler = context.inputHandler();
        if (context.params().contains("h")) {
            outputHandler.println("""
                        Syntax:
                        > insert
                        Insert command stands for adding new elements inside collection
                        After the command is received process of building new StudyGroup begins, where you should input valid field values for the new group
                        PARAMS:
                        -h / --help\t\tShow this menu
                        -r / --random\tSkip the process of creating group and instead generate it randomly
                        """);
            return;
        }
        if (context.params().contains("r")) {
            outputHandler.println("Inserting random element");
            CollectionManager.add(genRandomGroup());
            return;
        }
        outputHandler.println("Inserting new element");
        StudyGroup group = null;
        while (group == null) {
            group = requestGroup(inputHandler, outputHandler);
            outputHandler.println("You want to add group: " + group);
            outputHandler.print("Proceed? (Input empty line if yes) ");
            String response = inputHandler.get();
            if (!response.isBlank())
                group = null;
        }
        CollectionManager.add(group);
    }

    protected StudyGroup genRandomGroup() {
        final int MIN_GROUP_ID = 1000;
        final int MAX_GROUP_ID = 9999;
        final int MIN_GROUP_X_COORDINATE = -100000;
        final int MAX_GROUP_X_COORDINATE = 100000;
        final int MIN_GROUP_Y_COORDINATE = -216;
        final int MAX_GROUP_Y_COORDINATE = 100000;
        final int MIN_GROUP_STUDENTS = 5;
        final int MAX_GROUP_STUDENTS = 30;
        final int MIN_GROUP_AVG_MARK = 1;
        final int MAX_GROUP_AVG_MARK = 5;
        final int MIN_GROUP_EXPELLED_STUDENTS = 1;
        final int MAX_GROUP_EXPELLED_STUDENTS = 10;
        final int MIN_ADMIN_ID = 1000;
        final int MAX_ADMIN_ID = 9999;
        final int MIN_ADMIN_HEIGHT = 150;
        final int MAX_ADMIN_HEIGHT = 190;
        final int MIN_ADMIN_X_COORDINATE = -100000;
        final int MAX_ADMIN_X_COORDINATE = 100000;
        final int MIN_ADMIN_Y_COORDINATE = -100000;
        final int MAX_ADMIN_Y_COORDINATE = 100000;
        final int MIN_ADMIN_Z_COORDINATE = -100000;
        final int MAX_ADMIN_Z_COORDINATE = 100000;

        return new StudyGroup(CollectionManager.genId(),
                "GROUP_NAME" + (MIN_GROUP_ID + (int)(Math.random() * (MAX_GROUP_ID - MIN_GROUP_ID))),
                new Coordinates(MIN_GROUP_X_COORDINATE + (int)(Math.random() * (MAX_GROUP_X_COORDINATE - MIN_GROUP_X_COORDINATE)),
                        (long)MIN_GROUP_Y_COORDINATE + (long)(Math.random() * (MAX_GROUP_Y_COORDINATE - MIN_GROUP_Y_COORDINATE))),
                MIN_GROUP_STUDENTS + (int)(Math.random() * (MAX_GROUP_STUDENTS - MIN_GROUP_STUDENTS)),
                MIN_GROUP_EXPELLED_STUDENTS + (int)(Math.random() * (MAX_GROUP_EXPELLED_STUDENTS - MIN_GROUP_EXPELLED_STUDENTS)),
                MIN_GROUP_AVG_MARK + (int)(Math.random() * (MAX_GROUP_AVG_MARK - MIN_GROUP_AVG_MARK)),
                switch ((int)(Math.random() * Semester.values().length)) {
                    case 0 -> Semester.SECOND;
                    case 1 -> Semester.THIRD;
                    case 2 -> Semester.SEVENTH;
                    case 3 -> Semester.EIGHTH;
                    default -> null;
                },
                new Person(
                        "ADMIN_NAME" + (MIN_ADMIN_ID + (int)(Math.random() * (MAX_ADMIN_ID - MIN_ADMIN_ID))),
                (float)(MIN_ADMIN_HEIGHT + (int)(Math.random() * (MAX_ADMIN_HEIGHT - MIN_ADMIN_HEIGHT))),
                        switch ((int)(Math.random() * EyeColor.values().length)) {
                            case 0 -> EyeColor.GREEN;
                            case 1 -> EyeColor.WHITE;
                            default -> EyeColor.YELLOW;
                        },
                        switch ((int)(Math.random() * HairColor.values().length)) {
                            case 0 -> HairColor.BLACK;
                            case 1 -> HairColor.GREEN;
                            case 2 -> HairColor.ORANGE;
                            case 3 -> HairColor.RED;
                            default -> HairColor.WHITE;
                        },
                        switch ((int)(Math.random() * Country.values().length)) {
                            case 0 -> Country.CHINA;
                            case 1 -> Country.SOUTH_KOREA;
                            case 2 -> Country.UNITED_KINGDOM;
                            default -> Country.VATICAN;
                        },
                        new Location(
                                MIN_ADMIN_X_COORDINATE + (int)(Math.random() * (MAX_ADMIN_X_COORDINATE - MIN_ADMIN_X_COORDINATE)),
                                (double) (MIN_ADMIN_Y_COORDINATE + (int)(Math.random() * (MAX_ADMIN_Y_COORDINATE - MIN_ADMIN_Y_COORDINATE))),
                                (long) (MIN_ADMIN_Z_COORDINATE + (int)(Math.random() * (MAX_ADMIN_Z_COORDINATE - MIN_ADMIN_Z_COORDINATE)))
                        )
                )
        );
    }

    @Override
    public void validate(int id, Object[] data) throws IllegalArgumentException {
        if (id != 0) {
            throw new IllegalArgumentException("Invalid operation id");
        }
        if (data.length != 1) {
            throw new IllegalArgumentException("Suspicious number of arguments");
        }
        if (!(data[0] instanceof StudyGroup)) {
            throw new IllegalArgumentException("Invalid type of arguments");
        }
    }

    @Override
    public void execute(int id, Object[] data, OutputHandler outputHandler) {
        var group = (StudyGroup) data[0];
        group.setId(CollectionManager.genId());
        CollectionManager.add(group);
        outputHandler.println("Inserted new element");
    }
}
