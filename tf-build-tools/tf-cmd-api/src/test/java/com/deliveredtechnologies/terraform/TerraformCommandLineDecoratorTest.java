package com.deliveredtechnologies.terraform;

import com.deliveredtechnologies.io.CommandLine;
import com.deliveredtechnologies.io.Executable;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

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
}
