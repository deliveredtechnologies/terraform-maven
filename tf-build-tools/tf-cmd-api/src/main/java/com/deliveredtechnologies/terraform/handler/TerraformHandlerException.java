package com.deliveredtechnologies.terraform.handler;

/**
 * Terraform Handler Exception.
 */
public class TerraformHandlerException extends Exception {
  public TerraformHandlerException(String message) {
    super(message);
  }

  public TerraformHandlerException(String message, Throwable cause) {
    super(message, cause);
  }
}
