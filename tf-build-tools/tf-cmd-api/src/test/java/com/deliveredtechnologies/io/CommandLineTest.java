package com.deliveredtechnologies.io;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.InvalidExitValueException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tests for CommandLine.
 */
public class CommandLineTest {

  private Path directory;

  @Before
  public void setup() {
    directory = Paths.get(".");
  }

  @Test
  public void executeRunsCommandOnCommandLineShellAndReturnsTheOutput() throws IOException, InterruptedException {
    String echoString = "Hello World!";
    Executable commandLine = new CommandLine(directory, false, LoggerFactory.getLogger(CommandLine.class));
    String output = commandLine.execute(String.format("echo %1$s", echoString));
    Assert.assertEquals(echoString.trim(), output.trim());

    output = commandLine.execute(String.format("echo %1$s", echoString), 1000);
    Assert.assertEquals(echoString, output.trim());
  }

  @Test
  public void executeRunsInTheSpecifiedDirectory() throws IOException, InterruptedException {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    Path path = Paths.get("src", "main", "tf", "root");
    FileUtils.forceMkdir(path.toFile());
    Executable commandLine = new CommandLine(path, false, LoggerFactory.getLogger(CommandLine.class));
    if (isWindows) {
      if (!System.getProperties().containsKey("shellPath") && System.getenv("SHELL_PATH") == null) {
        String output = commandLine.execute("cd");
        Assert.assertEquals(String.format("%1$s%n", path.toAbsolutePath().toString()), output);
        return;
      }
    }

    String output = commandLine.execute("pwd");
    Assert.assertEquals(String.format("%1$s%n", path.toAbsolutePath().toString().replace("C:", "/c").replace('\\', '/')), String.format("%1$s%n", output.trim()));

    FileUtils.forceDelete(path.getParent().toFile());
  }

  @Test(expected = InvalidExitValueException.class)
  public void executeThatErrorsOnItsCommandThrowsTheErrorOutput() throws IOException, InterruptedException {
    String errorCommand = "exit 1";
    Executable commandLine = new CommandLine(directory, false, LoggerFactory.getLogger(CommandLine.class));
    commandLine.execute(errorCommand);
  }
}
