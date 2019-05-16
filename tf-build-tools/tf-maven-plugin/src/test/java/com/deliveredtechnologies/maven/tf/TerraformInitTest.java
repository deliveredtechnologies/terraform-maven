package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.io.Executable;
import com.deliveredtechnologies.maven.tf.TerraformInit.TerraformInitParam;

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
  public void executeCallsTerraformInitCommandWithNoOptions() throws IOException, InterruptedException, TerraformException {
    String successMessage = "tf init success!";
    Executable commandLine = Mockito.mock(Executable.class);
    Executable terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.INIT, commandLine);
    Mockito.when(commandLine.execute(Mockito.anyString())).thenReturn(successMessage);
    TerraformOperation<String> terraformInit = new TerraformInit(terraformDecorator);

    String response = terraformInit.execute(new Properties());

    Assert.assertEquals(successMessage, response);
    Mockito.verify(commandLine, Mockito.times(1)).execute("terraform init -no-color " + TerraformUtils.getDefaultTerraformRootModuleDir().toAbsolutePath().toString());

    //and now with a tfRootDir specified
    String tfRootDir = "somepath";
    Properties properties = new Properties();
    properties.put(TerraformInitParam.tfRootDir.toString(), tfRootDir);
    response = terraformInit.execute(properties);

    Assert.assertEquals(successMessage, response);
    Mockito.verify(commandLine, Mockito.times(1)).execute(String.format("terraform init -no-color %1$s", tfRootDir));
  }

  @Test
  public void executeCallsTerraformInitCommandWithAllOptions() throws IOException, InterruptedException, TerraformException {
    String successMessage = "tf init success!";
    Executable commandLine = Mockito.mock(Executable.class);
    Executable terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.INIT, commandLine);
    Mockito.when(commandLine.execute(Mockito.anyString())).thenReturn(successMessage);
    TerraformOperation<String> terraformInit = new TerraformInit(terraformDecorator);

    String tfRootDir = "somepath";
    String pluginDir = "somepluginpath";
    Properties properties = new Properties();
    properties.put(TerraformInitParam.tfRootDir.property, tfRootDir);
    properties.put(TerraformInitParam.getPlugins.property, "false");
    properties.put(TerraformInitParam.pluginDir.property, pluginDir);
    properties.put(TerraformInitParam.verifyPlugins.property, "false");
    String response = terraformInit.execute(properties);

    Assert.assertEquals(successMessage, response);
    Mockito.verify(commandLine, Mockito.times(1)).execute(String.format("terraform init -plugin-dir=%1$s -verify-plugins=false -get-plugins=false -no-color %2$s", pluginDir, tfRootDir));
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
