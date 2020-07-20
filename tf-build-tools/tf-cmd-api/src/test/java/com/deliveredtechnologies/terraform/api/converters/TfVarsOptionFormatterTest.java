package com.deliveredtechnologies.terraform.api.converters;

import org.junit.Assert;
import org.junit.Test;

public class TfVarsOptionFormatterTest {

  TfOptionFormatter formatter = new TfVarsOptionFormatter();

  @Test
  public void convertTfVarCsvOneVar() {
    String format = "-var";
    String value = "key1=value1";

    Assert.assertEquals(formatter.convert(format, value), "-var 'key1=value1' ");
  }

  @Test
  public void convertTfVarCsvManyVars() {
    String format = "-var";
    String value = "key1=value1, key2=value2,key3=value3";

    Assert.assertEquals(formatter.convert(format, value), "-var 'key1=value1' -var 'key2=value2' -var 'key3=value3' ");
  }
}
