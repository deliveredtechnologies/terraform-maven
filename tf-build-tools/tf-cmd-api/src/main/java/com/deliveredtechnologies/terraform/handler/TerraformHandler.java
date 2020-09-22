package com.deliveredtechnologies.terraform.handler;

import java.util.Optional;
import java.util.Properties;


/**
 * Chain of responsibility handler.
 */
public class TerraformHandler {

  private TerraformHandler nextHandler ;

  public TerraformHandler nextHandler(TerraformHandler nextHandler) {
    this.nextHandler = nextHandler;
    return nextHandler;
  }

  protected final void handleRequest(Properties properties) {
    doAction(properties);
    Optional.ofNullable(nextHandler).ifPresent(handler -> handler.handleRequest(properties));
  }

  public void doAction(Properties properties) {}
}

