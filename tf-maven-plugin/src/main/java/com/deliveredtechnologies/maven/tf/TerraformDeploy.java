package com.deliveredtechnologies.maven.tf;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

/**
 * API for deploying a Terraform config to a Maven repo
 */
public class TerraformDeploy implements TerraformOperation<String> {

  private Log log;
  private MavenProject project;

  @Override
  public String execute(Properties properties) throws TerraformException {
    return null;
  }

  final void getDependenciesFromMavenRepo(Invoker invoker, InvocationRequest request, Properties properties) throws TerraformException {
    if (!properties.containsKey("file")) {
      Path targetPath = Paths.get("target").resolve(String.format("%1$s-%2$s.zip", project.getArtifactId(), project.getVersion()));
      properties.setProperty("file", String.format("file://%1$s", targetPath.toAbsolutePath().toString()));
    }
    log.info(String.format("Deploying %1$s to %2$s", properties.getProperty("file"), properties.getProperty("url")));

    if (!properties.containsKey("pomFile")) {
      Path pomFile = Paths.get("pom.xml");
      properties.setProperty("pomFile", pomFile.toAbsolutePath().toString());
    }

    request.setGoals(Arrays.asList("deploy:deploy-file"));
    request.setProperties(properties);
    try {
      invoker.execute(request);
    } catch (MavenInvocationException e) {
      throw new TerraformException("Unable to deploy to Maven repo", e);
    }
  }
}
