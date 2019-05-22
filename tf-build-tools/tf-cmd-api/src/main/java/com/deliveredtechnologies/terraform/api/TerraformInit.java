package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * API for terraform init.
 * <br>
 * See <a href="https://www.terraform.io/docs/commands/init.html">https://www.terraform.io/docs/commands/init.html</a>
 */
public class TerraformInit implements TerraformOperation<String> {

  private Executable terraform;
  private Logger log;

  enum TerraformInitParam {
    pluginDir("plugin-dir"),
    tfRootDir("tfRootDir"),
    verifyPlugins("verify-plugins"),
    getPlugins("get-plugins");

    Optional<String> name = Optional.empty();
    String property;

    TerraformInitParam(String name) {
      this.property = this.toString();
      this.name = Optional.of(name);
    }

    @Override
    public String toString() {
      return name.orElse(super.toString());
    }
  }

  public TerraformInit() throws IOException {
    this(LoggerFactory.getLogger(TerraformInit.class), new TerraformCommandLineDecorator(TerraformCommand.INIT));
  }

  public TerraformInit(Logger log) throws IOException {
    this(log, new TerraformCommandLineDecorator(TerraformCommand.INIT));
  }

  TerraformInit(Executable terraform) {
    this(LoggerFactory.getLogger(TerraformInit.class), terraform);
  }

  TerraformInit(Logger log, Executable terraform) {
    this.log = log;
    this.terraform = terraform;
  }

  /**
   * Executes terraform init. <br>
   * <p>
   *   Valid Properties: <br>
   *   tfRootDir - the directory where terraform init is called
   * </p>
   * @param properties  paramter options and properties for terraform init
   * @return            the output of terraform init
   * @throws TerraformException
   */
  @Override
  public String execute(Properties properties) throws TerraformException {
    try {
      StringBuilder options = new StringBuilder();
      String workingDir = properties.getProperty(TerraformInitParam.tfRootDir.toString(), TerraformUtils.getDefaultTerraformRootModuleDir().toAbsolutePath().toString());
      log.info(String.format("*** Terraform root module directory is '%1$s' ***", workingDir));

      for (TerraformInitParam param : TerraformInitParam.values()) {
        if (properties.containsKey(param.property)) {
          switch (param) {
            case pluginDir:
            case getPlugins:
            case verifyPlugins:
              options.append(String.format("-%1$s=%2$s ", param.toString(), properties.getProperty(param.property)));
              break;
            default:
              break;
          }
        }
      }

      options.append(String.format("-no-color %1$s", workingDir));
      return terraform.execute(options.toString());
    } catch (InterruptedException | IOException e) {
      throw new TerraformException(e);
    }
  }
}
