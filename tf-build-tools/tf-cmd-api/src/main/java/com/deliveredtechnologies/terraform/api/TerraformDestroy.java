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
 * API for terraform destroy.
 */
public class TerraformDestroy implements TerraformOperation<String> {

  private Executable terraform;

  enum TerraformDestroyParam {
    lockTimeout("lock-timeout"),
    target("target"),
    tfVars("var"),
    tfVarFiles("var-file"),
    noColor("no-color"),
    refreshState("refresh"),
    timeout("timeout");

    Optional<String> name = Optional.empty();
    String property;

    TerraformDestroyParam(String name) {
      this.property = this.toString();
      this.name = Optional.of(name);
    }

    @Override
    public String toString() {
      return name.orElse(super.toString());
    }
  }

  TerraformDestroy(Executable terraform, Logger logger) {
    this.terraform = terraform;
    this.terraform.setLogger(logger);
  }

  TerraformDestroy(Executable terraform) {
    this.terraform = terraform;
  }

  public TerraformDestroy(Logger logger) throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.DESTROY, logger));
  }

  public TerraformDestroy() throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.DESTROY));
  }

  public TerraformDestroy(String tfRootDir) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.DESTROY, tfRootDir));
  }

  public TerraformDestroy(String tfRootDir, Logger logger) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.DESTROY, tfRootDir), logger);
  }

  /**
   * Executes terraform destroy. <br>
   * <p>
   *   Valid Properties: <br>
   *   lockTimeout - state file lock timeout<br>
   *   target - resource target<br>
   *   autoApprove - approve without prompt<br>
   *   noColor - remove color encoding from output<br>
   *   refreshState - if true then refresh the state prior to destroy<br>
   *   timeout - how long in milliseconds the terraform apply command can run<br>
   * </p>
   * @param properties  paramter options and properties for terraform apply
   * @return            the output of terraform apply
   * @throws TerraformException
   */
  @Override
  public String execute(Properties properties) throws TerraformException {
    StringBuilder options = new StringBuilder();
    for (TerraformDestroyParam param : TerraformDestroyParam.values()) {
      if (!properties.containsKey(param.property)) continue;

      if (param == TerraformDestroy.TerraformDestroyParam.tfVarFiles) {
        for (String file : (properties.getProperty(param.property)).split(",")) {
          options.append(String.format("-%1$s=%2$s ", param, file.trim()));
        }
        continue;
      }
      if (param == TerraformDestroyParam.tfVars) {
        for (String var : (properties.get(param.property)).toString().split(",")) {
          options.append(String.format("-%1$s '%2$s' ", param, var.trim()));
        }
        continue;
      }

      switch (param) {
        case lockTimeout:
        case target:
          options.append(String.format("-%1$s=%2$s ", param.toString(), properties.getProperty(param.property)));
          break;
        case noColor:
          options.append(String.format("-%1$s ", param));
          break;
        default:
          break;
      }
    }
    options.append("-auto-approve ");

    try {
      if (properties.containsKey(TerraformDestroyParam.timeout.property)) {
        return terraform.execute(options.toString(), Integer.parseInt(properties.get(TerraformDestroyParam.timeout.property).toString()));
      } else {
        return terraform.execute(options.toString());
      }
    } catch (IOException | InterruptedException e) {
      throw new TerraformException(e.getMessage(), e);
    }
  }
}
