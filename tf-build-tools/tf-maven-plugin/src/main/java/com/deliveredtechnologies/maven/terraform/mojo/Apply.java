package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.terraform.api.TerraformApply;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

/**
 * Mojo terraform apply goal.
 * <br>
 * Runs 'terraform apply'
 */
@Mojo(name = "apply")
public class Apply extends TerraformMojo<String> {
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      execute(new TerraformApply(), System.getProperties());
    } catch (IOException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
