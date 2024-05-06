package pmc.warlordz;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "history")
public class HistoryMojo extends AbstractMojo {
    public void execute() throws MojoExecutionException {
        getLog().info("Performing 'history' task");

        try {
            String commitHash = "HEAD";
            boolean buildSuccess = false;

            while (!commitHash.equals("")) {
                checkoutCommit(commitHash);
                if (compileProject()) {
                    getLog().info("Build succeeded at commit " + commitHash);
                    buildSuccess = true;
                    break;
                }
                commitHash = getPreviousCommit(commitHash);
            }

            if (!buildSuccess) {
                getLog().info("No successful build found, reached initial commit.");
            } else {
                generateDiff(commitHash);
            }

        } catch (Exception e) {
            throw new MojoExecutionException("Error executing history mojo", e);
        }
    }

    private boolean compileProject() throws Exception {
        ProcessBuilder builder = new ProcessBuilder("mvn.cmd", "compile");
        builder.redirectErrorStream(true);
        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            getLog().info(line);
        }
        int exitCode = process.waitFor();
        return exitCode == 0;
    }

    private void checkoutCommit(String commitHash) throws Exception {
        executeGitCommand("git", "checkout", commitHash);
    }

    private String getPreviousCommit(String commitHash) throws Exception {
        Process process = executeGitCommand("git", "rev-parse", commitHash + "^");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        return reader.readLine();
    }

    private void generateDiff(String commitHash) throws Exception {
        executeGitCommand("git", "diff", commitHash + "^", commitHash, "--", ">", "changes.diff");
    }

    private Process executeGitCommand(String... commands) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        return process;
    }
}
