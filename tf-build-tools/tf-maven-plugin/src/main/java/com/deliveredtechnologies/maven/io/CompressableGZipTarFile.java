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
import java.util.Optional;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

/**
 * Creates a Gzipped Tar File.
 */
public class CompressableGZipTarFile implements Compressable {
  private static int BUFFER_SIZE = 4096;

  private Set<Path> filesToCompress = new HashSet<>();
  private String filename;
  private Optional<Path> relativizeFrom = Optional.empty();

  /**
   * Instantiates CompressableGZipTarFile with a target filename using complete paths for all tar file entries.
   * @param filename  target/destination tar.gz filename
   */
  public CompressableGZipTarFile(String filename) {
    this(filename, null);
  }

  /**
   * Instantiates CompressableGZipTarFile with a target filename and a path to relativize entries from.
   * @param filename        target/destination tar.gz filename
   * @param relativizeFrom  Path to relativize tar entries from
   */
  public CompressableGZipTarFile(String filename, Path relativizeFrom) {
    this.filename = filename;
    this.relativizeFrom = Optional.ofNullable(relativizeFrom);
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
          compressFile(tarArchiveOutputStream, srcPath.getFileName().toString(), srcPath);
        }
      }
    }
    return Paths.get(filename);
  }

  private void compressFile(TarArchiveOutputStream tarArchiveOutputStream, String filename, Path source) throws IOException {
    File srcFile = source.toFile();
    tarArchiveOutputStream.putArchiveEntry(new TarArchiveEntry(srcFile, relativizeFrom.map(path -> path.relativize(source)).orElse(source).toString()));
    if (srcFile.isDirectory()) {
      tarArchiveOutputStream.closeArchiveEntry();
      for (File childFile : srcFile.listFiles()) {
        String childFilename = childFile.getName();
        compressFile(tarArchiveOutputStream, String.format("%1$s/%2$s", filename, childFilename), source.resolve(childFilename));
      }
      return;
    }
    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
    IOUtils.copy(bis, tarArchiveOutputStream);
    tarArchiveOutputStream.closeArchiveEntry();
    bis.close();
  }
}
