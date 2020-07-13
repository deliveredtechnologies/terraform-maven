package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.maven.terraform.TerraformGetMavenRootArtifact;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformInit;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * Mojo terraform init goal.
 * <br>
 * Runs 'terraform init'
 */
@Execute(goal = "get")
@Mojo(name = "init", requiresProject = false)
public class Init extends TerraformMojo<String> {
  @Parameter(property = "tfRootDir")
  String tfRootDir;

  @Parameter(property = "artifact")
  String artifact;

  @Parameter(property = "pluginDir")
  File pluginDir;

  @Parameter(property = "getPlugins")
  boolean getPlugins;

  @Parameter(property = "backendConfig")
  String backendConfig;

  @Parameter(property = "verifyPlugins")
  boolean verifyPlugins;

  @Parameter(property = "skipTfGet")
  boolean skipTfGet = false;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      if (!StringUtils.isEmpty(artifact)) {
        TerraformGetMavenRootArtifact mavenRepoExecutableOp = new TerraformGetMavenRootArtifact(artifact, tfRootDir, getLog());
        tfRootDir = mavenRepoExecutableOp.execute(getFieldsAsProperties());
      }
      execute(new TerraformInit(tfRootDir), getFieldsAsProperties());
    } catch (IOException | TerraformException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
