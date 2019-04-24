package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.io.CommandLine;
import com.deliveredtechnologies.maven.io.Executable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TerraformCmdLineDecorator implements Executable {

  private CommandLine commandLine;
  private TerraformCommand cmd;

  public TerraformCmdLineDecorator(TerraformCommand cmd, CommandLine commandLine) {
    this.commandLine = commandLine;
    this.cmd = cmd;
  }

  public TerraformCmdLineDecorator(TerraformCommand cmd) throws IOException {
    this.commandLine = new CommandLine(
      Files.walk(
        Paths.get("src", "main", "tf"), 1)
             .filter(path -> path.getFileName().toString() != "tf")
             .findFirst().orElseThrow(() -> new IOException("Terraform root module not found")));
  }

  @Override
  public String execute(String command, int timeout) throws IOException, InterruptedException {
    return commandLine.execute(getTerraformCommand(command), timeout);
  }

  @Override
  public String execute(String command) throws IOException, InterruptedException {
    return commandLine.execute(getTerraformCommand(command));
  }

  private String getTerraformCommand(String command) {
    return String.format("terraform %1$s %2$s", cmd.toString(), command);
  }
}
