package com.deliveredtechnologies.maven.terraform;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.maven.terraform.TerraformUpload.TerraformUploadParams;
import com.deliveredtechnologies.terraform.TerraformException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class TerraformUploadTest {

  private Properties properties;
  private Executable executable;
  private Logger logger;

  /**
   * Sets up the properties, mock(s) and the default terraform directory.
   */
  @Before
  public void setup() {
    properties = new Properties();
    executable = Mockito.mock(Executable.class);
    logger = Mockito.mock(Logger.class);
  }

  @Test
  public void terraformUploadCliUploadsToS3WithKmsEncryption() throws IOException, InterruptedException {
    TerraformUpload terraformUpload = new TerraformUpload(executable, logger);
    String planOutputFile = "test.json";
    String sse  = "aws:kms";
    String kmsKeyId = "4d6f7e4-b816-42f5-87b2-c5952285e53c";
    String s3BucketKey = "s3://terraform-maven-state/planfiles";
    terraformUpload.terraformUploadCli(planOutputFile, s3BucketKey, sse, kmsKeyId, false);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp test.json s3://terraform-maven-state/planfiles --sse aws:kms --sse-kms-key-id 4d6f7e4-b816-42f5-87b2-c5952285e53c ");
  }

  @Test
  public void terraformUploadCliUploadsToS3WithNoEncryption() throws IOException, InterruptedException {
    TerraformUpload terraformUpload = new TerraformUpload(executable, logger);
    String planOutputFile = "test.json";
    String sse  = "AES:256";
    String s3BucketKey = "s3://terraform-maven-state/planfiles";
    terraformUpload.terraformUploadCli(planOutputFile, s3BucketKey, sse, null, false);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp test.json s3://terraform-maven-state/planfiles ");
  }

  @Test
  public void terraformUploadCliUploadsToS3ReturnsPlanFileName() throws IOException, TerraformException {
    TerraformUpload terraformUpload = new TerraformUpload(executable, logger);
    String planOutputFile = "s3://terraform-maven-state/planfiles/test.json";
    String sse  = "aws:kms";
    String kmsKeyId = "4d6f7e4-b816-42f5-87b2-c5952285e53c";
    String planFileName = "test.json";
    properties.put(TerraformUploadParams.kmsKeyId.toString(),kmsKeyId);
    properties.put(TerraformUploadParams.sse.toString(),sse);
    properties.put(TerraformUploadParams.planOutputFile.toString(),planOutputFile);
    Assert.assertEquals(planFileName, terraformUpload.execute(properties));
    Assert.assertEquals(3, properties.size());
  }
}
