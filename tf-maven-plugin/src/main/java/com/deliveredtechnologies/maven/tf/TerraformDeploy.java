package com.deliveredtechnologies.maven.tf;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

/**
 * API for deploying a Terraform config to a Maven repo.
 */
public class TerraformDeploy implements TerraformOperation<String> {

  private Log log;
  private MavenProject project;

  enum TerraformDeployParam {
    file, pomFile, url;
  }

  public TerraformDeploy(Log log, MavenProject project) {
    this.log = log;
    this.project = project;
  }

  @Override
  public String execute(Properties properties) throws TerraformException {
    deployFileToMavenRepo(new DefaultInvoker(), new DefaultInvocationRequest(), System.getProperties());
    return "Successfully deployed";
  }

  /**
   * Deploys a file w/POM to the Maven repo at the specified URL.
   * <p>
   *   Properties accepted:<br/>
   *   file - the file to be deployed to the Maven repo<br/>
   *   url - the url of the Maven repo<br/>
   *   pomFile - the POM file to be associated with the file<br/>
   * </p>
   * @param invoker             Maven Invoker
   * @param request             Maven InvocationRequest
   * @param properties          Properties to be applied
   * @throws TerraformException
   */
  final void deployFileToMavenRepo(Invoker invoker, InvocationRequest request, Properties properties) throws TerraformException {
    if (!properties.containsKey(TerraformDeployParam.file.toString())) {
      Path targetPath = Paths.get("target").resolve(String.format("%1$s-%2$s.zip", project.getArtifactId(), project.getVersion()));
      properties.setProperty(TerraformDeployParam.file.toString(), String.format("file://%1$s", targetPath.toAbsolutePath().toString()));
    }
    if (!properties.containsKey(TerraformDeployParam.url.toString())) {
      throw new TerraformException("missing required parameter: url");
    }
    log.info(String.format("Deploying %1$s to %2$s", properties.getProperty(TerraformDeployParam.file.toString()), properties.getProperty(TerraformDeployParam.url.toString())));

    if (!properties.containsKey(TerraformDeployParam.pomFile.toString())) {
      Path pomFile = Paths.get("pom.xml");
      properties.setProperty(TerraformDeployParam.pomFile.toString(), pomFile.toAbsolutePath().toString());
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
