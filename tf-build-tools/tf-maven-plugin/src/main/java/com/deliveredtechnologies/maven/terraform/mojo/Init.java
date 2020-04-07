package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.maven.logs.MavenSlf4jAdapter;
import com.deliveredtechnologies.maven.terraform.TerraformGetMavenRootArtifact;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformInit;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

/**
 * Mojo terraform init goal.
 * <br>
 * Runs 'terraform init'
 */
@Mojo(name = "init", requiresProject = false)
public class Init extends TerraformMojo<String> {
  @Parameter(property = "tfRootDir")
  String tfRootDir;

  @Parameter(property = "artifact")
  String artifact;

  @Parameter(property = "pluginDir")
  String pluginDir;

  @Parameter(property = "getPlugins")
  String getPlugins;

  @Parameter(property = "backendConfig")
  String backendConfig;

  @Parameter(property = "verifyPlugins")
  String verifyPlugins;

  @Parameter(property = "skipTfGet")
  String skipTfGet;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      if (!StringUtils.isEmpty(artifact)) {
        TerraformGetMavenRootArtifact mavenRepoExecutableOp = new TerraformGetMavenRootArtifact(artifact, tfRootDir, getLog());
        tfRootDir = mavenRepoExecutableOp.execute(System.getProperties());
      }
      execute(new TerraformInit(tfRootDir, new MavenSlf4jAdapter(getLog())), System.getProperties());
    } catch (IOException | TerraformException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
