package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.io.Executable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Properties;

/**
 * Tests for TerraformInit.
 */
public class TerraformInitTest {

  @Test
  public void executeCallsTerraformInitCommand() throws IOException, InterruptedException, TerraformException {
    String successMessage = "tf init success!";
    Executable commandLine = Mockito.mock(Executable.class);
    Executable terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.INIT, commandLine);
    Mockito.when(commandLine.execute(Mockito.anyString())).thenReturn(successMessage);
    TerraformOperation<String> terraformInit = new TerraformInit(terraformDecorator);

    String response = terraformInit.execute(new Properties());

    Assert.assertEquals(successMessage, response);
    Mockito.verify(commandLine, Mockito.times(1)).execute("terraform init -no-color ");

    //and now with a tfRootDir specified
    String tfRootDir = "some path";
    Properties properties = new Properties();
    properties.put("tf/tfRootDir", tfRootDir);
    response = terraformInit.execute(properties);

    Assert.assertEquals(successMessage, response);
    Mockito.verify(commandLine, Mockito.times(1)).execute(String.format("terraform init -no-color %1$s", tfRootDir));
  }

  @Test(expected = TerraformException.class)
  public void executeThrowsTerraformExceptionOnError() throws IOException, InterruptedException, TerraformException {
    String successMessage = "tf init success!";
    Executable tfExecutable = Mockito.mock(Executable.class);
    Mockito.when(tfExecutable.execute(Mockito.anyString())).thenThrow(new IOException("boom!"));
    TerraformOperation<String> terraformInit = new TerraformInit(tfExecutable);

    terraformInit.execute(new Properties());

  }
}
