package com.deliveredtechnologies.maven.terraform.mojo;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


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
    File tf_dir = new File(System.getProperty("user.dir") + "\\.tf");
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
    File newPropFile = new File(System.getProperty("user.dir") + "\\.tf\\terraform-maven.properties2");
    boolean propExists = propFile.exists();

    if (propExists) {
      try {
        Scanner sc             = new Scanner(propFile);
        BufferedWriter propOut = new BufferedWriter(new FileWriter(newPropFile));

        /*
         * Here we read the properties file
         */
        while (sc.hasNextLine()) {
          String curLine = sc.nextLine();
          int distributionSiteIndex    = curLine.indexOf("distributionSite");
          int releaseDirIndex          = curLine.indexOf("releaseDir"      );
          int releaseNameIndex         = curLine.indexOf("releaseName"     );
          int releaseVerIndex          = curLine.indexOf("releaseVer"      );
          int releaseOSIndex           = curLine.indexOf("releaseOS"       );
          int releaseSuffixIndex       = curLine.indexOf("releaseSuffix"   );

          /*
           * Here we match each line to possibly update if input has been given
           * We write all the output to a temp output file
           */
          if (distributionSiteIndex != -1) {
            if (indistributionSite != null) {
              propOut.write("distributionSite=" + indistributionSite + "\n");
            } else {
              propOut.write(curLine + "\n");
            }
          }
          if (releaseDirIndex != -1) {
            if (inreleaseDir != null) {
              propOut.write("releaseDir=" + inreleaseDir + "\n");
            } else {
              propOut.write(curLine + "\n");
            }
          }
          if (releaseNameIndex != -1) {
            if (inreleaseName != null) {
              propOut.write("releaseName=" + inreleaseName + "\n");
            } else {
              propOut.write(curLine + "\n");
            }
          }
          if (releaseVerIndex != -1) {
            if (inreleaseVer != null) {
              propOut.write("releaseVer=" + inreleaseVer + "\n");
            } else {
              propOut.write(curLine + "\n");
            }
          }
          if (releaseOSIndex != -1) {
            if (inreleaseOS != null) {
              propOut.write("releaseOS=" + inreleaseOS + "\n");
            } else {
              propOut.write(curLine + "\n");
            }
          }
          if (releaseSuffixIndex != -1) {
            if (inreleaseSuffix != null) {
              propOut.write("releaseSuffix=" + inreleaseSuffix + "\n");
            } else {
              propOut.write(curLine + "\n");
            }
          }
        }
        /*
         * flush and close the temp output file
         */
        propOut.flush();
        propOut.close();
        /*
         * copy the temp output file into the existing prop file
         */
        FileUtils.copyFile(newPropFile,propFile);
        /*
         * delete the temp output file
         */
        newPropFile.delete();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}

