package com.deliveredtechnologies.terraform.terraformhandler;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.handler.TerraformChainHandler;
import com.deliveredtechnologies.terraform.handler.TerraformHandlerException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class TerraformChainHandlerTest {

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
  public void operationWithAllPropertiesSpecified() throws IOException, TerraformException, TerraformHandlerException {
    TerraformChainHandler terraformChainHandler = new TerraformChainHandler("src/test/resources/tf_initialized/root", logger);
    String kmsKeyId = "4d6f7e4-b816-42f5-87b2-c5952285e53c";
    String s3BucketKey = "test.json";
    properties.put("kmsKeyId", kmsKeyId);
    properties.put("planOutputFile", s3BucketKey);
    terraformChainHandler.initiateChain(properties);
  }

  @Test(expected = TerraformHandlerException.class)
  public void terraformChainHandlerThrowsTerraformHandlerExceptionOnError() throws IOException, InterruptedException, TerraformException, TerraformHandlerException {
    Mockito.when(this.executable.execute(Mockito.anyString())).thenThrow(new IOException("boom!"));
    TerraformChainHandler terraformChainHandler = new TerraformChainHandler(tfRootDir, logger);
    terraformChainHandler.initiateChain(properties);
  }
}
