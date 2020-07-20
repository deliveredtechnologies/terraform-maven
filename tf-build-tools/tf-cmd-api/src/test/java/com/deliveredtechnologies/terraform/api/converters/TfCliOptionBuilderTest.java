package com.deliveredtechnologies.terraform.api.converters;

import com.deliveredtechnologies.terraform.api.TerraformApply;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

public class TfCliOptionBuilderTest {


  @Test
  public void convert() throws IOException {
    Properties properties = new Properties();
    properties.put(TerraformApply.Option.tfVarFiles.getPropertyName(), "test1.txt, test2.txt");
    properties.put(TerraformApply.Option.tfVars.getPropertyName(), "key1=value1, key2=value2");
    properties.put(TerraformApply.Option.lockTimeout.getPropertyName(), "1000");
    properties.put(TerraformApply.Option.target.getPropertyName(), "module1.module2");
    properties.put(TerraformApply.Option.noColor.getPropertyName(), "true");
    properties.put(TerraformApply.Option.plan.getPropertyName(), "someplan.tfplan");
    properties.put(TerraformApply.Option.refreshState.getPropertyName(), "true");
    properties.put(TerraformApply.Option.autoApprove.getPropertyName(), "true");


    TfCliOptionBuilder tfCliOptionBuilder = new TfCliOptionBuilder(TfCliOptionBuilder.initializeFormatterRegistry());
    String options = tfCliOptionBuilder.convert(TerraformApply.Option.values(), properties);

    Assert.assertThat(options, CoreMatchers.containsString("-var 'key1=value1' -var 'key2=value2' "));
    Assert.assertThat(options, CoreMatchers.containsString("-var-file=test1.txt -var-file=test2.txt "));
    Assert.assertThat(options, CoreMatchers.containsString("-lock-timeout=1000 "));
    Assert.assertThat(options, CoreMatchers.containsString("-target=module1.module2 "));
    Assert.assertThat(options, CoreMatchers.containsString("-no-color "));
    Assert.assertThat(options, CoreMatchers.containsString("-auto-approve "));
    Assert.assertThat(options, CoreMatchers.containsString("-refresh=true "));
    Assert.assertThat(options, CoreMatchers.endsWith("someplan.tfplan "));
  }
}
