package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;

import java.io.IOException;
import java.util.Properties;

public class TerraformOutput implements TerraformOperation<String> {
  private Executable terraform;

  TerraformOutput(Executable terraform) {
    this.terraform = terraform;
  }

  public TerraformOutput() throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.OUTPUT));
  }

  public TerraformOutput(String tfRootDir) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.OUTPUT, tfRootDir));
  }

  /**
   * Executes `terraform output -json -module={tfRootDir}`.
   *
   * @param properties  parameter options for terraform output (currently, only tfRootDir)
   * @return            the output from terraform output -json
   * @throws TerraformException
   */
  @Override
  public String execute(Properties properties) throws TerraformException {
    try {

      String options = "-json";

      if (properties.containsKey("timeout")) {
        return terraform.execute(options, Integer.parseInt(properties.getProperty("timeout")));
      } else {
        return terraform.execute(options);
      }
    } catch (InterruptedException | IOException e) {
      throw new TerraformException(e.getMessage(), e);
    }
  }
}
