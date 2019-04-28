package com.deliveredtechnologies.maven.tf.mojo;

import com.deliveredtechnologies.maven.tf.TerraformApply;
import com.deliveredtechnologies.maven.tf.TerraformMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Mojo terraform apply goal.
 * <br/>
 * Runs 'terraform apply'
 */
public class Apply extends TerraformMojo<String> {
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      execute(new TerraformApply(), System.getProperties());
    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
