package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;

import java.io.IOException;

public class TerraformShow extends TerraformCliOperation {

  public enum Option implements TerraformOption {

    json("-json", "true"),
    noColor("-no-color"),

    //must go last
    path("%s");

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

  TerraformShow(Executable terraform) {
    super(terraform);
  }

  public TerraformShow(String tfRootDir) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.SHOW, tfRootDir));
  }

  @Override
  protected TerraformOption[] getTerraformParams() {
    return TerraformShow.Option.values();
  }
}
