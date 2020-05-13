package com.deliveredtechnologies.terraform;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class TerraformPlanFileUtils {

  private Logger logger;


  private static final Logger LOGGER = LoggerFactory.getLogger(TerraformUtils.class);

  private Executable executable;

  public TerraformPlanFileUtils(String tfRootDir, Logger logger) throws IOException {
    this(new CommandLine(tfRootDir == null  ? TerraformUtils.getDefaultTerraformRootModuleDir() : Paths.get(tfRootDir), logger), logger);
  }

  public TerraformPlanFileUtils(Executable executable, Logger logger) {
    this.executable = executable;
    this.logger = logger;
  }


  /**
   * Executes given planAction for a given backendType - GET/PUTs terraform plan files from/to specified backend.
   * @param properties property options
   * @return response
   * @throws IOException
   */
  public String executePlanFileOperation(Properties properties) throws IOException {
    //String response = "The backend destination was not specified in the format <backendType>://<backendKey> (ex: s3://<bucket-name>/<bucket-key>) to perform planFile operations";

    boolean isPlanFile = properties.containsKey("planOutputFile") || properties.containsKey("plan");
    if (isPlanFile) {
      String planOutputFile = properties.getProperty("planOutputFile");
      //String planFileName = planOutputFile.substring(planOutputFile.lastIndexOf("/")).replaceAll("/", "");
      String planFileName = planOutputFile.split("/")[planOutputFile.split("/").length - 1];

      String backendAction = "";
      if (!properties.getProperty("planOutputFile").isEmpty()) {
        backendAction = "PUT";
      } else if (!properties.getProperty("plan").isEmpty()) {
        backendAction = "GET";
      }

      if (!planOutputFile.equals(planFileName)) {
        String backendType = planOutputFile.split(":")[0].toLowerCase();
        switch (backendType) {
          case "s3":
            try {
              backendS3operations(backendAction, planOutputFile, properties);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            break;
          case "azurerm":
          default:
            LOGGER.info("PlanFile operations support is not available yet");
        }
      }
    }
    return null;
  }

  /**
   * Performs S3 operations.
   * @param action backend option either GET or PUT
   * @param s3Key fully qualified path for the s3 object to store or destination location of the file
   *
   */
  protected void backendS3operations(String action, String s3Key, Properties properties ) throws IOException, InterruptedException {
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
  }

}
