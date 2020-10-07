package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.converters.TfCliOptionBuilder;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * TerraformCliOperation abstracts the common functionality associated with putting together a terraform cli command.
 * Template Method Pattern is used.
 * Subclasses are expected to implement getAddtionalParams() for operation specific commands that can not be easily abstracted into key/value pairs for Terraform params.
 * Subclasses are also expected to provide an array of TerraformParam objects via getTerraformParams() that represent the parameters supported.
 * TerraformParam is enum friendly with only minor extensions required.
 */
public abstract class TerraformCliOperation implements TerraformOperation<String> {

  private static final String TIMEOUT = "timeout";

  private Executable terraform;
  private TfCliOptionBuilder tfCliOptionBuilder = new TfCliOptionBuilder(TfCliOptionBuilder.initializeFormatterRegistry());

  public TerraformCliOperation(Executable terraform) {
    this.terraform = terraform;
  }

  /**
   * Cli Operation.
   * @param terraform
   * @param logger
   */
  public TerraformCliOperation(Executable terraform, Logger logger) {
    this.terraform = terraform;
    this.terraform.setLogger(logger);
  }

  @Override
  public String execute(Properties properties) throws TerraformException {


    String options = tfCliOptionBuilder.convert(getTerraformParams(), properties);

    try {
      if (properties.containsKey(TIMEOUT)) {
        return terraform.execute(options.toString(), Integer.parseInt(properties.get(TIMEOUT).toString()));
      } else {
        return terraform.execute(options.toString());
      }
    } catch (InterruptedException | IOException e) {

      throw new TerraformException(e.getMessage(), e);
    }
  }

  /**
   * Gets the Optional value of a TerraformParam array representing the available paramters for a specific TerraformCliOperation.
   * @return Optional value of a TerraformParam array representing the available paramters for a specific TerraformCliOperation.
   */
  protected abstract TerraformOption[] getTerraformParams();
}
