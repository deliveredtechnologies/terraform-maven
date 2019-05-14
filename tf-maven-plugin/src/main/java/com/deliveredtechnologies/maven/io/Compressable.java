package com.deliveredtechnologies.maven.io;

import java.io.IOException;
import java.nio.file.Path;

/**
 * An interface for creating compressed files.
 */
public interface Compressable {
  /**
   * Adds an artifact to the compressed file.
   * @param artifactToAdd an artifact to be added to the compressed file
   * @return              true if successfully added, otherwise false
   */
  public boolean addToCompressedFile(Path artifactToAdd);

  /**
   * Creates the compressed file.
   * @return  the Path pointing to the compressed file just created
   * @throws IOException
   */
  public Path compress() throws IOException;
}
