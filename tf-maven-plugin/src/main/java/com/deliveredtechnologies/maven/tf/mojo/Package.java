package com.deliveredtechnologies.maven.tf.mojo;

import com.deliveredtechnologies.maven.io.Compressable;
import com.deliveredtechnologies.maven.io.CompressableZipFile;
import com.deliveredtechnologies.maven.tf.TerraformUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
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
import java.util.stream.Collectors;

/**
 * Mojo tfn:package goal.
 */
@Mojo(name = "package")
public class Package extends AbstractMojo {
  @Parameter(property = "tfModulesDir")
  String tfModulesDir;

  @Parameter(property = "tfRootDir")
  String tfRootDir;

  @Parameter(property = "fatZip", defaultValue = "false")
  boolean isFatZip;

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      Path targetDir = Paths.get("target");
      Path tfModulesPath = !StringUtils.isEmpty(tfModulesDir) ? Paths.get(tfModulesDir) : TerraformUtils.getDefaultTfModulesDir();
      Path tfRootPath = !StringUtils.isEmpty(tfRootDir)  ? Paths.get(tfRootDir) : TerraformUtils.getTerraformRootModuleDir();

      //copy tfRoot directory to target
      Path targetTfRootDir = targetDir.resolve("tf-root-module");
      if (targetTfRootDir.toFile().exists()) FileUtils.forceDelete(targetTfRootDir.toFile());

      targetTfRootDir.toFile().mkdir();
      List<Path> tfRootDirFiles = Files.walk(tfRootPath, 1)
          .filter(path -> !path.equals(tfRootPath))
          .collect(Collectors.toList());

      for (Path file : tfRootDirFiles) {
        if (file.toFile().isDirectory()) {
          FileUtils.copyDirectory(file.toFile(), targetTfRootDir.resolve(file.getFileName().toString()).toFile());
        } else {
          Files.copy(file, targetTfRootDir.resolve(file.getFileName().toString()));
        }
      }

      if (isFatZip) {
        getLog().info("Packaging files for fat zip...");
        updateDependenciesInTfRoot(targetTfRootDir, tfModulesPath, tfRootPath);
        //copy tfmodules directory into tfRoot directory; i.e. {targetTfRootDir}/{tfModulesDir}
        FileUtils.copyDirectory(tfModulesPath.toFile(), targetTfRootDir.resolve(tfModulesPath.getFileName().toString()).toFile());
      }
      createZip(targetDir, targetTfRootDir);
    } catch (IOException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private void updateDependenciesInTfRoot(Path targetTfRootDir, Path tfModulesPath, Path tfRootPath) throws IOException {
    //replace all ../../.tfmodules with .tfmodules
    String tfRootToModulesRelativePath = tfRootPath.relativize(tfModulesPath).toString();
    List<Path> filesInTargetTfRoot = Files.walk(targetTfRootDir)
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

  private void createZip(Path targetDir, Path targetTfRootDir) throws IOException {
    Compressable compressor = new CompressableZipFile(targetDir.resolve(
        String.format("%1$s-%2$s.zip", project.getArtifactId(), project.getVersion())).toString());
    Files.walk(targetTfRootDir, 1)
      .filter(path -> !path.equals(targetTfRootDir))
      .forEach(compressor::addToCompressedFile);
    compressor.compress();
    getLog().info("Successfully created zip: " + compressor.compress().toString());
  }
}
