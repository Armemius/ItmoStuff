package pmc.warlordz;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mojo(name = "env", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class EnvMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File targetDirectory;

    @Parameter(property = "configFile", required = true)
    private File configFile;

    public void execute() throws MojoExecutionException {
        try {
            List<EnvironmentConfig> environments = loadEnvironmentConfigs(configFile);
            for (EnvironmentConfig env : environments) {
                buildAndRun(env);
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing env mojo", e);
        }
    }

    private List<EnvironmentConfig> loadEnvironmentConfigs(File configFile) throws IOException {
        List<String> lines = Files.readAllLines(configFile.toPath());
        List<EnvironmentConfig> configs = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(";");
            if (parts.length >= 2) {
                String javaHome = parts[0].trim();
                String[] vmArgs = parts[1].trim().split("\\s+");
                configs.add(new EnvironmentConfig(javaHome, vmArgs));
            }
        }
        return configs;
    }

    private void buildAndRun(EnvironmentConfig env) throws Exception {
        getLog().info("Building and running in environment: Java Home - " + env.javaHome);
        ProcessBuilder builder = new ProcessBuilder("mvn", "clean", "install");
        Map<String, String> envMap = builder.environment();
        envMap.put("JAVA_HOME", env.javaHome);
        runProcess(builder);

        File jarFile = new File(targetDirectory, "your-app.jar"); // Update this to your JAR file name
        List<String> command = new ArrayList<>();
        command.add(env.javaHome + "/bin/java");
        command.addAll(Arrays.asList(env.vmArgs));
        command.add("-jar");
        command.add(jarFile.getAbsolutePath());

        ProcessBuilder runBuilder = new ProcessBuilder(command);
        runProcess(runBuilder);
    }

    private void runProcess(ProcessBuilder processBuilder) throws Exception {
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Process failed with exit code: " + exitCode);
        }
    }

    private static class EnvironmentConfig {
        String javaHome;
        String[] vmArgs;

        EnvironmentConfig(String javaHome, String[] vmArgs) {
            this.javaHome = javaHome;
            this.vmArgs = vmArgs;
        }
    }
}
