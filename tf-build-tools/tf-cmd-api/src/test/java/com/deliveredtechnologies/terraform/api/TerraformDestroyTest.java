package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
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
 * Tests for TerraformDestroy.
 */
public class TerraformDestroyTest {
  private Properties properties;
  private Executable executable;
  private String tfRootModule = "test";

  /**
   * Resets properties and mock Executable.
   */
  @Before
  public void setup() throws IOException {
    FileUtils.copyDirectory(
      Paths.get("src", "test", "resources", "tf_initialized", "root").toFile(),
      Paths.get("src", "main", "tf", tfRootModule).toFile()
    );

    properties = new Properties();
    executable = Mockito.mock(Executable.class);
  }

  @Test
  public void terraformDestroyExecutesWhenAllPossiblePropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.DESTROY, this.executable);
    Mockito.when(this.executable.execute(
      String.format("terraform destroy -lock-timeout=1000 -target=module1.module2 -no-color -auto-approve %1$s", TerraformUtils.getTerraformRootModuleDir(tfRootModule).toAbsolutePath().toString()),
      1111))
      .thenReturn("Success!");
    TerraformDestroy terraformDestroy = new TerraformDestroy(terraformDecorator);

    this.properties.put(TerraformDestroy.TerraformDestroyParam.lockTimeout.property, "1000");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.target.property, "module1.module2");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.noColor.property, "true");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.timeout.property, "1111");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.tfRootDir.property, tfRootModule);

    Assert.assertEquals("Success!", terraformDestroy.execute(properties));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString(), Mockito.anyInt());
  }

  @Test
  public void terraformDestroyExecutesWhenNoPropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.DESTROY, this.executable);
    Mockito.when(this.executable.execute(String.format("terraform destroy -auto-approve %1$s", TerraformUtils.getDefaultTerraformRootModuleDir().toAbsolutePath()))).thenReturn("Success!");
    TerraformDestroy terraformDestroy = new TerraformDestroy(terraformDecorator);

    Assert.assertEquals("Success!", terraformDestroy.execute(new Properties()));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString());
  }

  @Test(expected = TerraformException.class)
  public void terraformDestroyThrowsTerraformExceptionOnError() throws IOException, InterruptedException, TerraformException {
    Mockito.when(this.executable.execute(Mockito.anyString())).thenThrow(new IOException("boom!"));
    TerraformDestroy terraformDestroy = new TerraformDestroy(this.executable);
    terraformDestroy.execute(properties);
  }

  @After
  public void destroy() throws IOException {
    FileUtils.forceDelete(Paths.get("src", "main", "tf").toFile());
  }
}
