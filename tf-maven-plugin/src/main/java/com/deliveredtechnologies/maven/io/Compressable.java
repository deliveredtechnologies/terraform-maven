package com.deliveredtechnologies.maven.io;

import java.io.IOException;
import java.nio.file.Path;

public interface Compressable {
  public boolean addToCompressedFile(Path artifactToAdd);

  public Path compress() throws IOException;
}
