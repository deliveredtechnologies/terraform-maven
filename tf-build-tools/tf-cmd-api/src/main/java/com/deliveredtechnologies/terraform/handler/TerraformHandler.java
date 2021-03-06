package com.deliveredtechnologies.terraform.handler;

import java.util.Optional;
import java.util.Properties;


/**
 * Chain of responsibility handler.
 */
public abstract class TerraformHandler {

  private TerraformHandler nextHandler ;

  public void setNextHandler(TerraformHandler nextHandler) {
    this.nextHandler = nextHandler;
  }

  public final void handleRequest(Properties properties) {
    doAction(properties);
    Optional.ofNullable(nextHandler).ifPresent(handler -> handler.handleRequest(properties));
  }

  public void doAction(Properties properties) {}
}
