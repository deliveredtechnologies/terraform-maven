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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Tests for TerraformPlan.
 */
public class TerraformPlanTest {
  private Properties properties;
  private Executable executable;

  /**
   * Sets up the properties, mock(s) and the default terraform directory.
   * @throws IOException
   */
  @Before
  public void setup() throws IOException {
    FileUtils.copyDirectory(
        Paths.get("src", "test", "resources", "tf_initialized", "root").toFile(),
        Paths.get("src", "main", "tf", "test").toFile()
    );
    properties = new Properties();
    executable = Mockito.mock(Executable.class);
  }

  @Test
  public void terraformPlanExecutesWhenAllPossiblePropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    Path tfRootDir = Paths.get("src", "test", "resources", "tf_initialized", "root").toAbsolutePath();
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.PLAN, this.executable);
    Mockito.when(this.executable.execute(
      "terraform plan -var 'key1=value1' -var 'key2=value2' -var-file=test1.txt -var-file=test2.txt -lock-timeout=1000 -target=module1.module2 -out=destroy.plan -input=true -refresh=true -state=my.tfstate -no-color -destroy ",
      1111))
      .thenReturn("Success!");
    TerraformPlan terraformPlan = new TerraformPlan(terraformDecorator);

    this.properties.put(TerraformPlan.TerraformPlanParam.tfVarFiles.property, "test1.txt, test2.txt");
    this.properties.put(TerraformPlan.TerraformPlanParam.tfVars.property, "key1=value1, key2=value2");
    this.properties.put(TerraformPlan.TerraformPlanParam.lockTimeout.property, "1000");
    this.properties.put(TerraformPlan.TerraformPlanParam.target.property, "module1.module2");
    this.properties.put(TerraformPlan.TerraformPlanParam.planOutputFile.property, "destroy.plan");
    this.properties.put(TerraformPlan.TerraformPlanParam.planInput.property, "true");
    this.properties.put(TerraformPlan.TerraformPlanParam.refreshState.property, "true");
    this.properties.put(TerraformPlan.TerraformPlanParam.destroyPlan.property, "true");
    this.properties.put(TerraformPlan.TerraformPlanParam.tfState.property, "my.tfstate");
    this.properties.put(TerraformPlan.TerraformPlanParam.noColor.property, "true");
    this.properties.put(TerraformPlan.TerraformPlanParam.timeout.property, "1111");

    Assert.assertEquals("Success!", terraformPlan.execute(properties));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString(), Mockito.anyInt());
  }

  @Test
  public void terraformPlanExecutesWhenPlanOutputFileReferencesS3Path() throws IOException, InterruptedException, TerraformException {
    Path tfRootDir = Paths.get("src", "test", "resources", "tf_initialized", "root").toAbsolutePath();
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.PLAN, this.executable);
    Mockito.when(this.executable.execute(
      "terraform plan -out=destroy.plan -input=false ")).thenReturn("Success!");
    TerraformPlan terraformPlan = new TerraformPlan(terraformDecorator);

    this.properties.put(TerraformPlan.TerraformPlanParam.planOutputFile.property, "s3://plan-files-bucket/planfiles/destroy.plan");

    Assert.assertEquals("Success!", terraformPlan.execute(properties));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString());
  }

  @Test
  public void terraformPlanExecuteWhenRefreshStatePassedAsFalse() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.PLAN, this.executable);
    Mockito.when(this.executable.execute("terraform plan -input=true -refresh=false ",1111)).thenReturn("Success!");
    TerraformPlan terraformPlan = new TerraformPlan(terraformDecorator);

    this.properties.put(TerraformPlan.TerraformPlanParam.planInput.property, "true");
    this.properties.put(TerraformPlan.TerraformPlanParam.refreshState.property, "false");
    this.properties.put(TerraformPlan.TerraformPlanParam.timeout.property, "1111");

    Assert.assertEquals("Success!", terraformPlan.execute(properties));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString(), Mockito.anyInt());
  }

  @Test
  public void terraformPlanExecutesWhenTFvarsIsMap() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.PLAN, this.executable);
    Mockito.when(this.executable.execute("terraform plan -var 'key1=value1' -var 'key2=[\"value2\",\"value3\"]' -input=false ")).thenReturn("Success!");
    TerraformPlan terraformPlan = new TerraformPlan(terraformDecorator);

    Map tfvars = new HashMap();
    tfvars.put("key1", "value1");
    tfvars.put("key2", Arrays.asList("value2", "value3"));
    this.properties.put(TerraformApply.TerraformApplyParam.tfVars.property, tfvars);
    Assert.assertEquals("Success!", terraformPlan.execute(this.properties));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString());
  }

  @Test
  public void terraformPlanExecutesWhenNoPropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.PLAN, this.executable);
    Mockito.when(this.executable.execute("terraform plan -input=false ")).thenReturn("Success!");
    TerraformPlan terraformPlan = new TerraformPlan(terraformDecorator);

    Assert.assertEquals("Success!", terraformPlan.execute(new Properties()));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString());
  }

  @Test(expected = TerraformException.class)
  public void terraformPlanThrowsTerraformExceptionOnError() throws IOException, InterruptedException, TerraformException {
    Mockito.when(this.executable.execute(Mockito.anyString())).thenThrow(new IOException("boom!"));
    TerraformPlan terraformPlan = new TerraformPlan(this.executable);
    terraformPlan.execute(properties);
  }

  @Test
  public void terraformPlanPassesLoggerToExecutable() {
    Executable executable = Mockito.mock(Executable.class);
    Logger logger = Mockito.mock(Logger.class);
    TerraformPlan terraformPlan = new TerraformPlan(executable, logger);

    Mockito.verify(executable, Mockito.times(1)).setLogger(logger);
  }

  @After
  public void destroy() throws IOException {
    FileUtils.forceDelete(Paths.get("src", "main", "tf").toFile());
  }
}
