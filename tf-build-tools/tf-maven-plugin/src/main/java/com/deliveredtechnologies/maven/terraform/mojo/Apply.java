package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.maven.terraform.TerraformGetMavenRootArtifact;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformApply;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

/**
 * Mojo terraform apply goal.
 * <br>
 * Runs 'terraform apply'
 */
@Mojo(name = "apply", requiresProject = false)
public class Apply extends TerraformMojo<String> {

  @Parameter(property = "tfRootDir")
  String tfRootDir;

  @Parameter(property = "artifact")
  String artifact;

  @Parameter(property = "tfVars")
  String tfVars;

  @Parameter(property = "tfVarFiles")
  String tfVarFiles;

  @Parameter(property = "lockTimeout")
  String lockTimeout;

  @Parameter(property = "target")
  String target;

  @Parameter(property = "plan")
  String plan;

  @Parameter(property = "noColor")
  boolean noColor = true;

  @Parameter(property = "timeout")
  long timeout;

  @Parameter(property = "refreshState")
  boolean refreshState = true;

  @Parameter(property = "autoApprove")
  boolean autoApprove = true;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      if (!StringUtils.isEmpty(artifact)) {
        TerraformGetMavenRootArtifact mavenRepoExecutableOp = new TerraformGetMavenRootArtifact(artifact, tfRootDir, getLog());
        tfRootDir = mavenRepoExecutableOp.execute(getFieldsAsProperties());
      }
      execute(new TerraformApply(tfRootDir), getFieldsAsProperties());
    } catch (IOException | TerraformException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
