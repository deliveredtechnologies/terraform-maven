package com.deliveredtechnologies.maven.tf;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Properties;

/**
 * Abstract class to invoke TerraformOperations.
 * @param <T>
 */
public abstract class TerraformMojo<T> extends AbstractMojo {

  /**
   * Invokes a TerraformOperation w/properties.
   *
   * @param tfOperation the TerraformOperation to invoke
   * @param properties  the properties passed to the TerraformOperation
   * @throws MojoExecutionException
   */
  protected final void execute(TerraformOperation<T> tfOperation, Properties properties) throws MojoExecutionException {
    try {
      Object response = tfOperation.execute(properties);
      if (response instanceof String) {
        getLog().info((String) response);
      }
    } catch (TerraformException e) {
      throw new MojoExecutionException("Failed to execute terraform operation", e);
    }
  }
}
