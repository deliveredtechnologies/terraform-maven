package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.io.Executable;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class TerraformApply implements TerraformOperation<String> {
  private Executable terraform;

  private enum TerraformApplyParam {
    varFiles("tf_var_files"),
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

  @Override
  public String execute(Properties properties) throws TerraformException {
    String workingDir = properties.getProperty("tfRootDir", "");
    StringBuilder options = new StringBuilder();

    for (TerraformApplyParam param : TerraformApplyParam.values()) {
      if (properties.containsKey(param.property)) {
        if (param == TerraformApplyParam.varFiles) {
          for (String file : ((String)properties.get(param.property)).split(",")) {
            options.append(String.format("-var-file=%1$s ", file.trim()));
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

