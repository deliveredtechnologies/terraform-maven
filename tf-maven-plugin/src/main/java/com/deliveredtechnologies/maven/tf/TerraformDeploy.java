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

  private static String PACKAGING = "zip";

  private Log log;
  private MavenProject project;

  enum TerraformDeployParam {
    file, pomFile, url, packaging, generatePom, artifactId, groupId, version;
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
   *   Properties accepted:<br>
   *   file - the file to be deployed to the Maven repo<br>
   *   url - the url of the Maven repo<br>
   *   pomFile - the POM file to be associated with the file<br>
   * </p>
   * @param invoker             Maven Invoker
   * @param request             Maven InvocationRequest
   * @param properties          Properties to be applied
   * @throws TerraformException
   */
  final Properties deployFileToMavenRepo(Invoker invoker, InvocationRequest request, Properties properties) throws TerraformException {
    Properties deployFileProps = new Properties();

    if (!properties.containsKey(TerraformDeployParam.url.toString())) {
      throw new TerraformException("missing required parameter: url");
    }
    deployFileProps.put(TerraformDeployParam.url.toString(), properties.getProperty(TerraformDeployParam.url.toString()));

    if (!properties.containsKey(TerraformDeployParam.file.toString())) {
      Path targetPath = Paths.get("target").resolve(String.format("%1$s-%2$s.zip", project.getArtifactId(), project.getVersion()));
      deployFileProps.setProperty(TerraformDeployParam.file.toString(), targetPath.toString());
    } else {
      deployFileProps.setProperty(TerraformDeployParam.file.toString(), properties.getProperty(TerraformDeployParam.file.toString()));
    }
    deployFileProps.put(TerraformDeployParam.packaging.toString(), PACKAGING);
    log.info(String.format("Deploying %1$s to %2$s", deployFileProps.getProperty(TerraformDeployParam.file.toString()), deployFileProps.getProperty(TerraformDeployParam.url.toString())));

    if (properties.containsKey(TerraformDeployParam.generatePom.toString())) {
      deployFileProps.put(TerraformDeployParam.generatePom.toString(), properties.getProperty(TerraformDeployParam.generatePom.toString()));
      deployFileProps.put(TerraformDeployParam.artifactId.toString(), project.getArtifactId());
      deployFileProps.put(TerraformDeployParam.groupId.toString(), project.getGroupId());
      deployFileProps.put(TerraformDeployParam.version.toString(), project.getVersion());
      log.info("Using generated POM");
    } else {
      if (!properties.containsKey(TerraformDeployParam.pomFile.toString())) {
        Path pomFile = Paths.get("pom.xml");
        deployFileProps.setProperty(TerraformDeployParam.pomFile.toString(), pomFile.toAbsolutePath().toString());
      } else {
        deployFileProps.setProperty(TerraformDeployParam.pomFile.toString(), properties.getProperty(TerraformDeployParam.pomFile.toString()));
      }
      log.info(String.format("Using POM: %1$s", deployFileProps.getProperty(TerraformDeployParam.pomFile.toString())));
    }

    request.setGoals(Arrays.asList("deploy:deploy-file"));
    request.setProperties(deployFileProps);
    try {
      invoker.execute(request);
      return deployFileProps;
    } catch (MavenInvocationException e) {
      throw new TerraformException("Unable to deploy to Maven repo", e);
    }
  }
}
