package com.deliveredtechnologies.maven.io;

import com.deliveredtechnologies.maven.logs.Slf4jMavenAdapter;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class abstraction for expanding a ZIP file.
 */
public class ExpandableZippedArtifact implements Expandable {

  private static int BUFFER_SIZE = 4096;

  private Path zipFile;
  private Log log;

  public ExpandableZippedArtifact(Path zipFile, Log log) {
    this.zipFile = zipFile;
    this.log = log;
  }

  public ExpandableZippedArtifact(Path zipFile) {
    this(zipFile, new Slf4jMavenAdapter(LoggerFactory.getLogger(ExpandableZippedArtifact.class)));
  }


  @Override
  public Optional<Path> expand() {
    log.info("Expanding " + zipFile);

    Path artifactDir;

    try (IterableZipInputStream zipStream = new IterableZipInputStream(new FileInputStream(zipFile.toFile()))) {
      //split path for Windows or Linux
      String[] zipFilenamePathSplit = zipFile.getFileName().toString().split(File.separator.equals("/") ? "\\/" : "\\\\");
      //split the filename by hyphens, e.g. {artifact}-{version}-{qualifier}.zip
      String[] zipFilenameSplitByHyphen = zipFilenamePathSplit[zipFilenamePathSplit.length - 1].split("-");

      //get just the name of the unprefixed Maven artifact (e.g. s3-bucket or lambda or module1, etc.)
      //a supported convention for TerraformCommandLineDecorator Maven artifact prefixes is terraform-module-{artifact}-{version}-{qualifier}
      String artifactName = Arrays.stream(zipFilenameSplitByHyphen)
          .filter(s -> !s.startsWith("SNAPSHOT"))
          .filter(s -> !s.equals("rc"))
          .filter(s -> !s.equals("tf"))
          .filter(s -> !s.equals("module"))
          .filter(s -> {
            if (s.contains(".")) {
              String[] splitByDot = s.split("\\.");
              return !StringUtils.isNumeric(splitByDot[0])
                && !splitByDot[splitByDot.length - 1].equals("zip");
            }
            return true;
          })
          .reduce("", (s1, s2) -> s1 + (s1.length() > 0 ? "-" : "") + s2);

      Path modulesDir = zipFile.getParent();

      //create the directory as the name of the artifact
      artifactDir = Paths.get(modulesDir.toString(), artifactName);
      artifactDir.toFile().mkdir();

      //extract the contents of the artifact zip file into the directory created immediately above
      for (ZipEntry entry : zipStream) {
        if (entry.isDirectory()) {
          Paths.get(modulesDir.toString(), artifactName, entry.getName()).toFile().mkdir();
        } else {
          extractFile(zipStream, Paths.get(modulesDir.toString(), artifactName, entry.getName()));
        }
      }
    } catch (IOException e) {
      log.error("Error expanding " + zipFile.getFileName(), e);
      return Optional.empty();
    }

    if (!zipFile.toFile().delete()) {
      log.warn("Unable to delete " + zipFile.toAbsolutePath());
    }
    return Optional.of(artifactDir);
  }

  private void extractFile(ZipInputStream zipStream, Path filePath) throws IOException {
    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath.toString()))) {
      byte[] buffer = new byte[BUFFER_SIZE];
      int read;
      while ((read = zipStream.read(buffer)) >= 0) {
        bos.write(buffer, 0, read);
      }
    }
    finally {
      zipStream.closeEntry();
    }
  }
}
