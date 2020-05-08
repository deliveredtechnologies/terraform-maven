package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.maven.logs.MavenSlf4jAdapter;
import com.deliveredtechnologies.maven.terraform.TerraformGetMavenRootArtifact;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformDestroy;
import org.apache.commons.lang3.StringUtils;
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
@Mojo(name = "destroy", requiresProject = false)
public class Destroy extends TerraformMojo<String> {

  @Parameter(property = "tfRootDir")
  String tfRootDir;

  @Parameter(property = "artifact")
  String artifact;

  @Parameter(property = "lockTimeout")
  String lockTimeout;

  @Parameter(property = "tfVarFiles")
  String tfVarFiles;

  @Parameter(property = "tfVars")
  String tfVars;

  @Parameter(property = "target")
  String target;

  @Parameter(property = "noColor")
  boolean noColor = true;

  @Parameter(property = "timeout")
  long timeout;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      if (!StringUtils.isEmpty(artifact)) {
        TerraformGetMavenRootArtifact mavenRepoExecutableOp = new TerraformGetMavenRootArtifact(artifact, tfRootDir, getLog());
        tfRootDir = mavenRepoExecutableOp.execute(getFieldsAsProperties());
      }
      execute(new TerraformDestroy(tfRootDir, new MavenSlf4jAdapter(getLog())), getFieldsAsProperties());
    } catch (IOException | TerraformException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
