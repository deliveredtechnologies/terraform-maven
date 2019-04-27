package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.io.Executable;
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
      "terraform apply -var 'key1=value1' -var 'key2=value2' -var_file=test1.txt -var_file=test2.txt -lock-timeout=1000 -target=module1.module2 -auto-approve -no-color someplan.tfplan",
      1111))
      .thenReturn("Success!");
    TerraformApply terraformApply = new TerraformApply(terraformDecorator);

    this.properties.put("varFiles", "test1.txt, test2.txt");
    this.properties.put("tfVars", "key1=value1, key2=value2");
    this.properties.put("lockTimeout", "1000");
    this.properties.put("target", "module1.module2");
    this.properties.put("autoApprove", "true");
    this.properties.put("noColor", "true");
    this.properties.put("timeout", "1111");
    this.properties.put("plan", "someplan.tfplan");

    Assert.assertEquals("Success!", terraformApply.execute(properties));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString(), Mockito.anyInt());
  }

  @Test
  public void terraformApplyExecutesWhenNoPropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.APPLY, this.executable);
    Mockito.when(this.executable.execute("terraform apply ")).thenReturn("Success!");
    TerraformApply terraformApply = new TerraformApply(terraformDecorator);

    Assert.assertEquals("Success!", terraformApply.execute(new Properties()));
    Mockito.verify(this.executable, Mockito.times(1)).execute(Mockito.anyString());
  }

  @Test
  public void terraformApplyUsesPlanWhenBothPlanAndTfRootDirAreSpecified() throws IOException, InterruptedException, TerraformException {
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.APPLY, this.executable);
    Mockito.when(this.executable.execute("terraform apply someplan.tfplan")).thenReturn("Success!");
    TerraformApply terraformApply = new TerraformApply(terraformDecorator);
    this.properties.put("plan", "someplan.tfplan");
    this.properties.put("tfRootDir", "/somedir");

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
