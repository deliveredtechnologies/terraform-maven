package com.deliveredtechnologies.terraform.fluent.api

import spock.lang.Specification

class TerraformSpec extends Specification {


    def "init"() {

        Terraform terraform = new Terraform().withRootDir(new File("src/test/resources/tf/").getAbsolutePath())
            .withProperties([noColor: true])
        String result = terraform.init()
        println result

        expect:

        result.contains("Terraform has been successfully initialized!")
    }

    def "initAndPlan"() {
        Terraform terraform = new Terraform().withRootDir(new File("src/test/resources/tf/").getAbsolutePath())
                .withProperties([noColor: true])

        TfPlan tfPlan = terraform.initAndPlan()
        Map rawPlan = tfPlan.raw

        expect:

        rawPlan.output_changes.hello.after == "world"
    }

    def "initAndApply"() {
        Terraform terraform = new Terraform().withRootDir(new File("src/test/resources/tf/").getAbsolutePath())
                .withProperties([noColor: true])

        TfState tfState = terraform.initAndApply()
        Map rawState = tfState.raw

        expect:

        rawState.values.outputs.hello.value == "world"

        cleanup:
        terraform.destroy()
    }

}
