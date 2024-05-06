package pmc.warlordz;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * Maven plugin to perform native2ascii conversion for localization files.
 */
@Mojo(name = "native2ascii")
public class Native2AsciiMojo extends AbstractMojo {

    /**
     * Source directory containing localization files.
     */
    @Parameter(defaultValue = "${project.basedir}/src/main/resources/localization")
    private File sourceDirectory;

    /**
     * Output directory for converted localization files.
     */
    @Parameter(defaultValue = "${project.build.directory}/converted-localization")
    private File outputDirectory;

    /**
     * Perform native2ascii conversion for localization files.
     *
     * @throws MojoExecutionException If an error occurs during conversion.
     */
    public void execute() throws MojoExecutionException {
        if (!sourceDirectory.exists()) {
            getLog().warn("Source directory does not exist: " + sourceDirectory);
            return;
        }

        // Create output directory if it doesn't exist
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        // Get all files in the source directory
        File[] localizationFiles = sourceDirectory.listFiles();

        if (localizationFiles != null) {
            for (File file : localizationFiles) {
                // Perform conversion for each file
                convertFile(file);
            }
        }
    }

    /**
     * Perform native2ascii conversion for a single file.
     *
     * @param file The file to convert.
     * @throws MojoExecutionException If an error occurs during conversion.
     */
    private void convertFile(File file) throws MojoExecutionException {
        try {
            String content = FileUtils.readFileToString(file, "UTF-8");
            String convertedContent = native2Ascii(content);
            File outputFile = new File(outputDirectory, file.getName());
            FileUtils.writeStringToFile(outputFile, convertedContent, "ISO-8859-1");
            getLog().info("Converted file: " + file.getName());
        } catch (IOException e) {
            throw new MojoExecutionException("Error converting file: " + file.getName(), e);
        }
    }

    /**
     * Perform native2ascii conversion for a string.
     *
     * @param content The content to convert.
     * @return The converted content.
     */
    private String native2Ascii(String content) {
        // Convert each character to its integer representation and store them as consecutive strings with line breaks
        StringBuilder builder = new StringBuilder();
        for (char c : content.toCharArray()) {
            // Convert character to its integer representation
            String integerRepresentation = Integer.toString((int) c);
            builder.append(integerRepresentation).append("\n");
        }
        // Add empty line between each line
        return builder.toString().replaceAll("\\n", "\n\n");
    }


}
