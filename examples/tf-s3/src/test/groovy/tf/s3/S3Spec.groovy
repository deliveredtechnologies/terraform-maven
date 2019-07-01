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
    private Properties tfProperties = new Properties()
    private def jsonOutput

    def setup() {
        tfProperties.put("tfRootDir", "s3")

        def init = new TerraformInit()
        def apply = new TerraformApply()
        def output = new TerraformOutput("s3")

        init.execute(tfProperties)
        apply.execute(tfProperties)
        String tfOutput = output.execute([tfProperties])
        JsonSlurper slurper = new JsonSlurper()
        jsonOutput = slurper.parseText(tfOutput)

    }

    def "S3 module provisiones a bucket in AWS"() {
        given:
            AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient()
            String bucketName = jsonOutput.bucket_arns.value[(jsonOutput.bucket_arns.value.lastIndexOf(":") + 1)..-1]
        when:
            boolean bucketExists = s3.doesBucketExistV2(bucketName)
        then:
            bucketExists
    }

    def cleanup() {
        def destroy = new TerraformDestroy()
        destroy.execute(tfProperties)
    }
}
