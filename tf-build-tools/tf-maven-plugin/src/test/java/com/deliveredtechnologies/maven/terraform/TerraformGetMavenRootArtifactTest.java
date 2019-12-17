package com.deliveredtechnologies.maven.terraform;

import static org.apache.commons.io.FileUtils.copyURLToFile;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.apache.commons.io.FileUtils.touch;



import com.deliveredtechnologies.terraform.TerraformException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Testsm for TerraformGetMavenRootArtifact.
 */
public class TerraformGetMavenRootArtifactTest {

  Path tfWorkingPath = Paths.get(System.getProperty("user.dir"), ".tfproject");

  /**
   * Cleans up .tfproject directory.
   * @throws IOException  this shouldn't happen.
   */
  @After
  public void tearDown() throws IOException {
    File tfWorkingDir = tfWorkingPath.toFile();
    if (tfWorkingDir.exists() && tfWorkingDir.isDirectory()) {
      deleteDirectory(tfWorkingDir);
    }
  }

  @Test
  public void getArtifactFromMavenRepoCreatesTfDirAndInvokesDependencyCopy() throws TerraformException, MavenInvocationException, IOException {
    String artifact = "groupId:artifactId:0.1";

    Invoker invoker = Mockito.mock(Invoker.class);
    InvocationRequest invocationRequest = Mockito.spy(new DefaultInvocationRequest());
    Log log = Mockito.mock(Log.class);

    Mockito.when(invocationRequest.setProperties(Mockito.any())).thenCallRealMethod();
    Mockito.when(invocationRequest.getProperties()).thenCallRealMethod();
    Mockito.when(invocationRequest.setGoals(Mockito.any())).thenCallRealMethod();
    Mockito.when(invocationRequest.getGoals()).thenCallRealMethod();

    TerraformGetMavenRootArtifact terraformGetMavenRootArtifact = new TerraformGetMavenRootArtifact(artifact, log);
    terraformGetMavenRootArtifact.getArtifactFromMavenRepo(invoker, invocationRequest);

    Mockito.verify(invoker, Mockito.times(2)).execute(invocationRequest);

    //create the zip artifact
    touch(tfWorkingPath.resolve("artifactId-0.1.pom").toFile());
    terraformGetMavenRootArtifact.getArtifactFromMavenRepo(invoker, invocationRequest);

    Mockito.verify(invoker, Mockito.times(4)).execute(invocationRequest);
    Mockito.verify(log, Mockito.times(2)).info("Getting artifact dependencies from Maven");
    Assert.assertEquals(invocationRequest.getProperties().getProperty("artifact"), String.format("%1$s:pom", artifact));
    Assert.assertEquals(invocationRequest.getProperties().getProperty("outputDirectory"), tfWorkingPath.toAbsolutePath().toString());
    Assert.assertEquals(invocationRequest.getGoals().size(), 1);
    Assert.assertEquals(invocationRequest.getGoals().get(0), "dependency:copy");
    Assert.assertTrue(tfWorkingPath.toFile().exists() && tfWorkingPath.toFile().isDirectory());
  }

  @Test(expected = TerraformException.class)
  public void getArtifactFromMavenRepoThrowsTerraformException() throws TerraformException, MavenInvocationException {
    String artifact = "groupId:artifactId:0.1";

    Invoker invoker = Mockito.mock(Invoker.class);
    InvocationRequest invocationRequest = Mockito.spy(new DefaultInvocationRequest());
    Log log = Mockito.mock(Log.class);

    Mockito.when(invoker.execute(invocationRequest)).thenThrow(MavenInvocationException.class);


    TerraformGetMavenRootArtifact terraformGetMavenRootArtifact = new TerraformGetMavenRootArtifact(artifact, log);
    terraformGetMavenRootArtifact.getArtifactFromMavenRepo(invoker, invocationRequest);

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
    Path mainTfPath = tfWorkingPath.resolve("src").resolve("main").resolve("tf");

    Log log = Mockito.mock(Log.class);

    if (!mainTfPath.toFile().exists()) forceMkdir(mainTfPath.toFile());
    copyURLToFile(this.getClass().getResource("/zips/tf-module-my-module1-0.12-rc.zip"), mainTfPath.resolve("artifactId-0.1.zip").toFile());

    TerraformGetMavenRootArtifact terraformGetMavenRootArtifact = new TerraformGetMavenRootArtifact(artifact, log);
    terraformGetMavenRootArtifact.expandMavenArtifacts();
    Assert.assertTrue(mainTfPath.resolve("artifactId").toFile().exists());
    Assert.assertEquals(mainTfPath.resolve("artifactId").toFile().listFiles().length, 2);
  }
}
