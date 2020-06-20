package com.deliveredtechnologies.terraform.terraformhandler;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class TerraformPlanS3Handler extends TerraformHandler {

  private Logger logger;


  private static final Logger LOGGER = LoggerFactory.getLogger(TerraformUtils.class);

  private Executable executable;

  public TerraformPlanS3Handler(String tfRootDir, Logger logger) throws IOException, TerraformException {
    this(new CommandLine(tfRootDir == null  ? TerraformUtils.getDefaultTerraformRootModuleDir() : TerraformUtils.getTerraformRootModuleDir(tfRootDir), logger), logger);
  }

  public TerraformPlanS3Handler(Executable executable, Logger logger) {
    this.executable = executable;
    this.logger = logger;
  }

  @Override
  public void doAction(Properties properties) {

    if (properties.containsKey("planOutputFile") && properties.getProperty("planOutputFile").startsWith("s3")) {
      try {
        String planFile = properties.getProperty("planOutputFile");
        String fileName = planFile.substring(planFile.lastIndexOf("/")).replaceAll("/", "");
        if (properties.containsKey("kmsKeyId")) {
          LOGGER.debug("uploading plan files to Amazon S3 with kms encryption");
          executable.execute(String.format("aws s3 cp %1$s %2$s --sse aws:kms --sse-kms-key-id %3$s", fileName, planFile, properties.getProperty("kmsKeyId")));
        } else {
          LOGGER.debug("uploading plan files to Amazon S3 with default encryption (SSE:256)");
          executable.execute(String.format("aws s3 cp %1$s %2$s", fileName, planFile));
        }
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }
  }
}
