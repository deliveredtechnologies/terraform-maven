package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;

/**
 * API for cleaning up 'terraform' files.
 */
public class TerraformClean implements TerraformOperation<String> {

  private Path tfRootModulePath;
  private Path tfModulesPath;

  /**
   * Constructor instantiates TerraformClean.
   * @param tfModules     the tfModules directory; defaults to src/main/.tfmodules if empty
   * @param tfRootModule  the Terraform root module directory; defaults to src/main/terraform/{first dir} if empty
   * @throws IOException
   */
  public TerraformClean(String tfModules, String tfRootModule) throws IOException, TerraformException {
    this.tfModulesPath = StringUtils.isEmpty(tfModules)
      ? TerraformUtils.getDefaultTfModulesDir()
      : Paths.get(tfModules);
    this.tfRootModulePath = (StringUtils.isEmpty(tfRootModule)
      ? TerraformUtils.getDefaultTerraformRootModuleDir()
      : TerraformUtils.getTerraformRootModuleDir(tfRootModule)).getParent();
  }

  @Override
  public String execute(Properties properties) throws TerraformException {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    try {
      if (!this.tfRootModulePath.toFile().exists()) {
        throw new IOException(this.tfRootModulePath.toString() + " does not exist!");
      }

      //delete .tfmodules
      StringBuilder response = new StringBuilder("Deleting...\n");
      if (this.tfModulesPath.toFile().exists()) {
        response.append(this.tfModulesPath.toString()).append('\n');
        FileUtils.forceDelete(this.tfModulesPath.toFile());
      }

      //delete terraform files in terraform root module
      List<Path> terraformModuleFiles = recursivelyWalk(this.tfRootModulePath.getParent(), path -> path.getParent().toAbsolutePath().toString().endsWith(String.format(".terraform%1$smodules", File.separator)) || path.getFileName().toString().contains(".tfstate"));

      for (Path file : terraformModuleFiles) {

        //if it's Windows, remove the hard link to the local Terraform module
        if (isWindows && file.getParent().endsWith(String.format(".terraform%1$smodules", File.separator))) {
          try {
            response.append(file.toString()).append('\n');
            file.toFile().delete();
          } catch (Exception e) {
            response.append("**failed to delete ").append(file.toString()).append('\n');
          }
        }

        //if it's not running on Windows or if it is and the file/dir is still there, force delete
        if (file.toFile().exists()) {
          response.append(file.toString()).append('\n');
          FileUtils.forceDelete(file.toFile());
        }
      }

      //delete provisioned resources
      if () {
        //terraform apply call
      }

      //delete .terraform directories
      Files.walk(this.tfRootModulePath.getParent()).filter(path -> path.toString().endsWith(".terraform"))
          .map(Path::toFile)
          .forEach(file -> {
            response.append(file.toString()).append('\n');
            try {
              FileUtils.forceDelete(file);
            } catch (Exception ex) {
              response.append("**failed to delete ").append(file.toString()).append('\n');
            }
          });
      return response.toString();
    } catch (IOException e) {
      throw new TerraformException(e);
    }
  }

  /**
   * Recursive implmentation of Files.walk because Windows throws errors on junction files, which Terraform uses to reference modules with relative paths
   * @param path      the root path
   * @param condition the condition, when if true, adds the child path to the collection returned
   * @return          the collection of Paths in the directory tree matching the condition specified
   */
  private List<Path> recursivelyWalk(Path path, Predicate<Path> condition) {
    List<Path> pathCollection = new ArrayList<>();
    try {
      for (File file : path.toFile().listFiles()) {
        pathCollection.addAll(recursivelyWalk(file.toPath(), condition));
      }
    } catch (Exception ex) {
      //throw the exception away; this will happen when io and nio is out of synch (i.e. Windows junction files)
    }

    if (condition.test(path)) {
      pathCollection.add(path);
    }

    return pathCollection;
  }
}

