package com.deliveredtechnologies.maven.tf;

import com.deliveredtechnologies.maven.io.Executable;
import com.deliveredtechnologies.maven.logs.Slf4jMavenAdapter;
import org.apache.maven.plugin.logging.Log;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * API for terraform init.
 */
public class TerraformInit implements TerraformOperation<String> {

  private Executable terraform;
  private Log log;

  public TerraformInit() throws IOException {
    this(new Slf4jMavenAdapter(LoggerFactory.getLogger(TerraformInit.class)), new TerraformCommandLineDecorator(TerraformCommand.INIT));
  }

  public TerraformInit(Log log) throws IOException {
    this(log, new TerraformCommandLineDecorator(TerraformCommand.INIT));
  }

  TerraformInit(Executable terraform) {
    this(new Slf4jMavenAdapter(LoggerFactory.getLogger(TerraformInit.class)), terraform);
  }

  TerraformInit(Log log, Executable terraform) {
    this.log = log;
    this.terraform = terraform;
  }

  /**
   * Executes terraform init. <br>
   * <p>
   *   Valid Properties: <br>
   *   tfRootDir - the directory where terraform init is called
   * </p>
   * @param properties  paramter options and properties for terraform init
   * @return            the output of terraform init
   * @throws TerraformException
   */
  @Override
  public String execute(Properties properties) throws TerraformException {
    try {
      //TODO: Make tfRootDir an enum instead of a magic string property.
      String workingDir = properties.getProperty("tfRootDir", TerraformUtils.getTerraformRootModuleDir().toAbsolutePath().toString());
      log.info(String.format("*** Terraform root module directory is '%1$s' ***", workingDir));
      String params = String.format("-no-color %1$s", workingDir);
      return terraform.execute(params);
    } catch (InterruptedException | IOException e) {
      throw new TerraformException(e);
    }
  }
}
