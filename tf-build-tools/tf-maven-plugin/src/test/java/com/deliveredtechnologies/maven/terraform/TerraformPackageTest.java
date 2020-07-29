package com.deliveredtechnologies.maven.terraform;

import com.deliveredtechnologies.maven.io.IterableZipInputStream;
import com.deliveredtechnologies.maven.terraform.TerraformPackage.TerraformPackageParams;

import com.deliveredtechnologies.terraform.TerraformException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;

/**
 * Tests for TerraformPackage.
 */
public class TerraformPackageTest {
  private MavenProject project = new MavenProject();
  private Path targetTfRootModule;
  private TerraformPackage terraformPackage;
  private Path tfModules;
  private Path tfRoot;
  private List<Path> tfVarFiles;
  private String tfVars;
  private Properties properties;

  /**
   * Sets up dependencies for TerraformPackage.
   * @throws URISyntaxException
   */
  @Before
  public void setup() throws URISyntaxException, IOException {
    this.tfRoot = Paths.get(this.getClass().getResource("/tf/root").toURI());
    this.tfModules = Paths.get(this.getClass().getResource("/tfmodules").toURI());
    this.tfVarFiles = new ArrayList<>();
    tfVarFiles.add(Paths.get(this.getClass().getResource("/tf/root/variables/dev1.tfvars").toURI()));
    tfVarFiles.add(Paths.get(this.getClass().getResource("/tf/root/variables/dev2.tfvars").toURI()));
    this.tfVars = "key1=value1, key2=value2";
    this.targetTfRootModule = Paths.get("target/tf-root-module");
    this.terraformPackage = new TerraformPackage(project);
    this.properties = new Properties();
    this.properties.put(TerraformPackageParams.tfRootDir.toString(), tfRoot.toString());
    this.properties.put(TerraformPackageParams.tfModulesDir.toString(), tfModules.toString());
    this.properties.put(TerraformPackageParams.tfVars.toString(), tfVars);
    if (targetTfRootModule.toFile().exists()) FileUtils.forceDelete(targetTfRootModule.toFile());
  }

  @Test
  public void packageWithFatTarPackagesTfModulesInsideTfRootInTheTargetDir() throws IOException, TerraformException {
    properties.put(TerraformPackageParams.fatTar.toString(), true);
    this.properties.put(TerraformPackageParams.tfVarFiles.toString(), tfVarFiles.toString().replaceAll("(\\[|\\])", ""));
    String response = this.terraformPackage.execute(properties);
    Path tarFilePath = Paths.get(TerraformPackage.targetDir)
        .resolve(String.format("%1$s-%2$s.tar.gz", project.getArtifactId(), project.getVersion()));

    Assert.assertEquals(response, String.format("Created fatTar gzipped tar file '%1$s'", tarFilePath.toString()));
    Assert.assertEquals(5, this.targetTfRootModule.toFile().listFiles().length);
    Assert.assertEquals("dev1.auto.tfvars,dev2.auto.tfvars,main.tf",
        Files.walk(this.targetTfRootModule, 1)
        .filter(path -> !path.toFile().isDirectory())
        .map(path -> path.getFileName().toString())
          .sorted()
        .collect(Collectors.joining(",")));
    Assert.assertEquals(2, Files.walk(this.targetTfRootModule.resolve(tfModules.getFileName().toString()).resolve("test-module"), 1)
        .filter(path -> path.getFileName().toString().equals("main.tf")
        || path.getFileName().toString().equals("variables.tf"))
        .count());

    //validate that the path got changed in the Terraform source
    Path targetMainTf = Paths.get(TerraformPackage.targetDir, TerraformPackage.targetTfRootDir, "main.tf");
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(targetMainTf)))) {
      String tfModulesString = Paths.get(tfModules.getFileName().toString()).toString();
      int count = 0;
      while (reader.ready()) {
        String line = reader.readLine();
        if (line.trim().startsWith("source") && line.contains(String.format("\"%1$s/", tfModulesString))) count++;
      }
      Assert.assertEquals(1, count);
    }

    try (TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(tarFilePath.toString())))) {
      int count = 0;
      TarArchiveEntry entry;
      Set<String> tarEntryNames = new HashSet<>();
      while ((entry = tarArchiveInputStream.getNextTarEntry()) != null) {
        count++;
        tarEntryNames.add(entry.getName());
      }

      Assert.assertEquals(10, count);
      Assert.assertTrue(tarEntryNames.contains("main.tf"));
      Assert.assertTrue(tarEntryNames.contains("dev1.auto.tfvars"));
      Assert.assertTrue(tarEntryNames.contains("dev2.auto.tfvars"));
      Assert.assertTrue(tarEntryNames.contains("tfmodules/test-module/main.tf"));
      Assert.assertTrue(tarEntryNames.contains("tfmodules/test-module/variables.tf"));
    }
  }

  @Test
  public void packageGoalWithNoFatZipPackagesOnlyTfRootContentsInTheTargetDir() throws TerraformException, IOException {
    Path zipFilePath = Paths.get(TerraformPackage.targetDir)
        .resolve(String.format("%1$s-%2$s.zip", project.getArtifactId(), project.getVersion()));
    this.properties.put(TerraformPackageParams.tfVarFiles.toString(), "variables/dev1.tfvars,variables/dev2.tfvars");
    String response = this.terraformPackage.execute(properties);

    Assert.assertEquals(response, String.format("Created zip file '%1$s'", zipFilePath.toString()));
    Assert.assertEquals(4, this.targetTfRootModule.toFile().listFiles().length);
    Assert.assertEquals("dev1.auto.tfvars,dev2.auto.tfvars,main.tf",
        Files.walk(this.targetTfRootModule, 1)
          .filter(path -> !path.toFile().isDirectory())
          .map(path -> path.getFileName().toString())
          .sorted()
          .collect(Collectors.joining(",")));

    try (IterableZipInputStream zipStream = new IterableZipInputStream(new FileInputStream(zipFilePath.toFile()))) {
      int count = 0;
      Set<String> zipEntryNames = new HashSet<>();
      for (ZipEntry entry : zipStream) {
        count++;
        zipEntryNames.add(entry.getName());
      }

      Assert.assertEquals(6, count);
      Assert.assertTrue(zipEntryNames.contains("main.tf"));
      Assert.assertTrue(zipEntryNames.contains("dev1.auto.tfvars"));
      Assert.assertTrue(zipEntryNames.contains("dev2.auto.tfvars"));
    }
  }

  @Test
  public void packageGoalWithNoFatZipAndMultipleModulesPackagesBothTfSourceModulesInTheTargetDir() throws TerraformException, IOException, URISyntaxException {
    properties.remove(TerraformPackageParams.tfRootDir.toString());
    this.tfRoot = Paths.get(this.getClass().getResource("/tf_initialized").toURI());
    FileUtils.copyDirectory(this.tfRoot.toFile(), Paths.get("src", "main", "tf").toFile());
    Path zipFilePath = Paths.get(TerraformPackage.targetDir)
        .resolve(String.format("%1$s-%2$s.zip", project.getArtifactId(), project.getVersion()));
    String response = this.terraformPackage.execute(properties);

    Assert.assertEquals(response, String.format("Created zip file '%1$s'", zipFilePath.toString()));
    Assert.assertEquals(2, this.targetTfRootModule.toFile().listFiles().length);
    Assert.assertTrue(Arrays.stream(this.targetTfRootModule.toFile().listFiles()).anyMatch(file -> file.getName().equals("root")));
    Assert.assertTrue(Arrays.stream(this.targetTfRootModule.toFile().listFiles()).anyMatch(file -> file.getName().equals("other")));

    try (IterableZipInputStream zipStream = new IterableZipInputStream(new FileInputStream(zipFilePath.toFile()))) {
      int count = 0;
      Set<String> zipEntryNames = new HashSet<>();
      for (ZipEntry entry : zipStream) {
        count++;
        zipEntryNames.add(entry.getName());
      }

      Assert.assertEquals(4, count);
      Assert.assertTrue(zipEntryNames.contains("root/"));
      Assert.assertTrue(zipEntryNames.contains("other/"));
      Assert.assertTrue(zipEntryNames.contains("root/main.tf"));
      Assert.assertTrue(zipEntryNames.contains("other/main.tf"));
    }
  }

}
