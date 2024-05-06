package pmc.warlordz;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mojo to generate Javadoc and add checksums to MANIFEST.MF.
 */
@Mojo(name = "doc", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class EnhanceArchiveMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            generateJavadoc();
            // Additional methods for checksums and MANIFEST modifications here
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to enhance archive", e);
        }
    }

    private void generateJavadoc() throws IOException {
        File sourceDir = new File(project.getBuild().getSourceDirectory());
        File outputDir = new File(project.getBuild().getDirectory(), "javadoc");

        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Could not create directory: " + outputDir);
        }

        DocumentationTool documentationTool = ToolProvider.getSystemDocumentationTool();
        if (documentationTool == null) {
            throw new IllegalStateException("Documentation tool not found");
        }

        try (StandardJavaFileManager fileManager = documentationTool.getStandardFileManager(null, null, null)) {
            // Setting the correct file locations using File objects
            fileManager.setLocation(StandardLocation.SOURCE_PATH, Collections.singleton(sourceDir));
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(outputDir));

            // Get Java file objects from the source directory
            Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles(findJavaFiles(sourceDir));

            DocumentationTool.DocumentationTask task = documentationTool.getTask(null, fileManager, null, null, null, javaFileObjects);
            task.call();
        }
    }

    private List<File> findJavaFiles(File dir) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    javaFiles.addAll(findJavaFiles(f));
                } else if (f.getName().endsWith(".java")) {
                    javaFiles.add(f);
                }
            }
        }
        return javaFiles;
    }
}
