package com.deliveredtechnologies.maven.tf;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TerraformUtilsTest {

  @Test
  public void getDefaultTfModulesDirGetsTheDefaultTfModulesDirAsPath() {
    Path tfModulesPath = TerraformUtils.getDefaultTfModulesDir();
    Assert.assertEquals(Paths.get("src/main/.tfmodules"), tfModulesPath);
  }
}
