package com.deliveredtechnologies.terraform.api.converters;

import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformOption;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;


public class TfCliOptionBuilder {

  //TODO implement multikey lookup to consolidate csv and map converters
  private Map<String, TfOptionFormatter> registry;

  public TfCliOptionBuilder(Map<String, TfOptionFormatter> registry) {
    this.registry = registry;
  }

  /**
   * Converts Tf params to command line options string.
   *
   * @param params
   * @param properties
   * @return
   */
  public String convert(TerraformOption[] params, Properties properties) throws TerraformException {
    StringBuilder sb = new StringBuilder();

    for (TerraformOption param : params) { //iterate over all the supported parameters associated with the operation

      if (properties.containsKey(param.getPropertyName()) || param.getDefault() != null) {
        //get converter from registry
        TfOptionFormatter converter = Optional.ofNullable(registry.get(param.toString().toLowerCase()))
            .orElse(registry.get("default"));
        sb.append(converter.convert(param.getFormat(), properties.getOrDefault(param.getPropertyName(), param.getDefault())));
      }
    }
    return sb.toString();
  }

  /**
   * Static Factory to build registry of Fomatters using reflection.
   * @return
   */
  public static Map<String, TfOptionFormatter> initializeFormatterRegistry() {
    Map<String, TfOptionFormatter> registry = new HashMap<>();
    Reflections reflections = new Reflections(TfOptionFormatter.class.getPackage().getName());
    Set<Class<? extends TfOptionFormatter>> classes = reflections.getSubTypesOf(TfOptionFormatter.class);

    for (Class clazz : classes) {
      try {
        TfOptionFormatter converter = (TfOptionFormatter) clazz.newInstance();
        registry.put(converter.getClass().getSimpleName().toLowerCase().replace("optionformatter", ""), converter);
      } catch (InstantiationException e) {
        //TODO what to do here
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return registry;
  }
}
