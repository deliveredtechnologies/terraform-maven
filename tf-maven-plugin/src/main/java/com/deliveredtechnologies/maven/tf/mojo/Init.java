package com.deliveredtechnologies.maven.tf.mojo;

import com.deliveredtechnologies.maven.tf.TerraformException;
import com.deliveredtechnologies.maven.tf.TerraformInit;
import com.deliveredtechnologies.maven.tf.TerraformMojo;
import com.deliveredtechnologies.maven.tf.TerraformOperation;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

/**
 * Mojo terraform init goal.
 */
@Mojo(name = "init")
public class Init extends TerraformMojo<String> {
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      TerraformOperation<String> terraformInit = new TerraformInit();
      getLog().info(terraformInit.execute(System.getProperties()));
    } catch (IOException | TerraformException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
