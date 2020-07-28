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
 * Tests for {@link TerraformOutput}.
 */
public class TerraformOutputTest {

  private Map<String, String> properties;
  private TerraformOutput terraformOutput;

  @Mock
  private Executable executable;

  @Rule
  public MockitoRule rule = MockitoJUnit.rule();

  @Captor
  private ArgumentCaptor<String> captor;

  /**
   * Sets up mocks and properties.
   *
   * @throws IOException
   * @throws InterruptedException
   */
  @Before
  public void setup() throws IOException, InterruptedException {
    properties = new HashMap<>();
    terraformOutput = new TerraformOutput(new TerraformCommandLineDecorator(TerraformCommand.OUTPUT, executable));
  }

  @Test
  public void terraformOutputAllPropertiesAndDefaults() throws InterruptedException, IOException, TerraformException {
    properties.put(TerraformOutput.Option.state.getPropertyName(), "state/path");
    properties.put(TerraformInit.Option.noColor.getPropertyName(), "any");

    terraformOutput.execute(properties);

    Mockito.verify(this.executable, Mockito.times(1)).execute(captor.capture());
    String tfCommand = captor.getValue();

    Assert.assertThat(tfCommand, CoreMatchers.startsWith("terraform output"));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-json "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-no-color "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-state=state/path "));

  }

  @Test
  public void terraformOutputCallsOutputCommandWithTimeout() throws InterruptedException, IOException, TerraformException {
    String timeout = "1000";
    properties.put("timeout", timeout);

    terraformOutput.execute(properties);
    Mockito.verify(executable, Mockito.times(1)).execute("terraform output -json ", Integer.parseInt(timeout));
  }

  @Test(expected = TerraformException.class)
  public void terraformExecuteThrowsTerraformException() throws IOException, InterruptedException, TerraformException {
    Mockito.when(executable.execute(Mockito.anyString())).thenThrow(new IOException());

    TerraformOutput terraformOutput = new TerraformOutput(executable);
    terraformOutput.execute(properties);
  }

  @Test
  public void terraformOuputPassesLoggerToExecutable() {
    Logger logger = Mockito.mock(Logger.class);
    TerraformOutput terraformOutput = new TerraformOutput(executable, logger);

    Mockito.verify(executable, Mockito.times(1)).setLogger(logger);
  }
}
