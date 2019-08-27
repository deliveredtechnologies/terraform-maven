package com.deliveredtechnologies.io;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tests for CommandLine.
 */
public class CommandLineTest {

  private Path directory;

  @Before
  public void setup() throws URISyntaxException {
    directory = Paths.get(".");
  }

  @Test
  public void executeRunsCommandOnCommandLineShellAndReturnsTheOutput() throws IOException, InterruptedException {
    String echoString = "Hello World!";
    Executable commandLine = new CommandLine(directory, false, LoggerFactory.getLogger(CommandLine.class));
    String output = commandLine.execute(String.format("echo %1$s", echoString));
    Assert.assertEquals(String.format("%1$s%n", echoString), output);

    output = commandLine.execute(String.format("echo %1$s", echoString), 1000);
    Assert.assertEquals(String.format("%1$s%n", echoString), output);
  }

  @Test
  public void executeRunsInTheSpecifiedDirectory() throws IOException, InterruptedException {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    Path path = Paths.get("src", "main", "tf", "root");
    FileUtils.forceMkdir(path.toFile());
    Executable commandLine = new CommandLine(path, false, LoggerFactory.getLogger(CommandLine.class));;
    if (isWindows) {
      String output = commandLine.execute("cd");
      Assert.assertEquals(String.format("%1$s%n", path.toAbsolutePath().toString()), output);
    } else {
      String output = commandLine.execute("pwd");
      Assert.assertEquals(String.format("%1$s%n", path.toAbsolutePath().toString()), output);
    }
    FileUtils.forceDelete(path.getParent().toFile());
  }

  @Test(expected = IOException.class)
  public void executeThatErrorsOnItsCommandThrowsTheErrorOutput() throws IOException, InterruptedException {
    String errorCommand = "exit 1";
    Executable commandLine = new CommandLine(directory, false, LoggerFactory.getLogger(CommandLine.class));
    String output = commandLine.execute(errorCommand);
  }
}
