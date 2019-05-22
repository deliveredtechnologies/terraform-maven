package com.deliveredtechnologies.maven.io;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;

/**
 * Tests for IterableZipInputStream.
 */
public class IterableZipInputStreamTest {

  private File zipFile;

  @Before
  public void setup() throws URISyntaxException {
    zipFile = Paths.get(this.getClass().getResource("/zips").toURI()).resolve("terraform-module-my.module2-0.1.zip").toFile();
  }

  @Test
  public void iterableZipInputStreamIteratesOverElementsInZupUsingForEachSyntax() throws IOException {
    try (InputStream inputStream = new FileInputStream(zipFile)) {
      IterableZipInputStream zipInputStream = new IterableZipInputStream(inputStream);
      int count = 0;
      for (ZipEntry entry : zipInputStream) {
        Assert.assertTrue(entry.getName().startsWith("test.file") && entry.getName().endsWith(".txt"));
        count++;
      }
      Assert.assertEquals(count, 2);
    }
  }

  @Test
  public void iterableZipInputStreamIteratesOverElementsInZupUsingLambdaSyntax() throws IOException {
    try (InputStream inputStream = new FileInputStream(zipFile)) {
      IterableZipInputStream zipInputStream = new IterableZipInputStream(inputStream);
      zipInputStream.forEach(entry -> {
        Assert.assertTrue(entry.getName().startsWith("test.file") && entry.getName().endsWith(".txt"));
      });
    }
  }
}
