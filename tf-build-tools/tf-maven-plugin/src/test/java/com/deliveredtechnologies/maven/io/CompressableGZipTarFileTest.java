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
 * Tests for CompresseableGZipTarFile.
 */
public class CompressableGZipTarFileTest extends CompressableFileTest {

  @Test
  public void compressableZipFileCompressesFilesAndDirectories() throws IOException {
    String archiveName = "Archive";
    Compressable compressable = new CompressableGZipTarFile(uncompressedDir.resolve(archiveName + ".tar.gz").toAbsolutePath().toString(), uncompressedDir);
    Files.walk(uncompressedDir, 1)
      .filter(path -> !path.equals(uncompressedDir))
      .forEach(path -> compressable.addToCompressedFile(path));
    Path tarFile = compressable.compress();
  }
}
