package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformApply.TerraformApplyParam;

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

public class TerraformApplyTest {
  private Map<String,Object> properties;
  private Executable executable;
  private String tfRootModule = "test";

  /**
   * Sets up properties, Mock(s) and creates the terraform root module source.
   * @throws IOException
   */
  @Before
  public void setup() throws IOException {
    FileUtils.copyDirectory(
        Paths.get("src", "test", "resources", "tf_initialized", "root").toFile(),
        Paths.get("src", "main", "tf", tfRootModule).toFile()
    );


    properties = new HashMap<>();
    executable = Mockito.mock(Executable.class);
  }

  @Test
  public void terraformApplyExecutesWhenAllPossiblePropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.APPLY, this.executable);
    Mockito.when(this.executable.execute(
      "terraform apply -var 'key1=value1' -var 'key2=value2' -var-file=test1.txt -var-file=test2.txt -lock-timeout=1000 -target=module1.module2 -no-color -auto-approve someplan.tfplan",
      1111))
      .thenReturn("Success!");
    TerraformApply terraformApply = new TerraformApply(terraformDecorator);



    this.properties.put(TerraformApplyParam.tfVarFiles.property, "test1.txt, test2.txt");
    this.properties.put(TerraformApplyParam.tfVars.property, "key1=value1, key2=value2");
    this.properties.put(TerraformApplyParam.lockTimeout.property, "1000");
    this.properties.put(TerraformApplyParam.target.property, "module1.module2");
    this.properties.put(TerraformApplyParam.noColor.property, "true");
    this.properties.put(TerraformApplyParam.timeout.property, "1111");
    this.properties.put(TerraformApplyParam.plan.property, "someplan.tfplan");

    Assert.assertEquals("Success!", terraformApply.execute(properties));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString(), Mockito.anyInt());
  }

  @Test
  public void terraformApplyExecuteWhenRefreshStatePassedAsFalse() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.APPLY, this.executable);
    Mockito.when(this.executable.execute("terraform apply -refresh=false -auto-approve ",1111)).thenReturn("Success!");
    TerraformApply terraformApply = new TerraformApply(terraformDecorator);

    this.properties.put(TerraformApply.TerraformApplyParam.refreshState.property, "false");
    this.properties.put(TerraformApply.TerraformApplyParam.timeout.property, "1111");

    Assert.assertEquals("Success!", terraformApply.execute(properties));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString(), Mockito.anyInt());
  }
  @Test
  public void terraformApplyExecutesWhenTFvarsIsMap() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.APPLY, this.executable);
    Mockito.when(this.executable.execute("terraform apply -var 'key1=value1' -var 'key2=[\"value2\",\"value3\"]' -auto-approve ")).thenReturn("Success!");
    TerraformApply terraformApply = new TerraformApply(terraformDecorator);

    Map tfvars = new HashMap();
    tfvars.put("key1", "value1");
    tfvars.put("key2", Arrays.asList("value2", "value3"));
    this.properties.put(TerraformApplyParam.tfVars.property, tfvars);
    Assert.assertEquals("Success!", terraformApply.execute(this.properties));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString());
  }

  @Test
  public void terraformApplyExecutesWhenNoPropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.APPLY, this.executable);
    Mockito.when(this.executable.execute("terraform apply -auto-approve ")).thenReturn("Success!");
    TerraformApply terraformApply = new TerraformApply(terraformDecorator);

    Assert.assertEquals("Success!", terraformApply.execute(new Properties()));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString());
  }

  @Test(expected = TerraformException.class)
  public void terraformApplyThrowsTerraformExceptionOnError() throws IOException, InterruptedException, TerraformException {
    Mockito.when(this.executable.execute(Mockito.anyString())).thenThrow(new IOException("boom!"));
    TerraformApply terraformApply = new TerraformApply(this.executable);
    terraformApply.execute(properties);
  }

  @Test
  public void terraformApplyPassesLoggerToExecutable() {
    Executable executable = Mockito.mock(Executable.class);
    Logger logger = Mockito.mock(Logger.class);
    TerraformApply terraformApply = new TerraformApply(executable, logger);

    Mockito.verify(executable, Mockito.times(1)).setLogger(logger);
  }

  @After
  public void destroy() throws IOException {
    FileUtils.forceDelete(Paths.get("src", "main", "tf").toFile());
  }
}
