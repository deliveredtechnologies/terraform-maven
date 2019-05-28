package com.deliveredtechnologies.maven.logs;

import org.apache.maven.plugin.logging.Log;
import org.slf4j.Logger;

/**
 * Adapts SLF4J to Maven Log.
 */
public class Slf4jMavenAdapter implements Log {
  private Logger logger;

  public Slf4jMavenAdapter(Logger logger) {
    this.logger = logger;
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public void debug(CharSequence charSequence) {
    logger.debug(charSequence.toString());
  }

  @Override
  public void debug(CharSequence charSequence, Throwable throwable) {
    logger.debug(charSequence.toString(), throwable);
  }

  @Override
  public void debug(Throwable throwable) {
    logger.debug(throwable.getMessage(), throwable);
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  public void info(CharSequence charSequence) {
    logger.info(charSequence.toString());
  }

  @Override
  public void info(CharSequence charSequence, Throwable throwable) {
    logger.info(charSequence.toString(), throwable);
  }

  @Override
  public void info(Throwable throwable) {
    logger.info(throwable.getMessage(), throwable);
  }

  @Override
  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  @Override
  public void warn(CharSequence charSequence) {
    logger.warn(charSequence.toString());
  }

  @Override
  public void warn(CharSequence charSequence, Throwable throwable) {
    logger.warn(charSequence.toString(), throwable);
  }

  @Override
  public void warn(Throwable throwable) {
    logger.warn(throwable.getMessage(), throwable);
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  @Override
  public void error(CharSequence charSequence) {
    logger.error(charSequence.toString());
  }

  @Override
  public void error(CharSequence charSequence, Throwable throwable) {
    logger.error(charSequence.toString(), throwable);
  }

  @Override
  public void error(Throwable throwable) {
    logger.error(throwable.getMessage(), throwable);
  }
}
