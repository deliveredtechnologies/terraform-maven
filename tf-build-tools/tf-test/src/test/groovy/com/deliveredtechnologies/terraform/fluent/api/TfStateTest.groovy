package com.deliveredtechnologies.terraform.fluent.api


import spock.lang.Shared
import spock.lang.Specification

class TfStateTest extends Specification {


    @Shared
    TfState tfState

    def setupSpec() {
        tfState = new TfState(new File("./src/test/resources/tf_show_example.json").text)
    }
}
