package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.maven.terraform.TerraformGet;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Mojo terraform "get" goal.
 * <br>
 * Gets Terraform Maven dependencies and extracts into a tfModules dir.
 */
@Mojo(name = "get")
public class Get extends TerraformMojo<List<Path>> {
  @Parameter(property = "tfModulesDir")
  private String tfModulesDir;

  @Override
  public void execute() throws MojoExecutionException {
    try {
      execute(new TerraformGet(getLog(), tfModulesDir), System.getProperties());
    } catch (IOException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
