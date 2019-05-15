package com.deliveredtechnologies.maven.tf;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Tests for TerraformGet.
 */
public class TerraformGetTest {

  private Path tfModules;

  /**
   * Creates a tfmodules directory and copies the mock artifact zips in.
   * @throws URISyntaxException
   * @throws IOException
   */
  @Before
  public void setup() throws URISyntaxException, IOException {
    Path zipsDir = Paths.get(this.getClass().getResource("/zips").toURI());
    tfModules  = zipsDir.resolveSibling(".tfmodules");
    FileUtils.forceMkdir(tfModules.toFile());
    List<Path> zipFiles = Files.walk(zipsDir, 1)
        .filter(path -> !path.equals(zipsDir))
        .collect(Collectors.toList());
    for (Path zipFile : zipFiles) {
      Files.copy(zipFile, tfModules.resolve(zipFile.getFileName()));
    }
  }

  /**
   * Cleans up the tfmodules directory that was created.
   * @throws IOException
   */
  @After
  public void teardown() throws IOException {
    FileUtils.forceDelete(tfModules.toFile());
  }

  @Test
  public void getDependenciesFromMavenRepoGetsDependenciesIntoModulesDirectory() throws IOException, MavenInvocationException, TerraformException {
    Log log = Mockito.mock(Log.class);
    Invoker invoker = Mockito.mock(Invoker.class);
    InvocationRequest request = Mockito.mock(InvocationRequest.class);
    TerraformGet terraformGet = new TerraformGet(log, tfModules);
    Properties properties = new Properties();
    properties.setProperty("outputDirectory", tfModules.toAbsolutePath().toString());
    properties.setProperty("type", "zip");

    terraformGet.getDependenciesFromMavenRepo(invoker, request);

    Mockito.verify(log, Mockito.times(1)).info(Mockito.anyString());
    Mockito.verify(request, Mockito.times(1)).setGoals(Arrays.asList("dependency:copy-dependencies"));
    Mockito.verify(request, Mockito.times(1)).setProperties(properties);
    Mockito.verify(invoker, Mockito.times(1)).execute(request);
  }

  @Test(expected = TerraformException.class)
  public void getDependenciesThrowsTerraformExceptionOnError() throws IOException, MavenInvocationException, TerraformException {
    Log log = Mockito.mock(Log.class);
    Invoker invoker = Mockito.mock(Invoker.class);
    InvocationRequest request = Mockito.mock(InvocationRequest.class);
    TerraformGet terraformGet = new TerraformGet(log, tfModules);

    Mockito.when(invoker.execute(request)).thenThrow(new MavenInvocationException("blow up!"));
    terraformGet.getDependenciesFromMavenRepo(invoker, request);

    Mockito.verify(log, Mockito.times(1)).info(Mockito.anyString());
    Mockito.verify(request, Mockito.times(1)).setGoals(Arrays.asList("dependency:copy-dependencies"));
    Mockito.verify(invoker, Mockito.times(1)).execute(request);
  }

  @Test
  public void expandArtifactsUnzipsAllArtifactsInaDirectory() throws IOException, TerraformException {
    Log log = Mockito.mock(Log.class);
    TerraformGet terraformGet = new TerraformGet(log, tfModules);
    terraformGet.expandMavenArtifacts(tfModules);

    List<Path> directories = Files.walk(tfModules, 1)
        .filter(path -> !tfModules.equals(path))
        .collect(Collectors.toList());

    Assert.assertEquals(directories.size(), 3);
    Assert.assertTrue(directories.stream().anyMatch(path -> path.getFileName().toString().equals("my.module2")));
    Assert.assertTrue(directories.stream().anyMatch(path -> path.getFileName().toString().equals("my-module1")));
    Assert.assertTrue(directories.stream().anyMatch(path -> path.getFileName().toString().equals("my-module3")));
  }

  @Test
  public void terraformGetConstructorWithLogAndTfModulesStringCreatesInstanceWithoutError() throws IOException {
    Log log = Mockito.mock(Log.class);
    TerraformGet terraformGet = new TerraformGet(log, tfModules.toString());

    Assert.assertNotNull(terraformGet);
  }

  @Test
  public void terraformGetConstructorWithLogCreatesInstanceWithoutError() throws IOException {
    Log log = Mockito.mock(Log.class);
    TerraformGet terraformGet = new TerraformGet(log);

    Assert.assertNotNull(terraformGet);
  }

  @Test
  public void terraformGetConstructorWithNoArgsCreatesInstanceWithoutError() throws IOException {
    TerraformGet terraformGet = new TerraformGet();

    Assert.assertNotNull(terraformGet);
  }
}
