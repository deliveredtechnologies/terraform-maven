package com.deliveredtechnologies.terraform.api.converters;


public class PlanOutputFileOptionFormatter implements TfOptionFormatter {

  /**
   * Handles the planOutputFile.
   *
   * @param format Option type being converted
   * @param value  to convert from
   * @return formatted cli options
   */
  @Override
  public String convert(String format, Object value) {
    String str = "";
    if (value instanceof String) {
      str = (String) value;
      str = String.format("%s%s ", format, str.split("/")[str.split("/").length - 1]);
    }
    return str;
  }
}
