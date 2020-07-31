package com.deliveredtechnologies.terraform.handler;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Set;
import java.util.Properties;


public class TerraformChainHandler {

  private Logger logger;
  String tfRootDir;
  Set<Class<? extends TerraformHandler>> handlerClasses;

  public TerraformChainHandler(String tfRootDir, Logger logger) throws TerraformException, IOException {
    this.logger = logger;
    this.tfRootDir = (StringUtils.isEmpty(tfRootDir) ? TerraformUtils.getDefaultTerraformRootModuleDir() : TerraformUtils.getTerraformRootModuleDir(tfRootDir)).toString();
    this.handlerClasses = getHandlerClasses();

  }

  private Set<Class<? extends TerraformHandler>> getHandlerClasses() {
    Reflections reflections = new Reflections(this.getClass().getPackage());
    return reflections.getSubTypesOf(TerraformHandler.class);
  }

  public TerraformChainHandler(Logger logger) throws TerraformException, IOException {
    this(null, logger);
  }

  /**
   * Executes given planAction for a given backendType - GET/PUTs terraform plan files from/to specified backend.
   * @param properties property options
   */
  public void chainInitiator(Properties properties) throws IOException, TerraformException {
    //TODO: Make this use the list of handler classes that we got in the constructor
    TerraformHandler terraformHandler = new TerraformPlanS3Handler(tfRootDir, logger);
    terraformHandler.nextHandler(new TerraformApplyS3Handler(tfRootDir, logger));
    terraformHandler.handleRequest(properties);
  }
}
