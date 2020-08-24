package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * API for terraform destroy.
 */
public class TerraformDestroy extends TerraformCliOperation {

  public enum Option implements TerraformOption {
    tfVars("-var"),
    tfVarFiles("-var-file"),
    lockTimeout("-lock-timeout="),
    target("-target="),
    noColor("-no-color"),
    refreshState("-refresh="),
    autoApprove("-auto-approve", "true");

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

  TerraformDestroy(Executable terraform, Logger logger) {
    super(terraform, logger);
  }

  TerraformDestroy(Executable terraform) {
    super(terraform);
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

  @Override
  public TerraformOption[] getTerraformParams() {
    return TerraformDestroy.Option.values();
  }
}
