package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
      "terraform destroy -lock-timeout=1000 -target=module1.module2 -var 'var1=one' -var 'var2=two' -var-file=test1.txt -var-file=test2.txt -no-color -refresh=true -auto-approve ",
      1111))
      .thenReturn("Success!");
    TerraformDestroy terraformDestroy = new TerraformDestroy(terraformDecorator);

    this.properties.put(TerraformDestroy.TerraformDestroyParam.lockTimeout.property, "1000");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.target.property, "module1.module2");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.noColor.property, "true");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.timeout.property, "1111");
    this.properties.put(TerraformPlan.TerraformPlanParam.tfVarFiles.property, "test1.txt, test2.txt");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.tfVars.property, "var1=one,var2=two");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.refreshState.property, "true");

    Assert.assertEquals("Success!", terraformDestroy.execute(properties));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString(), Mockito.anyInt());
  }

  @Test
  public void terraformDestroyExecuteWhenRefreshStatePassedAsFalse() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.DESTROY, this.executable);
    Mockito.when(this.executable.execute("terraform destroy -refresh=false -auto-approve ", 1111)).thenReturn("Success!");
    TerraformDestroy terraformDestroy = new TerraformDestroy(terraformDecorator);

    this.properties.put(TerraformDestroy.TerraformDestroyParam.refreshState.property, "false");
    this.properties.put(TerraformDestroy.TerraformDestroyParam.timeout.property, "1111");

    Assert.assertEquals("Success!", terraformDestroy.execute(properties));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString(), Mockito.anyInt());
  }

  @Test
  public void terraformDestroyExecutesWhenTFvarsIsMap() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.DESTROY, this.executable);
    Mockito.when(this.executable.execute("terraform destroy -var 'key1=value1' -var 'key2=[\"value2\",\"value3\"]' -auto-approve ")).thenReturn("Success!");
    TerraformDestroy terraformDestroy = new TerraformDestroy(terraformDecorator);

    Map tfvars = new HashMap();
    tfvars.put("key1", "value1");
    tfvars.put("key2", Arrays.asList("value2", "value3"));
    this.properties.put(TerraformApply.TerraformApplyParam.tfVars.property, tfvars);
    Assert.assertEquals("Success!", terraformDestroy.execute(this.properties));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString());
  }


  @Test
  public void terraformDestroyExecutesWhenNoPropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.DESTROY, this.executable);
    Mockito.when(this.executable.execute("terraform destroy -auto-approve ")).thenReturn("Success!");
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

  @Test
  public void terraformDestroyPassesLoggerToExecutable() {
    Executable executable = Mockito.mock(Executable.class);
    Logger logger = Mockito.mock(Logger.class);
    TerraformDestroy terraformDestroy = new TerraformDestroy(executable, logger);

    Mockito.verify(executable, Mockito.times(1)).setLogger(logger);
  }

  @After
  public void destroy() throws IOException {
    FileUtils.forceDelete(Paths.get("src", "main", "tf").toFile());
  }
}
