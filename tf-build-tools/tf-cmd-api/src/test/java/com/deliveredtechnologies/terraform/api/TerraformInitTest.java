package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import com.deliveredtechnologies.terraform.api.TerraformInit.TerraformInitParam;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Tests for TerraformInit.
 */
public class TerraformInitTest {

  @Test
  public void executeCallsTerraformInitCommandWithNoOptions() throws IOException, InterruptedException, TerraformException {
    String successMessage = "terraform init success!";
    Executable commandLine = Mockito.mock(Executable.class);
    Executable terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.INIT, commandLine);
    Mockito.when(commandLine.execute(Mockito.anyString())).thenReturn(successMessage);
    TerraformOperation<String> terraformInit = new TerraformInit(terraformDecorator);

    String response = terraformInit.execute(new Properties());

    Assert.assertEquals(successMessage, response);
    Mockito.verify(commandLine, Mockito.times(1)).execute("terraform init -no-color " + TerraformUtils.getDefaultTerraformRootModuleDir().toAbsolutePath().toString());

    //and now with a tfRootDir specified
    String tfRootDir = Paths.get("src", "test", "resources", "tf_initialized", "root").toAbsolutePath().toString();
    Properties properties = new Properties();
    properties.put(TerraformInitParam.tfRootDir.toString(), tfRootDir);
    response = terraformInit.execute(properties);

    Assert.assertEquals(successMessage, response);
    Mockito.verify(commandLine, Mockito.times(1)).execute(String.format("terraform init -no-color %1$s", TerraformUtils.getTerraformRootModuleDir(tfRootDir)));
  }

  @Test
  public void executeCallsTerraformInitCommandWithAllOptions() throws IOException, InterruptedException, TerraformException {
    String successMessage = "terraform init success!";
    Executable commandLine = Mockito.mock(Executable.class);
    Executable terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.INIT, commandLine);
    Mockito.when(commandLine.execute(Mockito.anyString())).thenReturn(successMessage);
    TerraformOperation<String> terraformInit = new TerraformInit(terraformDecorator);

    String tfRootDir = "test";
    String pluginDir = "somepluginpath";
    Properties properties = new Properties();
    properties.put(TerraformInitParam.tfRootDir.property, tfRootDir);
    properties.put(TerraformInitParam.getPlugins.property, "false");
    properties.put(TerraformInitParam.pluginDir.property, pluginDir);
    properties.put(TerraformInitParam.verifyPlugins.property, "false");
    String response = terraformInit.execute(properties);

    Assert.assertEquals(successMessage, response);
    Mockito.verify(commandLine, Mockito.times(1)).execute(String.format("terraform init -plugin-dir=%1$s -verify-plugins=false -get-plugins=false -no-color %2$s", pluginDir, Paths.get("src", "main", "tf", tfRootDir).toAbsolutePath().toString()));
  }

  @Test(expected = TerraformException.class)
  public void executeThrowsTerraformExceptionOnError() throws IOException, InterruptedException, TerraformException {
    String successMessage = "terraform init success!";
    Executable tfExecutable = Mockito.mock(Executable.class);
    Mockito.when(tfExecutable.execute(Mockito.anyString())).thenThrow(new IOException("boom!"));
    TerraformOperation<String> terraformInit = new TerraformInit(tfExecutable);

    terraformInit.execute(new Properties());
  }
}
