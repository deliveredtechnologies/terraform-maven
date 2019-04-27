package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.io.Executable;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * API for terraform destroy.
 */
public class TerraformDestroy implements TerraformOperation<String> {

  private Executable terraform;

  private enum TerraformDestroyParam {
    lockTimeout("lock-timeout"),
    target("target"),
    tfRootDir("dir"),
    autoApprove("auto-approve"),
    noColor("no-color"),
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

  TerraformDestroy(Executable terraform) {
    this.terraform = terraform;
  }

  public TerraformDestroy() throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.DESTROY));
  }

  /**
   * Executes terraform destroy. <br/>
   * <p>
   *   Valid Properties: <br/>
   *   lockTimeout - state file lock timeout<br/>
   *   target - resource target<br/>
   *   autoApprove - approve without prompt<br/>
   *   tfRootDir - the directory in which to run the apply command
   *   noColor - remove color encoding from output<br/>
   *   timeout - how long in milliseconds the terraform apply command can run<br/>
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

      switch (param) {
        case lockTimeout:
        case target:
          options.append(String.format("-%1$s=%2$s ", param.toString(), properties.getProperty(param.property)));
          break;
        case autoApprove:
        case noColor:
          options.append(String.format("-%1$s ", param).toString());
          break;
        default:
          break;
      }
    }
    if (properties.containsKey(TerraformDestroyParam.tfRootDir.property)) {
      options.append(properties.getProperty(TerraformDestroyParam.tfRootDir.property));
    }
    try {
      if (properties.containsKey(TerraformDestroyParam.timeout.property)) {
        return terraform.execute(options.toString(), Integer.parseInt(properties.getProperty(TerraformDestroyParam.timeout.property)));
      } else {
        return terraform.execute(options.toString());
      }
    } catch (IOException | InterruptedException e) {
      throw new TerraformException(e.getMessage(), e);
    }
  }
}
