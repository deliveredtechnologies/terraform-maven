package com.deliveredtechnologies.terraform.planfileutils;

import java.util.Optional;
import java.util.Properties;


/**
 * Chain of responsibility handler.
 */

public abstract class TerraformHandler {
  Optional<TerraformHandler> nextHandler = Optional.empty();

  void handleRequest(Properties properties) {
    this.nextHandler.ifPresent(handler -> handler.doAction(properties));
  }

  public void doAction(Properties properties) {}
}

