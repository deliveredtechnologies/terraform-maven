package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformInit.TerraformInitParam;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Tests for TerraformInit.
 */
public class TerraformInitTest {

  /**
   * Sets up the default terraform directory.
   * @throws IOException
   */
  @Before
  public void setup() throws IOException {
    FileUtils.copyDirectory(
        Paths.get("src", "test", "resources", "tf_initialized", "root").toFile(),
        Paths.get("src", "main", "tf", "test").toFile()
    );
  }

  @Test
  public void executeCallsTerraformInitCommandWithNoOptions() throws IOException, InterruptedException, TerraformException {
    String successMessage = "terraform init success!";
    Executable commandLine = Mockito.mock(Executable.class);
    Executable terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.INIT, commandLine);
    Mockito.when(commandLine.execute(Mockito.anyString())).thenReturn(successMessage);
    TerraformOperation<String> terraformInit = new TerraformInit(terraformDecorator);

    String response = terraformInit.execute(new Properties());

    Assert.assertEquals(successMessage, response);
    Mockito.verify(commandLine, Mockito.times(1)).execute("terraform init -no-color ");
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
    properties.put(TerraformInitParam.getPlugins.property, "false");
    properties.put(TerraformInitParam.pluginDir.property, pluginDir);
    properties.put(TerraformInitParam.verifyPlugins.property, "false");
    properties.put(TerraformInitParam.backendConfig.property, "bucket=mybucket,key=/path/to/my/key,region=us-east-1");
    String response = terraformInit.execute(properties);

    Assert.assertEquals(successMessage, response);
    Mockito.verify(commandLine, Mockito.times(1)).execute(String.format("-plugin-dir=somepluginpath -verify-plugins=false -get-plugins=false -backend-config=\"bucket=mybucket\" -backend-config=\"key=/path/to/my/key\" -backend-config=\"region=us-east-1\" -no-color ", pluginDir));
  }

  @Test(expected = TerraformException.class)
  public void executeThrowsTerraformExceptionOnError() throws IOException, InterruptedException, TerraformException {
    String successMessage = "terraform init success!";
    Executable tfExecutable = Mockito.mock(Executable.class);
    Mockito.when(tfExecutable.execute(Mockito.anyString())).thenThrow(new IOException("boom!"));
    TerraformOperation<String> terraformInit = new TerraformInit(tfExecutable);

    terraformInit.execute(new Properties());
  }

  @After
  public void destroy() throws IOException {
    FileUtils.forceDelete(Paths.get("src", "main", "tf").toFile());
  }
}
