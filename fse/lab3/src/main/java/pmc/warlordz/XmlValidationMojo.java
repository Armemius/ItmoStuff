package pmc.warlordz;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

/**
 * Maven plugin to validate all XML files in the project.
 */
@Mojo(name = "xml")
public class XmlValidationMojo extends AbstractMojo {

    /**
     * Directory containing XML files to validate.
     */
    @Parameter(defaultValue = "${project.basedir}/src/main/resources")
    private File xmlDirectory;
    @Parameter(defaultValue = "${project.basedir}/src/main/resources/schema.xsd")
    private String schema;

    public void execute() throws MojoExecutionException {
        getLog().info("Starting XML validation...");

        if (!xmlDirectory.exists()) {
            getLog().warn("XML directory does not exist: " + xmlDirectory);
            return;
        }

        // Define XSD schema file
        File schemaFile = new File(schema);

        // Create SchemaFactory and Schema objects
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(schemaFile);

            // Create a validator instance from the schema
            Validator validator = schema.newValidator();

            // Define a handler to catch validation errors
            DefaultHandler errorHandler = new DefaultHandler() {
                @Override
                public void warning(SAXParseException e) throws SAXException {
                    getLog().warn("XML validation warning: " + e.getMessage());
                }

                @Override
                public void error(SAXParseException e) throws SAXException {
                    getLog().error("XML validation error: " + e.getMessage());
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    getLog().error("XML validation fatal error: " + e.getMessage());
                }
            };

            // Perform validation for each XML file in the directory
            for (File file : xmlDirectory.listFiles()) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".xml")) {
                    try {
                        validator.setErrorHandler(errorHandler);
                        validator.validate(new StreamSource(file));
                        getLog().info("XML validation successful for file: " + file.getName());
                    } catch (SAXException | IOException e) {
                        getLog().error("XML validation error for file: " + file.getName(), e);
                    }
                }
            }
        } catch (SAXException e) {
            throw new MojoExecutionException("Error creating XML schema or validator", e);
        }
    }
}
