package com.deliveredtechnologies.maven.io;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

/**
 * Tests for CompresseableZipFile.
 */
public class CompressableZipFileTest {

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

  @Test
  public void compressableZipFileCompressesFilesAndDirectories() throws IOException {
    String archiveName = "Archive";
    Compressable compressable = new CompressableZipFile(uncompressedDir.resolve(archiveName + ".zip").toAbsolutePath().toString());
    Files.walk(uncompressedDir, 1)
      .filter(path -> !path.equals(uncompressedDir))
      .forEach(path -> compressable.addToCompressedFile(path));
    Path zipFile = compressable.compress();


    try (IterableZipInputStream zipStream = new IterableZipInputStream(new FileInputStream(zipFile.toFile()))) {
      int count = 0;
      Set<String> zipEntryNames = new HashSet<>();
      for (ZipEntry entry : zipStream) {
        count++;
        zipEntryNames.add(entry.getName());
      }

      Assert.assertTrue(zipEntryNames.contains("test1/test1.1/test1.1.txt"));
      Assert.assertTrue(zipEntryNames.contains("test1/test.txt"));
      Assert.assertTrue(zipEntryNames.contains("test2/test2.txt"));
    }
  }
}
