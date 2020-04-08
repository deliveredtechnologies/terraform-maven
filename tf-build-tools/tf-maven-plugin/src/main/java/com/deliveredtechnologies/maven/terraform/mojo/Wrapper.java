package com.deliveredtechnologies.maven.terraform.mojo;
import java.io.*;
import java.net.URL;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "wrapper")
public class Wrapper extends TerraformMojo<String> {

  @Parameter( property = "distributionSite")
  private String in_distributionSite;
  @Parameter( property = "releaseDir"      )
  private String in_releaseDir;
  @Parameter( property = "releaseName"     )
  private String in_releaseName;
  @Parameter( property = "releaseVer"      )
  private String in_releaseVer;
  @Parameter( property = "releaseOS"       )
  private String in_releaseOS;
  @Parameter( property = "releaseSuffix"   )
  private String in_releaseSuffix;


  @Parameter(defaultValue = "${session}")
  protected MavenSession session;

  @Parameter(defaultValue = "${mojoExecution}")
  protected MojoExecution mojoExecution;


  @Override

  public void execute() throws MojoExecutionException, MojoFailureException {
    String fileName1 = "tf/tfw";
    String fileName2 = "tf/tfw.cmd";
    String fileName3 = "tf/tfw.ps1";
    String fileName4 = "tf/terraform-maven.properties";
    URL f1 = (this.getClass().getClassLoader().getResource(fileName1));
    URL f2 = (this.getClass().getClassLoader().getResource(fileName2));
    URL f3 = (this.getClass().getClassLoader().getResource(fileName3));
    URL f4 = (this.getClass().getClassLoader().getResource(fileName4));
    File dest1 = new File(System.getProperty("user.dir") + "\\.tf\\tfw");
    File dest2 = new File(System.getProperty("user.dir") + "\\.tf\\tfw.cmd");
    File dest3 = new File(System.getProperty("user.dir") + "\\.tf\\tfw.ps1");
    File dest4 = new File(System.getProperty("user.dir") + "\\.tf\\terraform-maven.properties");

    //**************************************************************
    // Here we create the .tf directory if it doesn't already exist
    //**************************************************************
    File tf_dir = new File(System.getProperty("user.dir") + "\\.tf");
    if (!tf_dir.exists())
      if (tf_dir.mkdir())
        getLog().info("Directory .tf is created");
      else
        getLog().info("Failed to create directory .tf");

    //********************************************************************************
    // Here we copy the scripts and properties file (if it doesn't already exist)
    // from the jar file to the .tf directory
    //********************************************************************************
    boolean newPropExists = dest4.exists();
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
    // Here we check for command line arguments (if any) and update the properties file
    //**********************************************************************************
    File propFile    = new File(System.getProperty("user.dir") + "\\.tf\\terraform-maven.properties");
    File newPropFile = new File(System.getProperty("user.dir") + "\\.tf\\terraform-maven.properties2");
    boolean propExists = propFile.exists();

    if (propExists) {
      try {
        Scanner sc             = new Scanner(propFile);
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

          //********************************************************************
          // Here we match each line to possibly update if input has been given
          // We write all the output to a temp output file
          //********************************************************************

          if (distributionSiteIndex != -1)
            if (in_distributionSite != null)
              propOut.write("distributionSite=" + in_distributionSite + "\n");
            else
              propOut.write(curLine + "\n");
          if (releaseDirIndex != -1)
            if (in_releaseDir != null)
              propOut.write("releaseDir="       + in_releaseDir       + "\n");
            else
              propOut.write(curLine + "\n");
          if (releaseNameIndex != -1)
            if (in_releaseName != null)
              propOut.write("releaseName="      + in_releaseName      + "\n");
            else
              propOut.write(curLine + "\n");
          if (releaseVerIndex != -1)
            if (in_releaseVer != null)
              propOut.write("releaseVer="       + in_releaseVer       + "\n");
            else
              propOut.write(curLine + "\n");
          if (releaseOSIndex != -1)
            if (in_releaseOS != null)
              propOut.write("releaseOS="        + in_releaseOS        + "\n");
            else
              propOut.write(curLine + "\n");
          if (releaseSuffixIndex != -1)
            if (in_releaseSuffix != null)
              propOut.write("releaseSuffix="    + in_releaseSuffix    + "\n");
            else
              propOut.write(curLine + "\n");
        }
        //**************************************
        // flush and close the temp output file
        //**************************************
        propOut.flush();
        propOut.close();
        //*******************************************************
        // copy the temp output file into the existing prop file
        //*******************************************************
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

