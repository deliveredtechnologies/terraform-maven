package com.deliveredtechnologies.terraform.api.converters;

import com.deliveredtechnologies.terraform.TerraformException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Interface for formatting Terraform CommandLine Options.
 */
public interface TfOptionFormatter {

  /**
   * Format values to the Terraform command line representation.
   * @param format Option type being converted
   * @param value to format from
   * @return fomatted string
   */
  String convert(String format, Object value) throws TerraformException;

  /**
   * Helper to convert CSV values.
   * @param format format to place values
   * @param csv string to be split
   * @return formatted string
   */
  default String csvConverter(String format, String csv) {
    StringBuilder sb = new StringBuilder();
    for (String var : csv.split(",")) {
      sb.append(String.format(format, var.trim()));
    }
    return sb.toString();
  }

  /**
   * Converts a map to command line options.
   * @param format option name.
   * @param map
   * @return
   * @throws TerraformException
   */
  default String convertMapToCommandLineOptions(String format, Map<String,Object> map) throws TerraformException {
    ObjectMapper mapper = new ObjectMapper();
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String,Object> entry: map.entrySet()) {
      Object val = entry.getValue();
      try {
        sb.append(String.format("%s \"%s=%s\" ", format , entry.getKey(), val instanceof String ? val : mapper.writeValueAsString(val)));
      } catch (JsonProcessingException e) {
        throw new TerraformException(e);
      }
    }
    return sb.toString();
  }
}
