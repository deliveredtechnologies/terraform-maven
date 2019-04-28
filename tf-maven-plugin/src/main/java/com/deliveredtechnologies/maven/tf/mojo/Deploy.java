package com.deliveredtechnologies.maven.tf.mojo;

import com.deliveredtechnologies.maven.tf.TerraformDeploy;
import com.deliveredtechnologies.maven.tf.TerraformMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Mojo tf:deploy goal.
 *<br/>
 * Deploys an artifact to a Maven repo.
 */
@Execute(goal = "package")
@Mojo(name = "deploy")
public class Deploy extends TerraformMojo<String> {
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    execute(new TerraformDeploy(), System.getProperties());
  }
}
