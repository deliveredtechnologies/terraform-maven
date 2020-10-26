package com.deliveredtechnologies.terraform;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TerraformPathResolverTest {


  @Test
  public void testGetPathNoWrapper() {
    TerraformPathResolver terraformPathResolver = new TerraformPathResolver();

    String path = terraformPathResolver.getPath();
    Assert.assertEquals("terraform", path);

  }

  @Test
  public void testGetPathWithWrapper() throws IOException {
    TerraformPathResolver terraformPathResolver = new TerraformPathResolver();

    Path dir =  Files.createDirectories(Paths.get(".tf/tfw"));
    //Path tempFile = Files.createFile(dir.resolve("tfw"));

    String tpath = terraformPathResolver.getPath();
    Assert.assertThat(tpath, CoreMatchers.endsWith(".tf/tfw"));
    Path path = Paths.get(".tf");
    Files.deleteIfExists(path.resolve("tfw"));
    Files.deleteIfExists(path);

  }
}
