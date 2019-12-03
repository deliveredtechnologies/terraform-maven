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
 * Fetches a Terraform artifact from Nexus and expands it into a .mvnproject directory
 */
public class TerraformGetMavenRootArtifact implements TerraformOperation<String> {

  private Path workingDir = Paths.get(System.getProperty("user.dir"), ".tf");
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
  }

  @Override
  public String execute(Properties properties) throws TerraformException {
    getArtifactFromMavenRepo(new DefaultInvoker(), new DefaultInvocationRequest());
    Path expandedArtifactPath = expandMavenArtifacts();
    try {
      String userDir = System.getProperty("user.dir");
      synchronized (this.getClass()) { //
        System.setProperty("user.dir", expandedArtifactPath.getParent().toAbsolutePath().toString());
        TerraformGet terraformGet = new TerraformGet(log, expandedArtifactPath.getParent().getParent().resolve(".tfmodules").toAbsolutePath().toString());
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
   * Gets the zip artifact and the pom from Maven and puts them in the .tf directory.
   * @param invoker
   * @param request
   * @throws TerraformException
   */
  final void getArtifactFromMavenRepo(Invoker invoker, InvocationRequest request) throws TerraformException {
    log.info("Getting artifact dependencies from Maven");

    String artifactZip = getArtifactZip(this.artifact);
    Properties properties = new Properties();
    properties.setProperty("artifact", String.format("%1$s:zip", this.artifact));
    properties.setProperty("outputDirectory", workingDir.toAbsolutePath().toString());

    request.setGoals(Arrays.asList("dependency:copy"));
    request.setProperties(properties);
    try {
      if (!workingDir.toFile().exists()) {
        FileUtils.forceMkdir(workingDir.toFile());
      }
      if (!workingDir.resolve(artifactZip).toFile().exists()) {
        invoker.execute(request);
        request.getProperties().setProperty("artifact", String.format("%1$s:pom", this.artifact));
        request.setProperties(properties);
        invoker.execute(request);
        Optional<Path> pomFile = Files.walk(workingDir)
          .filter(path -> path.getFileName().toString().endsWith(".pom"))
          .findFirst();
        if (pomFile.isPresent()) {
          FileUtils.moveFile(pomFile.get().toFile(), workingDir.resolve("pom.xml").toFile());
        }
      }
    } catch (MavenInvocationException | IOException e) {
      throw new TerraformException("Unable to copy dependencies from Maven repo", e);
    }
  }

  /**
   * Extracts files from a maven compressed artifact in the working directory path; defaults to '{working dir}/.tf'.
   * The name of the artifact is 'artifactId-version.zip'.
   * @return The path of the expanded root directory
   * @throws TerraformException
   */
  final Path expandMavenArtifacts() throws TerraformException {
    String artifactZip = getArtifactZip(this.artifact);
    Path artifactZipPath = workingDir.resolve(artifactZip);

    log.info("Expanding artifacts from " + artifactZipPath.toAbsolutePath());

    return (new ExpandableZippedArtifact(artifactZipPath, log)).expand()
      .orElseThrow(() -> new TerraformException("unable to extract " + artifactZipPath.getFileName()));
  }

  static String getArtifactZip(String artifact) {
    String artifactZip = artifact.indexOf(':') > 0 ? artifact.substring(artifact.indexOf(':') + 1).replace(":", "-") : artifact;
    return artifactZip.endsWith(".zip") ? artifactZip : String.format("%1$s.zip", artifactZip);
  }
}
