package com.deliveredtechnologies.maven.terraform.mojo;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * WrapperTest class.
 *
 */
public class WrapperTest {
  Log log;
  Wrapper wrapper;

  /**
   * WrapperTest class.
   *
   */
  @Before
  public void setup() {
    log = Mockito.mock(Log.class);
    wrapper = new Wrapper();
    wrapper.setLog(log);
  }


  @Test
  public void tryHappyWrapper() throws IOException, MojoFailureException, MojoExecutionException {
    wrapper.execute();
  }

  @Test
  public void theFilesAreCreatedAndPutInsideTheTfDirectoryWithDefaults() throws MojoFailureException, MojoExecutionException, IOException {
    Path tfPath = Paths.get(".tf");
    String[] tfwFileNames = {"tfw", "tfw.cmd", "tfw.ps1", "terraform-maven.properties"};
    File tfDir = tfPath.toFile();

    try {
      if (tfDir.exists()) {
        FileUtils.forceDelete(tfDir);
      }

      wrapper.execute();

      Mockito.verify(log, Mockito.times(1)).info("Directory .tf is created");
      Assert.assertTrue(tfDir.exists());
      for (String tfwFileName : tfwFileNames) {
        Assert.assertTrue(tfPath.resolve(tfwFileName).toFile().exists());
      }

      Properties props = new Properties();
      try (InputStream fis = new FileInputStream(tfPath.resolve(tfwFileNames[3]).toFile())) {
        props.load(fis);
        Assert.assertEquals(props.getProperty("distributionSite"), "https://releases.hashicorp.com");
        Assert.assertEquals(props.getProperty("releaseDir"), "terraform");
        Assert.assertEquals(props.getProperty("releaseName"), "terraform");
        Assert.assertEquals(props.getProperty("releaseVer"), "0.12.24");
        Assert.assertEquals(props.getProperty("releaseOS"), System.getProperty("os.name").contains("indow") ? "windows" : "linux");
        Assert.assertEquals(props.getProperty("releaseSuffix"), "amd64.zip");
      }

    } finally {
      if (tfDir.exists()) {
        FileUtils.forceDelete(tfDir);
      }
    }

    wrapper.execute();

    Assert.assertTrue(tfDir.exists());
    for (String tfwFileName : tfwFileNames) {
      Assert.assertTrue(tfPath.resolve(tfwFileName).toFile().exists());
    }

    Properties props = new Properties();
    try (InputStream fis = new FileInputStream(tfPath.resolve(tfwFileNames[3]).toFile())) {
      props.load(fis);
      Assert.assertEquals(props.getProperty("distributionSite"), "https://releases.hashicorp.com");
      Assert.assertEquals(props.getProperty("releaseDir"), "terraform");
      Assert.assertEquals(props.getProperty("releaseName"), "terraform");
      Assert.assertEquals(props.getProperty("releaseVer"), "0.12.24");
      Assert.assertEquals(props.getProperty("releaseOS"), System.getProperty("os.name").contains("indow") ? "windows" : "linux");
      Assert.assertEquals(props.getProperty("releaseSuffix"), "amd64.zip");
    }
  }

  @Test
  public void setInputParamsAndCheckTheyGetSetCorrectly() throws MojoFailureException, MojoExecutionException, IOException {
    Path tfPath = Paths.get(".tf");
    String[] tfwFileNames = {"tfw", "tfw.cmd", "tfw.ps1", "terraform-maven.properties"};
    File tfDir = tfPath.toFile();
    wrapper.indistributionSite = "https://releases.hashicorp.comTest";
    wrapper.inreleaseDir = "terraformTest";
    wrapper.inreleaseName = "terraformTest";
    wrapper.inreleaseVer = "0.12.24Test";
    wrapper.inreleaseOS = "windowsTest";
    wrapper.inreleaseSuffix = "amd64.zipTest";
    wrapper.execute();
    try (InputStream fis = new FileInputStream(tfPath.resolve(tfwFileNames[3]).toFile())) {
      Properties props = new Properties();
      props.load(fis);
      Assert.assertEquals(props.getProperty("distributionSite"), "https://releases.hashicorp.comTest");
      Assert.assertEquals(props.getProperty("releaseDir"), "terraformTest");
      Assert.assertEquals(props.getProperty("releaseName"), "terraformTest");
      Assert.assertEquals(props.getProperty("releaseVer"), "0.12.24Test");
      Assert.assertEquals(props.getProperty("releaseOS"), "windowsTest");
      Assert.assertEquals(props.getProperty("releaseSuffix"), "amd64.zipTest");
    }
  }
}
