package com.deliveredtechnologies.terraform.api.converters;


public class DefaultOptionFormatter implements TfOptionFormatter {

  /**
   * Handles the default case of option formatting.
   * 1) if the param.format contains '=' then clioption=value  i.e. (-refresh=true)
   * 2) if the param.format equals '%s' then value  i.e. (someplan.tfplan)
   * 3) else param.format i.e. (-no-color)
   * @param format Option type being converted
   * @param value to convert from
   * @return formatted cli options
   */
  @Override
  public String convert(String format, Object value) {

    if (format.contains("=")) {
      return String.format("%s%s ", format, value);
    } else if (format.equals("%s")) {
      return String.format(format + " ", value);
    } else {
      return String.format("%s ", format);
    }
  }
}
