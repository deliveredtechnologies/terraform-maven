package com.deliveredtechnologies.maven.tf.mojo;

import com.deliveredtechnologies.maven.tf.TerraformDestroy;
import com.deliveredtechnologies.maven.tf.TerraformMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Mojo terraform destroy goal.
 */
public class Destroy extends TerraformMojo<String> {
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      execute(new TerraformDestroy(), System.getProperties());
    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
