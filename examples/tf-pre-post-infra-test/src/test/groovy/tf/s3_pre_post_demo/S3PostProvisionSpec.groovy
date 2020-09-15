package tf.s3_pre_post_demo

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.*
import com.deliveredtechnologies.terraform.fluent.api.Terraform
import com.deliveredtechnologies.terraform.fluent.api.TfState
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class S3PostProvisionSpec extends Specification {

    @Shared
    Terraform terraform

    @Shared
    AmazonS3Client s3Client = AmazonS3ClientBuilder.defaultClient()

    @Shared
    TfState tfState

    @Unroll("#s3Bucket.values.id @#s3Bucket.address acls not public")
    def "S3 verify acls not public"() {

        AccessControlList acl = s3Client.getBucketAcl(s3Bucket.values.id)

        expect:
        !hasAcl( acl, GroupGrantee.AllUsers, Permission.Read, Permission.Write, Permission.FullControl)
        !hasAcl( acl, GroupGrantee.AuthenticatedUsers , Permission.Read, Permission.Write, Permission.FullControl)

        where:
        s3Bucket << tfState.getResourcesByType("aws_s3_bucket")
    }

    @Unroll("#s3Bucket.values.id @#s3Bucket.address versioning enabled == true")
    def "S3 versioning enabled"() {

        expect:
        isVersioningEnabled(s3Bucket)

        where:
        s3Bucket << tfState.getResourcesByType("aws_s3_bucket")
    }

    void isVersioningEnabled(Map resource) {
        BucketVersioningConfiguration result = s3Client.getBucketVersioningConfiguration(resource.values.id)
        assert result.status.equalsIgnoreCase("enabled")
    }

    boolean hasAcl(AccessControlList accessControlList, Grantee groupGrantee, Permission... permissions) {
        accessControlList.grantsAsList.any() {
            it.grantee == groupGrantee && permissions.contains(it.permission)
        }
    }

    def setupSpec() {
        terraform = new Terraform().withRootDir('s3_pre_post_demo')
                .withProperties([
                        noColor: true,
                ])
        tfState = terraform.initAndApply()
    }

    def cleanupSpec() {
        terraform.destroy()
    }
}
