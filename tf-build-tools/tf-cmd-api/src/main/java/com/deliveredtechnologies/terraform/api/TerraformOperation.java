package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.terraform.TerraformException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;
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

  /**
   * Converts a map to command line options.
   * @param name option name.
   * @param map
   * @return
   * @throws TerraformException
   */
  default StringBuilder convertMapToCommandLineOptions(String name, Map<String,Object> map) throws TerraformException {
    ObjectMapper mapper = new ObjectMapper();
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String,Object> entry: map.entrySet()) {
      Object val = entry.getValue();
      try {
        sb.append(String.format("-%s '%s=%s' ", name, entry.getKey(), val instanceof String ? val : mapper.writeValueAsString(val)));
      } catch (JsonProcessingException e) {
        throw new TerraformException(e);
      }
    }
    return sb;
  }
}
