package com.deliveredtechnologies.maven.io;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class CommandLine implements Executable {

  private static int DEFAULT_TIMEOUT = 600000;

  private Path directory;

  public CommandLine(Path directory) {
    this.directory = directory;
  }

  @Override
  public String execute(String command, int timeout) throws IOException, InterruptedException {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    Process process;
    if (isWindows) {
      String[] cmd = new String[] {
        "cmd.exe",
        "/c",
        command
      };
      ProcessBuilder processBuilder = new ProcessBuilder(cmd);
      processBuilder.directory(directory.toFile());
      process = processBuilder.inheritIO().start();
    } else {
      String[] cmd = new String[] {
        "bash",
        "-c",
        command
      };
      ProcessBuilder processBuilder = new ProcessBuilder(cmd);
      processBuilder.directory(directory.toFile());
      process = processBuilder.start();
    }

    String output = IOUtils.toString(new InputStreamReader(process.getInputStream()));
    String error = IOUtils.toString(new InputStreamReader(process.getErrorStream()));
    process.waitFor(timeout, TimeUnit.MILLISECONDS);

    if (process.exitValue() > 0) {
      throw new IOException("Exit value was greater than zero!\n" + error);
    }
    return output;
  }

  @Override
  public String execute(String command) throws IOException, InterruptedException {
    return this.execute(command, DEFAULT_TIMEOUT);
  }
}
