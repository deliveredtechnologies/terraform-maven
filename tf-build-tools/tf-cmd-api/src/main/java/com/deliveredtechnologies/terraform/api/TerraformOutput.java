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

public class TerraformOutput extends TerraformCliOperation {

  public enum Option implements TerraformOption {

    json("-json", "true"),
    noColor("-no-color"),
    state("-state=");

    public String format;
    public String defaultValue;

    Option(String format) {
      this.format = format;
    }

    Option(String format, String defaultValue) {
      this.format = format;
      this.defaultValue = defaultValue;
    }

    @Override
    public String getDefault() {
      return this.defaultValue;
    }

    @Override
    public String getFormat() {
      return this.format;
    }


  }

  TerraformOutput(Executable terraform) {
    super(terraform);
  }

  TerraformOutput(Executable terraform, Logger logger) {
    super(terraform, logger);
  }

  public TerraformOutput() throws IOException, TerraformException {
    this((String)null);
  }

  public TerraformOutput(String tfRootDir) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.OUTPUT, new CommandLine(tfRootDir == null || tfRootDir.isEmpty() ? TerraformUtils.getDefaultTerraformRootModuleDir() : TerraformUtils.getTerraformRootModuleDir(tfRootDir), false, LoggerFactory.getLogger(CommandLine.class))));
  }

  public TerraformOutput(String tfRootDir, Logger logger) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.OUTPUT, new CommandLine(tfRootDir == null || tfRootDir.isEmpty() ? TerraformUtils.getDefaultTerraformRootModuleDir() : TerraformUtils.getTerraformRootModuleDir(tfRootDir), false, logger)));
  }

  @Override
  protected TerraformOption[] getTerraformParams() {
    return TerraformOutput.Option.values();
  }
}
