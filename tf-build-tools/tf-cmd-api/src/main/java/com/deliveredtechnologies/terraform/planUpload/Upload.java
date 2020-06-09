package com.deliveredtechnologies.terraform.planUpload;


import java.io.IOException;
import java.util.Properties;

public abstract class Upload {

  public Upload nextHandler;

  public void setNextHandler(Upload nextHandler) {
    this.nextHandler = nextHandler;
  }

  public String executePlanFileOperation(Properties properties) throws IOException {
    boolean isPlanFile = properties.containsKey("planOutputFile") || properties.containsKey("plan");

    if (isPlanFile) {
      nextHandler.executePlanFileOperation(properties);
    }
    return null;
  }

}
