package com.deliveredtechnologies.maven.terraform;

import com.deliveredtechnologies.io.Executable;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.IOException;

public class TerraformUploadTest {

  @Test
  public void terraformUploadCliUploadsToS3withKmsEncryptionAndNoReplication() throws IOException, InterruptedException {
    Executable executable = Mockito.mock(Executable.class);
    Logger logger = Mockito.mock(Logger.class);

    TerraformUpload tfUpload = new TerraformUpload(executable, logger);

    tfUpload.terraformUploadCli("body", "s3://key", "sse", "kmsId", false);

    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp body s3://key --sse sse --sse-kms-key-id kmsId ");

  }
}
