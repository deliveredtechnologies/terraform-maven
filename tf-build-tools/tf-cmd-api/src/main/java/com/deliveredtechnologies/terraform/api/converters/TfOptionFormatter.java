package com.deliveredtechnologies.terraform.api.converters;

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
  String convert(String format, Object value);

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
}
