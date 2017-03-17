package com.mbi.configuration;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.params.HttpParams;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.Collections;

class GatewaySslSocketFactory extends SSLSocketFactory {

    private final String domain;
    private static final String[] PROTOCOLS = new String[]{"TLSv1.2"};

    GatewaySslSocketFactory(String domain, SSLContext sslContext, X509HostnameVerifier hostnameVerifier) {
        super(sslContext, hostnameVerifier);
        this.domain = domain;
    }

    @Override
    public Socket createSocket(HttpParams params) throws IOException {
        SSLSocket sslSocket = (SSLSocket) super.createSocket(params);
        // Set the encryption protocol
        sslSocket.setEnabledProtocols(PROTOCOLS);

        // Configure SNI
        SNIHostName serverName = new SNIHostName(domain);
        SSLParameters sslParams = sslSocket.getSSLParameters();
        sslParams.setServerNames(Collections.singletonList(serverName));
        sslSocket.setSSLParameters(sslParams);

        return sslSocket;
    }
}
