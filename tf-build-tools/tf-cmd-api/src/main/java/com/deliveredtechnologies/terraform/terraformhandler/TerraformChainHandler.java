package com.deliveredtechnologies.terraform.terraformhandler;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Properties;


public class TerraformChainHandler {

  private Logger logger;

  String tfRootDir;

  private Executable executable;

  public TerraformChainHandler(String tfRootDir, Logger logger) throws TerraformException, IOException {
    this(new CommandLine(tfRootDir == null  ? TerraformUtils.getDefaultTerraformRootModuleDir() : TerraformUtils.getTerraformRootModuleDir(tfRootDir), logger), logger);
  }

  public TerraformChainHandler(Executable executable, Logger logger) {
    this.executable = executable;
    this.logger = logger;
  }

  /**
   * Executes given planAction for a given backendType - GET/PUTs terraform plan files from/to specified backend.
   * @param properties property options
   */
  public void chainInitiator(Properties properties) throws IOException, TerraformException {
    TerraformHandler terraformHandler = new TerraformPlanS3Handler(tfRootDir, logger);
    terraformHandler.nextHandler(new TerraformApplyS3Handler(tfRootDir, logger));
    terraformHandler.doAction(properties);
  }
}
