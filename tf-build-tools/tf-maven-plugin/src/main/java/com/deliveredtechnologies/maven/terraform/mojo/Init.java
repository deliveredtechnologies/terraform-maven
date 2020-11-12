package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.maven.terraform.TerraformGetMavenRootArtifact;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import com.deliveredtechnologies.terraform.api.TerraformInit;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

  @Parameter(property = "backendType")
  String backendType;


  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      if (!StringUtils.isEmpty(artifact)) {
        TerraformGetMavenRootArtifact mavenRepoExecutableOp = new TerraformGetMavenRootArtifact(artifact, tfRootDir, getLog());
        tfRootDir = mavenRepoExecutableOp.execute(getFieldsAsProperties());
      }

      if (!StringUtils.isAllEmpty(backendType)) {
        //TODO new backend factory to support multiple backend types
        //File name convention should be <backendType>.backend.generated.tf.json
        Path dir = tfRootDir == null ? TerraformUtils.getDefaultTerraformRootModuleDir() : TerraformUtils.getTerraformRootModuleDir(tfRootDir);
        Files.write(Paths.get(dir.toString(), "s3.backend.generated.tf.json"), "{ \"terraform\": { \"backend\": { \"s3\": {} } } }".getBytes());
      }

      execute(new TerraformInit(tfRootDir), getFieldsAsProperties());
    } catch (IOException | TerraformException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
