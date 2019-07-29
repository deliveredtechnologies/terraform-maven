#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.deliveredtechnologies.terraform.api.TerraformApply
import com.deliveredtechnologies.terraform.api.TerraformDestroy
import com.deliveredtechnologies.terraform.api.TerraformInit
import com.deliveredtechnologies.terraform.api.TerraformOutput
import com.deliveredtechnologies.terraform.api.TerraformPlan
import groovy.json.JsonSlurper
import spock.lang.Specification

class S3Spec extends Specification {

    def "S3 module provisions a bucket in AWS"() {
        given:
            Properties tfProperties = new Properties()

            String region = 'us-east-1'
            String environment = 'dev'
            String stackName = 's3'

            TerraformInit init = new TerraformInit(stackName)
            TerraformApply apply = new TerraformApply(stackName)

            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1").build()

            tfProperties.put('tfRootDir', stackName)
            tfProperties.put('tfVars', "region=${symbol_dollar}{region},environment=${symbol_dollar}{environment}".toString())

        when:
            init.execute(tfProperties)
            apply.execute(tfProperties)
            def jsonOutput = getTerraformOutput(stackName)
            String bucketName = jsonOutput.bucket_arn.value[(jsonOutput.bucket_arn.value.lastIndexOf(":") + 1)..-1]

        then:
            s3.doesBucketExistV2 bucketName
            s3.getBucketTaggingConfiguration(bucketName).getTagSet().getTag('environment') == environment

        cleanup:
            TerraformDestroy destroy = new TerraformDestroy(stackName)
            destroy.execute(tfProperties)
    }

    def "Replicated bucket replicates S3 objects from east to west"() {
        given:
            Properties tfSrcProperties = new Properties()
            Properties tfDestProperties = new Properties()

            String srcRegion = 'us-east-1'
            String destRegion = 'us-west-1'
            String environment = 'dev'
            String srcStackName = 's3_replicated_src'
            String destStackName = 's3'
            String s3ObjectName = 'test.txt'

            TerraformInit initDest = new TerraformInit(destStackName)
            TerraformPlan planDest = new TerraformPlan(destStackName)
            TerraformApply applyDest = new TerraformApply(destStackName)
            TerraformInit initSrc = new TerraformInit(srcStackName)
            TerraformApply applySrc = new TerraformApply(srcStackName)

            AmazonS3 sourceS3 = AmazonS3ClientBuilder.standard().withRegion(srcRegion).build()
            AmazonS3 destS3 = AmazonS3ClientBuilder.standard().withRegion(destRegion).build()

        when:
            //provision destination bucket
            tfDestProperties.put('tfVars', "region=${symbol_dollar}{destRegion},environment=${symbol_dollar}{environment},is_versioned=true".toString())

            initDest.execute(tfDestProperties)
            applyDest.execute(tfDestProperties)
            def destJsonOutput = getTerraformOutput(destStackName)
            String destBucketArn = destJsonOutput.bucket_arn.value
            String destKmsKeyArn = destJsonOutput.kms_key_arn.value
            String destBucketName = destBucketArn[(destBucketArn.lastIndexOf(":") + 1)..-1]

            //provision source bucket with replication to destination bucket
            tfSrcProperties.put('tfVars', "region=${symbol_dollar}{srcRegion},environment=${symbol_dollar}{environment},destination_bucket_arn=${symbol_dollar}{destBucketArn},destination_kms_key_arn=${symbol_dollar}{destKmsKeyArn}".toString())

            initSrc.execute(tfSrcProperties)
            applySrc.execute(tfSrcProperties)

            def srcJsonOutput = getTerraformOutput(destStackName)
            String srcBucketArn = srcJsonOutput.bucket_arn.value
            String srcBucketName = srcBucketArn[(srcBucketArn.lastIndexOf(":") + 1)..-1]

            //upload a file to the source region
            sourceS3.putObject(srcBucketName, s3ObjectName, "This is a test!")

            //give a little time to replicate
            sleep(5000)

        then:
            sourceS3.getBucketTaggingConfiguration(srcBucketName).getTagSet().getTag('environment') == environment
            destS3.getBucketTaggingConfiguration(destBucketName).getTagSet().getTag('environment') == environment
            destS3.doesObjectExist(destBucketName, s3ObjectName)

        cleanup:
            TerraformDestroy destroySrc = new TerraformDestroy(srcStackName)
            TerraformDestroy destroyDest = new TerraformDestroy(destStackName)

            destroySrc.execute(tfSrcProperties)
            destroyDest.execute(tfDestProperties)
    }

    private def getTerraformOutput(String stackName) {
        TerraformOutput output = new TerraformOutput(stackName)
        String tfOutput = output.execute(new Properties())
        JsonSlurper slurper = new JsonSlurper()
        slurper.parseText(tfOutput)
    }
}
