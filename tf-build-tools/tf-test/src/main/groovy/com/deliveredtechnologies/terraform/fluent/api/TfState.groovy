package com.deliveredtechnologies.terraform.fluent.api

import groovy.json.JsonSlurper

class TfState {

    Map rawState

    JsonSlurper slurper = new JsonSlurper()

    TfState (String showJsonOutput) {
        rawState = (Map) slurper.parseText(showJsonOutput)
    }
}
