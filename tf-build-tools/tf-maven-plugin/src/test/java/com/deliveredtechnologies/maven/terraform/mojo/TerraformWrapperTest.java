package com.deliveredtechnologies.maven.terraform.mojo;


import com.deliveredtechnologies.maven.terraform.TerraformDeploy;
import com.deliveredtechnologies.terraform.TerraformException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

public class TerraformWrapperTest {

  private MavenProject project;
  private Log log;
  private Properties properties;
  private Wrapper terraformWrapper;
  private Invoker invoker;
  private InvocationRequest request;

  @Before
  public void setup() {
    this.properties = Mockito.spy(Properties.class);
    this.invoker = Mockito.mock(Invoker.class);
    this.request = Mockito.mock(InvocationRequest.class);
    this.project = new MavenProject();
    this.log = Mockito.mock(Log.class);
    this.terraformWrapper = new Wrapper();


  }
}
