package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformOperation;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


/**
 * Abstract class to invoke TerraformOperations.
 * @param <T>
 */
public abstract class TerraformMojo<T> extends AbstractMojo {

  /**
   * Invokes a TerraformOperation w/properties.
   *
   * @param tfOperation the TerraformOperation to invoke
   * @param properties  the properties passed to the TerraformOperation
   * @throws MojoExecutionException
   */
  protected final void execute(TerraformOperation<T> tfOperation, Properties properties) throws MojoExecutionException {
    try {
      tfOperation.execute(properties);
    } catch (TerraformException e) {
      throw new MojoExecutionException("Failed to execute terraform operation", e);
    }
  }

  /**
   * Invokes a TerraformOperation using the fields of the TerraformMojo object as the properties.
   *
   * @param tfOperation the TerraformOperation to invoke
   * @throws MojoExecutionException
   */
  protected final void execute(TerraformOperation<T> tfOperation) throws MojoExecutionException {
    execute(tfOperation, getFieldsAsProperties());
  }

  /**
   * Gets the fields of the TerraformMojo object as a Properties object.
   *
   * @return [fieldName = fieldValue] Properties object
   * @throws MojoExecutionException
   */
  protected final Properties getFieldsAsProperties() throws MojoExecutionException {
    Class clazz = this.getClass();
    List<Field> fieldList = Arrays.stream(clazz.getDeclaredFields())
        .collect(Collectors.toList());
    Properties properties = new Properties();

    for (Field field : fieldList) {
      try {
        field.setAccessible(true);
        Object fieldVal = field.get(this);
        if (fieldVal == null
            || (fieldVal.equals(false) && !field.getName().startsWith("refresh"))
            || (Number.class.isAssignableFrom(fieldVal.getClass()) && ((Number)fieldVal).longValue() <= 0)) {
          continue;
        }
        properties.put(field.getName(), field.get(this));
      } catch (NullPointerException npe) {
        //the field is null; just move along
      } catch (IllegalAccessException iae) {
        throw new MojoExecutionException("Unable to access " + field.getName() + " from Mojo!", iae);
      }
    }
    return properties;
  }
}
