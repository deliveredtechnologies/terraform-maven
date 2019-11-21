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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;


/**
 * Fetches a Terraform artifact from Nexus and expands it into a .mvnproject directory
 */
public class MavenRepoExecutableOp implements TerraformOperation<String> {

  private String workingDir = System.getProperty("user.dir");
  private String tfRootDir;
  private String artifact;
  private TerraformOperation<String> terraformOperation;
  private Log log;

  /**
   * Constructor.
   * @param artifact  artifact specified in the form groupId:artifact:version
   * @param tfRootDir the relative path to the tf root module from the root of the artifact
   * @param log       maven log
   */
  public MavenRepoExecutableOp(String artifact, String tfRootDir, Log log) {
    this.log = log;
    this.artifact = artifact;
    this.tfRootDir = tfRootDir;
  }

  @Override
  public String execute(Properties properties) throws TerraformException {
    getArtifactFromMavenRepo(new DefaultInvoker(), new DefaultInvocationRequest());
    Path expandedArtifactPath = expandMavenArtifacts();
    try {
      TerraformGet terraformGet = new TerraformGet(log, expandedArtifactPath.getParent().getParent().resolve(".tfmodules").toAbsolutePath().toString());
      terraformGet.execute(System.getProperties());
    } catch (IOException e) {
      throw new TerraformException(e);
    }
    if (!StringUtils.isEmpty(tfRootDir)) {
      return expandedArtifactPath.resolve(tfRootDir).toAbsolutePath().toString();
    }
    return expandedArtifactPath.toAbsolutePath().toString();
  }

  /**
   * Gets the zip artifact from Maven and puts them in the current directory.
   * @param invoker
   * @param request
   * @throws TerraformException
   */
  final void getArtifactFromMavenRepo(Invoker invoker, InvocationRequest request) throws TerraformException {
    log.info("Getting artifact dependencies from Maven");

    Path tfWorkingPath = Paths.get(this.workingDir, ".tf");

    Properties properties = new Properties();
    properties.setProperty("artifact", String.format("%1$s:zip", this.artifact));
    properties.setProperty("outputDirectory", tfWorkingPath.toAbsolutePath().toString());

    request.setGoals(Arrays.asList("dependency:copy"));
    request.setProperties(properties);
    try {
      if (!tfWorkingPath.toFile().exists()) {
        FileUtils.forceMkdir(tfWorkingPath.toFile());
      }
      invoker.execute(request);
    } catch (MavenInvocationException | IOException e) {
      throw new TerraformException("Unable to copy dependencies from Maven repo", e);
    }
  }

  final Path expandMavenArtifacts() throws TerraformException {
    String artifactZip = artifact.substring(artifact.indexOf(':') + 1).replace(":", "-");
    Path artifactZipPath = Paths.get(workingDir, artifactZip);

    log.info("Expanding artifacts from " + artifactZipPath.toAbsolutePath());

    return (new ExpandableZippedArtifact(artifactZipPath, log)).expand()
      .orElseThrow(() -> new TerraformException("unable to extract " + artifactZipPath.getFileName()));
  }
}
