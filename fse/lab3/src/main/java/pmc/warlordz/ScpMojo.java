package pmc.warlordz;

import com.jcraft.jsch.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * Maven plugin to transfer the built project using SCP to a selected server after the build phase.
 */
@Mojo(name = "scp", defaultPhase = LifecyclePhase.PACKAGE)
public class ScpMojo extends AbstractMojo {

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Hostname or IP address of the destination server.
     */
    @Parameter(property = "scp.host", required = true)
    private String host;

    /**
     * SSH username for authentication on the destination server.
     */
    @Parameter(property = "scp.username", required = true)
    private String username;

    /**
     * SSH password for authentication on the destination server.
     */
    @Parameter(property = "scp.password", required = true)
    private String password;

    /**
     * Remote directory on the destination server where the project will be transferred.
     */
    @Parameter(property = "scp.remoteDirectory", required = true)
    private String remoteDirectory;

    /**
     * Perform SCP transfer of the built project after the build phase.
     *
     * @throws MojoExecutionException If an error occurs during SCP transfer.
     */
    public void execute() throws MojoExecutionException {
        getLog().info("Executing SCP transfer...");

        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            // Establish SSH session
            session = jsch.getSession(username, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // Open SFTP channel
            Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;

            // Transfer the built project
            File builtProject = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + "." + project.getPackaging());
            channelSftp.put(builtProject.getAbsolutePath(), remoteDirectory);

            getLog().info("SCP transfer completed successfully.");
        } catch (JSchException | SftpException e) {
            throw new MojoExecutionException("Error during SCP transfer: " + e.getMessage(), e);
        } finally {
            // Disconnect session and channel
            if (channelSftp != null) {
                channelSftp.exit();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}