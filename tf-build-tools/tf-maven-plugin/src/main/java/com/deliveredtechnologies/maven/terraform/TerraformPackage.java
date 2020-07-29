package com.deliveredtechnologies.maven.terraform;

import com.deliveredtechnologies.maven.io.Compressable;
import com.deliveredtechnologies.maven.io.CompressableGZipTarFile;
import com.deliveredtechnologies.maven.io.CompressableZipFile;
import com.deliveredtechnologies.maven.logs.MavenSlf4jAdapter;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import com.deliveredtechnologies.terraform.api.TerraformOperation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * API for packaging a Terraform configuration as a zip.
 */
public class TerraformPackage implements TerraformOperation<String> {

  static final String targetDir = "target";
  static final String targetTfRootDir = "tf-root-module";
  static final List<String> excludedFiles = Arrays.asList(new String[] {".terraform", "terraform.tfstate", "terraform.tfstate.backup", ".terraform.tfstate.lock.info"});

  private Logger logger;
  private MavenProject project;

  //TODO: Consider using intsance variables instead.
  enum TerraformPackageParams {
    tfModulesDir,
    tfRootDir,
    tfVarFiles,
    fatTar;
  }

  public TerraformPackage(MavenProject project, Logger logger) {
    this.logger = logger;
    this.project = project;
  }

  public TerraformPackage(MavenProject project, Log log) {
    this(project, new MavenSlf4jAdapter(log));
  }

  public TerraformPackage(MavenProject project) {
    this(project, LoggerFactory.getLogger(TerraformPackage.class));
  }

  /**
   * Packages a Terraform config as a zip file.
   * <p>
   *   Valid Properties:
   *   tfModulesDir - the directory where Terraform Modules dependencies (i.e. Maven Terraform dependencies) are stored; defaults to src/main/.tfmodules<br>
   *   tfRootDir - the directory containing the Terraform root module configuration; defaults to src/main/tf/{first dir found}<br>
   *   fatTar - if "true", then tar.gz contains the Terraform code for all Maven dependencies ( valid values are "true" or "false"); defaults to "false"
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
      String tfVarFiles = properties.getProperty(TerraformPackageParams.tfVarFiles.toString());

      Object isFatTarObj = properties.getOrDefault(TerraformPackageParams.fatTar.toString(), false);
      boolean isFatTar = isFatTarObj instanceof Boolean ? (Boolean)isFatTarObj : Boolean.valueOf(isFatTarObj.toString());

      Path tfModulesPath = !StringUtils.isEmpty(tfModulesDir)
          ? Paths.get(tfModulesDir)
          : TerraformUtils.getDefaultTfModulesDir();
      logger.debug(String.format("tfModulesPath is %1$s", tfModulesPath.toAbsolutePath().toString()));

      if (targetTfRootPath.toFile().exists()) FileUtils.forceDelete(targetTfRootPath.toFile());
      FileUtils.forceMkdir(targetTfRootPath.toFile());

      File tfSourceFile = Paths.get("src", "main", "tf").toFile();

      Path tfRootPath = !StringUtils.isEmpty(tfRootDir)
          ? TerraformUtils.getTerraformRootModuleDir(tfRootDir)
          : (tfSourceFile.exists() && tfSourceFile.isDirectory() && tfSourceFile.listFiles().length > 1
          ? tfSourceFile.toPath()
          : TerraformUtils.getDefaultTerraformRootModuleDir());
      logger.debug(String.format("tfRootPath is %1$s", tfRootPath.toAbsolutePath().toString()));

      copyTfRootDir(tfRootPath, targetTfRootPath);

      copyTfVarFiles(tfRootPath, tfVarFiles, targetTfRootPath);

      if (isFatTar) {
        updateDependenciesInTfRoot(targetTfRootPath, tfModulesPath, tfRootPath);
        //copy tfmodules directory into tfRoot directory; i.e. {targetTfRootDir}/{tfModulesDir} if it exists
        if (tfModulesPath.toFile().exists() && tfModulesPath.toFile().isDirectory()) {
          FileUtils.copyDirectory(tfModulesPath.toFile(), targetTfRootPath.resolve(tfModulesPath.getFileName().toString()).toFile());
        }
        return String.format("Created fatTar gzipped tar file '%1$s'", createGZippedTar(targetPath, targetTfRootPath));
      }
      return String.format("Created zip file '%1$s'", createZip(targetPath, targetTfRootPath));
    } catch (IOException e) {
      throw new TerraformException(e.getMessage(), e);
    }
  }

  private void copyTfRootDir(Path sourceDir, Path targetDir) throws IOException {
    List<Path> files = Files.walk(sourceDir, 1)
        .filter(path -> !path.equals(sourceDir))
        .collect(Collectors.toList());

    for (Path file : files.stream().filter(f -> !excludedFiles.contains(f.getFileName().toString())).collect(Collectors.toList())) {
      if (file.toFile().isDirectory()) {
        FileUtils.copyDirectory(file.toFile(), targetDir.resolve(file.getFileName().toString()).toFile(), new FileFilter() {
          @Override
          public boolean accept(File pathname) {
            return excludedFiles.stream().noneMatch(excludedFile -> pathname.getName().endsWith(excludedFile));
          }
        });
      } else {
        Files.copy(file, targetDir.resolve(file.getFileName().toString()));
      }
    }
  }

  private void copyTfVarFiles(Path sourceDir, String files, Path targetDir) throws IOException {
    List<Path> filePaths = StringUtils.isEmpty(files)
        ? new ArrayList<>()
        : Arrays.stream(files.split(","))
        .map(file -> sourceDir.resolve(file.trim()))
        .collect(Collectors.toList());
    logger.debug(String.format("tfVarFiles is %1$s", filePaths));

    for (Path filePath : filePaths) {
      Files.copy(filePath, targetDir.resolve(filePath.getFileName().toString().replaceAll(".tfvars", "").concat(".auto.tfvars")));
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
            fileWriter.write(fileReader.readLine().replace(tfRootToModulesRelativePath.replace('\\', '/'), tfModulesPath.getFileName().toString()));
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

  private String createGZippedTar(Path targetDir, Path targetTfRootPath) throws IOException {
    String tarFilename = targetDir.resolve(
        String.format("%1$s-%2$s.tar.gz", project.getArtifactId(), project.getVersion())).toString();
    Compressable compressor = new CompressableGZipTarFile(tarFilename, targetTfRootPath);
    Files.walk(targetTfRootPath, 1)
      .filter(path -> !path.equals(targetTfRootPath))
      .forEach(compressor::addToCompressedFile);
    compressor.compress();
    return tarFilename;
  }

}
