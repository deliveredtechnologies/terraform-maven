package com.deliveredtechnologies.terraform;

public class TerraformException extends Exception {
  public TerraformException(String message) {
    super(message);
  }

  public TerraformException(String message, Throwable cause) {
    super(message, cause);
  }

  public TerraformException(Throwable cause) {
    super(cause);
  }
}
