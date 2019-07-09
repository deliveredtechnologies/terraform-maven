package tf.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.deliveredtechnologies.terraform.api.TerraformApply
import com.deliveredtechnologies.terraform.api.TerraformDestroy
import com.deliveredtechnologies.terraform.api.TerraformInit
import com.deliveredtechnologies.terraform.api.TerraformOutput
import groovy.json.JsonSlurper
import spock.lang.Specification

class S3Spec extends Specification {
    private Properties tfProperties
    private TerraformInit init = new TerraformInit()
    private TerraformApply apply = new TerraformApply()

    def setup() {
        tfProperties = new Properties()
    }

    def "S3 module provisiones a bucket in AWS"() {
        given:
            String region = 'us-east-1'
            String environment = 'dev'
            String stackName = 's3'
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1").build()
            tfProperties.put('tfRootDir', stackName)
            tfProperties.put('tfVars', "region=${region},environment=${environment}".toString())

        when:
            init.execute(tfProperties)
            apply.execute(tfProperties)
            def jsonOutput = getTerraformOutput(stackName)
            String bucketName = jsonOutput.bucket_arn.value[(jsonOutput.bucket_arn.value.lastIndexOf(":") + 1)..-1]

        then:
            s3.doesBucketExistV2(bucketName)
            s3.getBucketTaggingConfiguration(bucketName).getTagSet().getTag('environment') == environment
    }

    def "Replicated bucket replicates S3 objects from east to west"() {
        given:
            String srcRegion = 'us-east-1'
            String destRegion = 'us-west-1'
            String environment = 'dev'
            String srcStackName = 's3_replicated_src'
            String destStackName = 's3'
            String s3ObjectName = 'test.txt'

            AmazonS3 sourceS3 = AmazonS3ClientBuilder.standard().withRegion(srcRegion).build()
            AmazonS3 destS3 = AmazonS3ClientBuilder.standard().withRegion(destRegion).build()

        when:
            //provision destination bucket
            tfProperties.put('tfVars', "region=${destRegion},environment=${environment}".toString())
            tfProperties.put('tfRootDir', destStackName)
            init.execute(tfProperties)
            apply.execute(tfProperties)
            def destJsonOutput = getTerraformOutput(destStackName)
            String destBucketArn = destJsonOutput.bucket_arn.value
            String destKmsKeyArn = destJsonOutput.kms_key_arn.value

            //provision source bucket with replication to destination bucket
            tfProperties.put('tfVars', "region=${srcRegion},environment=${environment},destination_bucket_arn=${destBucketArn},destination_kms_key_arn=${destKmsKeyArn}".toString())
            tfProperties.put('tfRootDir', srcStackName)
            init.execute(tfProperties)
            apply.execute(tfProperties)
            def srcJsonOutput = getTerraformOutput(destStackName)
            String srcBucketArn = srcJsonOutput.bucket_arn.value
            String srcKmsKeyArn = srcJsonOutput.kms_key_arn.value

            //upload a file to the source region
            sourceS3.putObject(srcJsonOutput.bucket_arn.value[(srcJsonOutput.bucket_arn.value.lastIndexOf(":") + 1)..-1], s3ObjectName, "This is a test!")

            //give a little time to replicate
            sleep(5000)

        then:
            sourceS3.getBucketTaggingConfiguration(bucketName).getTagSet().getTag('environment') == environment
            destS3.doesObjectExist(s3ObjectName)
    }

    def cleanup() {
        def destroy = new TerraformDestroy()
        destroy.execute(tfProperties)
    }

    private def getTerraformOutput(String stackName) {
        TerraformOutput output = new TerraformOutput(stackName)
        String tfOutput = output.execute(tfProperties)
        JsonSlurper slurper = new JsonSlurper()
        slurper.parseText(tfOutput)
    }
}
