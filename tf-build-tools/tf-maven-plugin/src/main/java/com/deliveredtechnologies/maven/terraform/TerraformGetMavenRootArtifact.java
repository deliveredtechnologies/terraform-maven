package com.deliveredtechnologies.maven.terraform;

import com.deliveredtechnologies.maven.io.ExpandableZippedArtifact;
import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;


/**
 * Fetches a Terraform artifact from Nexus and expands it into a .tf directory
 */
public class TerraformGetMavenRootArtifact implements TerraformOperation<String> {

  private Path workingPath = Paths.get(System.getProperty("user.dir"), ".tfproject");
  private Path mainTfPath;
  private String tfRootDir;
  private String artifact;
  private Log log;

  /**
   * Constructor.
   * @param artifact  artifact specified in the form groupId:artifact:version
   * @param log       maven log
   */
  public TerraformGetMavenRootArtifact(String artifact, Log log) {
    this(artifact, null, log);
  }

  /**
   * Constructor.
   * @param artifact  artifact specified in the form groupId:artifact:version
   * @param tfRootDir the relative path to the tf root module from the root of the artifact
   * @param log       maven log
   */
  public TerraformGetMavenRootArtifact(String artifact, String tfRootDir, Log log) {
    this.log = log;
    this.artifact = artifact;
    this.tfRootDir = tfRootDir;
    this.mainTfPath = workingPath.resolve("src").resolve("main").resolve("tf");
  }

  @Override
  public String execute(Properties properties) throws TerraformException {
    getArtifactFromMavenRepo(new DefaultInvoker(), new DefaultInvocationRequest());
    Path expandedArtifactPath = expandMavenArtifacts();
    try {
      String userDir = System.getProperty("user.dir");
      synchronized (this.getClass()) { //synchronized to lock adverse effects from changing user.dir; unlikely, but possible
        System.setProperty("user.dir", workingPath.toAbsolutePath().toString());
        TerraformGet terraformGet = new TerraformGet(log, mainTfPath.getParent().resolve(".tfmodules").toAbsolutePath().toString());
        terraformGet.execute(properties);
        System.setProperty("user.dir", userDir);
      }
    } catch (IOException e) {
      throw new TerraformException(e);
    }
    if (!StringUtils.isEmpty(tfRootDir)) {
      return expandedArtifactPath.resolve(tfRootDir).toAbsolutePath().toString();
    }
    return expandedArtifactPath.toAbsolutePath().toString();
  }

  /**
   * Gets the zip artifact and the pom from Maven and puts them in the .tfproject/src/main and .tfproject directories, respectively.
   * @param invoker
   * @param request
   * @throws TerraformException
   */
  final void getArtifactFromMavenRepo(Invoker invoker, InvocationRequest request) throws TerraformException {
    log.info("Getting artifact dependencies from Maven");

    String artifactZip = getArtifactZip(this.artifact);
    Properties properties = new Properties();
    properties.setProperty("artifact", String.format("%1$s:zip", this.artifact));
    properties.setProperty("outputDirectory", mainTfPath.toAbsolutePath().toString());

    request.setGoals(Arrays.asList("dependency:copy"));
    request.setProperties(properties);
    try {
      if (!mainTfPath.toFile().exists()) {
        FileUtils.forceMkdir(mainTfPath.toFile());
      }

      invoker.execute(request);
      request.getProperties().setProperty("artifact", String.format("%1$s:pom", this.artifact));
      request.getProperties().setProperty("outputDirectory", workingPath.toAbsolutePath().toString());
      request.setProperties(properties);
      invoker.execute(request);
      Optional<Path> pomFile = Files.walk(workingPath)
          .filter(path -> path.getFileName().toString().endsWith(".pom"))
          .findFirst();
      if (pomFile.isPresent()) {
        FileUtils.moveFile(pomFile.get().toFile(), workingPath.resolve("pom.xml").toFile());
      }

    } catch (MavenInvocationException | IOException e) {
      throw new TerraformException("Unable to copy dependencies from Maven repo", e);
    }
  }

  /**
   * Extracts files from a maven compressed artifact in the working directory path; defaults to '{working dir}/tf'.
   * The name of the artifact is 'artifactId-version.zip'.
   * @return The path of the expanded root directory
   * @throws TerraformException
   */
  final Path expandMavenArtifacts() throws TerraformException {
    String artifactZip = getArtifactZip(this.artifact);
    Path artifactZipPath = mainTfPath.resolve(artifactZip);

    log.info("Expanding artifacts from " + artifactZipPath.toAbsolutePath());

    return (new ExpandableZippedArtifact(artifactZipPath, log)).expand()
      .orElseThrow(() -> new TerraformException("unable to extract " + artifactZipPath.getFileName()));
  }

  static String getArtifactZip(String artifact) {
    String artifactZip = artifact.indexOf(':') > 0 ? artifact.substring(artifact.indexOf(':') + 1).replace(":", "-") : artifact;
    return artifactZip.endsWith(".zip") ? artifactZip : String.format("%1$s.zip", artifactZip);
  }
}
