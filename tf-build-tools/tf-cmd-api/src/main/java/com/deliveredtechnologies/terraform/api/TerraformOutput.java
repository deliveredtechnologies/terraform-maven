package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class TerraformOutput implements TerraformOperation<String> {
  private Executable terraform;

  TerraformOutput(Executable terraform) {
    this.terraform = terraform;
  }

  TerraformOutput(Executable terraform, Logger logger) {
    this.terraform = terraform;
    this.terraform.setLogger(logger);
  }

  public TerraformOutput() throws IOException, TerraformException {
    this(new String());
  }

  public TerraformOutput(String tfRootDir) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.OUTPUT, new CommandLine(tfRootDir == null || tfRootDir.isEmpty() ? TerraformUtils.getDefaultTerraformRootModuleDir() : TerraformUtils.getTerraformRootModuleDir(tfRootDir), false, LoggerFactory.getLogger(CommandLine.class))));
  }

  public TerraformOutput(String tfRootDir, Logger logger) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.OUTPUT, new CommandLine(tfRootDir == null || tfRootDir.isEmpty() ? TerraformUtils.getDefaultTerraformRootModuleDir() : TerraformUtils.getTerraformRootModuleDir(tfRootDir), false, logger)));
  }

  /**
   * Executes `terraform output -json -module={tfRootDir}`.
   *
   * @param properties  parameter options for terraform output (currently, only tfRootDir)
   * @return            the output from terraform output -json
   * @throws TerraformException
   */
  @Override
  public String execute(Properties properties) throws TerraformException {
    try {

      String options = "-json";

      if (properties.containsKey("timeout")) {
        return terraform.execute(options, Integer.parseInt(properties.get("timeout").toString()));
      } else {
        return terraform.execute(options);
      }
    } catch (InterruptedException | IOException e) {
      throw new TerraformException(e.getMessage(), e);
    }
  }
}
