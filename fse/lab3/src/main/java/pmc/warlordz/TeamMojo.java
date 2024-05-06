package pmc.warlordz;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Mojo(name = "team")
public class TeamMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File outputDirectory;

    @Parameter(property = "svnUrl", required = true)
    private String svnUrl;

    public void execute() throws MojoExecutionException {
        try {
            checkoutAndBuildRevision("HEAD~1");
            checkoutAndBuildRevision("HEAD~2");

            zipJars();
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing team mojo", e);
        }
    }

    private void checkoutAndBuildRevision(String revision) throws Exception {
        getLog().info("Checking out and building revision: " + revision);
        File tempDir = new File("temp");
        tempDir.mkdir();
        ProcessBuilder checkoutProcess = new ProcessBuilder("svn", "checkout", "-r", revision, svnUrl, tempDir.getPath());
        runProcess(checkoutProcess);

        ProcessBuilder buildProcess = new ProcessBuilder("mvn", "clean", "install");
        buildProcess.directory(tempDir);
        runProcess(buildProcess);

        // Cleanup the checkout directory after build
        deleteDirectory(tempDir);
    }

    private void runProcess(ProcessBuilder processBuilder) throws Exception {
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Process failed with exit code " + exitCode);
        }
    }

    private void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }

    private void zipJars() throws Exception {
        File zipFile = new File(outputDirectory, "revisions.zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
            File[] files = new File(outputDirectory.getPath()).listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".jar")) {
                        zos.putNextEntry(new ZipEntry(file.getName()));
                        try (FileInputStream fis = new FileInputStream(file)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) >= 0) {
                                zos.write(buffer, 0, length);
                            }
                        }
                        zos.closeEntry();
                    }
                }
            }
        }
        getLog().info("Created zip archive: " + zipFile.getPath());
    }
}