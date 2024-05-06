package pmc.warlordz;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Parameter;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Maven Mojo for playing music after the build phase.
 */
@Mojo(name = "music")
@Execute(phase = LifecyclePhase.INSTALL)
public class MusicMojo extends AbstractMojo {

    /**
     * Path to the music file.
     */
    @Parameter(defaultValue = "${project.basedir}/src/main/resources/ba.wav")
    private String musicFilePath;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            File soundFile = new File(musicFilePath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();

            clip.open(audioIn);
            clip.start();

            // Use a LineListener to wait for the clip to finish playing
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    event.getLine().close();
                }
            });

            // Wait for the clip to finish
            while (clip.isRunning()) {
                Thread.sleep(100);
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            throw new MojoExecutionException("Error playing music file: " + musicFilePath, e);
        }
    }
}
