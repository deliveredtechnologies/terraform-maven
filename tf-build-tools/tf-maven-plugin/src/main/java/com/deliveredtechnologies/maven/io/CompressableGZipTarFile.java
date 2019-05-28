package com.deliveredtechnologies.maven.io;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

/**
 * Creates a Gzipped Tar File
 */
public class CompressableGZipTarFile implements Compressable {
  private static int BUFFER_SIZE = 4096;

  private Set<Path> filesToCompress = new HashSet<>();
  private String filename;

  public CompressableGZipTarFile(String filename) {
    this.filename = filename;
  }

  @Override
  public boolean addToCompressedFile(Path artifactToAdd) {
    return filesToCompress.add(artifactToAdd);
  }

  @Override
  public Path compress() throws IOException {
    try (FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
      try (TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(new GZIPOutputStream(fileOutputStream))) {
        tarArchiveOutputStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
        tarArchiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        for (Path srcPath : filesToCompress) {
          compressFile(tarArchiveOutputStream, srcPath.toAbsolutePath().getFileName().toString(), srcPath);
        }
      }
    }
    return Paths.get(filename);
  }

  private void compressFile(TarArchiveOutputStream tarArchiveOutputStream, String filename, Path source) throws IOException {

    File srcFile = source.toFile();
    tarArchiveOutputStream.putArchiveEntry(new TarArchiveEntry(srcFile, source.toString()));
    if (srcFile.isDirectory()) {
      tarArchiveOutputStream.closeArchiveEntry();
      for (File childFile : srcFile.listFiles()) {
        compressFile(tarArchiveOutputStream, childFile.getName(), source);
      }
      return;
    }
    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
    IOUtils.copy(bis, tarArchiveOutputStream);
    tarArchiveOutputStream.closeArchiveEntry();
    bis.close();
  }
}
