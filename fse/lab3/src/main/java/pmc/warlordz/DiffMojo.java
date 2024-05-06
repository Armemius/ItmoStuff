package pmc.warlordz;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Mojo(name = "diff")
public class DiffMojo extends AbstractMojo {
    @Parameter(property = "paramFile", defaultValue = "classes.txt")
    private File paramFile;

    public void execute() throws MojoExecutionException {
        getLog().info("Checking for changes in specific classes... SUS");

        try {
            List<String> classesToMonitor = Files.readAllLines(paramFile.toPath());
            List<String> modifiedClasses = getModifiedClasses();


            for (String file : classesToMonitor) {
                getLog().warn(file);
            }
            getLog().warn("SUS");
            for (String file : modifiedClasses) {
                getLog().warn(file);
            }

            List<String> classesToCommit = new LinkedList<>();

            for (String modifiedClass : modifiedClasses) {
                if (classesToMonitor.contains(modifiedClass)) {
                    classesToCommit.add(modifiedClass);
                }
            }

            if (!classesToCommit.isEmpty()) {
                String message = "Committing changes for monitored classes";
                commitChanges(classesToCommit, message);
                getLog().info("Committed changes for: " + classesToCommit);
            } else {
                getLog().info("No changes detected in monitored classes.");
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing diff mojo", e);
        }
    }

    private List<String> getModifiedClasses() throws Exception {
        // This should use a Git command to fetch modified files
        Process process = new ProcessBuilder("git", "diff", "--name-only").start();
        List<String> output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines().collect(Collectors.toList());
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            return output;
        } else {
            throw new RuntimeException("Failed to get modified files");
        }
    }

    private void commitChanges(List<String> filesToCommit, String message) throws Exception {
        for (String file : filesToCommit) {
            new ProcessBuilder("git", "add", file).start().waitFor();
        }
        new ProcessBuilder("git", "commit", "-m", message).start().waitFor();
    }
}