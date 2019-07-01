package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Properties;

/**
 * Tests for {@link TerraformOutput}.
 */
public class TerraformOutputTest {

  private final String successResponse = "Success!";
  private Executable commandLine;
  private Properties properties;
  private Executable terraform;

  @Before
  public void setup() throws IOException, InterruptedException {
    commandLine = Mockito.mock(Executable.class);
    properties = new Properties();
    terraform = new TerraformCommandLineDecorator(TerraformCommand.OUTPUT, commandLine);

  }

  @Test
  public void terraformOutputCallsOutputCommand() throws InterruptedException, IOException, TerraformException {
    Mockito.when(commandLine.execute("terraform output -json")).thenReturn(successResponse);

    TerraformOutput terraformOutput = new TerraformOutput(terraform);
    Assert.assertEquals(successResponse, terraformOutput.execute(properties));
    Mockito.verify(commandLine, Mockito.times(1)).execute(Mockito.anyString());
  }

  @Test
  public void terraformOutputCallsOutputCommandWithTimeout() throws InterruptedException, IOException, TerraformException {
    String timeout = "1000";
    properties.put("timeout", timeout);

    Mockito.when(commandLine.execute("terraform output -json", Integer.parseInt(timeout))).thenReturn(successResponse);

    TerraformOutput terraformOutput = new TerraformOutput(terraform);
    Assert.assertEquals(successResponse, terraformOutput.execute(properties));
    Mockito.verify(commandLine, Mockito.times(1)).execute(Mockito.anyString(), Mockito.anyInt());
  }
}
