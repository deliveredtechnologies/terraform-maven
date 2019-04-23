package com.deliveredtechnologies.maven.io;

import java.io.IOException;

public interface Executable {
  public String execute(String command, int timeout) throws IOException, InterruptedException;
  public String execute(String command) throws IOException, InterruptedException;
}
