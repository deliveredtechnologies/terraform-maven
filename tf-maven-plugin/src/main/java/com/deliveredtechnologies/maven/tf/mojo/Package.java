package com.deliveredtechnologies.maven.tf.mojo;

import com.deliveredtechnologies.maven.tf.TerraformMojo;
import com.deliveredtechnologies.maven.tf.TerraformPackage;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Mojo tfn:package goal.
 * <br>
 * Packages terraform artifacts in a zip for deployment.
 */
@Mojo(name = "package")
public class Package extends TerraformMojo<String> {
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    execute(new TerraformPackage(project), System.getProperties());
  }
}
