package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * API for terraform apply.
 */
public class TerraformApply extends TerraformCliOperation {

  public enum Option implements TerraformOption {
    tfVars("-var"),
    tfVarFiles("-var-file"),
    lockTimeout("-lock-timeout="),
    target("-target="),
    noColor("-no-color"),
    refreshState("-refresh="),
    autoApprove("-auto-approve", "true"),
    //TODO add input and align with plan
    //Must be last option on command line
    plan("%s");
    //timeout("-timeout="),

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

  TerraformApply(Executable terraform, Logger logger) {
    super(terraform, logger);
  }

  TerraformApply(Executable terraform) {
    super(terraform);
  }

  public TerraformApply() throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.APPLY));
  }

  public TerraformApply(Logger logger) throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.APPLY, logger));
  }

  public TerraformApply(String tfRootDir) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.APPLY, tfRootDir));
  }

  public TerraformApply(String tfRootDir, Logger logger) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.APPLY, tfRootDir), logger);
  }

  @Override
  public TerraformOption[] getTerraformParams() {
    return Option.values();
  }
}

