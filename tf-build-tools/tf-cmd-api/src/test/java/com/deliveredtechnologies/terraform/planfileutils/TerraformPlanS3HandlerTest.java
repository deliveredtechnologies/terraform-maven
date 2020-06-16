package com.deliveredtechnologies.terraform.planfileutils;

import com.deliveredtechnologies.io.Executable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class TerraformPlanS3HandlerTest {

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
  public void planOperationWithAllPropertiesSpecified() throws IOException, InterruptedException {
    TerraformHandler planFileUtils = new TerraformPlanS3Handler(executable, logger);

    String kmsKeyId = "4d6f7e4-b816-42f5-87b2-c5952285e53c";
    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";
    properties.put("kmsKeyId", kmsKeyId);
    properties.put("planOutputFile", s3BucketKey);

    planFileUtils.doAction(properties);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp test.json s3://terraform-maven-state/planfiles/test.json --sse aws:kms --sse-kms-key-id 4d6f7e4-b816-42f5-87b2-c5952285e53c");
  }

  @Test
  public void planOperationWithAllPropertiesSpecifiedWithoutKmsKeyId() throws IOException, InterruptedException {
    TerraformHandler planFileUtils = new TerraformPlanS3Handler(executable, logger);

    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";

    properties.put("planOutputFile", s3BucketKey);
    planFileUtils.doAction(properties);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp test.json s3://terraform-maven-state/planfiles/test.json");

  }

  @Test
  public void expectNextHandlerOperation() throws IOException, InterruptedException {
    TerraformHandler planFileUtils = new TerraformPlanS3Handler(executable, logger);
    planFileUtils.nextHandlerAction(new TerraformApplyS3Handler(executable, logger));

    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";

    properties.put("planOutputFile", "create.json");
    properties.put("plan", s3BucketKey);
    planFileUtils.doAction(properties);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3api get-object --bucket terraform-maven-state --key planfiles/test.json test.json");
  }
}
