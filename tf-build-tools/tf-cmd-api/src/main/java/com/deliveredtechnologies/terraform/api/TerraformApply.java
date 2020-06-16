package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * API for terraform apply.
 */
public class TerraformApply implements TerraformOperation<String> {
  private Executable terraform;

  enum TerraformApplyParam {
    tfVars("var"),
    tfVarFiles("var-file"),
    lockTimeout("lock-timeout"),
    target("target"),
    plan("plan"),
    noColor("no-color"),
    refreshState("refresh"),
    timeout("timeout");

    Optional<String> name = Optional.empty();
    String property;

    TerraformApplyParam(String name) {
      this.property = this.toString();
      this.name = Optional.of(name);
    }

    @Override
    public String toString() {
      return name.orElse(super.toString());
    }
  }

  TerraformApply(Executable terraform, Logger logger) {
    this.terraform = terraform;
    this.terraform.setLogger(logger);
  }

  TerraformApply(Executable terraform) {
    this.terraform = terraform;
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

  /**
   * Executes terraform apply.
   * <p>
   *   Valid Properties: <br>
   *   tfVars - a comma delimited list of terraform variables<br>
   *   varFiles - a comma delimited list of terraform vars files<br>
   *   lockTimeout - state file lock timeout<br>
   *   target - resource target<br>
   *   autoApprove - approve without prompt<br>
   *   plan - the plan file to run the apply against<br>
   *   noColor - remove color encoding from output<br>
   *   refreshState - if true then refresh the state prior to apply<br>
   *   timeout - how long in milliseconds the terraform apply command can run<br>
   * </p>
   * @param properties  parameter options and properties for terraform apply
   * @return            the output of terraform apply
   * @throws TerraformException
   */
  @Override
  public String execute(Properties properties) throws TerraformException {
    StringBuilder options = new StringBuilder();

    for (TerraformApplyParam param : TerraformApplyParam.values()) {
      if (properties.containsKey(param.property)) {
        if (param == TerraformApplyParam.tfVarFiles) {
          for (String file : (properties.getProperty(param.property)).split(",")) {
            options.append(String.format("-%1$s=%2$s ", param, file.trim()));
          }
          continue;
        }
        if (param == TerraformApplyParam.tfVars) {
          for (String var : (properties.get(param.property)).toString().split(",")) {
            options.append(String.format("-%1$s '%2$s' ", param, var.trim()));
          }
          continue;
        }
        switch (param) {
          case noColor:
            options.append(String.format("-%1$s ", param));
            break;
          case timeout:
          case plan:
            break;
          default:
            options.append(String.format("-%1$s=%2$s ", param, properties.get(param.property)));
        }
      }
    }

    options.append("-auto-approve ");

    try {
      if (properties.containsKey(TerraformApplyParam.plan.property)) {
        options.append(properties.getProperty(TerraformApplyParam.plan.property));
      }

      if (properties.containsKey(TerraformApplyParam.timeout.property)) {
        return terraform.execute(options.toString(), Integer.parseInt(properties.get(TerraformApplyParam.timeout.property).toString()));
      } else {
        return terraform.execute(options.toString());
      }
    } catch (InterruptedException | IOException e) {
      throw new TerraformException(e.getMessage(), e);
    }
  }
}

