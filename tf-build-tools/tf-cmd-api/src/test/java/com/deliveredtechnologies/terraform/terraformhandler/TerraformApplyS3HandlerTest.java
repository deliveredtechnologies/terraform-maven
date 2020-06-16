package com.deliveredtechnologies.terraform.terraformhandler;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformException;
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
  public void planOperationWithAllPropertiesSpecified() throws IOException, InterruptedException {
    TerraformHandler terraformHandler = new TerraformApplyS3Handler(executable,logger);

    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";
    properties.put("plan", s3BucketKey);

    terraformHandler.doAction(properties);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3api get-object --bucket terraform-maven-state --key planfiles/test.json test.json");
  }

  @Test
  public void planPropertyNotSpecified() throws IOException, InterruptedException {
    TerraformHandler terraformHandler = new TerraformApplyS3Handler(executable, logger);
    terraformHandler.nextHandlerAction(new TerraformPlanS3Handler(executable, logger));

    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";

    properties.put("plan", "create.json");
    properties.put("planOutputFile", s3BucketKey);
    terraformHandler.doAction(properties);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp test.json s3://terraform-maven-state/planfiles/test.json");

  }

  @Test
  public void planOperationWithtfRootDirTerraformApplyS3Handler() throws IOException, InterruptedException, TerraformException {


    TerraformHandler planFileUtils = new TerraformApplyS3Handler(tfRootDir, logger);

    String s3BucketKey = "S3://terraform-maven-state/planfiles/test.json";

    properties.put("plan", s3BucketKey);
    planFileUtils.doAction(properties);
  }

}
