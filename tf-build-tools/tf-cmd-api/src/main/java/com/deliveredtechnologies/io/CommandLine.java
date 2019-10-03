package com.deliveredtechnologies.io;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Command line abstraction; executes any command via the command line (cmd.exe or bash based on OS).
 */
public class CommandLine implements Executable {

  private static int DEFAULT_TIMEOUT = 600000;

  private Path directory;
  private boolean inheritIO;
  private Logger logger;

  public CommandLine(Path directory) {
    this(directory, LoggerFactory.getLogger(CommandLine.class));
  }

  public CommandLine(Path directory, Logger logger) {
    this(directory, true, logger);
  }

  /**
   * CommandLine Constructor.
   * @param directory the directory in which to run the command
   * @param inheritIO true if the process running the command should inherit the stdout of the parent process
   * @param logger    SLF4J Logger
   */
  public CommandLine(Path directory, boolean inheritIO, Logger logger) {
    this.logger = logger;
    this.inheritIO = inheritIO;
    this.directory = directory;
  }

  /**
   * Runs the command specified on the command line (cmd.exe or bash based on OS).
   * @param command the command to be run on the command line
   * @param timeout the max amount of time in milliseconds the command is allowed to run before interruption
   * @return        the output from the command line
   * @throws IOException
   * @throws InterruptedException
   */
  @Override
  public String execute(String command, int timeout) throws IOException, InterruptedException {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    ProcessBuilder processBuilder;
    Optional<String> output;
    Optional<String> error;
    if (isWindows) {
      String defaultWindowsShell = "cmd.exe";
      String shell = System.getProperty("shellPath", System.getenv("SHELL_PATH") == null ? defaultWindowsShell : System.getenv("SHELL_PATH"));
      String[] cmd = new String[] {
        shell,
        shell.equals(defaultWindowsShell) ? "/c" : "-c",
        command
      };
      processBuilder = new ProcessBuilder(cmd);
      processBuilder.directory(directory.toAbsolutePath().toFile());
    } else {
      String[] cmd = new String[] {
        "bash",
        "-c",
        command
      };
      processBuilder = new ProcessBuilder(cmd);
      processBuilder.directory(directory.toFile());
    }
    logger.debug(String.format("*** directory: %1$s ***", getDirectory().toAbsolutePath()));
    logger.debug(String.format("*** command: %1$s ***", command));

    Process process;
    if (inheritIO) {
      processBuilder = processBuilder.inheritIO();
      output = Optional.empty();
      error = Optional.empty();
      process = processBuilder.start();
    } else {
      process = processBuilder.start();
      output = Optional.ofNullable(IOUtils.toString(new InputStreamReader(process.getInputStream())));
      error = Optional.ofNullable(IOUtils.toString(new InputStreamReader(process.getErrorStream())));
    }
    addShutdownHook(process);
    process.waitFor(timeout, TimeUnit.MILLISECONDS);

    if (process.exitValue() > 0) {
      throw new IOException("Exit value was greater than zero!\n" + error.orElse(""));
    }
    return output.orElse("");
  }

  @Override
  public String execute(String command) throws IOException, InterruptedException {
    return this.execute(command, DEFAULT_TIMEOUT);
  }

  @Override
  public void setLogger(Logger logger) {
    this.logger = logger;
  }

  public Path getDirectory() {
    return this.directory;
  }

  private void addShutdownHook(Process process) {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> process.destroyForcibly()));
  }
}
