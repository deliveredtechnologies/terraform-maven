package com.deliveredtechnologies.terraform.api.converters;


public class TfVarFilesOptionFormatter implements TfOptionFormatter {

  /**
   * Formats Tfvarfiles to command line options.
   * @param format Option type being converted
   * @param value to format from
   * @return
   */
  public String convert(String format, Object value) {
    String str = "";
    if (value instanceof String) {
      str = csvConverter("-var-file=%s ", (String) value);
    }
    return str;
  }
}
