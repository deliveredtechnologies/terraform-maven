package com.deliveredtechnologies.maven.tf.mojo;

import com.deliveredtechnologies.maven.tf.TerraformUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.nio.file.Path;

@Mojo(name = "package")
public class Package extends AbstractMojo {
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      Path tfRootDir = TerraformUtils.getTerraformRootModuleDir();
    } catch (IOException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
