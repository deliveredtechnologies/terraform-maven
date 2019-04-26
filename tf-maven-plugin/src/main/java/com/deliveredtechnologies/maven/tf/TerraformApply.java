package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.io.Executable;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * API for terraform apply.
 */
public class TerraformApply implements TerraformOperation<String> {
  private Executable terraform;

  private enum TerraformApplyParam {
    tfVars("var"),
    varFiles("var_file"),
    lockTimeout("lock-timeout"),
    target("target"),
    autoApprove("auto-approve"),
    noColor("no-color"),
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

  TerraformApply(Executable terraform) {
    this.terraform = terraform;
  }

  public TerraformApply() throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.APPLY));
  }

  /**
   * Executes terraform apply. <br/>
   * <p>
   *   Valid Properties: <br/>
   *   tfVars - a comma delimited list of terraform variables<br/>
   *   varFiles - a comma delimited list of terraform vars files<br/>
   *   lockTimeout - state file lock timeout<br/>
   *   target - resource target<br/>
   *   autoApprove - approve without prompt<br/>
   *   noColor - remove color encoding from output<br/>
   *   timeout - how long in milliseconds the terraform apply command can run<br/>   *
   * </p>
   * @param properties  paramter options and properties for terraform apply
   * @return            the output of terraform apply
   * @throws TerraformException
   */
  @Override
  public String execute(Properties properties) throws TerraformException {
    String workingDir = properties.getProperty("tfRootDir", "");
    StringBuilder options = new StringBuilder();

    for (TerraformApplyParam param : TerraformApplyParam.values()) {
      if (properties.containsKey(param.property)) {
        if (param == TerraformApplyParam.varFiles) {
          for (String file : ((String)properties.get(param.property)).split(",")) {
            options.append(String.format("-%1$s=%2$s ", param, file.trim()));
          }
          continue;
        }
        if (param == TerraformApplyParam.tfVars) {
          for (String var : ((String)properties.get(param.property)).split(",")) {
            options.append(String.format("-%1$s '%2$s' ", param, var.trim()));
          }
          continue;
        }
        switch (param) {
          case autoApprove:
          case noColor:
            options.append(String.format("-%1$s ", param));
            break;
          case timeout:
            break;
          default:
            options.append(String.format("-%1$s=%2$s ", param, properties.get(param.property)));
        }
      }
    }
    for (String key :
        properties.keySet().stream()
        .filter(key -> ((String)key).startsWith("tfVar."))
        .map(key -> (String)key).collect(Collectors.toList())) {
      {
        options.append(String.format("-var '%1$s=%2$s' ", key.substring(6), properties.getProperty(key)));
      }
    }
    try {
      if (properties.containsKey("timeout")) {
        return terraform.execute(options.toString(), Integer.parseInt((String) properties.get("timeout")));
      } else {
        return terraform.execute(options.toString());
      }
    } catch (InterruptedException | IOException e) {
      throw new TerraformException(e.getMessage(), e);
    }
  }
}

