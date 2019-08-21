package com.deliveredtechnologies.terraform;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tests for TerraformUtils.
 */
public class TerraformUtilsTest {

  @Test
  public void getDefaultTfModulesDirGetsTheDefaultTfModulesDirAsPath() {
    Path tfModulesPath = TerraformUtils.getDefaultTfModulesDir();
    Assert.assertEquals(Paths.get("src/main/.tfmodules"), tfModulesPath);
  }

  @Test
  public void getTerraformRootModuleDirReturnsTheDefaultDirWhenTheTfSourceDirIsNotFound() throws IOException {
    Path tfRootModulePath = TerraformUtils.getDefaultTerraformRootModuleDir();
    Assert.assertEquals(Paths.get("."), tfRootModulePath);
  }

  @Test
  public void getTerraformRootModuleDirReturnsTheRootModuleDirRelativeToTheSourceDir() throws IOException, TerraformException {
    Path testModule = Paths.get("src", "main", "tf", "test");
    try {
      FileUtils.copyDirectory(
          Paths.get("src", "test", "resources", "tf_initialized", "root").toFile(),
          testModule.toFile()
      );
      Path tfRootModulePath = TerraformUtils.getTerraformRootModuleDir("test");
      Assert.assertEquals(Paths.get("src", "main", "tf", "test"), tfRootModulePath);
    } finally {
      List<Path> paths = Files.walk(testModule).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
      paths.forEach(path -> path.toFile().delete());
    }
  }

  @Test
  public void getTerraformExampleModuleDirReturnsTheRootModuleDirRelativeToTheTfExamplesDir() throws IOException, TerraformException {
    try {
      FileUtils.copyDirectory(
          Paths.get("src", "test", "resources", "tf_initialized", "root").toFile(),
          Paths.get("src", "main", "tf-examples", "test").toFile()
      );
      Path tfRootModulePath = TerraformUtils.getTerraformRootModuleDir("tf-examples/test");
      Assert.assertEquals(Paths.get("src", "main", "tf-examples", "test"), tfRootModulePath);
    } finally {
      FileUtils.forceDelete(Paths.get("src", "main", "tf-examples").toFile());
    }
  }
}
