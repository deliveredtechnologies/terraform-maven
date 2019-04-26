package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.io.CommandLine;
import com.deliveredtechnologies.maven.io.Executable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Decorates Executable (for use with CommandLine) to put in the context of Terraform commands.
 */
public class TerraformCommandLineDecorator implements Executable {

  private Executable commandLine;
  private TerraformCommand cmd;

  public TerraformCommandLineDecorator(TerraformCommand cmd, Executable commandLine) {
    this.commandLine = commandLine;
    this.cmd = cmd;
  }

  /**
   * Instantiates TerraformCommandLineDecorator using TerraformCommand.<br/>
   * The directory where commands are executed is src/main/tf/{root module dir}.
   * @param cmd the Terraform command to be executed
   * @throws IOException
   */
  public TerraformCommandLineDecorator(TerraformCommand cmd) throws IOException {
    this.commandLine = new CommandLine(
      TerraformUtils.getTerraformRootModuleDir());
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