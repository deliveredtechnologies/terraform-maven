package com.deliveredtechnologies.terraform.terraformhandler;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.handler.TerraformHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

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
  }
}
