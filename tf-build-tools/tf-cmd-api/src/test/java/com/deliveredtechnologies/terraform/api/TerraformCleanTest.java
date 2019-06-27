package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.terraform.TerraformException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Tests for TerraformClean.
 */
public class TerraformCleanTest {
  Path tfInitWorkingDir;
  Path tfModulesWorkingDir;

  /**
   * Setup some terraform working directories simulating initialized terraform configs.
   * @throws URISyntaxException
   * @throws IOException
   */
  @Before
  public void setup() throws URISyntaxException, IOException {
    Path tfInitDir = Paths.get("target", "test-classes", "tf_initialized");
    Path tfModulesDir = Paths.get(this.getClass().getResource("/tfmodules").toURI());
    this.tfInitWorkingDir = tfModulesDir.resolveSibling("tf_initialized_working");
    this.tfModulesWorkingDir = tfModulesDir.resolveSibling("tfModules_working");
    this.tfInitWorkingDir.toFile().mkdir();

    FileUtils.copyDirectory(tfModulesDir.toFile(), tfModulesWorkingDir.toFile());

    List<Path> tfInitDirContents = Files.walk(tfInitDir, 1)
        .filter(path -> !path.equals(tfInitDir))
        .collect(Collectors.toList());
    for (Path path : tfInitDirContents) {
      if (path.toFile().isDirectory()) {
        FileUtils.copyDirectory(path.toFile(), this.tfInitWorkingDir.resolve(path.getFileName().toString()).toFile());
        this.tfInitWorkingDir.resolve(path.getFileName().toString()).resolve("terraform.tfstate").toFile().createNewFile();
        this.tfInitWorkingDir.resolve(path.getFileName().toString()).resolve(".terraform").toFile().mkdir();
      } else {
        Files.copy(path, this.tfInitWorkingDir.resolve(path.getFileName().toString()));
      }
    }
  }

  @Test
  public void terraformCleanCleansUpTerraformStateFilesAndTerraformWorkingDirectory() throws IOException, TerraformException {
    TerraformClean terraformClean = new TerraformClean(
        this.tfModulesWorkingDir.toString(),
        this.tfInitWorkingDir.resolve("root").toString());

    terraformClean.execute(new Properties());

    Assert.assertFalse(this.tfModulesWorkingDir.toFile().exists());

    Assert.assertFalse(Files.walk(this.tfInitWorkingDir)
        .filter(path -> path.getFileName().toString().endsWith(".terraform") || path.getFileName().toString().endsWith(".tfstate"))
        .findAny().isPresent());
  }

  @Test(expected = TerraformException.class)
  public void terraformCleanThrowsTerraformExceptionOnInvalidPath() throws IOException, TerraformException {
    TerraformClean terraformClean = new TerraformClean(
        this.tfModulesWorkingDir.resolveSibling("invalid").toString(),
        this.tfInitWorkingDir.toString());

    terraformClean.execute(new Properties());
  }

  /**
   * Clean up working directories.
   * @throws IOException
   */
  @After
  public void teardown() throws IOException {
    FileUtils.forceDelete(tfInitWorkingDir.toFile());
  }
}
