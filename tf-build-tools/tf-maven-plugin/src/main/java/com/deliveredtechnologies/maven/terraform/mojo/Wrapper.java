package com.deliveredtechnologies.maven.terraform.mojo;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This is the wrapper class.
 * This class will
 * 1) Create the properties file including any -D command line modifications
 * 2) Determine the OS and add that to the properties file
 */
@Mojo(name = "wrapper")
public class Wrapper extends TerraformMojo<String> {
  /*
   * These are all the possible input parameters the user can supply with
   * the -D switch
   */
  @Parameter( property = "distributionSite")
  private String indistributionSite;

  @Parameter( property = "releaseDir"      )
  private String inreleaseDir;

  @Parameter( property = "releaseName"     )
  private String inreleaseName;

  @Parameter( property = "releaseVer"      )
  private String inreleaseVer;

  @Parameter( property = "releaseOS"       )
  private String inreleaseOS;

  @Parameter( property = "releaseSuffix"   )
  private String inreleaseSuffix;

  @Parameter(defaultValue = "${session}")
  protected MavenSession session;

  @Parameter(defaultValue = "${mojoExecution}")
  protected MojoExecution mojoExecution;


  @Override

  public void execute() throws MojoExecutionException, MojoFailureException {
    /*
     * Here we determine the OS in order to add that to the prop file
     */
    final String os_name = System.getProperty("os.name");
    int windowsIndex    = os_name.indexOf("indow");
    if (inreleaseOS == null) {
      if (windowsIndex != -1) {
        inreleaseOS = "windows";
      } else {
        inreleaseOS = "linux";
      }
    }

    /*
     * Here we create the .tf directory if it doesn't already exist
     */
    final File tf_dir = new File(System.getProperty("user.dir") + "\\.tf");
    if (!tf_dir.exists()) {
      if (tf_dir.mkdir()) {
        getLog().info("Directory .tf is created");
      } else {
        getLog().info("Failed to create directory .tf");
      }
    }

    /*
     * Here we copy the scripts and properties file (if it doesn't already exist)
     * from the jar file to the .tf directory
     */
    String[] tfwFileNames = {"tfw", "tfw.cmd", "tfw.ps1", "terraform-maven.properties"};
    Path tfWrapperPath = Paths.get(".tf");
    for (String tfwFileName : tfwFileNames) {
      File tfwFile = tfWrapperPath.resolve(tfwFileName).toFile();
      if (!(tfwFileName.endsWith(".properties") && tfwFile.exists())) {
        try {
          FileUtils.copyURLToFile(this.getClass().getClassLoader().getResource("tf/" + tfwFileName), tfWrapperPath.resolve(tfwFileName).toFile());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    /*
     * Here we check for command line arguments (if any) and update the properties file
     */
    File propFile    = new File(System.getProperty("user.dir") + "\\.tf\\terraform-maven.properties");

    // Read from properties file
    Properties prop = new Properties();
    prop.setProperty("releaseDir", (System.getProperty("user.dir") + "\\.tf"));
    try  {
      InputStream fis = new FileInputStream(propFile);
      prop.load(fis);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    // Write to properties file
    try {
      OutputStream fos = new FileOutputStream(propFile);
      if (indistributionSite != null) {
        prop.setProperty("distributionSite", indistributionSite);
      }
      if (inreleaseDir != null) {
        prop.setProperty("releaseDir", inreleaseDir);
      }
      if (inreleaseName != null) {
        prop.setProperty("releaseName", inreleaseName);
      }
      if (inreleaseVer != null) {
        prop.setProperty("releaseVer", inreleaseVer);
      }
      if (inreleaseOS != null) {
        prop.setProperty("releaseOS", inreleaseOS);
      }
      if (inreleaseSuffix != null) {
        prop.setProperty("releaseSuffix", inreleaseSuffix);
      }
      prop.store(fos, "Terraform Wrapper Properties");
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}

