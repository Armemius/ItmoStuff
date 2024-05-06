package pmc.warlordz;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mojo(name = "alt")
public class AltMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.build.sourceDirectory}", required = true)
    private File sourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File targetDirectory;

    @Parameter(property = "configFile", required = true)
    private File configFile;

    public void execute() throws MojoExecutionException {
        try {
            Map<String, String> replacements = loadReplacements(configFile);
            modifySourceFiles(sourceDirectory, replacements);
            buildProject();
            packageProject();
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing alt mojo", e);
        }
    }

    private Map<String, String> loadReplacements(File configFile) throws IOException {
        Map<String, String> replacements = new HashMap<>();
        List<String> lines = Files.readAllLines(configFile.toPath());
        for (String line : lines) {
            String[] parts = line.split("=");
            if (parts.length == 2) {
                replacements.put(parts[0].trim(), parts[1].trim());
            }
        }
        return replacements;
    }

    private void modifySourceFiles(File sourceDirectory, Map<String, String> replacements) throws IOException {
        File[] files = sourceDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".java")) {
                    String content = new String(Files.readAllBytes(file.toPath()), "UTF-8");
                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                        content = content.replace(entry.getKey(), entry.getValue());
                    }
                    Files.write(file.toPath(), content.getBytes("UTF-8"));
                }
            }
        }
    }

    private void buildProject() throws Exception {
        ProcessBuilder builder = new ProcessBuilder("mvn", "clean", "compile");
        builder.directory(new File("."));
        runProcess(builder);
    }

    private void packageProject() throws Exception {
        ProcessBuilder builder = new ProcessBuilder("mvn", "package");
        builder.directory(new File("."));
        runProcess(builder);
    }

    private void runProcess(ProcessBuilder processBuilder) throws Exception {
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Build or packaging failed with exit code: " + exitCode);
        }
    }
}
