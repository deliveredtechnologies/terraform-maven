package com.deliveredtechnologies.maven.logs;

import com.deliveredtechnologies.maven.tf.TerraformException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

public class Slf4jMavenAdapterTest {

  @Test
  public void slf4jMavenAdapterAdaptsSlf4jToMavenLog() {
    String logMessage = "Test Log Message!";
    Exception logException = new TerraformException(logMessage);
    Logger slf4jLogger = Mockito.mock(Logger.class);
    Log mavenLog = new Slf4jMavenAdapter(slf4jLogger);

    Mockito.when(slf4jLogger.isInfoEnabled()).thenReturn(true);
    Mockito.when(slf4jLogger.isDebugEnabled()).thenReturn(true);
    Mockito.when(slf4jLogger.isErrorEnabled()).thenReturn(false);
    Mockito.when(slf4jLogger.isWarnEnabled()).thenReturn(true);

    mavenLog.info(logMessage);
    mavenLog.info(logException);
    mavenLog.info(logMessage, logException);

    mavenLog.debug(logMessage);
    mavenLog.debug(logException);
    mavenLog.debug(logMessage, logException);

    mavenLog.warn(logMessage);
    mavenLog.warn(logException);
    mavenLog.warn(logMessage, logException);

    mavenLog.error(logMessage);
    mavenLog.error(logException);
    mavenLog.error(logMessage, logException);

    Assert.assertTrue(mavenLog.isDebugEnabled());
    Assert.assertTrue(mavenLog.isWarnEnabled());
    Assert.assertTrue(mavenLog.isInfoEnabled());
    Assert.assertFalse(mavenLog.isErrorEnabled());

    Mockito.verify(slf4jLogger, Mockito.times(1)).info(logMessage);
    Mockito.verify(slf4jLogger, Mockito.times(2)).info(logMessage, logException);

    Mockito.verify(slf4jLogger, Mockito.times(1)).debug(logMessage);
    Mockito.verify(slf4jLogger, Mockito.times(2)).debug(logMessage, logException);

    Mockito.verify(slf4jLogger, Mockito.times(1)).warn(logMessage);
    Mockito.verify(slf4jLogger, Mockito.times(2)).warn(logMessage, logException);

    Mockito.verify(slf4jLogger, Mockito.times(1)).error(logMessage);
    Mockito.verify(slf4jLogger, Mockito.times(2)).error(logMessage, logException);
  }
}
