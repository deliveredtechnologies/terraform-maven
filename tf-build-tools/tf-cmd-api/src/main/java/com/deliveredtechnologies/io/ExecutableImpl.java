package com.deliveredtechnologies.io;

import org.slf4j.Logger;

import java.io.IOException;

public class ExecutableImpl implements Executable {

  @Override
  public String execute(String command, int timeout) throws IOException, InterruptedException {
    System.out.println("hello in execute 2 arg");
    return null;
  }

  @Override
  public String execute(String command) throws IOException, InterruptedException {
    System.out.println("hello in execute 1 arg");
    return null;
  }

  @Override
  public void setLogger(Logger logger) {

  }
}
