package com.deliveredtechnologies.terraform;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class TerraformPlanFileUtils {

  String tfRootDir;
  private Logger logger;

  public TerraformPlanFileUtils(String tfRootDir) {
    tfRootDir = this.tfRootDir;
  }

  enum TerraformPlanFileUtilsParams {
    tfRootDir,
    artifactory,
    azurerm,
    consul,
    cos,
    etcd,
    etcdv3,
    gcs,
    http,
    manta,
    oss,
    pg,
    s3,
    swift,
    terraformEnterprise;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(TerraformUtils.class);

  private Executable executable;


  /*
  enum TerraformS3UtilsParams {
    planOutputFile,
    plan,
    sse,
    kmsKeyId;
  }

   */

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
  public String  executePlanFileOperation(Properties properties) throws IOException {
    LOGGER.info("checking for prop:::" + properties.getProperty("planOutputFile"));
    String planAction = "";
    if (properties.containsKey("plan")) {
      planAction = "download";
    } else if (properties.containsKey("planOutputFile")) {
      planAction = "upload";
    }
    String response = "Unable to perform S3 upload. The S3 destination was not specified in the format s3://<bucket-name>/<bucket-key>";
    /*
    String backendType = "s3";
    String planFileName = "";
    String planOutputFile = "";

     */
    boolean isPlanOutputFile = !properties.getProperty("planOutputFile").isEmpty();
    LOGGER.info(String.valueOf(isPlanOutputFile));


    String planOutputFile = properties.getProperty("planOutputFile").isEmpty() ? "planning" : properties.getProperty("planOutputFile");
    LOGGER.info("planOutputFile" + planOutputFile);
    String backendType = properties.getProperty("planOutputFile").isEmpty() ? "s3" : planOutputFile.split(":")[0];
    LOGGER.info("backend" + backendType);
    String planFileName = properties.getProperty("planOutputFile").isEmpty() ? "output.json" : planOutputFile.substring(planOutputFile.lastIndexOf("/")).replaceAll("/", "");
    LOGGER.info("planFileName" + planFileName);


    LOGGER.info(String.format("Running terraformPlanUtils for given backend parameters -%1$s  -%2$s  -%3$s -%4$s", planOutputFile, backendType, planFileName, planAction));

    switch (backendType) {
      case "s3":
        if (planAction == "upload") {
          String sse =  "AES:256";
          LOGGER.info(sse);
          String kmsKeyId = "jdkaf;afn;dannfda;n;d";
          LOGGER.info(kmsKeyId);
          LOGGER.info("running command for terraform upload");
          response = terraformS3UploadCli(planFileName, planOutputFile, sse, kmsKeyId, false);
        } else if (planAction == "download") {
          String bucketName = properties.getProperty("planOutputFile").isEmpty() ? "" : planOutputFile.split("/")[2];
          response = terraformS3GetCli(bucketName, planOutputFile.replaceAll("s3://" + bucketName + "/", ""), planFileName);
        }
        break;
      case "azurerm":
        break;
      default:
        response = "Plan File operations support is not available yet";
    }
    return response;
  }


  /**
   * Uploads objects to s3.
   * @param body body of the content that need to be uploaded
   * @param key fully qualified path for the s3 object to store or destination location of the file
   * @param sse Server side encryption mechanism either of AES:256 or aws:kms; defaults to AES:256
   * @param kmsKeyId KmsKeyId to encrypt the Objects.
   * @param recursive extends s3 cp command with recursive--true if sets to true
   *
   * @return
   */
  protected String terraformS3UploadCli(String body, String key, String sse, String kmsKeyId, boolean recursive ) throws IOException {
    LOGGER.info("started s3 upload");
    try {
      String recCmd = recursive ? "--recursive" : "";
      if (sse.equals("AES:256")) {
        LOGGER.debug("uploading plan files to Amazon S3 with default encryption (SSE:256)");
        executable.execute(String.format("aws s3 cp %1$s %2$s %3$s", body, key, recCmd));

      } else {
        LOGGER.debug("uploading plan files to Amazon S3 with kms encryption");
        executable.execute(String.format("aws s3 cp %1$s %2$s --sse %3$s --sse-kms-key-id %4$s %5$s", body, key, sse, kmsKeyId, recCmd));
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return "success";
  }

  /**
   * Uploads objects to s3.
   * @param bucketName Name of the bucket
   * @param key fully qualified path for the s3 object to store or destination location of the file
   * @param outputFile fileName used to store the s3 key contents in destination location
   *
   * @return
   */
  protected String terraformS3GetCli(String bucketName, String key, String outputFile) throws IOException {
    try {
      LOGGER.debug("Object from s3://" + bucketName + key + " stored as " + outputFile);
      executable.execute(String.format("aws s3api get-object --bucket %1$s --key %2$s %3$s", bucketName, key, outputFile));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return bucketName;
  }

}
