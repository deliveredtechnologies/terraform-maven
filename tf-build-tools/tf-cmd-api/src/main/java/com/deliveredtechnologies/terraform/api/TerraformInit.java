package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * API for terraform init.
 * <br>
 * See <a href="https://www.terraform.io/docs/commands/init.html">https://www.terraform.io/docs/commands/init.html</a>
 */
public class TerraformInit extends TerraformCliOperation {

  public enum Option implements TerraformOption {

    input("-input="),
    lock("-lock="),
    lockTimeout("-lock-timeout="),
    noColor("-no-color"),
    upgrade("-upgrade"),
    fromModule("-from-module="),
    forceCopy("-force-copy"),
    //TODO custom csv and map
    backendConfig("-backend-config="),
    pluginDir("-plugin-dir="),
    getPlugins("-get-plugins="),
    verifyPlugins("-verify-plugins=");

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
    public String getFormat() {
      return this.format;
    }

    @Override
    public String getDefault() {
      return this.defaultValue;
    }
  }

  public TerraformInit() throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.INIT));
  }

  public TerraformInit(String tfRootDir) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.INIT, tfRootDir));
  }

  public TerraformInit(Logger logger) throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.INIT, logger));
  }

  public TerraformInit(String tfRootDir, Logger logger) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.INIT, tfRootDir), logger);
  }

  TerraformInit(Executable terraform) {
    super(terraform);
  }

  TerraformInit(Executable terraform, Logger logger) {
    super(terraform, logger);
  }

  @Override
  protected TerraformOption[] getTerraformParams() {
    return TerraformInit.Option.values();
  }
}
