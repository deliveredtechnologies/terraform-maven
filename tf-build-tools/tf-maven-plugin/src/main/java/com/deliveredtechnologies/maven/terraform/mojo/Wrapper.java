package com.deliveredtechnologies.maven.terraform.mojo;
import java.io.*;
import java.net.URL;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "wrapper")
public class Wrapper extends TerraformMojo<String> {
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    String in_distributionSite = "";
    String in_releaseDir       = "";
    String in_releaseName      = "";
    String in_releaseVer       = "0.12.22";
    String in_releaseOS        = "";
    String in_releaseSuffix    = "";
    String fileName1           = "tf/tfw";
    String fileName2           = "tf/tfw.cmd";
    String fileName3           = "tf/tfw.ps1";
    String fileName4           = "tf/terraform-maven.properties";
    URL f1 = (this.getClass().getClassLoader().getResource(fileName1));
    URL f2 = (this.getClass().getClassLoader().getResource(fileName2));
    URL f3 = (this.getClass().getClassLoader().getResource(fileName3));
    URL f4 = (this.getClass().getClassLoader().getResource(fileName4));
    File dest1 = new File(System.getProperty("user.dir") + "\\.tf\\tfw");
    File dest2 = new File(System.getProperty("user.dir") + "\\.tf\\tfw.cmd");
    File dest3 = new File(System.getProperty("user.dir") + "\\.tf\\tfw.ps1");
    File dest4 = new File(System.getProperty("user.dir") + "\\.tf\\terraform-maven.properties");
    boolean newPropExists = dest4.exists();

    getLog().info("Jeffs terraform wrapper");

    //***********************************
    // Here we create the .tf directory
    //***********************************
    File tf_dir = new File(System.getProperty("user.dir") + "\\.tf");
    if (!tf_dir.exists()) {
      if (tf_dir.mkdir()) {
        getLog().info("Directory is created");
      } else {
        getLog().info("Failed to create directory");
      }
    }

    //********************************************************************************
    // Here we copy the scripts and prop file from the jar file to the .tf directory
    //********************************************************************************
    try {
      FileUtils.copyURLToFile(f1,dest1);
      FileUtils.copyURLToFile(f2,dest2);
      FileUtils.copyURLToFile(f3,dest3);
      if (!newPropExists)
        FileUtils.copyURLToFile(f4,dest4);
    } catch (IOException e) {
      e.printStackTrace();
    }

    //**********************************************************************************
    // Here we check for command line arguments and update the properties file if any
    //**********************************************************************************
    File propFile    = new File(System.getProperty("user.dir") + "\\.tf\\terraform-maven.properties");
    File newPropFile = new File(System.getProperty("user.dir") + "\\.tf\\terraform-maven.properties2");
    boolean propExists = propFile.exists();
    if (propExists) {
      try {
        Scanner sc = new Scanner(propFile);
        BufferedWriter propOut = new BufferedWriter(new FileWriter(newPropFile));

        //***********************************
        // Here we read the properties file
        //***********************************
        while (sc.hasNextLine()) {
          String curLine = sc.nextLine();
          int distributionSiteIndex    = curLine.indexOf("distributionSite");
          int releaseDirIndex          = curLine.indexOf("releaseDir"      );
          int releaseNameIndex         = curLine.indexOf("releaseName"     );
          int releaseVerIndex          = curLine.indexOf("releaseVer"      );
          int releaseOSIndex           = curLine.indexOf("releaseOS"       );
          int releaseSuffixIndex       = curLine.indexOf("releaseSuffix"   );

          //******************************************************
          // Here we match each line to possibly update if input
          //******************************************************
          if (distributionSiteIndex != -1) {
            if (!in_distributionSite.equals(""))
              propOut.write("distributionSite=" + in_distributionSite);
            else
              propOut.write(curLine);
            propOut.newLine();
          }
          if (releaseDirIndex != -1) {
            if (!in_releaseDir.equals(""))
              propOut.write("releaseDir=" + in_releaseDir);
            else
              propOut.write(curLine);
            propOut.newLine();
          }
          if (releaseNameIndex != -1) {
            if (!in_releaseName.equals(""))
              propOut.write("releaseName=" + in_releaseName);
            else
              propOut.write(curLine);
            propOut.newLine();
          }
          if (releaseVerIndex != -1) {
            if (!in_releaseVer.equals(""))
              propOut.write("releaseVer=" + in_releaseVer);
            else
              propOut.write(curLine);
            propOut.newLine();
          }
          if (releaseOSIndex != -1) {
            if (!in_releaseOS.equals(""))
              propOut.write("releaseOS=" + in_releaseOS);
            else
              propOut.write(curLine);
            propOut.newLine();
          }
          if (releaseSuffixIndex != -1) {
            if (!in_releaseSuffix.equals(""))
              propOut.write("releaseSuffix=" + in_releaseSuffix);
            else
              propOut.write(curLine);
            propOut.newLine();
          }
        }
        //**********************************
        // flush and close the output file
        //**********************************
        propOut.flush();
        propOut.close();
        //***********************************************
        // copy the new prop file into the existing one
        //***********************************************
        FileUtils.copyFile(newPropFile,propFile);
        //******************************
        // delete the temp output file
        //******************************
        newPropFile.delete();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }
}

