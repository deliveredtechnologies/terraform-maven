package com.deliveredtechnologies.terraform.api.converters;

import org.junit.Assert;
import org.junit.Test;

public class DefaultOptionFormatterTest {

  TfOptionFormatter formatter = new DefaultOptionFormatter();

  @Test
  public void convertKeyValueOption() {
    String format = "-lock-timeout=";
    String value = "10s";

    Assert.assertEquals(formatter.convert(format, value), "-lock-timeout=10s ");
  }

  @Test
  public void convertKeyOption() {
    String format = "-no-color";
    String value = "any";

    Assert.assertEquals(formatter.convert(format, value), "-no-color ");
  }

  @Test
  public void convertValueOption() {
    String format = "%s";
    String value = "path/some.plan";

    Assert.assertEquals(formatter.convert(format, value), "path/some.plan ");
  }

}
