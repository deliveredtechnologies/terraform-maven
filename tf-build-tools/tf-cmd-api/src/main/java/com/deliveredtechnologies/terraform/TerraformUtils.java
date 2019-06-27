package com.deliveredtechnologies.terraform;

import com.deliveredtechnologies.terraform.TerraformException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Static utilities for Terraform related default Paths and stuff.
 */
public class TerraformUtils {
  private TerraformUtils() { }

  /**
   * Gets the default Terraform root module directory `src/main/terraform/{root module dir}`.
   * @return  a Path corresponding to the Terraform root module directory
   * @throws IOException
   */
  public static Path getDefaultTerraformRootModuleDir() throws IOException {
    Path tfSourcePath = Paths.get("src", "main", "tf");
    if (tfSourcePath.toFile().exists() && tfSourcePath.toFile().isDirectory()) {
      return Files.walk(Paths.get("src", "main", "tf"), 2)
          .filter(path -> !path.toFile().isDirectory())
          .filter(path -> path.getFileName().toString().endsWith(".tf"))
          .findFirst().orElseThrow(() -> new IOException("Terraform root module not found")).getParent();
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
    if (tfmodule.contains("/")) { //relative or absolute path
      path = Paths.get(tfmodule);
    } else {
      path = Paths.get("src", "main", "tf", tfmodule);
    }
    if (!path.toFile().exists() || !Arrays.stream(path.toFile().listFiles()).anyMatch(p -> p.isFile() && p.getName().endsWith(".tf"))) {
      throw new TerraformException(String.format("%1$s does not contain any Terraform (*.terraform) files!", tfmodule));
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
