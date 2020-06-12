package com.deliveredtechnologies.terraform.planfileutils;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class TerraformApplyS3Handler extends PlanFileActions {
  private Logger logger;


  private static final Logger LOGGER = LoggerFactory.getLogger(TerraformUtils.class);

  private Executable executable;

  public TerraformApplyS3Handler(String tfRootDir, Logger logger) throws IOException, TerraformException {
    this(new CommandLine(tfRootDir == null  ? TerraformUtils.getDefaultTerraformRootModuleDir() : TerraformUtils.getTerraformRootModuleDir(tfRootDir), logger), logger);
  }

  public TerraformApplyS3Handler(Executable executable, Logger logger) {
    this.executable = executable;
    this.logger = logger;
  }

  @Override
  public void doAction(Properties properties) {

    if (properties.containsKey("plan")) {
      String planFile = properties.getProperty("plan");
      String planFileName = planFile.split("/")[planFile.split("/").length - 1];

      if (!planFile.equals(planFileName)) {
        try {
          String bucketName = planFile.split("/")[2];
          String fileName = planFile.substring(planFile.lastIndexOf("/")).replaceAll("/", "");
          executable.execute(String.format("aws s3api get-object --bucket %1$s --key %2$s %3$s", bucketName, planFile, fileName));
        } catch (InterruptedException | IOException e) {
          e.printStackTrace();
        }
      }
    } else {
      nextPlanFileAction.doAction(properties);
    }
  }
}
