package com.deliveredtechnologies.terraform.helpers

import com.amazonaws.services.autoscaling.AmazonAutoScaling
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder
import com.amazonaws.services.autoscaling.model.AmazonAutoScalingException
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult
import net.jodah.failsafe.Failsafe
import net.jodah.failsafe.RetryPolicy
import net.jodah.failsafe.function.CheckedSupplier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration

class Aws {

    AmazonAutoScaling autoScalingClient

    Logger log = LoggerFactory.getLogger(getClass())


    Aws() {
        this.autoScalingClient = AmazonAutoScalingClientBuilder.defaultClient()
    }

    Aws(AmazonAutoScaling autoScalingClient) {
        this.autoScalingClient = autoScalingClient
    }

    Map waitForCapacity(String asgName, Duration delay, maxRetries) {

        RetryPolicy<Object> retryPolicy = new RetryPolicy<>()
            .withDelay(delay)
            .withMaxRetries(maxRetries)

        Map capacityInfo = Failsafe.with(retryPolicy).get({ ->
            Map capacityInfo = getCapacityInfoAsg(asgName)
            if(capacityInfo.currentCapactiy != capacityInfo.desiredCapacity) {
                log.info("Capacity Not Met")
                throw new AmazonAutoScalingException("Capacity Not Met")
            }
            log.info("Capacity Met")
            return capacityInfo
        } as CheckedSupplier)
        return capacityInfo
    }

    Map getCapacityInfoAsg(String asgName) {
        DescribeAutoScalingGroupsResult result = autoScalingClient.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest().withAutoScalingGroupNames(asgName))

        def groups = result.getAutoScalingGroups()
        if(groups.size() == 0) {
            throw new AmazonAutoScalingException("Autoscaling Group does not exist")
        }
        def group = groups[0]
        [
                minSize : group.minSize,
                maxSize : group.maxSize,
                desiredCapacity : group.desiredCapacity,
                currentCapactiy : group.instances.size()
        ]
    }

}
