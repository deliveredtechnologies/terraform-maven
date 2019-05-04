package com.deliveredtechnologies.maven.tf;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * API for cleaning up 'terraform' files.
 */
public class TerraformClean implements TerraformOperation<String> {

  private Path tfRootModulePath;
  private Path tfModulesPath;

  /**
   * Constructor instantiates TerraformClean.
   * @param tfModules     the tfModules directory; defaults to src/main/.tfmodules if empty
   * @param tfRootModule  the Terraform root module directory; defaults to src/main/tf/{first dir} if empty
   * @throws IOException
   */
  public TerraformClean(String tfModules, String tfRootModule) throws IOException {
    this.tfModulesPath = StringUtils.isEmpty(tfModules)
      ? TerraformUtils.getDefaultTfModulesDir()
      : Paths.get(tfModules);
    this.tfRootModulePath = StringUtils.isEmpty(tfRootModule)
      ? TerraformUtils.getTerraformRootModuleDir()
      : Paths.get(tfModules);
  }

  @Override
  public String execute(Properties properties) throws TerraformException {
    try {
      StringBuilder response = new StringBuilder("Deleting...\n");
      FileUtils.forceDelete(this.tfModulesPath.toFile());
      List<Path> terraformFiles = Files.walk(this.tfRootModulePath.getParent())
          .filter(path -> path.getFileName().toString().contains("terraform"))
          .collect(Collectors.toList());
      for (Path file : terraformFiles) {
        response.append(file.toString()).append('\n');
        FileUtils.forceDelete(file.toFile());
      }
      return response.toString();
    } catch (IOException e) {
      throw new TerraformException(e);
    }
  }
}
