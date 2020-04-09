package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.maven.logs.MavenSlf4jAdapter;
import com.deliveredtechnologies.maven.terraform.TerraformGetMavenRootArtifact;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformPlan;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

/**
 * Mojo terraform plan goal.
 * <br>
 * Runs 'terraform plan'
 */
@Mojo(name = "plan", requiresProject = false, defaultPhase = LifecyclePhase.VERIFY)
public class Plan extends TerraformMojo<String> {
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

  @Parameter(property = "planInput")
  String planInput;

  @Parameter(property = "refreshState")
  String refreshState;

  @Parameter(property = "tfState")
  String tfState;

  @Parameter(property = "noColor")
  String noColor;

  @Parameter(property = "destroyPlan")
  String destroyPlan;

  @Parameter(property = "timeout")
  String timeout;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      if (!StringUtils.isEmpty(artifact)) {
        TerraformGetMavenRootArtifact mavenRepoExecutableOp = new TerraformGetMavenRootArtifact(artifact, tfRootDir, getLog());
        tfRootDir = mavenRepoExecutableOp.execute(getFieldsAsProperties());
      }
      execute(new TerraformPlan(tfRootDir, new MavenSlf4jAdapter(getLog())));
    } catch (IOException | TerraformException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
