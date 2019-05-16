package com.deliveredtechnologies.maven.tf;

import java.util.Properties;

public interface TerraformOperation<T> {
  public T execute(Properties properties) throws TerraformException;
}
