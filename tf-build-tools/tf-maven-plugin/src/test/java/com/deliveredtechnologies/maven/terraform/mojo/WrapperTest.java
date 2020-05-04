package com.deliveredtechnologies.maven.terraform.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.deliveredtechnologies.terraform.TerraformException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class WrapperTest {

  @Test
  public void TryHappyWrapper() throws IOException, MojoFailureException, MojoExecutionException {
    Log log = Mockito.mock(Log.class);
    Invoker invoker = Mockito.mock(Invoker.class);
    Wrapper wrapper = new Wrapper();
    Assert.assertNotNull(wrapper);
    wrapper.execute();
  }

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
  @Test
  public void SetIncomingParameters() throws IOException, MojoExecutionException, MojoFailureException {
    String indistributionSite = "https://releases.hashicorp.com";
    String inreleaseDir = "terraform";
    String inreleaseName = "terraform";
    String inreleaseVer = "0.12.24";
    String inreleaseOS = "linux";
    String inreleaseSuffix = "amd64.zip";
    Log log = Mockito.mock(Log.class);
    Invoker invoker = Mockito.mock(Invoker.class);
    Wrapper wrapper = new Wrapper();
    wrapper.execute();
  }

  @Test
  public void testSetup() {
    String str = "Testing junit setup";
    assertEquals("Testing junit setup",str);
  }
}
