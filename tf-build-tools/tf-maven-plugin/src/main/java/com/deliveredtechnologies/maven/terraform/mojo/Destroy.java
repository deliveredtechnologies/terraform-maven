package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.terraform.api.TerraformDestroy;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

/**
 * Mojo terraform destroy goal.
 * <br>
 * Runs 'terraform destroy'
 */
@Mojo(name = "destroy")
public class Destroy extends TerraformMojo<String> {
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      execute(new TerraformDestroy(), System.getProperties());
    } catch (IOException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
