package com.deliveredtechnologies.terraform.helpers

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

class HttpsTrustManager implements X509TrustManager {

    private static TrustManager[] trustManagers = new TrustManager[]{new HttpsTrustManager()}


    @Override
    void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0]
    }

    public static void allowAllSSL() {
        HttpsURLConnection.setDefaultHostnameVerifier({arg0, argr1 -> true})
        HttpsURLConnection.setDefaultSSLSocketFactory(SSLContext.getInstance("TLS").init(null, trustManagers, new SecureRandom()))
    }
}
