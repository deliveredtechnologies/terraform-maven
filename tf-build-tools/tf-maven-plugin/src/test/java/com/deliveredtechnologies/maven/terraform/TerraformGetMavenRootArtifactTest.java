package com.deliveredtechnologies.maven.terraform;

import com.deliveredtechnologies.terraform.TerraformException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TerraformGetMavenRootArtifactTest {

  private static String workingDir = System.getProperty("user.dir");

  @Test
  public void getArtifactFromMavenRepoCreatesTfDirAndInvokesDependencyCopy() throws TerraformException, MavenInvocationException, IOException {
    String artifact = "groupId:artifactId:0.1";
    Path tfWorkingPath = Paths.get(this.workingDir, ".tf");

    Invoker invoker = Mockito.mock(Invoker.class);
    InvocationRequest invocationRequest = Mockito.spy(new DefaultInvocationRequest());
    Log log = Mockito.mock(Log.class);

    Mockito.when(invocationRequest.setProperties(Mockito.any())).thenCallRealMethod();
    Mockito.when(invocationRequest.getProperties()).thenCallRealMethod();
    Mockito.when(invocationRequest.setGoals(Mockito.any())).thenCallRealMethod();
    Mockito.when(invocationRequest.getGoals()).thenCallRealMethod();

    try {
      TerraformGetMavenRootArtifact terraformGetMavenRootArtifact = new TerraformGetMavenRootArtifact(artifact, log);
      terraformGetMavenRootArtifact.getArtifactFromMavenRepo(invoker, invocationRequest);

      Mockito.verify(invoker, Mockito.times(2)).execute(invocationRequest);

      //create the zip artifact
      FileUtils.touch(tfWorkingPath.resolve("artifactId-0.1.zip").toFile());
      terraformGetMavenRootArtifact.getArtifactFromMavenRepo(invoker, invocationRequest);

      Mockito.verify(invoker, Mockito.times(4)).execute(invocationRequest);
      Mockito.verify(log, Mockito.times(2)).info("Getting artifact dependencies from Maven");
      Assert.assertEquals(invocationRequest.getProperties().getProperty("artifact"), String.format("%1$s:pom", artifact));
      Assert.assertEquals(invocationRequest.getProperties().getProperty("outputDirectory"), tfWorkingPath.toAbsolutePath().toString());
      Assert.assertEquals(invocationRequest.getGoals().size(), 1);
      Assert.assertEquals(invocationRequest.getGoals().get(0), "dependency:copy");
      Assert.assertTrue(tfWorkingPath.toFile().exists() && tfWorkingPath.toFile().isDirectory());

    } finally {
      FileUtils.deleteDirectory(tfWorkingPath.toFile());
    }
  }

  @Test
  public void getArtifactZipGetsMavenZipArtifactNameWithoutGroupId() {
    String artifact = "groupId:artifactId:0.1";
    Assert.assertEquals(TerraformGetMavenRootArtifact.getArtifactZip(artifact), "artifactId-0.1.zip");
    Assert.assertEquals(TerraformGetMavenRootArtifact.getArtifactZip(String.format("%1$s.zip", artifact)), "artifactId-0.1.zip");
    Assert.assertEquals("artifactId-0.1.zip", "artifactId-0.1.zip");
  }

  @Test
  public void expandMavenArtifactsExpandsTheMavenZipArtifactInTheWorkingDir() throws IOException, TerraformException {
    String artifact = "groupId:artifactId:0.1";
    Path tfWorkingPath = Paths.get(workingDir, ".tf");

    Log log = Mockito.mock(Log.class);

    if (!tfWorkingPath.toFile().exists()) FileUtils.forceMkdir(tfWorkingPath.toFile());
    FileUtils.copyURLToFile(this.getClass().getResource("/zips/tf-module-my-module1-0.12-rc.zip"), tfWorkingPath.resolve("artifactId-0.1.zip").toFile());

    try {
      TerraformGetMavenRootArtifact terraformGetMavenRootArtifact = new TerraformGetMavenRootArtifact(artifact, log);
      terraformGetMavenRootArtifact.expandMavenArtifacts();
      Assert.assertTrue(tfWorkingPath.resolve("artifactId").toFile().exists());
      Assert.assertEquals(tfWorkingPath.resolve("artifactId").toFile().listFiles().length, 2);
    } finally {
      FileUtils.deleteDirectory(tfWorkingPath.toFile());
    }
  }
}
