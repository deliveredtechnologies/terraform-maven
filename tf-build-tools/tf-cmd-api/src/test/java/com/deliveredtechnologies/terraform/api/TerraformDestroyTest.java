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
 * Tests for TerraformDestroy.
 */
public class TerraformDestroyTest {
  private Map<String, String> properties;
  private TerraformDestroy terraformDestroy;

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
    terraformDestroy = new TerraformDestroy(new TerraformCommandLineDecorator(TerraformCommand.DESTROY, executable));
  }

  @Test
  public void terraformDestroyExecutesWhenAllPossiblePropertiesArePassed() throws IOException, InterruptedException, TerraformException {

    properties.put(TerraformDestroy.Option.tfVars.getPropertyName(), "key1=value1, key2=value2");
    properties.put(TerraformDestroy.Option.tfVarFiles.getPropertyName(), "test1.txt, test2.txt");
    properties.put(TerraformDestroy.Option.lockTimeout.getPropertyName(), "1000s");
    properties.put(TerraformDestroy.Option.target.getPropertyName(), "module1.module2");
    properties.put(TerraformDestroy.Option.noColor.getPropertyName(), "true");
    properties.put(TerraformDestroy.Option.refreshState.getPropertyName(), "false");
    properties.put(TerraformDestroy.Option.autoApprove.getPropertyName(), "any");

    terraformDestroy.execute(properties);

    Mockito.verify(this.executable, Mockito.times(1)).execute(captor.capture());
    String tfCommand = captor.getValue();

    Assert.assertThat(tfCommand, CoreMatchers.startsWith("terraform destroy"));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-var 'key1=value1' -var 'key2=value2' "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-var-file=test1.txt -var-file=test2.txt "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-lock-timeout=1000s "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-target=module1.module2 "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-no-color "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-auto-approve "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-refresh=false "));
  }

  @Test
  public void terraformDestroyExecutesWhenNoPropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformDestroy terraformDestroy = new TerraformDestroy(new TerraformCommandLineDecorator(TerraformCommand.DESTROY, this.executable));
    terraformDestroy.execute(properties);

    Mockito.verify(this.executable, Mockito.times(1)).execute("terraform destroy -auto-approve ");
  }

  @Test(expected = TerraformException.class)
  public void terraformDestroyThrowsTerraformExceptionOnError() throws IOException, InterruptedException, TerraformException {
    Mockito.when(this.executable.execute(Mockito.anyString())).thenThrow(new IOException("boom!"));
    TerraformDestroy terraformDestroy = new TerraformDestroy(this.executable);
    terraformDestroy.execute(properties);
  }

  @Test
  public void terraformDestroyPassesLoggerToExecutable() {

    Logger logger = Mockito.mock(Logger.class);
    new TerraformDestroy(executable, logger);

    Mockito.verify(executable, Mockito.times(1)).setLogger(logger);
  }
}
