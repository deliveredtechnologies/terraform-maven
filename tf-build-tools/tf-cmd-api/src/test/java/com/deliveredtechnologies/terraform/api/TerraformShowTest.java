package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformShow.Option;
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

public class TerraformShowTest {
  private Map<String,Object> properties;
  private TerraformShow terraformShow;

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
    terraformShow = new TerraformShow(new TerraformCommandLineDecorator(TerraformCommand.SHOW, executable));
  }
  //TODO maybe a timeout test

  @Test
  public void terraformSowExecutesWhenAllPossiblePropertiesArePassed() throws IOException, InterruptedException, TerraformException {


    properties.put(Option.noColor.getPropertyName(), "true");
    properties.put(Option.path.getPropertyName(), "someplan.tfplan");

    terraformShow.execute(properties);

    Mockito.verify(this.executable, Mockito.times(1)).execute(captor.capture());
    String tfCommand = captor.getValue();

    Assert.assertThat(tfCommand, CoreMatchers.startsWith("terraform show"));
    Assert.assertThat(tfCommand, CoreMatchers.containsString("-json "));
    Assert.assertThat(tfCommand, CoreMatchers.endsWith("someplan.tfplan "));
  }


  @Test
  public void terraformShowExecutesWhenNoPropertiesArePassed() throws IOException, InterruptedException, TerraformException {
    TerraformShow terraformShow = new TerraformShow(new TerraformCommandLineDecorator(TerraformCommand.SHOW, this.executable));
    terraformShow.execute(properties);

    Mockito.verify(this.executable, Mockito.times(1)).execute("terraform show -json ");
  }
}
