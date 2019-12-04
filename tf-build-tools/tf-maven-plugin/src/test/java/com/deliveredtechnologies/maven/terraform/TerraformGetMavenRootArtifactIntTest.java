package com.deliveredtechnologies.maven.terraform;

import com.deliveredtechnologies.maven.logs.Slf4jMavenAdapter;
import com.deliveredtechnologies.terraform.TerraformException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Integration tests for TerraformGetMavenRootArtifact.
 */
@Category(com.deliveredtechnologies.test.categories.Integration.class)
public class TerraformGetMavenRootArtifactIntTest {

  private static String workingDir = System.getProperty("user.dir");

  @Test
  public void executeInvokesGetsTheArtifactFromMavenAndExpandsItUnderTheTfWorkingDir() throws TerraformException, IOException {
    String artifact = "com.deliveredtechnologies.example.maven.tf:tf-s3-consumer:1.0";
    Path tfWorkingPath = Paths.get(this.workingDir, ".tf");

    try {
      TerraformGetMavenRootArtifact terraformGetMavenRootArtifact = new TerraformGetMavenRootArtifact(artifact, new Slf4jMavenAdapter(LoggerFactory.getLogger(this.getClass())));
      terraformGetMavenRootArtifact.execute(new Properties());
      Assert.assertTrue(tfWorkingPath.resolve("s3-consumer").toFile().exists());
      Assert.assertTrue(tfWorkingPath.getParent().resolve(".tfmodules").resolve("s3").resolve("s3_replicated_src").toFile().exists());
      Assert.assertTrue(tfWorkingPath.getParent().resolve(".tfmodules").resolve("s3").resolve("s3").toFile().exists());
    } finally {
      FileUtils.deleteDirectory(tfWorkingPath.toFile());
      FileUtils.deleteDirectory(tfWorkingPath.getParent().resolve(".tfmodules").toFile());
    }
  }
}
