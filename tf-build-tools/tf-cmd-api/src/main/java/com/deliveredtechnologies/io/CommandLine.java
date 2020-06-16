package com.deliveredtechnologies.io;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    logger.info("*** directory: {} ***", getDirectory().toAbsolutePath());
    logger.info("*** command: {} ***", command);

    ProcessExecutor processExecutor = new ProcessExecutor();

    List<String> cmd = new ArrayList<>(getShell());
    cmd.add(command);

    Optional<String> output;

    String [] baseCommand = command.split(" ");
    String loggerName = baseCommand.length == 2 ? baseCommand[0] + "-" + baseCommand[1] : baseCommand[0];
    try {
      output = Optional.ofNullable(processExecutor.command(cmd)
        .directory(directory.toAbsolutePath().toFile())
        .redirectOutput(Slf4jStream.of(LoggerFactory.getLogger(loggerName)).asInfo())
        .timeout(timeout, TimeUnit.MILLISECONDS)
        .readOutput(true)
        .exitValueNormal()
        .destroyOnExit()
        .execute()
        .outputString());
    } catch (TimeoutException e) {
      logger.error("Processed timed out", e);
      throw new InterruptedException(e.getMessage());
    }
    return output.orElse("");
  }

  @Override
  public String execute(String command) throws IOException, InterruptedException {
    return this.execute(command, DEFAULT_TIMEOUT);
  }

  private List<String> getShell() {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    String shellPath = System.getProperty("shellPath", System.getenv().getOrDefault("SHELL_PATH", ""));
    List<String> shell;
    if (!shellPath.isEmpty()) {
      shell = Arrays.asList(shellPath, shellPath.contains("cmd.exe") ? "/c" : "-c");

    } else if (isWindows) {
      shell = Arrays.asList("cmd.exe", "/c");

    } else {
      shell = Arrays.asList("bash", "-c");
    }
    return shell;
  }

  @Override
  public void setLogger(Logger logger) {
    this.logger = logger;
  }

  public Path getDirectory() {
    return this.directory;
  }
}
