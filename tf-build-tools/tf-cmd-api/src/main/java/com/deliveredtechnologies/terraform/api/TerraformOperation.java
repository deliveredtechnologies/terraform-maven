package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.terraform.TerraformException;

import java.util.Map;
import java.util.Properties;

public interface TerraformOperation<T> {
  T execute(Properties properties) throws TerraformException;

  /**
   * Convenience method that converts a map to properties.
   * @param properties Map of Terraform configuration values
   * @return T
   * @throws TerraformException
   */
  default T execute(Map properties) throws TerraformException {
    Properties props = new Properties();
    props.putAll(properties);
    return execute(props);
  }
}
