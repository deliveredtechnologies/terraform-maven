package com.deliveredtechnologies.terraform.handler;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class TerraformApplyS3Handler extends TerraformHandler {
  private Logger logger;


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
    if (properties.containsKey("plan") && properties.getProperty("plan").startsWith("s3")) {
      try {
        String planFile = properties.getProperty("plan");
        String bucketName = planFile.split("/")[2];
        String fileName = planFile.replaceAll("s3://" + bucketName + "/", "");
        executable.execute(String.format("aws s3api get-object --bucket %1$s --key %2$s %3$s", bucketName, fileName, planFile.substring(planFile.lastIndexOf("/")).replaceAll("/", "")));
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }
  }
}

