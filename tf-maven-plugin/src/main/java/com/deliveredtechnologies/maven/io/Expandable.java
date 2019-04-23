package com.deliveredtechnologies.maven.io;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A compressed artifact that can be expanded.
 */
public interface Expandable {
  /**
   * Expands the compressed artifact.
   * @return  the Path of the directory containing the successfully uncompressed contents.
   */
  Optional<Path> expand();
}
