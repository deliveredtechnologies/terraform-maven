package com.deliveredtechnologies.terraform.helpers

import com.amazonaws.services.autoscaling.AmazonAutoScaling
import com.amazonaws.services.autoscaling.model.AmazonAutoScalingException
import com.amazonaws.services.autoscaling.model.AutoScalingGroup
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult
import com.amazonaws.services.autoscaling.model.Instance
import spock.lang.Shared
import spock.lang.Specification

import java.time.Duration

class AwsTest extends Specification {


    @Shared
    Aws aws = new Aws()

    AmazonAutoScaling asc = Stub()
    AutoScalingGroup asg = Mock()

    def setup() {
        aws.client = asc
    }

    def "WaitForCapacityMetWithinRetryLimit"() {

        given:
        DescribeAutoScalingGroupsResult describeAutoScalingGroupsResult = new DescribeAutoScalingGroupsResult()
        describeAutoScalingGroupsResult.withAutoScalingGroups(asg)
        asc.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
        aws.waitForCapacity("foo", Duration.ofMillis(1000), 1)

        then: "Capacity not met. Instances size < Current capacity"
        1 * asg.minSize >> 1
        1 * asg.maxSize >> 3
        1 * asg.desiredCapacity >> 3
        1 * asg.instances >> Arrays.asList(new Instance(), new Instance())

        then: "Capacity met. Instances size == Current capacity"
        1 * asg.minSize >> 1
        1 * asg.maxSize >> 3
        1 * asg.desiredCapacity >> 3
        1 * asg.instances >> Arrays.asList(new Instance(), new Instance(), new Instance())
        //Strict mock no other interactions with asg mock
        0 * asg._
    }

    def "WaitForCapacityNotMetWithinRetryLimit"() {

        given:
        DescribeAutoScalingGroupsResult describeAutoScalingGroupsResult = new DescribeAutoScalingGroupsResult()
        describeAutoScalingGroupsResult.withAutoScalingGroups(asg)
        asc.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
        aws.waitForCapacity("foo", Duration.ofMillis(1000), 0)

        then: "Capacity not met. Instances size < Current capacity"
        1 * asg.minSize >> 1
        1 * asg.maxSize >> 3
        1 * asg.desiredCapacity >> 3
        1 * asg.instances >> Arrays.asList(new Instance(), new Instance())
        def e = thrown(AmazonAutoScalingException)
        0 * asg._
    }

    def "WaitForCapacityAsgDoesNotExist"() {

        given:
        DescribeAutoScalingGroupsResult describeAutoScalingGroupsResult = new DescribeAutoScalingGroupsResult()
        asc.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
        aws.waitForCapacity("foo", Duration.ofMillis(1000), 0)

        then: "Capacity not met. Instances size < Current capacity"
        def e = thrown(AmazonAutoScalingException)
        0 * asg._
    }

    def "GetCapacityInfoAsg"() {
    }
}
