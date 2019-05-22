package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.terraform.TerraformException;

import java.util.Properties;

public interface TerraformOperation<T> {
  public T execute(Properties properties) throws TerraformException;
}
