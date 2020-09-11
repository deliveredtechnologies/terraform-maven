package com.deliveredtechnologies.terraform.fluent.api


import spock.lang.Shared
import spock.lang.Specification

class TfPlanSpec extends Specification {

    @Shared
    TfPlan tfPlan

    def setupSpec() {
        tfPlan = new TfPlan(new File("./src/test/resources/terraform.tfplan.json").text)
    }

    def "getResourcesByType"() {
        given:
        List<Map> resources = tfPlan.getResourcesByType("aws_s3_bucket")

        expect:
        resources.size() == 3
        resources[0].type == "aws_s3_bucket"
        resources[1].type == "aws_s3_bucket"
        resources[2].type == "aws_s3_bucket"

    }

    def "getResourcesBy"() {
        given:
        List<Map> resources = tfPlan.getResourcesBy({it.type == "aws_s3_bucket"})

        expect:
        resources.size() == 3
        resources[0].type == "aws_s3_bucket"
        resources[1].type == "aws_s3_bucket"
        resources[2].type == "aws_s3_bucket"
    }

    def "getOutputs"() {
        given:
        Map outputs = tfPlan.getOutputs()

        expect:
        outputs.bucket_names.value.size() == 3
        outputs.bucket_names.value.contains("tftest-bucket1")
        outputs.bucket_names.value.contains("tftest-bucket2")
        outputs.bucket_names.value.contains("tftest-bucket3")
    }
}
