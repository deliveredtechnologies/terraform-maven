package com.deliveredtechnologies.terraform.api.converters;


public class TfVarsOptionFormatter implements TfOptionFormatter {

  /**
   * Formats Tfvars to command line options.
   * @param format Option type being converted
   * @param value to format from
   * @return
   */
  public String convert(String format, Object value) {
    String str = "";
    if (value instanceof String) {
      str = csvConverter("-var '%s' ", (String) value);
    }
    return str;
  }
}
