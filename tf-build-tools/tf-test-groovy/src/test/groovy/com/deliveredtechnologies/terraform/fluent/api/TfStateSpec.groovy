package com.deliveredtechnologies.terraform.fluent.api


import spock.lang.Shared
import spock.lang.Specification

class TfStateSpec extends Specification {


    @Shared
    TfState tfState

    def setupSpec() {
        tfState = new TfState(new File("./src/test/resources/terraform.tfstate.json").text)
    }

    def "GetResourcesByType"() {
        given:
        List<Map> resources = tfState.getResourcesByType("aws_s3_bucket")

        expect:
        resources.size() == 3
        resources[0].type == "aws_s3_bucket"
        resources[1].type == "aws_s3_bucket"
        resources[2].type == "aws_s3_bucket"

    }

    def "GetResourcesBy"() {
        given:
        List<Map> resources = tfState.getResourcesBy({it.type == "aws_s3_bucket"})

        expect:
        resources.size() == 3
        resources[0].type == "aws_s3_bucket"
        resources[1].type == "aws_s3_bucket"
        resources[2].type == "aws_s3_bucket"
    }

    def "GetOutputs"() {
        given:
        Map outputs = tfState.getOutputs()

        expect:
        outputs.bucket_names.value.size() == 3
        outputs.bucket_names.value.contains("tftest-bucket1")
        outputs.bucket_names.value.contains("tftest-bucket2")
        outputs.bucket_names.value.contains("tftest-bucket3")
    }

}
