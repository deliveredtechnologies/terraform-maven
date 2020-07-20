package com.deliveredtechnologies.terraform.api;

/**
 * Represents a Terraform parameter that is available to the TerraformCliOperation that it is associated with.
 */
public interface TerraformOption {

  /**
   * Gets the associated Terraform parameter as a Property (String) value.
   * @return the String value of the Terraform parameter.
   */
  String getFormat();

  /**
   * The default for this param.
   * @return
   */
  String getDefault();

  /**
   * Gets the Maven parameter as a String value.
   * @return  the String value of the Maven parameter.
   */
  default String getPropertyName() {
    return this.toString();
  }

}


