package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.io.Executable;

import java.io.IOException;
import java.util.Properties;

/**
 * API for terraform init.
 */
public class TerraformInit implements TerraformOperation<String> {

  private Executable terraform;

  public TerraformInit() throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.INIT));
  }

  TerraformInit(Executable terraform) {
    this.terraform = terraform;
  }

  /**
   * Executes terraform init. <br/>
   * <p>
   *   Valid Properties: <br/>
   *   tfRootDir - the directory where terraform init is called
   * </p>
   * @param properties  paramter options and properties for terraform init
   * @return            the output of terraform init
   * @throws TerraformException
   */
  @Override
  public String execute(Properties properties) throws TerraformException {
    try {
      String workingDir = properties.getProperty("tfRootDir", "");
      String params = String.format("-no-color %1$s", workingDir);
      return terraform.execute(params);
    } catch (InterruptedException | IOException e) {
      throw new TerraformException(e);
    }
  }
}
