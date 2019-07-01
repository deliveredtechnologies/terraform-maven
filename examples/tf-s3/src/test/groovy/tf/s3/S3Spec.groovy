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
    private def jsonOutput
    private TerraformInit init = new TerraformInit()
    private TerraformApply apply = new TerraformApply()
    private TerraformOutput output = new TerraformOutput()

    def setup() {
        tfProperties = new Properties()
        tfProperties.put("tfRootDir", "s3")

        String tfOutput = output.execute([tfProperties])
        JsonSlurper slurper = new JsonSlurper()
        jsonOutput = slurper.parseText(tfOutput)
    }

    def "S3 module provisiones a bucket in AWS"() {
        given:
            init.execute(tfProperties)
            apply.execute(tfProperties)

        when:
            AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient()
            String bucketName = jsonOutput.bucket_arns.value[(jsonOutput.bucket_arns.value.lastIndexOf(":") + 1)..-1]

        then:
            s3.doesBucketExistV2(bucketName)
    }

    def cleanup() {
        def destroy = new TerraformDestroy()
        destroy.execute(tfProperties)
    }
}
