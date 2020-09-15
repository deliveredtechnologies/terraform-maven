package com.deliveredtechnologies.terraform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Static utilities for Terraform related default Paths and stuff.
 */
public class TerraformUtils {
  private TerraformUtils() { }

  private static final Logger LOGGER = LoggerFactory.getLogger(TerraformUtils.class);

  /**
   * Gets the default Terraform root module directory `src/main/terraform/{root module dir}`.
   * @return  a Path corresponding to the Terraform root module directory
   * @throws IOException
   */
  public static Path getDefaultTerraformRootModuleDir() throws IOException {
    try {
      Path tfSourcePath = Paths.get("src", "main", "tf");
      if (tfSourcePath.toFile().exists() && tfSourcePath.toFile().isDirectory()) {
        return Files.walk(tfSourcePath, 2)
          .filter(path -> !path.toFile().isDirectory())
          .filter(path -> path.getFileName().toString().endsWith(".tf"))
          .findFirst().orElseThrow(() -> new IOException("Terraform root module not found")).getParent();
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      LOGGER.info("Unable to determine default Terraform root module directory; using current directory.");
    }
    return Paths.get(".");
  }

  /**
   * Resolves the Path of the tfmodule, which can be an absolute or relative path or a module name under src/main/terraform.
   * @param tfmodule  an absolute or relative path or a module name under src/main/terraform
   * @return          the Path associated with the Terraform module
   * @throws IOException
   * @throws TerraformException
   */
  public static Path getTerraformRootModuleDir(String tfmodule) throws IOException, TerraformException {
    Path path;
    if (tfmodule.startsWith("tf-examples")) {
      path = Paths.get("src", "main", tfmodule);
    } else if (tfmodule.contains(File.separator)) { //relative or absolute path
      path = Paths.get(tfmodule);
    } else {
      path = Paths.get("src", "main", "tf", tfmodule);
    }
    if (!path.toFile().exists()) {
      throw new TerraformException(String.format("%1$s does not exist", tfmodule));
    }

    return path;
  }

  /**
   * Gets the default Terraform dependency module path `/src/main/.tfmodules`.
   * @return
   */
  public static Path getDefaultTfModulesDir() {
    return Paths.get("src", "main", ".tfmodules");
  }
}
