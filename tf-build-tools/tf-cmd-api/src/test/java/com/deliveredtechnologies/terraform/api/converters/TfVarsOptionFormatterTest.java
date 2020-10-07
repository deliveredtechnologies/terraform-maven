package com.deliveredtechnologies.terraform.api.converters;

import com.deliveredtechnologies.terraform.TerraformException;
import org.junit.Assert;
import org.junit.Test;

public class TfVarsOptionFormatterTest {

  TfOptionFormatter formatter = new TfVarsOptionFormatter();

  @Test
  public void convertTfVarCsvOneVar() throws TerraformException {
    String format = "-var";
    String value = "key1=value1";

    Assert.assertEquals(formatter.convert(format, value), "-var \"key1=value1\" ");
  }

  @Test
  public void convertTfVarCsvManyVars() throws TerraformException {
    String format = "-var";
    String value = "key1=value1, key2=value2,key3=value3";

    Assert.assertEquals(formatter.convert(format, value), "-var \"key1=value1\" -var \"key2=value2\" -var \"key3=value3\" ");
  }
}
