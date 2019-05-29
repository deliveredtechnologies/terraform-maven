package com.deliveredtechnologies.maven.io;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Tests for CompresseableGZipTarFile.
 */
public class CompressableGZipTarFileTest extends CompressableFileTest {

  @Test
  public void compressableGZipTarFileCompressesFilesAndDirectories() throws IOException {
    String archiveName = "Archive";
    Compressable compressable = new CompressableGZipTarFile(uncompressedDir.resolve(archiveName + ".tar.gz").toAbsolutePath().toString(), uncompressedDir);
    Files.walk(uncompressedDir, 1)
      .filter(path -> !path.equals(uncompressedDir))
      .forEach(path -> compressable.addToCompressedFile(path));
    Path tarFile = compressable.compress();

    try (TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(uncompressedDir.resolve(archiveName + ".tar.gz").toAbsolutePath().toString())))) {
      int count = 0;
      TarArchiveEntry entry;
      Set<String> tarEntryNames = new HashSet<>();
      while ((entry = tarArchiveInputStream.getNextTarEntry()) != null) {
        count++;
        tarEntryNames.add(entry.getName());
      }

      Assert.assertEquals(6, count);
      Assert.assertTrue(tarEntryNames.contains("test1/test1.1/test1.1.txt"));
      Assert.assertTrue(tarEntryNames.contains("test1/test.txt"));
      Assert.assertTrue(tarEntryNames.contains("test2/test2.txt"));
    }
  }
}
