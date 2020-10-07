package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for TerraformInit.
 */
public class TerraformInitTest {
  private Map<String, String> properties;
  private TerraformInit terraformInit;

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
    terraformInit = new TerraformInit(new TerraformCommandLineDecorator(TerraformCommand.INIT, executable));
  }


  @Test
  public void terraformInitExecutesWhenAllPossiblePropertiesArePassed() throws IOException, InterruptedException, TerraformException {


    properties.put(TerraformInit.Option.input.getPropertyName(), "false");
    properties.put(TerraformInit.Option.lock.getPropertyName(), "false");
    properties.put(TerraformInit.Option.lockTimeout.getPropertyName(), "1000s");
    properties.put(TerraformInit.Option.noColor.getPropertyName(), "any");
    properties.put(TerraformInit.Option.upgrade.getPropertyName(), "any");
    properties.put(TerraformInit.Option.fromModule.getPropertyName(), "module_dir");
    properties.put(TerraformInit.Option.forceCopy.getPropertyName(), "any");
    properties.put(TerraformInit.Option.backendConfig.getPropertyName(), "bucket=foo,key=bar");
    properties.put(TerraformInit.Option.pluginDir.getPropertyName(), "/plugins");
    properties.put(TerraformInit.Option.getPlugins.getPropertyName(), "false");
    properties.put(TerraformInit.Option.verifyPlugins.getPropertyName(), "false");

    terraformInit.execute(properties);

    Mockito.verify(this.executable, Mockito.times(1)).execute(captor.capture());
    String tfCommand = captor.getValue();

    Assert.assertThat(tfCommand, CoreMatchers.startsWith("terraform init"));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-input=false "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-lock=false "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-lock-timeout=1000s "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-no-color "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-upgrade "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-from-module=module_dir"));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-force-copy"));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-backend-config=\"bucket=foo\" -backend-config=\"key=bar\" "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-plugin-dir=/plugins "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-get-plugins=false "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-verify-plugins=false "));

  }

  @Test
  public void terraformApplyExecutesWhenNoPropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformInit terraformInit = new TerraformInit(new TerraformCommandLineDecorator(TerraformCommand.INIT, this.executable));
    terraformInit.execute(properties);

    Mockito.verify(this.executable, Mockito.times(1)).execute("terraform init ");
  }

  @Test(expected = TerraformException.class)
  public void terraformApplyThrowsTerraformExceptionOnError() throws IOException, InterruptedException, TerraformException {
    Mockito.when(this.executable.execute(Mockito.anyString())).thenThrow(new IOException("boom!"));
    TerraformInit terraformApply = new TerraformInit(this.executable);
    terraformInit.execute(properties);
  }

  @Test
  public void terraformApplyPassesLoggerToExecutable() {

    Logger logger = Mockito.mock(Logger.class);
    new TerraformInit(executable, logger);

    Mockito.verify(executable, Mockito.times(1)).setLogger(logger);
  }
}
