/*
package com.deliveredtechnologies.terraform;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;


public class TerraformPlanFileUtils {

  private Logger logger;


  private static final Logger LOGGER = LoggerFactory.getLogger(TerraformUtils.class);

  private Executable executable;

  public TerraformPlanFileUtils(String tfRootDir, Logger logger) throws IOException, TerraformException {
    this(new CommandLine(tfRootDir == null  ? TerraformUtils.getDefaultTerraformRootModuleDir() : TerraformUtils.getTerraformRootModuleDir(tfRootDir), logger), logger);
  }


  public TerraformPlanFileUtils(Executable executable, Logger logger) {
    this.executable = executable;
    this.logger = logger;
  }



  public String executePlanFileOperation(Properties properties) throws IOException {

    boolean isPlanFile = properties.containsKey("planOutputFile") || properties.containsKey("plan");

    if (isPlanFile) {
      String planFile = properties.getProperty("planOutputFile") == null ? properties.getProperty("plan") : properties.getProperty("planOutputFile");
      String planFileName = planFile.split("/")[planFile.split("/").length - 1];

      String backendAction = properties.getProperty("planOutputFile") == null ? "GET" : "PUT";

      if (!planFile.equals(planFileName)) {
        String backendType = planFile.split(":")[0].toLowerCase();
        switch (backendType) {
          case "s3":
            backendS3operations(backendAction, planFile, properties);
            break;
          default:
            break;
        }
      }
    }
    return null;
  }


  protected void backendS3operations(String action, String s3Key, Properties properties ) throws IOException {
    try {
      String bucketName = s3Key.split("/")[2];
      String fileName = s3Key.substring(s3Key.lastIndexOf("/")).replaceAll("/", "");
      if (action.equals("GET")) {
        executable.execute(String.format("aws s3api get-object --bucket %1$s --key %2$s %3$s", bucketName, fileName, fileName));
      } else if (action.equals("PUT")) {
        if (properties.containsKey("kmsKeyId")) {
          LOGGER.debug("uploading plan files to Amazon S3 with kms encryption");
          executable.execute(String.format("aws s3 cp %1$s %2$s --sse aws:kms --sse-kms-key-id %3$s", fileName, s3Key, properties.getProperty("kmsKeyId")));
        } else {
          LOGGER.debug("uploading plan files to Amazon S3 with default encryption (SSE:256)");
          executable.execute(String.format("aws s3 cp %1$s %2$s", fileName, s3Key));
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

 */


