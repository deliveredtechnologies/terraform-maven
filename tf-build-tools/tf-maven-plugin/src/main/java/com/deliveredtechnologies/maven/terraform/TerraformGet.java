package com.deliveredtechnologies.maven.terraform;

import com.deliveredtechnologies.maven.io.ExpandableZippedArtifact;

import com.deliveredtechnologies.maven.logs.Slf4jMavenAdapter;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.TerraformUtils;
import com.deliveredtechnologies.terraform.api.TerraformOperation;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Java API for retrieving Maven artifacts and extracting them into a modules (i.e. tfmodules) directory.
 */
public class TerraformGet implements TerraformOperation<List<Path>> {

  private static final String PACKAGING = "zip";

  private Path tfModules;
  private Log log;

  /**
   * Constructor.
   * @param log       Maven Log
   * @param tfModules the common modules directory; if null, it's defaulted to src/main/.tfmodules
   */
  public TerraformGet(Log log, String tfModules) throws IOException {
    this(log, TerraformUtils.getDefaultTfModulesDir());
  }

  protected TerraformGet(Log log, Path tfModules) throws IOException {
    this.log = log;
    this.tfModules = tfModules;
    if (!this.tfModules.toFile().exists()) FileUtils.forceMkdir(this.tfModules.toFile());
  }

  /**
   * Constructor.
   * The common modules directory is defaulted to src/main/.tfmodules
   * @param log Maven Log
   */
  public TerraformGet(Log log) throws IOException {
    this(log, (String)null);
  }

  /**
   * Constructor.
   * The common modules directory is defaulted to src/main/.tfmodules
   */
  public TerraformGet() throws IOException {
    this(new Slf4jMavenAdapter(LoggerFactory.getLogger(TerraformGet.class)), (String)null);
  }

  @Override
  public List<Path> execute(Properties properties) throws TerraformException {
    if (Boolean.valueOf(properties.getProperty("skipTfGet", "false"))) {
      return new ArrayList<Path>();
    }
    getDependenciesFromMavenRepo(new DefaultInvoker(), new DefaultInvocationRequest());
    return expandMavenArtifacts(tfModules);
  }

  /**
   * Gets the artifacts from Maven and puts them in the common modules directory.
   * @param invoker
   * @param request
   * @throws TerraformException
   */
  final void getDependenciesFromMavenRepo(Invoker invoker, InvocationRequest request) throws TerraformException {
    log.info("Getting artifact dependencies from Maven");

    //TODO: clean up magic strings
    Properties properties = new Properties();
    properties.setProperty("outputDirectory", tfModules.toAbsolutePath().toString());
    properties.setProperty("includeTypes", PACKAGING);

    request.setGoals(Arrays.asList("dependency:copy-dependencies"));
    request.setProperties(properties);
    try {
      invoker.execute(request);
    } catch (MavenInvocationException e) {
      throw new TerraformException("Unable to copy dependencies from Maven repo", e);
    }
  }

  /**
   * Expands the compressed Maven artifacts under their parent directory.
   * @param directory directory containing the compressed artifacts
   * @return          a List of the expanded directories
   * @throws TerraformException
   */
  final List<Path> expandMavenArtifacts(Path directory) throws TerraformException {
    log.info("Expanding artifacts from " + directory.toAbsolutePath());

    try {
      List<Path> result = new ArrayList<>();
      List<Path> zipFiles = Files.walk(directory, 1)
          .filter(path -> path.getFileName().toString().endsWith(".zip"))
          .collect(Collectors.toList());

      for (Path zipFile : zipFiles) {
        result.add((new ExpandableZippedArtifact(zipFile, log)).expand()
            .orElseThrow(() -> new TerraformException("unable to extract " + zipFile.getFileName())));
      }
      return result;
    } catch (IOException e) {
      throw new TerraformException("Unable to extract maven artifacts");
    }
  }
}
