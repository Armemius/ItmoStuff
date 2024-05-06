package pmc.warlordz;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.File;
import java.io.FilenameFilter;

@Mojo(name = "report", defaultPhase = LifecyclePhase.TEST)
public class ReportMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.build.directory}/surefire-reports", readonly = true)
    private File reportDirectory;

    @Parameter(defaultValue = "${project.baseDir}", readonly = true)
    private File baseDir;

    @Parameter(property = "svnUrl", required = true)
    private String svnUrl;

    public void execute() throws MojoExecutionException {
        try {
            // Run Maven test and generate reports
            runMavenTest();
            // Add and commit JUnit XML reports to SVN
            addAndCommitReportsToSVN();
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing report mojo", e);
        }
    }

    private void runMavenTest() throws Exception {
        getLog().info("Running Maven tests...");
        ProcessBuilder builder = new ProcessBuilder("mvn", "test");
        builder.directory(baseDir);
        Process process = builder.start();
        int result = process.waitFor();
        if (result != 0) {
            throw new RuntimeException("Tests failed, not committing reports.");
        }
    }

    private void addAndCommitReportsToSVN() throws Exception {
        getLog().info("Adding JUnit reports to SVN...");
        File[] files = reportDirectory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });

        if (files != null) {
            for (File file : files) {
                ProcessBuilder addBuilder = new ProcessBuilder("svn", "add", file.getAbsolutePath());
                runProcess(addBuilder);
            }
            ProcessBuilder commitBuilder = new ProcessBuilder("svn", "commit", "-m", "Adding JUnit reports");
            runProcess(commitBuilder);
        } else {
            getLog().info("No JUnit XML reports found to commit.");
        }
    }

    private void runProcess(ProcessBuilder processBuilder) throws Exception {
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Process failed with exit code " + exitCode);
        }
    }
}
