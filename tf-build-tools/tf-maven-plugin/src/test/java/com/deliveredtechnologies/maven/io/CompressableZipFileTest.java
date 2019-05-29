package com.deliveredtechnologies.maven.io;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;

/**
 * Tests for CompresseableZipFile.
 */
public class CompressableZipFileTest extends CompressableFileTest {

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

      Assert.assertEquals(6, count);
      Assert.assertTrue(zipEntryNames.contains("test1/test1.1/test1.1.txt"));
      Assert.assertTrue(zipEntryNames.contains("test1/test.txt"));
      Assert.assertTrue(zipEntryNames.contains("test2/test2.txt"));
    }
  }
}
