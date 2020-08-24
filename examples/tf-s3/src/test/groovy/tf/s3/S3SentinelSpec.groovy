package tf.s3


import com.deliveredtechnologies.terraform.fluent.api.Terraform
import com.deliveredtechnologies.terraform.fluent.api.TfPlan
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class S3SentinelSpec extends Specification {

    @Shared
    TfPlan tfplan;
    @Shared
    mandatory_tags = [
            "application_id",
            "stack_name",
            "created_by",
    ]

    @Unroll("#s3Bucket.address has all the mandatory tags")
    def "S3 resources have all mandatory tags"() {

        expect:
        hasMandatoryTags(s3Bucket)

        where:
        s3Bucket << tfplan.getResourcesByType("aws_s3_bucket")
    }

    @Unroll("#s3Bucket.address versioning enabled == true")
    def "S3 versioning enabled"() {

        expect:
        isVersioningEnabled(s3Bucket)

        where:
        s3Bucket << tfplan.getResourcesByType("aws_s3_bucket")
    }

    void isVersioningEnabled(Map resource) {
        Map m = resource.change.after
        assert m.containsKey("versioning")
        assert m.versioning[0].enabled == true
    }

    void hasMandatoryTags(Map resource) {
        mandatory_tags.each {tag ->
            Map resourceTags = resource.change.after.tags
            assert resourceTags.containsKey(tag)
        }
    }

    def setupSpec() {

        Terraform terraform = new Terraform().withRootDir('s3_sentinel_demo')
                .withProperties([
                        noColor: true,
                ])
        tfplan = terraform.initAndPlan()
    }

}
