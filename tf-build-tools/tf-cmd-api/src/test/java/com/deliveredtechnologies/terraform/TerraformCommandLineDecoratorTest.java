package com.deliveredtechnologies.terraform;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class TerraformCommandLineDecoratorTest {


  @Test
  public void executeDelegatesToExecutable() throws IOException, InterruptedException {

    String response = "Success!";
    String terraformCommand = "terraform init ";
    Executable executable = Mockito.mock(Executable.class);
    Mockito.when(executable.execute(terraformCommand)).thenReturn(response);
    TerraformCommandLineDecorator terraform = new TerraformCommandLineDecorator(TerraformCommand.INIT, executable);

    String output = terraform.execute("");
    Assert.assertEquals(response, output);
    Mockito.verify(executable, Mockito.times(1)).execute(terraformCommand);
  }

  @Test
  public void executeDelegatesToExecutableWithTimeout() throws IOException, InterruptedException {
    String response = "Success!";
    int timeout = 1000;
    String terraformCommand = "terraform init ";
    Executable executable = Mockito.mock(Executable.class);
    Mockito.when(executable.execute(terraformCommand, timeout)).thenReturn(response);
    TerraformCommandLineDecorator terraform = new TerraformCommandLineDecorator(TerraformCommand.INIT, executable);

    String output = terraform.execute("", timeout);
    Assert.assertEquals(response, output);
    Mockito.verify(executable, Mockito.times(1)).execute(terraformCommand, timeout);
  }

  @Test
  public void terraformCommandLineDecoratorUsesDefaultRootModuleDirWhenTfSourceDirIsNotFound() throws IOException {
    TerraformCommandLineDecorator terraformCommandLineDecorator = new TerraformCommandLineDecorator(TerraformCommand.APPLY);
    Assert.assertEquals(Paths.get("."), ((CommandLine)terraformCommandLineDecorator.getCommandLine()).getDirectory());
  }

  @Test
  public void terraformCommandLineDecoratorSucceedsOnCreationWhenTfSourceDirIsFound() throws IOException {
    Path tfMainPath = Paths.get("src", "main");
    Path tfSrcPath = tfMainPath.resolve("tf");
    if (tfSrcPath.toFile().exists()) Files.delete(tfSrcPath);
    Path rootModulePath = tfSrcPath.resolve("root");
    Files.createDirectory(tfSrcPath);
    Files.createDirectory(rootModulePath);
    Files.createFile(rootModulePath.resolve("main.tf"));
    TerraformCommandLineDecorator terraformCommandLineDecorator = new TerraformCommandLineDecorator(TerraformCommand.APPLY);
    Files.walk(tfSrcPath).sorted(Comparator.reverseOrder()).map(path -> path.toFile()).forEach(File::delete);
  }

  @Test
  public void loggerPassedToTerraformCommandLineDecoratorIsUsedInCommandLineObject() throws IOException, InterruptedException {
    Logger logger = Mockito.mock(Logger.class);
    TerraformCommandLineDecorator terraformCommandLineDecorator = new TerraformCommandLineDecorator(TerraformCommand.VERSION, logger);
    terraformCommandLineDecorator.execute("");

    Mockito.verify(logger, Mockito.times(1)).info(Mockito.anyString(), Mockito.any(Path.class));
    Mockito.verify(logger, Mockito.times(1)).info(Mockito.anyString(), Mockito.anyString());

    logger = Mockito.mock(Logger.class);
    terraformCommandLineDecorator = new TerraformCommandLineDecorator(TerraformCommand.VERSION, new CommandLine(TerraformUtils.getDefaultTerraformRootModuleDir()), logger);
    terraformCommandLineDecorator.execute("");

    Mockito.verify(logger, Mockito.times(1)).info(Mockito.anyString(), Mockito.any(Path.class));
    Mockito.verify(logger, Mockito.times(1)).info(Mockito.anyString(), Mockito.anyString());

    Executable executable = Mockito.mock(Executable.class);
    terraformCommandLineDecorator = new TerraformCommandLineDecorator(TerraformCommand.VERSION, executable);
    terraformCommandLineDecorator.setLogger(logger);

    Mockito.verify(executable, Mockito.times(1)).setLogger(logger);
  }

  @Ignore("not yet ready , Please ignore.")
  @Test
  public void terraformCommandLineDecoratorDoesntBlowUpWithoutLogging() throws IOException, InterruptedException {

    Path tfPath = Paths.get(".tf");
    File fetchFile = new File(String.format("..%stf-maven-plugin%ssrc%smain%sresources%stf",File.separator,File.separator,File.separator,File.separator,File.separator));
    File tfDir = tfPath.toFile();
    String[] tfwFileNames = {"tfw", "tfw.cmd", "tfw.ps1", "terraform-maven.properties"};
    if (!tfDir.exists()) {
      try {
        tfDir.mkdir();
      } catch (Exception e) {
        System.out.printf("Unable to create .tf directory\n");
      }
    }
    for (String tfwFileName : tfwFileNames) {
      String tfwFile = (fetchFile.toPath().toFile() + File.separator +  tfwFileName);
      String tfwFileDestName = (".tf" + File.separator +  tfwFileName);
      File tfwFileSource = new File(tfwFile);
      File tfwFileDest = new File(tfwFileDestName);
      if (!tfwFileDest.exists()) {
        Files.copy(tfwFileSource.toPath(), tfwFileDest.toPath());
        tfwFileDest.setExecutable(true,false);
      }
    }

    TerraformCommandLineDecorator terraformCommandLineDecorator = new TerraformCommandLineDecorator(TerraformCommand.VERSION);
    terraformCommandLineDecorator.execute("init");
    String[]entries = tfDir.list();
    for (String s: entries) {
      File currentFile = new File(tfDir.getPath(), s);
      currentFile.delete();
    }
    tfDir.delete();
  }
}
