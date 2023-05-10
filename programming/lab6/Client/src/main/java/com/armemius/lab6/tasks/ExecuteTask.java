package com.armemius.lab6.tasks;

import com.armemius.lab6.commands.CommandContext;
import com.armemius.lab6.commands.exceptions.CommandArgumentException;
import com.armemius.lab6.commands.params.Parametrized;
import com.armemius.lab6.io.OutputHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Parametrized
public class ExecuteTask implements Task {
    /**
     * Action for <b>execute</b> command
     * Receives one argument
     * @param context Context of executing command
     */
    @Override
    public void execute(CommandContext context) {
        OutputHandler outputHandler = context.outputHandler();
        if (context.params().contains("h")) {
            outputHandler.println("""
                        Syntax:
                        > execute <filename>
                        Command responsive for executing scripts from files
                        PARAMS:
                        -h / --help\tShow this menu
                        """);
            return;
        }
        if (context.args().size() < 1)
            throw new CommandArgumentException("Argument wasn't provided");
        outputHandler.println("Executing script");
        String scriptPath = context.args().get(0);
        String test = checkRecursion(scriptPath, new HashSet<>());
        if (test != null) {
            throw new CommandArgumentException("Recursion detected: " + test);
        }
        try (Scanner scanner = new Scanner(new File(scriptPath))) {
            while (scanner.hasNextLine()) {
                var buffer = scanner.nextLine();
                if (!buffer.isBlank()) {
                    context.parser().parse(buffer);
                }
            }
        } catch (FileNotFoundException e) {
            throw new CommandArgumentException("Error while reading the file");
        }
    }

    private String checkRecursion(String path, Set<String> used) {
        StringBuilder script = new StringBuilder();
        try (Scanner scanner = new Scanner(new File(path))) {
            while (scanner.hasNextLine()) {
                script.append(scanner.nextLine()).append('\n');
            }
            Matcher matcher = Pattern.compile("execute\\s+(.+)\n", Pattern.MULTILINE).matcher(script);
            while (matcher.find()) {
                String match = matcher.group(1);
                if (used.contains(path)) {
                    return path;
                }
                used.add(path);
                System.out.println(match);
                String recursion = checkRecursion(match, new HashSet<>(used));
                if (recursion != null) {
                    return path + " -> " + recursion;
                }
            }
            return null;
        } catch (FileNotFoundException e) {
            throw new CommandArgumentException("Error while reading the file");
        }
    }
}
