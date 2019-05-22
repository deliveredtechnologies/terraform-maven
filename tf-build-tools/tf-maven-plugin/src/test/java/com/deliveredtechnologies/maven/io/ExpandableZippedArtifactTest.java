package com.deliveredtechnologies.maven.io;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tests for ExpandableZippedArtifact.
 */
public class ExpandableZippedArtifactTest {

  private Path zipFileDir;

  /**
   * Creates a temporary working directory.
   * @throws URISyntaxException
   * @throws IOException
   */
  @Before
  public void setup() throws URISyntaxException, IOException {
    zipFileDir = Paths.get(this.getClass().getResource("/zips").toURI());
    Path workingZipFileDir = zipFileDir.resolveSibling("working_zips");
    workingZipFileDir.toFile().mkdir();
    for (Path zipFile : Files.walk(zipFileDir, 1).filter(path -> !path.equals(zipFileDir)).collect(Collectors.toList())) {
      Files.copy(zipFile, workingZipFileDir.resolve(zipFile.getFileName().toString()));
    }

    zipFileDir = workingZipFileDir;
  }

  /**
   * Cleans up the temporary working directory.
   * @throws IOException
   */
  @After
  public void teardown() throws IOException {
    FileUtils.forceDelete(zipFileDir.toFile());
  }

  @Test
  public void zippedArtifactExtractsArtifactsWithaDotInTheName() throws URISyntaxException, IOException {
    Path zipFile = zipFileDir.resolve("terraform-module-my.module2-0.1.zip");
    Expandable expandableArtifact = new ExpandableZippedArtifact(zipFile);
    expandableArtifact.expand();

    Path expandedDir = zipFileDir.resolve("my.module2");
    Assert.assertTrue(expandedDir.toFile().isDirectory());

    Assert.assertEquals(Files.walk(expandedDir, 1)
        .filter(path -> !path.equals(expandedDir))
        .count(), 2);

    Assert.assertTrue(Files.walk(expandedDir, 1)
        .filter(path -> !path.equals(expandedDir))
        .allMatch(path -> path.getFileName().toString().startsWith("test.file") && path.getFileName().toString().endsWith(".txt")));

    Assert.assertFalse(zipFile.toFile().exists());
  }

  @Test
  public void zippedArtifactExtractsArtifactsWithaReleaseQualifierInTheName() throws URISyntaxException, IOException {
    Log log = Mockito.mock(Log.class);
    Path zipFile = zipFileDir.resolve("terraform-module-my-module1-0.12-rc.zip");
    Expandable expandableArtifact = new ExpandableZippedArtifact(zipFile, log);
    expandableArtifact.expand();

    Path expandedDir = zipFileDir.resolve("my-module1");
    Assert.assertTrue(expandedDir.toFile().isDirectory());

    Assert.assertEquals(Files.walk(expandedDir, 1)
        .filter(path -> !path.equals(expandedDir))
        .count(), 2);

    Assert.assertTrue(Files.walk(expandedDir, 1)
        .filter(path -> !path.equals(expandedDir))
        .allMatch(path -> path.getFileName().toString().startsWith("test.file") && path.getFileName().toString().endsWith(".txt")));

    Assert.assertFalse(zipFile.toFile().exists());

    Mockito.verify(log, Mockito.times(1)).info(Mockito.anyString());
  }

  @Test
  public void zippedArtifactExtractsSnapshotArtifacts() throws URISyntaxException, IOException {
    Path zipFile = zipFileDir.resolve("terraform-module-my-module3-1.2.3-SNAPSHOT.zip");
    Expandable expandableArtifact = new ExpandableZippedArtifact(zipFile);
    expandableArtifact.expand();

    Path expandedDir = zipFileDir.resolve("my-module3");
    Assert.assertTrue(expandedDir.toFile().isDirectory());

    Assert.assertEquals(Files.walk(expandedDir, 1)
        .filter(path -> !path.equals(expandedDir))
        .count(), 2);

    Assert.assertTrue(Files.walk(expandedDir, 1)
        .filter(path -> !path.equals(expandedDir))
        .anyMatch(path -> path.getFileName().toString().equals("test.file1.txt")));

    List<Path> filesInSubDir = Files.walk(expandedDir.resolve("test_dir"), 1)
        .filter(path -> !path.equals(expandedDir.resolve("test_dir")))
        .collect(Collectors.toList());

    Assert.assertEquals(1, filesInSubDir.size());
    Assert.assertEquals("test.file2.txt", filesInSubDir.get(0).getFileName().toString());

    Assert.assertFalse(zipFile.toFile().exists());
  }
}
