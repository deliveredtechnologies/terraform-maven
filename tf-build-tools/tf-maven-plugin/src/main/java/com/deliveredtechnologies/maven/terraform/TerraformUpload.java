package com.deliveredtechnologies.maven.terraform;


import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import com.deliveredtechnologies.terraform.api.TerraformOperation;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


/**
 * upload plan files to s3 after tf:plan.
 */
public class TerraformUpload implements TerraformOperation<String> {

  private String tfRootDir;
  private Logger logger;

  enum TerraformUploadParams {
    planOutputFile,
    sse,
    kmsKeyId;
  }

  public TerraformUpload(String tfRootDir, Logger logger) {
    this.logger = logger;
    this.tfRootDir = tfRootDir;
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
    Path tfRootPath = tfRootDir == null  ? TerraformUtils.getDefaultTerraformRootModuleDir() : Paths.get(tfRootDir);
    logger.debug(String.format("tfRootPath is %1$s", tfRootPath.toAbsolutePath().toString()));
    String planOutputFile = properties.getProperty(TerraformUploadParams.planOutputFile.toString()); //TODO for destroyPlan
    String planFileName = planOutputFile.startsWith("s3") ? planOutputFile.split("/")[planOutputFile.split("/").length - 1] : properties.getProperty(TerraformUploadParams.planOutputFile.toString());
    String sse = properties.getProperty(TerraformUploadParams.sse.toString()) == null ? "AES:256" : properties.getProperty(TerraformUploadParams.sse.toString());
    String kmsKeyId = properties.getProperty(TerraformUploadParams.kmsKeyId.toString());
    terraformUploadCli(tfRootPath, planFileName, planOutputFile, sse, kmsKeyId, false);
    return planFileName;
  }

  /**
   * Uploads objects to s3.
   * @param tfRootPath path to the working directory
   * @param body body of the content that need to be uploaded
   * @param key fully qualified path for the s3 object to store or destination location of the file
   * @param sse Server side encryption mechanism either of AES:256 or aws:kms; defaults to AES:256
   * @param kmsKeyId KmsKeyId to encrypt the Objects.
   * @param recursive extends s3 cp command with recursive--true if sets to true
   *
   */
  public void terraformUploadCli(Path tfRootPath, String body, String key, String sse, String kmsKeyId, boolean recursive ) throws IOException {
    CommandLine cli = new CommandLine(tfRootPath, logger);
    try {
      String recCmd = recursive ? "--recursive" : "";
      if (key.startsWith("s3")) {
        if (sse.equals("AES:256")) {
          logger.debug("uploading plan files to Amazon S3 with default encryption (SSE:256)");
          cli.execute(String.format("aws s3 cp %1$s %2$s %3$s", body, key, recCmd));
        } else {
          logger.debug("uploading plan files to Amazon S3 with kms encryption");
          cli.execute(String.format("aws s3 cp %1$s %2$s --sse %3$s --sse-kms-key-id %4$s %5$s", body, key, sse, kmsKeyId, recCmd));
        }
      } else {
        logger.debug("pass -DplanOutputFile value as s3://<bucket_name>/<key_prefix>/<key_name>  to store plan files in s3");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
