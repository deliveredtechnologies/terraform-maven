package com.deliveredtechnologies.maven.io;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class CompressableFileTest {
  Path uncompressedDir;

  /**
   * Sets up a working directory for compressing files into an archive.
   * @throws URISyntaxException
   * @throws IOException
   */
  @Before
  public void setup() throws URISyntaxException, IOException {
    uncompressedDir = Paths.get(this.getClass().getResource("/uncompressed").toURI());
    Path workingUncompressedFileDir = uncompressedDir.resolveSibling("working_uncompressed");
    workingUncompressedFileDir.toFile().mkdir();
    for (Path file : Files.walk(uncompressedDir, 1).filter(path -> !path.equals(uncompressedDir)).collect(Collectors.toList())) {
      if (file.toFile().isDirectory()) {
        FileUtils.copyDirectory(file.toFile(), workingUncompressedFileDir.resolve(file.getFileName().toString()).toFile());
      } else {
        Files.copy(file, workingUncompressedFileDir.resolve(file.getFileName().toString()));
      }
    }

    uncompressedDir = workingUncompressedFileDir;
  }

  @After
  public void teardown() throws IOException {
    FileUtils.forceDelete(uncompressedDir.toFile());
  }
}
