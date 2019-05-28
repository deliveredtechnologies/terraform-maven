package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.maven.terraform.TerraformDeploy;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Mojo terraform:deploy goal.
 *<br>
 * Deploys an artifact to a Maven repo.
 */
@Mojo(name = "deploy")
public class Deploy extends TerraformMojo<String> {

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    execute(new TerraformDeploy(getLog(), project), System.getProperties());
  }
}
