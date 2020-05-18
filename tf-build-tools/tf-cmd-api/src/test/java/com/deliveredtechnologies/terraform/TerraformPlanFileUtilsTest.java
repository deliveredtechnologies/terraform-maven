package com.deliveredtechnologies.terraform;

import com.deliveredtechnologies.io.Executable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class TerraformPlanFileUtilsTest {

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
  public void backendS3operationsWhenAllPropertiesSpecified() throws IOException, InterruptedException {
    TerraformPlanFileUtils planFileUtils = new TerraformPlanFileUtils(executable, logger);

    String kmsKeyId = "4d6f7e4-b816-42f5-87b2-c5952285e53c";
    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";
    properties.put("kmsKeyId", kmsKeyId);
    properties.put("planOutputFile", s3BucketKey);

    planFileUtils.executePlanFileOperation(properties);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp test.json s3://terraform-maven-state/planfiles/test.json --sse aws:kms --sse-kms-key-id 4d6f7e4-b816-42f5-87b2-c5952285e53c");
  }

  @Test
  public void backendS3operationsWhenBackendTypeNotSpecified() throws IOException {
    TerraformPlanFileUtils planFileUtils = new TerraformPlanFileUtils(executable, logger);

    String kmsKeyId = "4d6f7e4-b816-42f5-87b2-c5952285e53c";
    String planFileName = "test.json";
    properties.put("kmsKeyId", kmsKeyId);
    properties.put("planOutputFile", planFileName);

    Assert.assertNull(planFileUtils.executePlanFileOperation(properties));
  }

  @Test
  public void backendS3operationsWhenPlanPropertySpecified() throws IOException {
    TerraformPlanFileUtils planFileUtils = new TerraformPlanFileUtils(executable, logger);

    String planFileName = "test.json";

    properties.put("plan", planFileName);

    Assert.assertNull(planFileUtils.executePlanFileOperation(properties));
  }


  @Test
  public void backendS3operationsWhenGetActionSpecified() throws IOException, InterruptedException {
    TerraformPlanFileUtils planFileUtils = new TerraformPlanFileUtils(executable, logger);

    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";
    properties.put("planOutputFile", s3BucketKey);
    planFileUtils.backendS3operations("GET", s3BucketKey, properties);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3api get-object --bucket terraform-maven-state --key test.json test.json");
  }

  @Test
  public void backendS3operationsWhenPutActionSpecified() throws IOException, InterruptedException {
    TerraformPlanFileUtils planFileUtils = new TerraformPlanFileUtils(executable, logger);

    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";
    properties.put("planOutputFile", s3BucketKey);
    planFileUtils.backendS3operations("PUT", s3BucketKey, properties);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp test.json s3://terraform-maven-state/planfiles/test.json");
  }


  @Test
  public void backendS3operationsWhenPutActionWithKmsKeySpecified() throws IOException, InterruptedException {
    TerraformPlanFileUtils planFileUtils = new TerraformPlanFileUtils(executable, logger);
    String kmsKeyId = "4d6f7e4-b816-42f5-87b2-c5952285e53c";
    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";
    properties.put("kmsKeyId", kmsKeyId);
    properties.put("planOutputFile", s3BucketKey);
    planFileUtils.backendS3operations("PUT", s3BucketKey, properties);
    Mockito.verify(executable, Mockito.times(1)).execute("aws s3 cp test.json s3://terraform-maven-state/planfiles/test.json --sse aws:kms --sse-kms-key-id 4d6f7e4-b816-42f5-87b2-c5952285e53c");
  }

  @Test(expected = MissingMethodInvocationException.class)
  public void backendS3operationsExpectsInterruptedException() throws IOException, InterruptedException {

    TerraformPlanFileUtils planFileUtils = new TerraformPlanFileUtils(executable, logger);
    String s3BucketKey = "s3://terraform-maven-state/planfiles/test.json";

    properties.put("planOutputFile", s3BucketKey);
    Mockito.when(planFileUtils.backendS3operations("upload",s3BucketKey,properties)).thenThrow(InterruptedException.class);

  }

}


