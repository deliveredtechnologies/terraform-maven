package com.deliveredtechnologies.terraform.fluent.api

class TfState extends TfShow {

    List<Map> resources

    TfState (String showJsonOutput) {
        super(showJsonOutput)
        resources = flattenModules(raw.values.root_module)
    }

    List<Map> getResourcesByType(String type) {
        resources.findAll({it.type == type})
    }

    List<Map> getResourcesBy(Closure predicate) {
        resources.findAll(predicate)
    }

    Map getOutputs() {
        raw.values.outputs
    }
}
