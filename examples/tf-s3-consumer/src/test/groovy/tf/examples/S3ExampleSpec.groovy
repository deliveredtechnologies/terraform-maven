package tf.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.deliveredtechnologies.terraform.api.TerraformApply
import com.deliveredtechnologies.terraform.api.TerraformDestroy
import com.deliveredtechnologies.terraform.api.TerraformInit
import com.deliveredtechnologies.terraform.api.TerraformOutput
import com.deliveredtechnologies.terraform.api.TerraformPlan
import groovy.json.JsonSlurper
import spock.lang.Specification

class S3ExampleSpec extends Specification {
    def "S3 module provisions a bucket in AWS"() {
        given:
            Properties tfProperties = new Properties()

            String region = 'us-east-1'
            String environment = 'dev'
            String stackName = 'tf-examples/example'

            TerraformInit init = new TerraformInit(stackName)
            TerraformApply apply = new TerraformApply(stackName)

            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1").build()

            tfProperties.put('tfRootDir', stackName)
            tfProperties.put('tfVars', "region=${region},environment=${environment}".toString())

        when:
            init.execute(tfProperties)
            apply.execute(tfProperties)
            def jsonOutput = getTerraformOutput(stackName)
            String bucketName = jsonOutput?.bucket?.value?.bucket

        then:
            s3.doesBucketExistV2 bucketName
            s3.getBucketTaggingConfiguration(bucketName).getTagSet().getTag('environment') == environment

        cleanup:
            TerraformDestroy destroy = new TerraformDestroy(stackName)
            destroy.execute(tfProperties)
    }

    private def getTerraformOutput(String stackName) {
        TerraformOutput output = new TerraformOutput(stackName)
        String tfOutput = output.execute(new Properties())
        JsonSlurper slurper = new JsonSlurper()
        println tfOutput
        slurper.parseText(tfOutput)
    }
}
