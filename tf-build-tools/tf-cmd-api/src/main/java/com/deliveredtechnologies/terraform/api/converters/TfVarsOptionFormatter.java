package com.deliveredtechnologies.terraform.api.converters;


import com.deliveredtechnologies.terraform.TerraformException;

import java.util.Map;

public class TfVarsOptionFormatter implements TfOptionFormatter {

  /**
   * Formats Tfvars to command line options.
   * @param format Option type being converted
   * @param value to format from
   * @return
   */
  public String convert(String format, Object value) throws TerraformException {
    String str = "";

    if (value instanceof Map) {
      str = convertMapToCommandLineOptions(format, (Map<String, Object>) value);
    }

    if (value instanceof String) {
      str = csvConverter("-var '%s' ", (String) value);
    }
    return str;
  }
}
