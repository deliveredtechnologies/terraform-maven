package com.deliveredtechnologies.terraform.terraformhandler;

import java.util.Properties;


/**
 * Chain of responsibility handler.
 */
public abstract class TerraformHandler {

  private TerraformHandler nextHandlerAction;

  public TerraformHandler nextHandlerAction(TerraformHandler nextHandlerAction) {
    this.nextHandlerAction = nextHandlerAction;
    return nextHandlerAction;
  }

  void handleRequest(Properties properties) {
    if (nextHandlerAction != null) {
      nextHandlerAction.doAction(properties);
    }
  }

  public void doAction(Properties properties) {}
}

