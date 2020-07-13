package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.maven.terraform.TerraformDeploy;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;

/**
 * Mojo terraform:deploy goal.
 *<br>
 * Deploys an artifact to a Maven repo.
 */
@Mojo(name = "deploy", defaultPhase = LifecyclePhase.DEPLOY)
public class Deploy extends TerraformMojo<String> {

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  MavenProject project;

  @Parameter(property = "file")
  File file;

  @Parameter(property = "url")
  String url;

  @Parameter(property = "pomFile")
  File pomFile;

  @Parameter(property = "generatePom")
  boolean generatePom;

  @Parameter(property = "groupId")
  String groupId;

  @Parameter(property = "artifactId")
  String artifactId;

  @Parameter(property = "version")
  String version;

  @Parameter(property = "repositoryId")
  String repositoryId;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    execute(new TerraformDeploy(getLog(), project));
  }
}
