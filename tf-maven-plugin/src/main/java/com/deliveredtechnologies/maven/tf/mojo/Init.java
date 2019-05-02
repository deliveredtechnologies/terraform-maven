package com.deliveredtechnologies.maven.tf.mojo;

import com.deliveredtechnologies.maven.tf.TerraformInit;
import com.deliveredtechnologies.maven.tf.TerraformMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Mojo terraform init goal.
 * <br/>
 * Runs 'terraform init'
 */
@Execute(goal = "get")
@Mojo(name = "init")
public class Init extends TerraformMojo<String> {
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      execute(new TerraformInit(), System.getProperties());
    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
