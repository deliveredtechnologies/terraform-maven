package com.deliveredtechnologies.terraform.api.converters;

import com.deliveredtechnologies.terraform.TerraformException;
import org.junit.Assert;
import org.junit.Test;

public class DefaultOptionFormatterTest {

  TfOptionFormatter formatter = new DefaultOptionFormatter();

  @Test
  public void convertKeyValueOption() throws TerraformException {
    String format = "-lock-timeout=";
    String value = "10s";

    Assert.assertEquals(formatter.convert(format, value), "-lock-timeout=10s ");
  }

  @Test
  public void convertKeyOption() throws TerraformException {
    String format = "-no-color";
    String value = "any";

    Assert.assertEquals(formatter.convert(format, value), "-no-color ");
  }

  @Test
  public void convertValueOption() throws TerraformException {
    String format = "%s";
    String value = "path/some.plan";

    Assert.assertEquals(formatter.convert(format, value), "path/some.plan ");
  }

}
