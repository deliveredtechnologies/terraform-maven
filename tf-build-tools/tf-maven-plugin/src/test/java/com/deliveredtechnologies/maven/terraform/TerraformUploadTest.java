package com.deliveredtechnologies.maven.terraform;

import com.deliveredtechnologies.maven.terraform.TerraformUpload.TerraformUploadParams;
import com.deliveredtechnologies.io.Executable;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.util.Properties;

public class TerraformUploadTest {

//  private MavenProject project;
  private Log log;
  private Properties properties;
//  private TerraformUpload terraformUpload;
//  private Invoker invoker;
//  private InvocationRequest request;


  @Before
  public void setup() {
//    this.project = new MavenProject();
    this.log = Mockito.mock(Log.class);
    this.properties = Mockito.spy(Properties.class);
//    this.terraformUpload = new TerraformUpload(properties);
  }

  @Test
  public void terraformUploadCliUploadsToS3WithKmsEncryption() throws IOException, InterruptedException {
    String planOutputFile = "test.json";
    String sse  = "sse";
    String kmsKeyId = "1234";

    properties.put(TerraformUploadParams.kmsKeyId.toString(),kmsKeyId);
    properties.put(TerraformUploadParams.sse.toString(),sse);
    properties.put(TerraformUploadParams.planOutputFile.toString(),planOutputFile);

    Executable executable = Mockito.mock(Executable.class);
    Logger logger = Mockito.mock(Logger.class);

    TerraformUpload tfUpload = new TerraformUpload(executable, logger);

    tfUpload.terraformUploadCli("body", "s3://key", "sse", "kmsId", false);

    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp body s3://key --sse sse --sse-kms-key-id kmsId ");

  }

  @Test
  public void terraformUploadCliUploadsToS3WithNoEncryption() throws IOException, InterruptedException {

    String planOutputFile = "test.json";
    String sse  = "AES:256";
    String kmsKeyId = "1234";

    properties.put(TerraformUploadParams.kmsKeyId.toString(),kmsKeyId);
    properties.put(TerraformUploadParams.sse.toString(),sse);
    properties.put(TerraformUploadParams.planOutputFile.toString(),planOutputFile);

    Executable executable = Mockito.mock(Executable.class);
    Logger logger = Mockito.mock(Logger.class);

    TerraformUpload tfupload = new TerraformUpload(executable, logger);

    tfupload.terraformUploadCli("body", "s3://key", "AES:256", "kmsId", false);

    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp body s3://key ");

  }
}
