package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformOperation;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Properties;

/**
 * Tests for TerraformMojo.
 */
public class TerraformMojoTest {

  private Log log;
  private TerraformOperation terraformOperation;
  private Properties properties;

  /**
   * Setup the mocks and props.
   */
  @Before
  public void setup() {
    log = Mockito.mock(Log.class);
    terraformOperation = Mockito.mock(TerraformOperation.class);
    properties = new Properties();
  }

  @Test
  public void executeOnTerraformOperationWithStringReturnValueWritesToInfoLog() throws TerraformException, MojoFailureException, MojoExecutionException {
    Mockito.when(terraformOperation.execute(Mockito.any())).thenReturn("Testing!");
    TerraformMojo<String> terraformMojo = new TerraformMojo<String>() {
      @Override
      @SuppressWarnings("unchecked")
      public void execute() throws MojoExecutionException, MojoFailureException {
        execute((TerraformOperation<String>)terraformOperation, properties);
      }

      @Override
      public Log getLog() {
        return log;
      }
    };

    terraformMojo.execute();

    Mockito.verify(terraformOperation, Mockito.times(1)).execute(properties);
    Mockito.verify(log, Mockito.times(1)).info("Testing!");
  }

  @Test
  public void executeOnTerraformOperationDelegatesToTerraformOperation() throws TerraformException, MojoFailureException, MojoExecutionException {
    Mockito.when(terraformOperation.execute(Mockito.any())).thenReturn(new Object());
    TerraformMojo terraformMojo = new TerraformMojo() {
      @Override
      public void execute() throws MojoExecutionException, MojoFailureException {
        execute(terraformOperation, properties);
      }

      @Override
      public Log getLog() {
        return log;
      }
    };

    terraformMojo.execute();

    Mockito.verify(terraformOperation, Mockito.times(1)).execute(properties);
    Mockito.verify(log, Mockito.times(0)).info(Mockito.anyString());
  }

  @Test(expected = MojoExecutionException.class)
  public void terraformExceptionIsThrownAsMojoExecutionException() throws TerraformException, MojoExecutionException, MojoFailureException {
    Mockito.when(terraformOperation.execute(Mockito.any())).thenThrow(new TerraformException("boom!"));
    TerraformMojo terraformMojo = new TerraformMojo() {
      @Override
      public void execute() throws MojoExecutionException, MojoFailureException {
        execute(terraformOperation, properties);
      }

    };

    terraformMojo.execute();
  }

  @Test
  public void getFieldAsPropertiesGetsOnlyTheSetSubclassFieldsAsProperties() throws MojoExecutionException {
    TerraformMojoStub terraformMojoStub = new TerraformMojoStub();
    terraformMojoStub.boolProp = true;
    terraformMojoStub.intProp = 22;
    terraformMojoStub.longProp = 1000;
    terraformMojoStub.stringProp = "Hello";

    properties = terraformMojoStub.getFieldsAsProperties();

    Assert.assertEquals(properties.size(), 5);
    Assert.assertEquals(properties.get("boolProp"), true);
    Assert.assertEquals(properties.get("intProp"), 22);
    Assert.assertEquals(properties.get("longProp"), 1000L);
    Assert.assertEquals(properties.get("stringProp"), "Hello");

    terraformMojoStub.boolProp = false;
    terraformMojoStub.intProp = 0;
    terraformMojoStub.longProp = 0;

    properties = terraformMojoStub.getFieldsAsProperties();

    Assert.assertEquals(properties.size(), 2);
    Assert.assertEquals(properties.get("stringProp"), "Hello");
  }

  @Test
  public void getFieldAsPropertiesToDisplayRefreshStateFalseParameter() throws MojoExecutionException {
    TerraformMojoStub terraformMojoStub = new TerraformMojoStub();
    terraformMojoStub.refreshState = false;

    properties = terraformMojoStub.getFieldsAsProperties();

    Assert.assertEquals(properties.get("refreshState"), false);

  }
}
