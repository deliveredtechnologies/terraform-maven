package com.deliveredtechnologies.terraform.fluent.api

import groovy.json.JsonSlurper

abstract class TfShow {

    Map raw
    JsonSlurper slurper = new JsonSlurper()

    TfShow(String showJsonOutput) {
        raw = (Map) slurper.parseText(showJsonOutput)

    }

    List<Map> flattenModules(Map map) {
        map.collect { k, v ->
            if ( k == "resources") {
                return convertToAbsoluteAddress(map.getOrDefault("address", ""), v)
            }
            if ( k == "child_modules") {
                return v.collect {
                    flattenModules(it)
                }
            }
            []
        }.flatten() as List<Map>
    }

    List<Map> convertToAbsoluteAddress(String moduleAddress, List<Map> resources) {
        if (!moduleAddress.empty) {
            resources.each {it.address = "${moduleAddress}.${it.address}".toString() }
        }
        return resources
    }
}
