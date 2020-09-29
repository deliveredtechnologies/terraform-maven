package com.deliveredtechnologies.terraform.helpers

import spock.lang.Specification

import javax.net.ssl.HttpsURLConnection
import java.time.Duration

class HttpTest extends Specification {
//    def "HttpGetWithCustomValidation"() {
////        URL anyUrl = GroovySpy(global: true, constructorArgs: ["http://someUrl"])
////        URLConnection connection = Mock()
//
//        when:
//        Http.httpGetWithCustomValidation("http://google.com", {status -> assert status == 200}, {response -> assert response.contains("green")})
//
//        then:
//        1 == 1
////        anyUrl.openConnection(_) >> connection
////        1 * connection.content >> "this is green"
////        1 * connection.responseCode >> 200
//
//    }

//    def "HttpGetWithCustomValidation"() {
//        URL anyUrl = GroovySpy(global: true, constructorArgs: ["http://someUrl"])
//        URLConnection connection = Mock(HttpsURLConnection)
//
//        when:
//        Http.httpGetWithCustomValidation("http://someUrl", {status -> assert status == 200}, {response -> assert response.contains("green")})
//
//        then:
//        anyUrl.openConnection(_) >> connection
//        1 * connection.content >> new ByteArrayInputStream("this is green".getBytes())
//        1 * connection.responseCode >> 200
//
//    }

    def "HttpGetRetryWithCustomValidation"() {

        URL anyUrl = GroovySpy(global: true, constructorArgs: ["http://someUrl"])
        URLConnection connection = Mock(HttpsURLConnection)

        when:
        Http.httpGetRetryWithCustomValidation("http://someUrl",
                Duration.ofMillis(1000),
                1,
                {assert 1==2},
                {response -> assert response.contains("green")})

        then:
        anyUrl.openConnection(_) >> connection
        1 * connection.content >> new ByteArrayInputStream("Not found".getBytes())
        1 * connection.responseCode >> 404

        then:
        //anyUrl.openConnection(_) >> connection
        1 * connection.content >> new ByteArrayInputStream("Ho Ho Ho green giant".getBytes())
        1 * connection.responseCode >> 200
        0 * connection._
    }

}
