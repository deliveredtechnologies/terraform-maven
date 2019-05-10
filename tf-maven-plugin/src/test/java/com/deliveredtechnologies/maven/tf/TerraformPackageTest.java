package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.tf.TerraformPackage.TerraformPackageParams;

import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Tests for TerraformPackage.
 */
public class TerraformPackageTest {
  private MavenProject project = new MavenProject();
  private Path targetTfRootModule;
  private TerraformPackage terraformPackage;
  private Path tfModules;
  private Path tfRoot;
  private Properties properties;

  /**
   * Sets up dependencies for TerraformPackage.
   * @throws URISyntaxException
   */
  @Before
  public void setup() throws URISyntaxException {
    this.tfRoot = Paths.get(this.getClass().getResource("/tf/root").toURI());
    this.tfModules = Paths.get(this.getClass().getResource("/tfmodules").toURI());
    this.targetTfRootModule = Paths.get("target/tf-root-module");
    this.terraformPackage = new TerraformPackage(project);
    this.properties = new Properties();
    this.properties.put(TerraformPackageParams.tfRootDir.toString(), tfRoot.toString());
    this.properties.put(TerraformPackageParams.tfModulesDir.toString(), tfModules.toString());
  }

  @Test
  public void packageWithFatZipPackagesTfModulesInsideTfRootInTheTargetDir() throws IOException, TerraformException {
    properties.put(TerraformPackageParams.fat.toString(), "true");
    String response = this.terraformPackage.execute(properties);

    Assert.assertEquals(response,
        String.format("Created zip '%1$s'",
        Paths.get(TerraformPackage.targetDir)
            .resolve(String.format("%1$s-%2$s.zip", project.getArtifactId(), project.getVersion())).toString()));
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

    //validate that the path got changed in the Terraform source
    Path targetMainTf = Paths.get(TerraformPackage.targetDir, TerraformPackage.targetTfRootDir, "main.tf");
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(targetMainTf)))) {
      String tfModulesString = Paths.get(tfModules.getFileName().toString()).toString();
      int count = 0;
      while (reader.ready()) {
        String line = reader.readLine();
        if (line.trim().startsWith("source") && line.contains(String.format("\"%1$s/", tfModulesString))) count++;
      }
      Assert.assertEquals(1, count);
    }
  }

  @Test
  public void packageGoalWithNoFatZipPackagesOnlyTfRootContentsInTheTargetDir() throws TerraformException, IOException {
    this.terraformPackage.execute(properties);

    Assert.assertEquals(1, this.targetTfRootModule.toFile().listFiles().length);
    Assert.assertEquals("main.tf",
        Files.walk(this.targetTfRootModule, 1)
        .filter(path -> !path.toFile().isDirectory())
        .findAny()
        .get().getFileName().toString());
  }
}
