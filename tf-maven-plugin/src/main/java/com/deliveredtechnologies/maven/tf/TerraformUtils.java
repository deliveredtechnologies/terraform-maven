package com.deliveredtechnologies.maven.tf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Static utilities for Terraform related default Paths and stuff.
 */
public class TerraformUtils {
  private TerraformUtils() { }

  /**
   * Gets the Terraform root module directory `src/main/tf/{root module dir}`.
   * @return  a Path corresponding to the Terraform root module directory
   * @throws IOException
   */
  public static Path getTerraformRootModuleDir() throws IOException {
    Path tfSourcePath = Paths.get("src", "main", "tf");
    if (tfSourcePath.toFile().exists() && tfSourcePath.toFile().isDirectory()) {
      return Files.walk(Paths.get("src", "main", "tf"), 1)
          .filter(path -> path.getFileName().toString() != "tf")
          .findFirst().orElseThrow(() -> new IOException("Terraform root module not found"));
    }
    return Paths.get(".");
  }

  /**
   * Gets the default Terraform dependency module path `/src/main/.tfmodules`.
   * @return
   */
  public static Path getDefaultTfModulesDir() {
    return Paths.get("src", "main", ".tfmodules");
  }
}
