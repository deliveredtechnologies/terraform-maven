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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for TerraformPlan.
 */
public class TerraformPlanTest {
  private Map<String, String> properties;
  private TerraformPlan terraformPlan;

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
    terraformPlan = new TerraformPlan(new TerraformCommandLineDecorator(TerraformCommand.PLAN, executable));
  }

  @Test
  public void terraformPlanExecutesWhenAllPossiblePropertiesArePassed() throws IOException, InterruptedException, TerraformException {

    properties.put(TerraformPlan.Option.compactWarnings.getPropertyName(), "true");
    properties.put(TerraformPlan.Option.destroyPlan.getPropertyName(), "true");
    properties.put(TerraformPlan.Option.detailedExitcode.getPropertyName(), "true");
    properties.put(TerraformPlan.Option.planInput.getPropertyName(), "true");
    properties.put(TerraformPlan.Option.lock.getPropertyName(), "true");
    properties.put(TerraformPlan.Option.lockTimeout.getPropertyName(), "1000s");
    properties.put(TerraformPlan.Option.noColor.getPropertyName(), "true");
    properties.put(TerraformPlan.Option.planOutputFile.getPropertyName(), "destroy.plan");
    properties.put(TerraformPlan.Option.refreshState.getPropertyName(), "false");
    properties.put(TerraformPlan.Option.tfState.getPropertyName(), "my.tfstate");
    properties.put(TerraformPlan.Option.target.getPropertyName(), "module1.module2");
    properties.put(TerraformPlan.Option.tfVars.getPropertyName(), "key1=value1, key2=value2");
    properties.put(TerraformPlan.Option.tfVarFiles.getPropertyName(), "test1.txt, test2.txt");

    terraformPlan.execute(properties);

    Mockito.verify(this.executable, Mockito.times(1)).execute(captor.capture());
    String tfCommand = captor.getValue();

    Assert.assertThat(tfCommand, CoreMatchers.startsWith("terraform plan"));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-compact-warnings "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-destroy "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-detailed-exitcode "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-input=true "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-lock=true "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-lock-timeout=1000s "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-no-color "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-out=destroy.plan "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-refresh=false "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-state=my.tfstate "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-target=module1.module2 "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-var 'key1=value1' -var 'key2=value2' "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-var-file=test1.txt -var-file=test2.txt "));
  }

  @Test
  public void terraformPlanExecutesWhenPlanOutputFileReferencesS3Path() throws IOException, InterruptedException, TerraformException {

    properties.put(TerraformPlan.Option.planOutputFile.getPropertyName(), "s3://plan-files-bucket/planfiles/destroy.plan");
    terraformPlan.execute(properties);

    Mockito.verify(this.executable, Mockito.times(1)).execute(captor.capture());
    String tfCommand = captor.getValue();

    Assert.assertThat(tfCommand, CoreMatchers.startsWith("terraform plan"));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-out=destroy.plan "));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-input=false "));

  }
}
