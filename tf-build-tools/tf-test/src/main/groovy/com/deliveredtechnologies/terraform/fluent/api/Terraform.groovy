package com.deliveredtechnologies.terraform.fluent.api

import com.deliveredtechnologies.terraform.api.TerraformApply
import com.deliveredtechnologies.terraform.api.TerraformDestroy
import com.deliveredtechnologies.terraform.api.TerraformInit
import com.deliveredtechnologies.terraform.api.TerraformOutput
import com.deliveredtechnologies.terraform.api.TerraformPlan
import com.deliveredtechnologies.terraform.api.TerraformShow

class Terraform {

    String rootDir
    TerraformInit init
    TerraformPlan plan
    TerraformApply apply
    TerraformOutput output
    TerraformShow show
    TerraformDestroy destroy
    Map properties = new HashMap()
    TfPlan tfplan
    TfState tfState
    String defaultPlanOutputPath

    Terraform withRootDir(String rootDir) {
        this.rootDir = rootDir
        this.defaultPlanOutputPath = "terraform.tfplan"
        this.init = new TerraformInit(rootDir)
        this.plan = new TerraformPlan(rootDir)
        this.apply = new TerraformApply(rootDir)
        this.output = new TerraformOutput(rootDir)
        this.show = new TerraformShow(rootDir)
        this.destroy = new TerraformDestroy(rootDir)
        return this

    }

    Terraform withProperties(Map tfProperties) {
        if(!tfProperties.containsKey(TerraformPlan.Option.planOutputFile.getPropertyName())) {
            properties.put(TerraformPlan.Option.planOutputFile.getPropertyName(), defaultPlanOutputPath)

        }
        properties.putAll(tfProperties)
        return this
    }

    String init() {
        return init.execute(properties)
    }

    TfPlan initAndPlan() {
        properties.put(TerraformShow.Option.path.getPropertyName(), defaultPlanOutputPath)
        init.execute(properties)
        plan.execute(properties)
        tfplan = new TfPlan(show.execute(properties))
        return tfplan
    }

    TfState initAndApply() {
        initAndPlan()
        properties.put(TerraformApply.Option.plan.getPropertyName(), defaultPlanOutputPath)
        apply.execute(properties)
        properties.put(TerraformShow.Option.path.getPropertyName(), "terraform.tfstate")
        tfState = new TfState(show.execute(properties))
        return tfState
    }

}
