package com.deliveredtechnologies.maven.terraform;


import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import com.deliveredtechnologies.terraform.api.TerraformOperation;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;


/**
 * upload plan files to s3 after tf:plan.
 */
public class TerraformUpload implements TerraformOperation<String> {
  private Executable executable;

  private Logger logger;

  enum TerraformUploadParams {
    planOutputFile,
    sse,
    kmsKeyId;
  }

  public TerraformUpload(String tfRootDir, Logger logger) throws IOException {
    this(new CommandLine(tfRootDir == null  ? TerraformUtils.getDefaultTerraformRootModuleDir() : Paths.get(tfRootDir), logger), logger);
  }

  public TerraformUpload(Executable executable, Logger logger) {
    this.executable = executable;
    this.logger = logger;
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
  @Override
  public String execute(Properties properties) throws TerraformException, IOException {
    String response = null;
    if (properties.getProperty(TerraformUploadParams.planOutputFile.toString()) != null) {
      String planOutputFile = properties.getProperty(TerraformUploadParams.planOutputFile.toString()); //TODO for destroyPlan
      String planFileName = planOutputFile.startsWith("s3") ? planOutputFile.split("/")[planOutputFile.split("/").length - 1] : planOutputFile;
      logger.debug("checking for plan file " + planFileName);
      String sse = properties.getProperty(TerraformUploadParams.sse.toString()) == null ? "AES:256" : properties.getProperty(TerraformUploadParams.sse.toString());
      String kmsKeyId = properties.getProperty(TerraformUploadParams.kmsKeyId.toString());
      if (planOutputFile.startsWith("s3")) {
        response = String.format("Plan saved to %1$s", planOutputFile);
        logger.debug("File Uploading to S3");
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
  protected void terraformUploadCli(String body, String key, String sse, String kmsKeyId, boolean recursive ) throws IOException {
    try {
      String recCmd = recursive ? "--recursive" : "";
      if (sse.equals("AES:256")) {
        logger.debug("uploading plan files to Amazon S3 with default encryption (SSE:256)");
        executable.execute(String.format("aws s3 cp %1$s %2$s %3$s", body, key, recCmd));

      } else {
        logger.debug("uploading plan files to Amazon S3 with kms encryption");
        executable.execute(String.format("aws s3 cp %1$s %2$s --sse %3$s --sse-kms-key-id %4$s %5$s", body, key, sse, kmsKeyId, recCmd));
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
