package com.deliveredtechnologies.terraform.helpers

import net.jodah.failsafe.Failsafe
import net.jodah.failsafe.RetryPolicy
import net.jodah.failsafe.function.CheckedSupplier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.util.function.Consumer

class Http {

    private final Logger log = LoggerFactory.getLogger(getClass())

    private Http(){
        throw new IllegalStateException("Static utility class")
    }

    static Map httpGetWithCustomValidation(String url, Consumer<Integer> statusCode, Consumer<String> response, Proxy proxy = Proxy.NO_PROXY) {
        def connection = new URL(url).openConnection(proxy)
        def body = connection.content.text
        int responseCode = connection.responseCode

        statusCode.accept(responseCode)
        response.accept(body)
        [statusCode: statusCode, body: body]
    }

    static Map httpGetRetryWithCustomValidation(String url, Duration delay, int maxRetries, Consumer<Integer> statusCode, Consumer<String> response, Proxy proxy = Proxy.NO_PROXY) {
        RetryPolicy<Object> retryPolicy = new RetryPolicy<>()
            .withDelay(delay)
            .withMaxRetries(maxRetries)

        Map res = Failsafe.with(retryPolicy).get({ ->
            def r = httpGetWithCustomValidation(url, statusCode, response, proxy)

        } as CheckedSupplier)
        return res
    }
}
