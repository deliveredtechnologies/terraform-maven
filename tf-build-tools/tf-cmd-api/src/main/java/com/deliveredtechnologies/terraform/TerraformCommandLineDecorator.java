package com.deliveredtechnologies.terraform;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Decorates Executable (for use with CommandLine) to put in the context of Terraform commands.
 */
public class TerraformCommandLineDecorator implements Executable {

  private Executable commandLine;
  private TerraformCommand cmd;
  private Optional<Logger> logger = Optional.empty();

  /**
   * Instantiates TerraformCommandLineDecorator with a TerraformCommand (e.g. INIT, PLAN, etc.), an Executable (i.e. CommandLine), and an SLF4J Logger.
   * @param cmd         A TerraformCommand (e.g. INIT, PLAN, APPLY, etc.)
   * @param commandLine A CommandLine instance
   */
  public TerraformCommandLineDecorator(TerraformCommand cmd, Executable commandLine, Logger logger) {
    this.commandLine = commandLine;
    this.cmd = cmd;

    this.logger = Optional.ofNullable(logger);
    this.logger.ifPresent(log -> this.commandLine.setLogger(log));
  }

  /**
   * Instantiates TerraformCommandLineDecorator with a TerraformCommand (e.g. INIT, PLAN, etc.) and an Executable (i.e. CommandLine).
   * @param cmd         A TerraformCommand (e.g. INIT, PLAN, APPLY, etc.)
   * @param commandLine A CommandLine instance
   */
  public TerraformCommandLineDecorator(TerraformCommand cmd, Executable commandLine) {
    this(cmd, commandLine, null);
  }

  /**
   * Instantiates TerraformCommandLineDecorator using TerraformCommand and SLF4J Logger.<br>
   * The directory where commands are executed is src/main/terraform/{root module dir}.
   * @param cmd the Terraform command to be executed
   * @param logger SLF4J Logger object
   *
   * @throws IOException
   */
  public TerraformCommandLineDecorator(TerraformCommand cmd, Logger logger) throws IOException {
    this(cmd, new CommandLine(TerraformUtils.getDefaultTerraformRootModuleDir()), logger);
  }

  /**
   * Instantiates TerraformCommandLineDecorator using TerraformCommand.<br>
   * The directory where commands are executed is src/main/terraform/{root module dir}.
   * @param cmd the Terraform command to be executed
   * @throws IOException
   */
  public TerraformCommandLineDecorator(TerraformCommand cmd) throws IOException {
    this(cmd, new CommandLine(TerraformUtils.getDefaultTerraformRootModuleDir()));
  }

  /**
   * Instantiates TerraformCommandLineDecorator using TerraformCommand.<br>
   * The directory where commands are executed is based on {tfRootDir}.
   * @param cmd the Terraform command to be executed
   * @throws IOException
   */
  public TerraformCommandLineDecorator(TerraformCommand cmd, String tfRootDir) throws IOException, TerraformException {
    this(cmd, new CommandLine(tfRootDir == null ? TerraformUtils.getDefaultTerraformRootModuleDir() : TerraformUtils.getTerraformRootModuleDir(tfRootDir)));
  }

  @Override
  public String execute(String command, int timeout) throws IOException, InterruptedException {
    return commandLine.execute(getTerraformCommand(command), timeout);
  }

  @Override
  public String execute(String command) throws IOException, InterruptedException {
    return commandLine.execute(getTerraformCommand(command));
  }

  @Override
  public void setLogger(Logger logger) {
    this.logger = Optional.ofNullable(logger);
    this.commandLine.setLogger(logger);
  }

  public Executable getCommandLine() {
    return this.commandLine;
  }

  private String getTerraformCommand(String command) {

    return String.format("%s %s %s", getTFExecutable(), cmd.toString(), StringUtils.isEmpty(command) ? "" : command);
  }

  private String getTFExecutable() {
    File tfwrapper = new File(".tf/tfw");
    if (tfwrapper.exists()) {
      tfwrapper.setExecutable(true);
    }
    return tfwrapper.exists() ? tfwrapper.getAbsolutePath().replaceAll("[\\\\]","/") : "terraform";
  }
}
