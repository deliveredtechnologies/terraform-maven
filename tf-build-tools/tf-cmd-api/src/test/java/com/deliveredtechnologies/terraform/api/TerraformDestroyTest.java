package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Properties;

/**
 * Tests for TerraformDestroy.
 */
public class TerraformDestroyTest {
  private Properties properties;
  private Executable executable;

  /**
   * Resets properties and mock Executable.
   */
  @Before
  public void setup() {
    properties = new Properties();
    executable = Mockito.mock(Executable.class);
  }

  @Test
  public void terraformDestroyExecutesWhenAllPossiblePropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.DESTROY, this.executable);
    Mockito.when(this.executable.execute(
      "terraform destroy -lock-timeout=1000 -target=module1.module2 -no-color -auto-approve /somedir",
      1111))
      .thenReturn("Success!");
    TerraformDestroy terraformDestroy = new TerraformDestroy(terraformDecorator);

    this.properties.put("lockTimeout", "1000");
    this.properties.put("target", "module1.module2");
    this.properties.put("autoApprove", "true");
    this.properties.put("noColor", "true");
    this.properties.put("timeout", "1111");
    this.properties.put("tfRootDir", "/somedir");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.lockTimeout.property, "1000");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.target.property, "module1.module2");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.noColor.property, "true");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.timeout.property, "1111");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.tfRootDir.property, "/somedir");


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
}
