package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformOperation;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
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
}
