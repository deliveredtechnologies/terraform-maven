package com.deliveredtechnologies.maven.terraform;

import com.deliveredtechnologies.io.Executable;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.IOException;

public class TerraformUploadTest {

  @Test
  public void terraformUploadCliUploadsToS3WithKmsEncryption() throws IOException, InterruptedException {
    Executable executable = Mockito.mock(Executable.class);
    Logger logger = Mockito.mock(Logger.class);

    TerraformUpload tfUpload = new TerraformUpload(executable, logger);

    tfUpload.terraformUploadCli("body", "s3://key", "sse", "kmsId", false);

    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp body s3://key --sse sse --sse-kms-key-id kmsId ");

  }

  @Test
  public void terraformUploadCliUploadsToS3WithNoEncryption() throws IOException, InterruptedException {
    Executable executable = Mockito.mock(Executable.class);
    Logger logger = Mockito.mock(Logger.class);

    TerraformUpload tfupload = new TerraformUpload(executable, logger);

    tfupload.terraformUploadCli("body", "s3://key", "AES:256", "kmsId", false);

    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp body s3://key ");

  }
}
