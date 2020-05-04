package com.deliveredtechnologies.maven.terraform.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.deliveredtechnologies.terraform.TerraformException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class WrapperTest {
  Log log;
  Wrapper wrapper;

  @Before
  public void setup() {
    log = Mockito.mock(Log.class);
    wrapper = new Wrapper();
    wrapper.setLog(log);
  }


  @Test
  public void TryHappyWrapper() throws IOException, MojoFailureException, MojoExecutionException {
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

  }

  /*
  @Test
  public void FetchPropertiesAndCheck() throws IOException, MojoExecutionException, MojoFailureException {
    Log log = Mockito.mock(Log.class);
    Invoker invoker = Mockito.mock(Invoker.class);
    Wrapper wrapper = new Wrapper();
    wrapper.execute();
    Properties prop = new Properties();
    File propFile = new File(System.getProperty("user.dir") + "\\.tf\\terraform-maven.properties");
    InputStream fis = new FileInputStream(propFile);
    prop.load(fis);
    String releaseDir = prop.getProperty("releaseDir");
    assertEquals("terraform",releaseDir);
    String releaseName = prop.getProperty("releaseName");
    assertEquals("terraform",releaseName);
    String releaseSuffix = prop.getProperty("releaseSuffix");
    assertEquals("amd64.zip",releaseSuffix);
    String releaseOS = prop.getProperty("releaseOS");
    assertEquals("windows",releaseOS);
    String releaseVer = prop.getProperty("releaseVer");
    assertEquals("0.12.24",releaseVer);
    String distributionSite = prop.getProperty("distributionSite");
    assertEquals("https://releases.hashicorp.com",distributionSite);
  }
  */

  @Test
  public void SetIncomingParameters() throws IOException, MojoExecutionException, MojoFailureException {
    String indistributionSite = "https://releases.hashicorp.com";
    String inreleaseDir = "terraform";
    String inreleaseName = "terraform";
    String inreleaseVer = "0.12.24";
    String inreleaseOS = "linux";
    String inreleaseSuffix = "amd64.zip";
    wrapper.execute();
  }

  @Test
  public void testSetup() {
    String str = "Testing junit setup";
    assertEquals("Testing junit setup",str);
  }
}
