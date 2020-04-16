package com.deliveredtechnologies.io;

import org.slf4j.Logger;

import java.io.IOException;

/**
 * Executable interface for defining process executions w/commands.
 */
public interface Executable {
  public String execute(String command, int timeout) throws IOException, InterruptedException;

  public String execute(String command) throws IOException, InterruptedException;

  public void setLogger(Logger logger);
}

