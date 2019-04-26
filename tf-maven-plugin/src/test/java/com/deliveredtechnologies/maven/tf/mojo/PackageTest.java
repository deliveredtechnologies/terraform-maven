package com.deliveredtechnologies.maven.tf.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tests for Package mojo goal.
 */
public class PackageTest {

  private Path targetTfRootModule;
  private Package mojoPackage;
  private Path tfModules;

  /**
   * Sets up dependencies for Mojo Package goal.
   * @throws URISyntaxException
   */
  @Before
  public void setup() throws URISyntaxException {
    Path tfRoot = Paths.get(this.getClass().getResource("/tf/root").toURI());
    this.tfModules = Paths.get(this.getClass().getResource("/tfmodules").toURI());
    this.targetTfRootModule = Paths.get("target/tf-root-module");
    this.mojoPackage = new Package();
    this.mojoPackage.tfRootDir = tfRoot.toAbsolutePath().toString();
    this.mojoPackage.tfModulesDir = tfModules.toAbsolutePath().toString();
    this.mojoPackage.project = new MavenProject();
  }

  @Test
  public void packageGoalWithFatZipPackagesTfModulesInsideTfRootInTheTargetDir() throws URISyntaxException, MojoFailureException, MojoExecutionException, IOException {
    this.mojoPackage.isFatZip = true;
    this.mojoPackage.execute();

    Assert.assertEquals(2, this.targetTfRootModule.toFile().listFiles().length);
    Assert.assertEquals("main.tf",
        Files.walk(this.targetTfRootModule, 1)
          .filter(path -> !path.toFile().isDirectory())
          .findAny()
          .get().getFileName().toString());
    Assert.assertEquals(2, Files.walk(this.targetTfRootModule.resolve(tfModules.getFileName().toString()).resolve("test-module"), 1)
        .filter(path -> path.getFileName().toString().equals("main.tf")
          || path.getFileName().toString().equals("variables.tf"))
        .count());
    //TODO: Check that module source dependencies were updated.
  }

  @Test
  public void packageGoalWithNoFatZipPackagesOnlyTfRootContentsInTheTargetDir() throws URISyntaxException, MojoFailureException, MojoExecutionException, IOException {
    this.mojoPackage.isFatZip = false;
    this.mojoPackage.execute();

    Assert.assertEquals(1, this.targetTfRootModule.toFile().listFiles().length);
    Assert.assertEquals("main.tf",
        Files.walk(this.targetTfRootModule, 1)
          .filter(path -> !path.toFile().isDirectory())
          .findAny()
          .get().getFileName().toString());
  }
}
