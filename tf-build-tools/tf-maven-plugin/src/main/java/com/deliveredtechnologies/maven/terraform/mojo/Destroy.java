package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.maven.logs.MavenSlf4jAdapter;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformDestroy;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

/**
 * Mojo terraform destroy goal.
 * <br>
 * Runs 'terraform destroy'
 */
@Mojo(name = "destroy")
public class Destroy extends TerraformMojo<String> {
  @Parameter(property = "tfRootDir")
  String tfRootDir;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      getLog().info("tfRootDir is " + tfRootDir);
      execute(new TerraformDestroy(tfRootDir, new MavenSlf4jAdapter(getLog())), System.getProperties());
    } catch (IOException | TerraformException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
