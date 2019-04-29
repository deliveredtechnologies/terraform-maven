package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.tf.TerraformDeploy.TerraformDeployParam;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

/**
 * Tests for TerraformDeploy.
 */
public class TerraformDeployTest {

  private MavenProject project;
  private Log log;
  private Properties properties;
  private TerraformDeploy terraformDeploy;
  private Invoker invoker;
  private InvocationRequest request;

  /**
   * sets up objects and mocks.
   */
  @Before
  public void setup() {
    this.properties = Mockito.spy(Properties.class);
    this.invoker = Mockito.mock(Invoker.class);
    this.request = Mockito.mock(InvocationRequest.class);
    this.project = new MavenProject();
    this.log = Mockito.mock(Log.class);
    this.terraformDeploy = new TerraformDeploy(log, project);

  }

  @Test
  public void deployFileToMavenRepoDeploysWithPomAndFileSpecified() throws TerraformException, MavenInvocationException {
    String filename = "file1.txt";
    String pom = "mypom.xml";
    String url = "http://someurl.com";

    properties.put(TerraformDeployParam.url.toString(), url);
    properties.put(TerraformDeployParam.file.toString(), filename);
    properties.put(TerraformDeployParam.pomFile.toString(), pom);

    terraformDeploy.deployFileToMavenRepo(invoker, request, properties);

    Mockito.verify(request, Mockito.times(1)).setProperties(properties);
    Mockito.verify(request, Mockito.times(1)).setGoals(Arrays.asList("deploy:deploy-file"));
    Mockito.verify(invoker, Mockito.times(1)).execute(request);
  }

  @Test
  public void deployFileToMavenRepoDeploysWithPomAndFileOmitted() throws TerraformException, MavenInvocationException {
    String url = "http://someurl.com";
    Path targetPath = Paths.get("target")
        .resolve(String.format("%1$s-%2$s.zip", project.getArtifactId(), project.getVersion()));

    properties.put(TerraformDeployParam.url.toString(), url);
    terraformDeploy.deployFileToMavenRepo(invoker, request, properties);

    Mockito.verify(request, Mockito.times(1)).setProperties(properties);
    Mockito.verify(request, Mockito.times(1)).setGoals(Arrays.asList("deploy:deploy-file"));
    Mockito.verify(invoker, Mockito.times(1)).execute(request);
    Mockito.verify(properties, Mockito.times(1))
      .put(TerraformDeployParam.file.toString(), String.format("file://%1$s", targetPath.toAbsolutePath().toString()));
    Mockito.verify(properties, Mockito.times(1))
      .put(TerraformDeployParam.pomFile.toString(), Paths.get("pom.xml").toAbsolutePath().toString());
  }

  @Test(expected = TerraformException.class)
  public void deployFileToMavenRepoDeploysWithUrlOmitted() throws TerraformException, MavenInvocationException {
    terraformDeploy.deployFileToMavenRepo(invoker, request, properties);
  }
}
