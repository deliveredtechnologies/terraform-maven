package com.deliveredtechnologies.terraform;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.api.TerraformOperation;
import com.deliveredtechnologies.terraform.api.TerraformPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class TerraformPlanFileUtils {

  private TerraformPlanFileUtils() {}

  enum TerraformPlanFileUtilsParams {
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

  /**
   * GET/PUTs terraform plan files in specified backend.
   * <p>
   *   Valid Properties: <br>
   *   tfVars - a comma delimited list of terraform variables<br>
   *   varFiles - a comma delimited list of terraform vars files<br>
   *   lockTimeout - state file lock timeout<br>
   *   target - resource target<br>
   *   planInput - ask for input for variables not directly set<br>
   *   planOutputFile - path to save the generated execution plan<br>
   *   refreshState - if true then refresh the state prior to running plan<br>
   *   tfState - path to the state file; defaults to "terraform.tfstate"<br>
   *   noColor - remove color encoding from output<br>
   *   destroyPlan - if set then output a destroy plan<br>
   * </p>
   * @param properties  parameter options and properties for terraform apply
   * @return            the output of terraform apply
   * @throws TerraformException
   */

  public void runTerraformPlanFileUtils(Properties properties) {

    for (TerraformPlanFileUtilsParams backend : TerraformPlanFileUtilsParams.values()) {
      String planAction = properties.getProperty("planAction");
      String backendType = properties.getProperty("backendType");
      switch (backend) {
        case s3:
        case pg:
        case cos:
        case oss:
        case gcs:
        case etcd:
        case http:
        case manta:
        case swift:
        case consul:
        case etcdv3:
        case azurerm:
        case artifactory:
        case terraformEnterprise:
        default:
          executePlanFileAction(backendType, planAction, properties);
      }
    }
  }


  /**
   * Upload PlanFiles to s3 if -out argument starts with s3.
   * <p>
   *   Valid Properties:
   *   tfRootDir - the directory containing the Terraform root module configuration; defaults to src/main/tf/{first dir found}<br>
   *   planOutputFile - name or fully qualified s3 object name represents target location of plan files
   *   sse - Server Side Encryption, can be either of SSE:KMS or AES:256 - defaults to AES:256
   *   kmsKeyId - key id to encrypt s3 objects
   * </p>
   * @param properties  property options for Terraform plan
   * @return            planFileName
   * @throws TerraformException
   */

  void executePlanFileAction(String backEndType, String planAction, Properties properties) {
    String response = "Unable to Store planFiles in remote locations. The remote destination was not specified in the format";
    switch (backEndType) {
      case "s3":
        planAction == "upload" ? terraforms3Operations(properties);
    }

    if (properties.getProperty(planOutputFile.toString()) != null) {
      String planOutputFile = properties.getProperty(TerraformUpload.TerraformUploadParams.planOutputFile.toString()); //TODO for destroyPlan
      String planFileName = planOutputFile.startsWith("s3") ? planOutputFile.split("/")[planOutputFile.split("/").length - 1] : planOutputFile;
      LOGGER.debug("checking for plan file " + planFileName);
      String sse = properties.getProperty(TerraformUpload.TerraformUploadParams.sse.toString()) == null ? "AES:256" : properties.getProperty(TerraformUpload.TerraformUploadParams.sse.toString());
      String kmsKeyId = properties.getProperty(TerraformUpload.TerraformUploadParams.kmsKeyId.toString());
      if (planOutputFile.startsWith("s3")) {
        response = String.format("Plan saved to %1$s", planOutputFile);
        LOGGER.debug("File Uploading to S3");
        terraformUploadCli(planFileName, planOutputFile, sse, kmsKeyId, false);
      }
    }
    return response;
  }
  void uploadPlanFiles(String backEndType, String planAction, Properties properties) {
    String response = "Unable to perform S3 upload. The S3 destination was not specified in the format s3://<bucket-name>/<bucket-key>";
    if (properties.getProperty(planOutputFile.toString()) != null) {
      String planOutputFile = properties.getProperty(TerraformUpload.TerraformUploadParams.planOutputFile.toString()); //TODO for destroyPlan
      String planFileName = planOutputFile.startsWith("s3") ? planOutputFile.split("/")[planOutputFile.split("/").length - 1] : planOutputFile;
      LOGGER.debug("checking for plan file " + planFileName);
      String sse = properties.getProperty(TerraformUpload.TerraformUploadParams.sse.toString()) == null ? "AES:256" : properties.getProperty(TerraformUpload.TerraformUploadParams.sse.toString());
      String kmsKeyId = properties.getProperty(TerraformUpload.TerraformUploadParams.kmsKeyId.toString());
      if (planOutputFile.startsWith("s3")) {
        response = String.format("Plan saved to %1$s", planOutputFile);
        LOGGER.debug("File Uploading to S3");
        terraformUploadCli(planFileName, planOutputFile, sse, kmsKeyId, false);
      }
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
   */
  protected void terraform3UploadCli(String body, String key, String sse, String kmsKeyId, boolean recursive ) throws IOException {
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
  }

  /**
   * Uploads objects to s3.
   * @param bucketName Name of the bucket
   * @param key fully qualified path for the s3 object to store or destination location of the file
   * @param outputFile fileName used to store the s3 key contents in destination location
   *
   */
  protected void terraforms3GetCli(String bucketName, String key, String outputFile) throws IOException {
    try {
      LOGGER.debug("Object from s3://" + bucketName + key + " stored as " + outputFile);
      executable.execute(String.format("aws s3api get-object --bucket %1$s --key %2$s %3$s", bucketName, key, outputFile));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Uploads objects to s3.
   * @param body body of the content that need to be uploaded
   * @param key fully qualified path for the s3 object to store or destination location of the file
   * @param sse Server side encryption mechanism either of AES:256 or aws:kms; defaults to AES:256
   * @param kmsKeyId KmsKeyId to encrypt the Objects.
   * @param recursive extends s3 cp command with recursive--true if sets to true
   *
   */
  protected void terraformUploadCli(String body, String key, String sse, String kmsKeyId, boolean recursive ) throws IOException {
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
  }


}



}
