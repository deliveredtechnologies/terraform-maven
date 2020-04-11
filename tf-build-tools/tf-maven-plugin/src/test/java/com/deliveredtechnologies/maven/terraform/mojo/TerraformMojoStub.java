package com.deliveredtechnologies.maven.terraform.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.net.URL;

public class TerraformMojoStub extends TerraformMojo<String> {

  protected int intProp;

  protected long longProp;

  protected File fileProp;

  protected URL urlProp;

  protected boolean boolProp;

  protected String stringProp;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    // do nothing
  }
}
