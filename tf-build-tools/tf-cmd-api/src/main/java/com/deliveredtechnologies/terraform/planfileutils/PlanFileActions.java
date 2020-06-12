package com.deliveredtechnologies.terraform.planfileutils;

import java.util.Properties;


/**
 * Chain of responsibility handler.
 */

public abstract class PlanFileActions {

  PlanFileActions nextPlanFileAction;

  public void planFileOperation(PlanFileActions nextPlanFileAction) {

    this.nextPlanFileAction = nextPlanFileAction;
  }

  public void doAction(Properties properties) {

  }

}

