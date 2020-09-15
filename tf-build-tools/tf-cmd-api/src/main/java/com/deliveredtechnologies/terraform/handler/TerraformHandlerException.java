package com.deliveredtechnologies.terraform.handler;

public class TerraformHandlerException extends Exception {
  public TerraformHandlerException(String message) {
    super(message);
  }

  public TerraformHandlerException(String message, Throwable cause) {
    super(message, cause);
  }
}
