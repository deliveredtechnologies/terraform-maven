package com.deliveredtechnologies.terraform.api;

import com.deliveredtechnologies.io.Executable;
import com.deliveredtechnologies.terraform.TerraformCommand;
import com.deliveredtechnologies.terraform.TerraformCommandLineDecorator;
import com.deliveredtechnologies.terraform.TerraformException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * API for terraform plan.
 */
public class TerraformPlan implements TerraformOperation<String> {

  private Executable terraform;
  private ObjectMapper mapper = new ObjectMapper();

  enum TerraformPlanParam {
    tfVars("var"),
    tfVarFiles("var-file"),
    lockTimeout("lock-timeout"),
    target("target"),
    planOutputFile("out"),
    planInput("input"),
    refreshState("refresh"),
    tfState("state"),
    noColor("no-color"),
    destroyPlan("destroy"),
    timeout("timeout");

    Optional<String> name = Optional.empty();
    String property;

    TerraformPlanParam(String name) {
      this.property = this.toString();
      this.name = Optional.of(name);
    }

    @Override
    public String toString() {
      return name.orElse(super.toString());
    }
  }

  TerraformPlan(Executable terraform) {
    this.terraform = terraform;
  }

  TerraformPlan(Executable terraform, Logger logger) {
    this.terraform = terraform;
    this.terraform.setLogger(logger);
  }

  public TerraformPlan() throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.PLAN));
  }

  public TerraformPlan(Logger logger) throws IOException {
    this(new TerraformCommandLineDecorator(TerraformCommand.PLAN, logger));
  }

  public TerraformPlan(String tfRootDir) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.PLAN, tfRootDir));
  }

  public TerraformPlan(String tfRootDir, Logger logger) throws IOException, TerraformException {
    this(new TerraformCommandLineDecorator(TerraformCommand.PLAN, tfRootDir), logger);
  }

  /**
   * Executes terraform plan.
   * <p>
   *   Valid Properties: <br>
   *   tfVars - a comma delimited list of terraform variables<br>
   *   varFiles - a comma delimited list of terraform vars files<br>
   *   lockTimeout - state file lock timeout<br>
   *   target - resource target<br>
   *   planInput - ask for input for variables not directly set<br>
   *   planOutputFile - path to save the generated execution plan<br>
   *   refreshState - if true then refresh the state prior to running plan<br>
   *   tfState - path to the state file; defaults to "terraform.tfstate"<br>
   *   noColor - remove color encoding from output<br>
   *   destroyPlan - if set then output a destroy plan<br>
   * </p>
   * @param properties  parameter options and properties for terraform apply
   * @return            the output of terraform apply
   * @throws TerraformException
   */
  @Override
  public String execute(Properties properties) throws TerraformException {
    StringBuilder options = new StringBuilder();

    for (TerraformPlanParam param : TerraformPlanParam.values()) {
      if (properties.containsKey(param.property)) {
        if (param == TerraformPlanParam.tfVarFiles) {
          for (String file : (properties.getProperty(param.property)).split(",")) {
            options.append(String.format("-%1$s=%2$s ", param, file.trim()));
          }
          continue;
        }
        if (param == TerraformPlanParam.tfVars) {

          Object value = properties.get(param.property);

          if (value instanceof Map) {

            ((Map) value ).forEach((key, val) -> {
              try {
                options.append(String.format("-%s '%s=%s' ", param, key, val instanceof String ? val : mapper.writeValueAsString(val)));
              } catch (JsonProcessingException e) {
                e.printStackTrace();
              }
            });
          }
          if (value instanceof String) {
            for (String var : (properties.get(param.property)).toString().split(",")) {
              options.append(String.format("-%1$s '%2$s' ", param, var.trim()));
            }
          }
          continue;
        }
        switch (param) {
          case planOutputFile:
            options.append(String.format("-%1$s=%2$s ", param, properties.getProperty(param.property).split("/")[properties.getProperty(param.property).split("/").length - 1]));
            break;
          case destroyPlan:
          case noColor:
            options.append(String.format("-%1$s ", param));
            break;
          case timeout:
            break;
          case lockTimeout:
          default:
            options.append(String.format("-%1$s=%2$s ", param, properties.get(param.property)));
        }
      }
    }

    if (!properties.containsKey(TerraformPlanParam.planInput.property)) {
      options.append((String.format("-%1$s=false ", TerraformPlanParam.planInput.toString())));
    }

    try {
      if (properties.containsKey(TerraformPlanParam.timeout.property)) {
        return terraform.execute(options.toString(), Integer.parseInt(properties.get(TerraformPlanParam.timeout.property).toString()));
      } else {
        return terraform.execute(options.toString());
      }
    } catch (InterruptedException | IOException e) {
      throw new TerraformException(e.getMessage(), e);
    }
  }
}
