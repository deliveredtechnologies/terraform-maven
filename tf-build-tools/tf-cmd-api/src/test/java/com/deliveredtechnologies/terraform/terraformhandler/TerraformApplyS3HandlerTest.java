package com.deliveredtechnologies.terraform.terraformhandler;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.handler.TerraformApplyS3Handler;
import com.deliveredtechnologies.terraform.handler.TerraformChainHandler;
import com.deliveredtechnologies.terraform.handler.TerraformHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class TerraformApplyS3HandlerTest {

  private Properties properties;
  private Executable executable;
  private Logger logger;
  String tfRootDir;

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
  public void initiatingTheChainFromApply() throws IOException, TerraformException, InterruptedException, ClassNotFoundException {
    TerraformChainHandler terraformChainHandler = new TerraformChainHandler(tfRootDir,logger);
    terraformChainHandler.chainInitiator(properties);
    TerraformHandler terraformHandler = new TerraformApplyS3Handler(executable,logger);

    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";
    properties.put("plan", s3BucketKey);

    terraformHandler.doAction(properties);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3api get-object --bucket terraform-maven-state --key planfiles/test.json test.json");
  }

  @Test
  public void planOperationWithAllPropertiesSpecified() throws IOException, InterruptedException{
    TerraformHandler terraformHandler = new TerraformApplyS3Handler(executable,logger);

    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";
    properties.put("plan", s3BucketKey);

    terraformHandler.doAction(properties);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3api get-object --bucket terraform-maven-state --key planfiles/test.json test.json");
  }

  @Test
  public void planOperationWithTfRootDirTerraformApplyS3Handler() throws IOException, TerraformException {
    TerraformHandler terraformHandler = new TerraformApplyS3Handler(tfRootDir, logger);

    String s3BucketKey = "S3://terraform-maven-state/planfiles/test.json";
    properties.put("plan", s3BucketKey);

    terraformHandler.doAction(properties);
  }
}
