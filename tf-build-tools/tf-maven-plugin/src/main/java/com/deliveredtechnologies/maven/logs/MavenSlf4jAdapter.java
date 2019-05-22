package com.deliveredtechnologies.maven.logs;

import org.apache.maven.plugin.logging.Log;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Adapts Maven Log to SLF4J Logger.
 */
public class MavenSlf4jAdapter implements Logger {

  private Log log;

  public MavenSlf4jAdapter(Log log) {
    this.log = log;
  }

  @Override
  public String getName() {
    return getClass().getName();
  }

  @Override
  public boolean isTraceEnabled() {
    return false;
  }


  @Override
  public boolean isTraceEnabled(Marker marker) {
    return false;
  }

  @Override
  public void trace(String msg) {
    throw new RuntimeException("MavenSlf4jAdapter trace() method is not valid");
  }

  @Override
  public void trace(String format, Object arg) {
    throw new RuntimeException("MavenSlf4jAdapter trace() method is not valid");
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    throw new RuntimeException("MavenSlf4jAdapter trace() method is not valid");
  }

  @Override
  public void trace(String format, Object... arguments) {
    throw new RuntimeException("MavenSlf4jAdapter trace() method is not valid");
  }

  @Override
  public void trace(String msg, Throwable exception) {
    throw new RuntimeException("MavenSlf4jAdapter trace() method is not valid");
  }

  @Override
  public void trace(Marker marker, String msg) {
    throw new RuntimeException("MavenSlf4jAdapter trace() method is not valid");
  }

  @Override
  public void trace(Marker marker, String format, Object arg) {
    throw new RuntimeException("MavenSlf4jAdapter trace() method is not valid");
  }

  @Override
  public void trace(Marker marker, String format, Object arg1, Object arg2) {
    throw new RuntimeException("MavenSlf4jAdapter trace() method is not valid");
  }

  @Override
  public void trace(Marker marker, String format, Object... argArray) {
    throw new RuntimeException("MavenSlf4jAdapter trace() method is not valid");
  }

  @Override
  public void trace(Marker marker, String msg, Throwable exception) {
    throw new RuntimeException("MavenSlf4jAdapter trace() method is not valid");
  }

  @Override
  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return log.isDebugEnabled();
  }

  @Override
  public void debug(String msg) {
    log.debug(msg);
  }

  @Override
  public void debug(String format, Object arg) {
    this.debug(format, new Object[] {arg});
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    this.debug(format, new Object[] {arg1, arg2});
  }

  @Override
  public void debug(String format, Object... arguments) {
    log.debug(String.format(format, arguments));
  }

  @Override
  public void debug(String msg, Throwable exception) {
    log.debug(msg, exception);
  }

  @Override
  public void debug(Marker marker, String msg) {
    log.debug(msg);
  }

  @Override
  public void debug(Marker marker, String format, Object arg) {
    this.debug(format, arg);
  }

  @Override
  public void debug(Marker marker, String format, Object arg1, Object arg2) {
    this.debug(format, arg1, arg2);
  }

  @Override
  public void debug(Marker marker, String format, Object... arguments) {
    this.debug(format, arguments);
  }

  @Override
  public void debug(Marker marker, String msg, Throwable exception) {
    this.debug(msg, exception);
  }

  @Override
  public boolean isInfoEnabled() {
    return log.isInfoEnabled();
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return log.isInfoEnabled();
  }

  @Override
  public void info(String msg) {
    log.info(msg);
  }

  @Override
  public void info(String format, Object arg) {
    this.info(format, new Object[] {arg});
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    this.info(format, new Object[] {arg1, arg2});
  }

  @Override
  public void info(String format, Object... arguments) {
    log.info(String.format(format, arguments));
  }

  @Override
  public void info(String msg, Throwable exception) {
    log.info(msg, exception);
  }

  @Override
  public void info(Marker marker, String msg) {
    this.info(msg);
  }

  @Override
  public void info(Marker marker, String format, Object arg) {
    this.info(format, arg);
  }

  @Override
  public void info(Marker marker, String format, Object arg1, Object arg2) {
    this.info(format, arg1, arg2);
  }

  @Override
  public void info(Marker marker, String format, Object... arguments) {
    this.info(format, arguments);
  }

  @Override
  public void info(Marker marker, String msg, Throwable exception) {
    this.info(marker, msg, exception);
  }

  @Override
  public boolean isWarnEnabled() {
    return log.isWarnEnabled();
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return log.isWarnEnabled();
  }

  @Override
  public void warn(String msg) {
    log.warn(msg);
  }

  @Override
  public void warn(String format, Object arg) {
    this.warn(format, new Object[] {arg});
  }

  @Override
  public void warn(String format, Object... arguments) {
    log.warn(String.format(format, arguments));
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    this.warn(format, new Object[] {arg1, arg2});
  }

  @Override
  public void warn(String msg, Throwable exception) {
    this.warn(msg, exception);
  }

  @Override
  public void warn(Marker marker, String msg) {
    this.warn(msg);
  }

  @Override
  public void warn(Marker marker, String format, Object arg) {
    this.warn(format, arg);
  }

  @Override
  public void warn(Marker marker, String format, Object arg1, Object arg2) {
    this.warn(format, arg1, arg2);
  }

  @Override
  public void warn(Marker marker, String format, Object... arguments) {
    this.warn(format, arguments);
  }

  @Override
  public void warn(Marker marker, String msg, Throwable exception) {
    this.warn(msg, exception);
  }

  @Override
  public boolean isErrorEnabled() {
    return log.isErrorEnabled();
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return log.isErrorEnabled();
  }

  @Override
  public void error(String msg) {
    log.error(msg);
  }

  @Override
  public void error(String format, Object arg) {
    this.error(format, new Object[] {arg});
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    this.error(format, new Object[] {arg1, arg2});
  }

  @Override
  public void error(String format, Object... arguments) {
    log.error(String.format(format, arguments));
  }

  @Override
  public void error(String msg, Throwable exception) {
    log.error(msg, exception);
  }

  @Override
  public void error(Marker marker, String msg) {
    this.error(msg);
  }

  @Override
  public void error(Marker marker, String format, Object arg) {
    this.error(format, arg);
  }

  @Override
  public void error(Marker marker, String format, Object arg1, Object arg2) {
    this.error(format, arg1, arg2);
  }

  @Override
  public void error(Marker marker, String format, Object... arguments) {
    this.error(format, arguments);
  }

  @Override
  public void error(Marker marker, String msg, Throwable exception) {
    this.error(msg, exception);
  }
}
