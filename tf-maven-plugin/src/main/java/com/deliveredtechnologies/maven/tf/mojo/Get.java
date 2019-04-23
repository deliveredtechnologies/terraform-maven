package com.deliveredtechnologies.maven.tf.mojo;

import com.deliveredtechnologies.maven.tf.TerraformGet;
import com.deliveredtechnologies.maven.tf.TerraformMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Mojo(name = "get")
public class Get
    extends TerraformMojo<List<Path>>
{
  @Parameter(property = "tf_modules_dir")
  private String tfModulesDir;

  public void execute() throws MojoExecutionException
  {
    try {
      execute(new TerraformGet(getLog(), tfModulesDir), System.getProperties());
    } catch (IOException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
