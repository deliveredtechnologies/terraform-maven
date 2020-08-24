package com.deliveredtechnologies.terraform.fluent.api

import groovy.json.JsonSlurper

class TfPlan {

    Map rawPlan

    JsonSlurper slurper = new JsonSlurper()

    TfPlan(String showJsonOutput) {
        rawPlan = (Map) slurper.parseText(showJsonOutput)
    }

    List<Map> getResourcesByType(String type) {
        rawPlan.resource_changes.findAll({it.type == type})
    }

    List<Map> getResourcesBy(Closure predicate) {
        rawPlan.resource_changes.findAll(predicate)
    }

    Map getOutputs() {
        rawPlan.planned_values.outputs
    }

}
