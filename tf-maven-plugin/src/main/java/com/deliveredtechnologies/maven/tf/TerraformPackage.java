package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.io.Compressable;
import com.deliveredtechnologies.maven.io.CompressableZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * API for packaging a Terraform configuration as a zip.
 */
public class TerraformPackage implements TerraformOperation<String> {

  static final String targetDir = "target";
  static final String targetTfRootDir = "tf-root-module";

  private MavenProject project;

  //TODO: Consider using intsance variables instead.
  enum TerraformPackageParams {
    tfModulesDir,
    tfRootDir,
    fatZip;
  }

  public TerraformPackage(MavenProject project) {
    this.project = project;
  }

  /**
   * Packages a Terraform config as a zip file.
   * <p>
   *   Valid Properties:
   *   tfModulesDir - the directory where Terraform Modules dependencies (i.e. Maven Terraform dependencies) are stored; defaults to src/main/.tfmodules<br/>
   *   tfRootDir - the directory containing the Terraform root module configuration; defaults to src/main/tf/{first dir found}<br/>
   *   isFatZip - if "true", then zip contains the Terraform code for all Maven dependencies ( valid values are "true" or "false"); defaults to "false"
   * </p>
   * @param properties  property options for packaging a Terraform configuration
   * @return            String message with the zip filename included
   * @throws TerraformException
   */
  @Override
  public String execute(Properties properties) throws TerraformException {
    try {
      Path targetPath = Paths.get(targetDir);
      Path targetTfRootPath = targetPath.resolve(targetTfRootDir);
      String tfModulesDir = properties.getProperty(TerraformPackageParams.tfModulesDir.toString());
      String tfRootDir = properties.getProperty(TerraformPackageParams.tfRootDir.toString());
      boolean isFatZip = Boolean.valueOf(properties.getProperty(TerraformPackageParams.fatZip.toString(), "false"));

      Path tfModulesPath = !StringUtils.isEmpty(tfModulesDir)
          ? Paths.get(tfModulesDir)
          : TerraformUtils.getDefaultTfModulesDir();
      Path tfRootPath = !StringUtils.isEmpty(tfRootDir)  ? Paths.get(tfRootDir) : TerraformUtils.getTerraformRootModuleDir();

      //copy tfRoot directory to target
      if (targetTfRootPath.toFile().exists()) FileUtils.forceDelete(targetTfRootPath.toFile());

      targetTfRootPath.toFile().mkdir();
      List<Path> tfRootDirFiles = Files.walk(tfRootPath, 1)
          .filter(path -> !path.equals(tfRootPath))
          .collect(Collectors.toList());

      for (Path file : tfRootDirFiles) {
        if (file.toFile().isDirectory()) {
          FileUtils.copyDirectory(file.toFile(), targetTfRootPath.resolve(file.getFileName().toString()).toFile());
        } else {
          Files.copy(file, targetTfRootPath.resolve(file.getFileName().toString()));
        }
      }

      if (isFatZip) {
        updateDependenciesInTfRoot(targetTfRootPath, tfModulesPath, tfRootPath);
        //copy tfmodules directory into tfRoot directory; i.e. {targetTfRootDir}/{tfModulesDir} if it exists
        if (tfModulesPath.toFile().exists() && tfModulesPath.toFile().isDirectory()) {
          FileUtils.copyDirectory(tfModulesPath.toFile(), targetTfRootPath.resolve(tfModulesPath.getFileName().toString()).toFile());
        }
      }
      return String.format("Created zip '%1$s'", createZip(targetPath, targetTfRootPath));
    } catch (IOException e) {
      throw new TerraformException(e.getMessage(), e);
    }
  }

  private void updateDependenciesInTfRoot(Path targetTfRootPath, Path tfModulesPath, Path tfRootPath) throws IOException {
    //replace all ../../.tfmodules with .tfmodules
    String tfRootToModulesRelativePath = tfRootPath.relativize(tfModulesPath).toString();
    List<Path> filesInTargetTfRoot = Files.walk(targetTfRootPath)
        .filter(path -> !path.toFile().isDirectory())
        .collect(Collectors.toList());

    for (Path file : filesInTargetTfRoot) {
      try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile())))) {
        try (BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.resolveSibling(file.getFileName().toString() + ".working").toFile())))) {
          while (fileReader.ready()) {
            fileWriter.write(fileReader.readLine().replace(tfRootToModulesRelativePath, tfModulesPath.getFileName().toString()));
            fileWriter.newLine();
          }
        }
      }
      Files.delete(file);
      Files.move(file.resolveSibling(file.getFileName().toString() + ".working"), file);
    }
  }

  private String createZip(Path targetDir, Path targetTfRootPath) throws IOException {
    String zipFilename = targetDir.resolve(
        String.format("%1$s-%2$s.zip", project.getArtifactId(), project.getVersion())).toString();
    Compressable compressor = new CompressableZipFile(zipFilename);
    Files.walk(targetTfRootPath, 1)
      .filter(path -> !path.equals(targetTfRootPath))
      .forEach(compressor::addToCompressedFile);
    compressor.compress();
    return zipFilename;
  }
}
