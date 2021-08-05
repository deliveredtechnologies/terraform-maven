package com.deliveredtechnologies.terraform.terraformhandler;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.handler.TerraformApplyS3Handler;
import com.deliveredtechnologies.terraform.handler.TerraformHandler;
import com.deliveredtechnologies.terraform.handler.TerraformPlanS3Handler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class TerraformHandlerTest {
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
  public void executeHandlerWithDoAction() {
    TerraformHandler handler = Mockito.mock(TerraformHandler.class, Mockito.CALLS_REAL_METHODS);
    String kmsKeyId = "4d6f7e4-b816-42f5-87b2-c5952285e53c";
    String s3BucketKey = "test.json";
    properties.put("kmsKeyId", kmsKeyId);
    properties.put("planOutputFile", s3BucketKey);
    handler.doAction(properties);
  }

  @Test
  public void executeHandlerWithHandleRequest() {
    TerraformHandler handler = Mockito.mock(TerraformHandler.class, Mockito.CALLS_REAL_METHODS);
    String kmsKeyId = "4d6f7e4-b816-42f5-87b2-c5952285e53c";
    String s3BucketKey = "test.json";
    properties.put("kmsKeyId", kmsKeyId);
    properties.put("planOutputFile", s3BucketKey);
    handler.handleRequest(properties);
    Mockito.verify(handler, Mockito.times(1)).doAction(properties);
  }

  @Test
  public void validatedoActionWithCurrentHandler() {
    String kmsKeyId = "4d6f7e4-b816-42f5-87b2-c5952285e53c";
    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";
    properties.put("kmsKeyId", kmsKeyId);
    properties.put("planOutputFile", s3BucketKey);
    TerraformPlanS3Handler handler = Mockito.mock(TerraformPlanS3Handler.class);
    handler.doAction(properties);
    Mockito.verify(handler, Mockito.times(1)).doAction(properties);
  }

  @Test
  public void validateNextHandler() throws IOException, TerraformException, InterruptedException {
    String kmsKeyId = "4d6f7e4-b816-42f5-87b2-c5952285e53c";
    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";
    String tfRootDir = "src/test/resources/tf_initialized/root";
    properties.put("kmsKeyId", kmsKeyId);
    properties.put("planOutputFile", s3BucketKey);
    TerraformPlanS3Handler handler = new TerraformPlanS3Handler(executable, logger);
    handler.setNextHandler(new TerraformApplyS3Handler(executable, logger));
    handler.doAction(properties);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp test.json s3://terraform-maven-state/planfiles/test.json --sse aws:kms --sse-kms-key-id 4d6f7e4-b816-42f5-87b2-c5952285e53c");
  }

  @Test
  public void validatedoActionWithNextHandler() {
    TerraformPlanS3Handler handler1 = Mockito.mock(TerraformPlanS3Handler.class);
    TerraformApplyS3Handler handler2 = Mockito.mock(TerraformApplyS3Handler.class);
    String kmsKeyId = "4d6f7e4-b816-42f5-87b2-c5952285e53c";
    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";
    properties.put("kmsKeyId", kmsKeyId);
    properties.put("planOutputFile", s3BucketKey);
    properties.put("plan", s3BucketKey);
    handler1.setNextHandler(handler2);
    handler1.handleRequest(properties);
    Mockito.verify(handler1, Mockito.times(1)).doAction(properties);
    Mockito.verify(handler1, Mockito.times(1)).setNextHandler(handler2);
  }
}
