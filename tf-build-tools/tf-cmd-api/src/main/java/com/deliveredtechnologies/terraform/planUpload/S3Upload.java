/*

package com.deliveredtechnologies.terraform.planUpload;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import com.deliveredtechnologies.terraform.planUpload.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;


public class S3Upload extends Upload {

  public S3Upload(Properties properties) {

    private Logger logger;

    private static final Logger LOGGER = LoggerFactory.getLogger(TerraformUtils.class);

    private Executable executable;

    public S3Upload(String tfRootDir, Logger logger) throws IOException, TerraformException {
      this(new CommandLine(tfRootDir == null  ? TerraformUtils.getDefaultTerraformRootModuleDir() : TerraformUtils.getTerraformRootModuleDir(tfRootDir), logger), logger);
    }


    public S3Upload(Executable executable, Logger logger) {
      this.executable = executable;
      this.logger = logger;
    }

    String planFile = properties.getProperty("planOutputFile") == null ? properties.getProperty("plan") : properties.getProperty("planOutputFile");
    String planFileName = planFile.split("/")[planFile.split("/").length - 1];

    String backendAction = properties.getProperty("planOutputFile") == null ? "GET" : "PUT";

    if (!planFile.equals(planFileName)) {
      String backendType = planFile.split(":")[0].toLowerCase();

      String bucketName = planFile.split("/")[2];
      String fileName = planFile.substring(planFile.lastIndexOf("/")).replaceAll("/", "");
      if (backendAction.equals("GET")) {
        executable.execute(String.format("aws s3api get-object --bucket %1$s --key %2$s %3$s", bucketName, fileName, fileName));
      } else if (backendAction.equals("PUT")) {
        if (properties.containsKey("kmsKeyId")) {
          LOGGER.debug("uploading plan files to Amazon S3 with kms encryption");
          executable.execute(String.format("aws s3 cp %1$s %2$s --sse aws:kms --sse-kms-key-id %3$s", fileName, planFile, properties.getProperty("kmsKeyId")));
        } else {
          LOGGER.debug("uploading plan files to Amazon S3 with default encryption (SSE:256)");
          executable.execute(String.format("aws s3 cp %1$s %2$s", fileName, planFile));
        }
      }
    }

  }

}

 */
