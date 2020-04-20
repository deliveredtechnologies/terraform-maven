package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.terraform.TerraformException;

import java.io.IOException;
import java.util.Properties;

public interface TerraformOperation<T> {
  public T execute(Properties properties) throws TerraformException, IOException, InterruptedException;
}
