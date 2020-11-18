package com.deliveredtechnologies.terraform;

import java.io.File;

public class TerraformPathResolver {

  /**
   * Gets the Path for Terraform bin.
   * @return
   */
  public String  getPath() {
    File tfwrapper = new File(".tf/tfw");
    if (tfwrapper.exists()) {
      tfwrapper.setExecutable(true);
    }
    return tfwrapper.exists() ? tfwrapper.getAbsolutePath().replaceAll("[\\\\]","/") : "terraform";
  }

}
