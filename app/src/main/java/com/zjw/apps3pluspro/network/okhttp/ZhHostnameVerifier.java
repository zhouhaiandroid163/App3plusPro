package com.zjw.apps3pluspro.network.okhttp;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

class ZhHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}