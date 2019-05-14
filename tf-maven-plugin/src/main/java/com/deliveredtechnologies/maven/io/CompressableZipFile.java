package com.deliveredtechnologies.maven.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Class abstraction for creating a ZIP file.
 */
public class CompressableZipFile implements Compressable {
  private static int BUFFER_SIZE = 4096;

  private Set<Path> filesToCompress = new HashSet<>();
  private String filename;

  public CompressableZipFile(String filename) {
    this.filename = filename;
  }

  @Override
  public boolean addToCompressedFile(Path artifactToAdd) {
    return filesToCompress.add(artifactToAdd);
  }

  @Override
  public Path compress() throws IOException {
    try (FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
      try (ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
        for (Path srcPath : filesToCompress) {
          compressFile(zipOutputStream, srcPath.toAbsolutePath().getFileName().toString(), srcPath);
        }
      }
    }
    return Paths.get(filename);
  }

  private void compressFile(ZipOutputStream zipOut, String filename, Path source) throws IOException {
    File srcFile = source.toFile();
    if (srcFile.isDirectory()) {
      String endChar = filename.endsWith("/") ? "" : "/";
      zipOut.putNextEntry(new ZipEntry(filename + endChar));
      zipOut.closeEntry();
      for (String childFilename : srcFile.list()) {
        Path childPath = source.resolve(childFilename);
        compressFile(zipOut, String.format("%1$s/%2$s", filename, childFilename), childPath);
      }
      return;
    }
    try (FileInputStream fileInputStream = new FileInputStream(source.toFile())) {
      ZipEntry zipEntry = new ZipEntry(filename);
      zipOut.putNextEntry(zipEntry);
      byte[] bytes = new byte[BUFFER_SIZE];
      int length;
      while ((length = fileInputStream.read(bytes)) >= 0) {
        zipOut.write(bytes, 0, length);
      }
    }
  }
}
