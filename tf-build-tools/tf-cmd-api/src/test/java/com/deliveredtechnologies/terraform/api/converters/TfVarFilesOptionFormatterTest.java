package com.deliveredtechnologies.terraform.api.converters;

import com.deliveredtechnologies.terraform.TerraformException;
import org.junit.Assert;
import org.junit.Test;

public class TfVarFilesOptionFormatterTest {

  TfOptionFormatter formatter = new TfVarFilesOptionFormatter();

  @Test
  public void convertTfVarFilesCsvOneFile() throws TerraformException {
    String format = "-var-file";
    String value = "test1.txt ";

    Assert.assertEquals(formatter.convert(format, value), "-var-file=test1.txt ");
  }

  @Test
  public void convertTfVarFilesCsvManyFiles() throws TerraformException {
    String format = "-var-file";
    String value = "test1.txt, test2.txt,test3.txt";

    Assert.assertEquals(formatter.convert(format, value), "-var-file=test1.txt -var-file=test2.txt -var-file=test3.txt ");
  }
}
