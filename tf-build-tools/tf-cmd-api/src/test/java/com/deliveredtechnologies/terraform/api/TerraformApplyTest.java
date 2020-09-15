package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformApply.Option;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TerraformApplyTest {
  private Map<String,Object> properties;
  private TerraformApply terraformApply;

  @Mock
  private Executable executable;

  @Rule
  public MockitoRule rule = MockitoJUnit.rule();

  @Captor
  private ArgumentCaptor<String> captor;

  /**
   * Sets up properties, Mock(s) and creates the terraform root module source.
   *
   * @throws IOException
   */
  @Before
  public void setup() {

    properties = new HashMap<>();
    terraformApply = new TerraformApply(new TerraformCommandLineDecorator(TerraformCommand.APPLY, executable));
  }
  //TODO maybe a timeout test

  @Test
  public void terraformApplyExecutesWhenAllPossiblePropertiesArePassed() throws IOException, InterruptedException, TerraformException {


    properties.put(Option.tfVars.getPropertyName(), "key1=value1, key2=value2");
    properties.put(Option.tfVarFiles.getPropertyName(), "test1.txt, test2.txt");
    properties.put(Option.lockTimeout.getPropertyName(), "1000s");
    properties.put(Option.target.getPropertyName(), "module1.module2");
    properties.put(Option.noColor.getPropertyName(), "true");
    properties.put(Option.refreshState.getPropertyName(), "false");
    properties.put(Option.autoApprove.getPropertyName(), "any");
    properties.put(Option.plan.getPropertyName(), "someplan.tfplan");

    terraformApply.execute(properties);

    Mockito.verify(this.executable, Mockito.times(1)).execute(captor.capture());
    String tfCommand = captor.getValue();

    Assert.assertThat(tfCommand, CoreMatchers.startsWith("terraform apply"));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-var 'key1=value1' -var 'key2=value2' "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-var-file=test1.txt -var-file=test2.txt "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-lock-timeout=1000s "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-target=module1.module2 "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-no-color "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-auto-approve "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-refresh=false "));
    Assert.assertThat(tfCommand, CoreMatchers.endsWith("someplan.tfplan "));
  }

  @Test
  public void terraformApplyExecutesWhenTFvarsIsMap() throws IOException, InterruptedException, TerraformException {
    String str = "terraform apply -var 'key1=value1' -var 'key2=[\"value2\",\"value3\"]' -auto-approve ";

    Map tfvars = new HashMap();
    tfvars.put("key1", "value1");
    tfvars.put("key2", Arrays.asList("value2", "value3"));
    properties.put(TerraformApply.Option.tfVars.getPropertyName(), tfvars);

    terraformApply.execute(properties);

    Mockito.verify(this.executable, Mockito.times(1)).execute(captor.capture());
    String tfCommand = captor.getValue();

    Assert.assertThat(tfCommand, CoreMatchers.startsWith("terraform apply"));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-var 'key1=value1' "));

  }

  @Test
  public void terraformApplyExecutesWhenNoPropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformApply terraformApply = new TerraformApply(new TerraformCommandLineDecorator(TerraformCommand.APPLY, this.executable));
    terraformApply.execute(properties);

    Mockito.verify(this.executable, Mockito.times(1)).execute("terraform apply -auto-approve ");
  }

  @Test(expected = TerraformException.class)
  public void terraformApplyThrowsTerraformExceptionOnError() throws IOException, InterruptedException, TerraformException {
    Mockito.when(this.executable.execute(Mockito.anyString())).thenThrow(new IOException("boom!"));
    TerraformApply terraformApply = new TerraformApply(this.executable);
    terraformApply.execute(properties);
  }

  @Test
  public void terraformApplyPassesLoggerToExecutable() {

    Logger logger = Mockito.mock(Logger.class);
    new TerraformApply(executable, logger);

    Mockito.verify(executable, Mockito.times(1)).setLogger(logger);
  }
}
