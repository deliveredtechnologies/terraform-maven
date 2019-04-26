package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.io.Executable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Properties;

public class TerraformApplyTest {
  @Test
  public void terraformApplyExecutesWhenAllPossiblePropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    Executable commandLine = Mockito.mock(Executable.class);
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.APPLY, commandLine);
    Mockito.when(commandLine.execute(
      "terraform apply -var-file=test1.txt -var-file=test2.txt -lock-timeout=1000 -target=module1.module2 -auto-approve -no-color ",
      1111))
      .thenReturn("Success!");
    TerraformApply terraformApply = new TerraformApply(terraformDecorator);

    Properties properties = new Properties();
    properties.put("varFiles", "test1.txt, test2.txt");
    properties.put("lockTimeout", "1000");
    properties.put("target", "module1.module2");
    properties.put("autoApprove", "true");
    properties.put("noColor", "true");
    properties.put("timeout", "1111");


    Assert.assertEquals("Success!", terraformApply.execute(properties));
    Mockito.verify(commandLine, Mockito.times(1)).execute(Mockito.anyString(), Mockito.anyInt());
  }

  @Test
  public void terraformApplyExecutesWhenNoPropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    Executable commandLine = Mockito.mock(Executable.class);
    TerraformCommandLineDecorator terraformDecorator = new TerraformCommandLineDecorator(TerraformCommand.APPLY, commandLine);
    Mockito.when(commandLine.execute("terraform apply ")).thenReturn("Success!");
    TerraformApply terraformApply = new TerraformApply(terraformDecorator);

    Assert.assertEquals("Success!", terraformApply.execute(new Properties()));
    Mockito.verify(commandLine, Mockito.times(1)).execute(Mockito.anyString());
  }
}
