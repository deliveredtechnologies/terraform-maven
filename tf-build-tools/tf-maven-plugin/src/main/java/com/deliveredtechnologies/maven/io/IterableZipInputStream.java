package com.deliveredtechnologies.maven.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Adapts ZipInputStream to Iterable interface for supporting Java's foreach syntax, including lambda.
 */
public class IterableZipInputStream extends ZipInputStream implements Iterable<ZipEntry> {

  public IterableZipInputStream(InputStream inputStream) {
    super(inputStream);
  }

  @Override
  public Iterator<ZipEntry> iterator() {
    IterableZipInputStream self = this;
    return new Iterator<ZipEntry>() {
      ZipEntry next = null;

      @Override
      public boolean hasNext() {
        try {
          next = self.getNextEntry();
          return next != null;
        } catch (IOException e) {
          return false;
        }
      }

      @Override
      public ZipEntry next() {
        return next;
      }
    };
  }

  @Override
  public void forEach(Consumer<? super ZipEntry> action) {
    for (ZipEntry entry : this) {
      action.accept(entry);
    }
  }
}
