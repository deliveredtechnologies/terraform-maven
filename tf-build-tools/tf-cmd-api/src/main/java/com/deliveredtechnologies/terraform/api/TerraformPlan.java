package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * API for terraform plan.
 */
public class TerraformPlan extends TerraformCliOperation {

  public enum Option implements TerraformOption {

    compactWarnings("-compact-warnings"),
    destroyPlan("-destroy"),
    detailedExitcode("-detailed-exitcode"),
    planInput("-input=","false"),
    lock("-lock="),
    lockTimeout("-lock-timeout="),
    noColor("-no-color"),
    planOutputFile("-out="),
    refreshState("-refresh="),
    tfState("-state="),
    target("-target="),
    tfVars("-var"),
    tfVarFiles("-var-file");

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

  TerraformPlan(Executable terraform) {
    super(terraform);
  }

  TerraformPlan(Executable terraform, Logger logger) {
    super(terraform, logger);
  }

  public TerraformPlan() throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.PLAN));
  }

  public TerraformPlan(Logger logger) throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.PLAN, logger));
  }

  public TerraformPlan(String tfRootDir) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.PLAN, tfRootDir));
  }

  public TerraformPlan(String tfRootDir, Logger logger) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.PLAN, tfRootDir), logger);
  }

  @Override
  protected TerraformOption[] getTerraformParams() {
    return TerraformPlan.Option.values();
  }
}
