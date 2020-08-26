package com.deliveredtechnologies.terraform.fluent.api


import spock.lang.Shared
import spock.lang.Specification

class TfPlanTest extends Specification {

    @Shared
    TfPlan tfPlan

    def setupSpec() {
        tfPlan = new TfPlan(new File("./src/test/resources/tf_show_example.json").text)
    }

    def "GetResourcesByType"() {
        given:
        List<Map> resources = tfPlan.getResourcesByType("aws_s3_bucket")

        expect:
        resources.size() == 1
        resources[0].name == "bucket"

    }

    def "GetResourcesBy"() {
        given:
        List<Map> resources = tfPlan.getResourcesBy({it.type == "aws_s3_bucket"})

        expect:
        resources.size() == 1
        resources[0].name == "bucket"
    }

    def "GetOutputs"() {
        given:
        Map outputs = tfPlan.getOutputs()

        expect:
        outputs.bucket.value.bucket == "bucket-bb8937d0c3d09cab"
    }
}
