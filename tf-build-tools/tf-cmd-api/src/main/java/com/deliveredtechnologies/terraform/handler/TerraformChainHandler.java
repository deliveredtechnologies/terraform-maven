package com.deliveredtechnologies.terraform.handler;

import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.Set;

public class TerraformChainHandler {

  private Logger logger;

  String tfRootDir;

  Set<Class<? extends TerraformHandler>> handlerClasses;

  /**
   * Executes given planAction for a given backendType - GET/PUTs terraform plan files from/to specified backend.
   * @param tfRootDir property options
   */
  public TerraformChainHandler(String tfRootDir, Logger logger) throws TerraformException, IOException {
    this.logger = logger;
    this.tfRootDir = (StringUtils.isEmpty(tfRootDir) ? TerraformUtils.getDefaultTerraformRootModuleDir() : TerraformUtils.getTerraformRootModuleDir(tfRootDir)).toString();
    this.handlerClasses = getHandlerClasses();
  }

  public TerraformChainHandler(Logger logger) throws TerraformException, IOException {
    this(null, logger);
  }

  private Set<Class<? extends TerraformHandler>> getHandlerClasses() {
    Reflections reflections = new Reflections(this.getClass().getPackage());
    return reflections.getSubTypesOf(TerraformHandler.class);
  }

  /**
   * Executes given planAction for a given backendType - GET/PUTs terraform plan files from/to specified backend.
   * @param properties property options
   */
  public void chainInitiator(Properties properties) throws ClassNotFoundException {
    for (Class<? extends TerraformHandler> terraformHandler : this.handlerClasses) {
      try {
        Class cls = Class.forName(terraformHandler.getName());
        Object obj = cls.getConstructor(new Class[]{String.class, Logger.class}).newInstance(new Object[]{tfRootDir, logger});
        ((TerraformHandler) obj).doAction(properties);
      } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }
}
