package com.deliveredtechnologies.maven.io;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    Executable commandLine = new CommandLine(directory);
    String output = commandLine.execute(String.format("echo \"%1$s\"", echoString));
    Assert.assertEquals(String.format("%1$s\n", echoString), output);

    output = commandLine.execute(String.format("echo \"%1$s\"", echoString), 1000);
    Assert.assertEquals(String.format("%1$s\n", echoString), output);
  }

  @Test(expected = IOException.class)
  public void executeThatErrorsOnItsCommandThrowsTheErrorOutput() throws IOException, InterruptedException {
    String errorCommand = "exit 1";
    Executable commandLine = new CommandLine(directory);
    String output = commandLine.execute(errorCommand);
  }
}
