package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.io.Executable;
import com.deliveredtechnologies.maven.tf.TerraformApply.TerraformApplyParam;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Properties;

public class TerraformApplyTest {
  private Properties properties;
  private Executable executable;

  @Before
  public void setup() {
    properties = new Properties();
    executable = Mockito.mock(Executable.class);
  }

  @Test
  public void terraformApplyExecutesWhenAllPossiblePropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.APPLY, this.executable);
    Mockito.when(this.executable.execute(
      "terraform apply -var 'key1=value1' -var 'key2=value2' -var_file=test1.txt -var_file=test2.txt -lock-timeout=1000 -target=module1.module2 -no-color -auto-approve someplan.tfplan",
      1111))
      .thenReturn("Success!");
    TerraformApply terraformApply = new TerraformApply(terraformDecorator);

    this.properties.put(TerraformApplyParam.varFiles.property, "test1.txt, test2.txt");
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
  public void terraformApplyExecutesWhenNoPropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.APPLY, this.executable);
    Mockito.when(this.executable.execute(String.format("terraform apply -auto-approve %1$s", TerraformUtils.getTerraformRootModuleDir().toAbsolutePath()))).thenReturn("Success!");
    TerraformApply terraformApply = new TerraformApply(terraformDecorator);

    Assert.assertEquals("Success!", terraformApply.execute(new Properties()));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString());
  }

  @Test
  public void terraformApplyUsesPlanWhenBothPlanAndTfRootDirAreSpecified() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.APPLY, this.executable);
    Mockito.when(this.executable.execute("terraform apply -auto-approve someplan.tfplan")).thenReturn("Success!");
    TerraformApply terraformApply = new TerraformApply(terraformDecorator);
    this.properties.put(TerraformApplyParam.plan.property, "someplan.tfplan");
    this.properties.put(TerraformApplyParam.tfRootDir.property, "/somedir");

    Assert.assertEquals("Success!", terraformApply.execute(properties));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString());
  }

  @Test(expected = TerraformException.class)
  public void terraformApplyThrowsTerraformExceptionOnError() throws IOException, InterruptedException, TerraformException {
    Mockito.when(this.executable.execute(Mockito.anyString())).thenThrow(new IOException("boom!"));
    TerraformApply terraformApply = new TerraformApply(this.executable);
    terraformApply.execute(properties);
  }
}
