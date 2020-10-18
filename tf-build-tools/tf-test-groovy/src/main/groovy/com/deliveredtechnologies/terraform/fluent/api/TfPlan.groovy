package com.deliveredtechnologies.terraform.fluent.api

class TfPlan extends TfShow {

    TfPlan(String showJsonOutput) {
        super(showJsonOutput)
    }

    List<Map> getResourcesByType(String type) {
        raw.resource_changes.findAll({it.type == type})
    }

    List<Map> getResourcesBy(Closure predicate) {
        raw.resource_changes.findAll(predicate)
    }

    Map getOutputs() {
        raw.planned_values.outputs
    }
}
