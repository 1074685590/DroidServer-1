package com.gnepux.droidserver;

import java.net.Socket;
import java.util.HashMap;

/**
 * Created by xupeng on 17/2/14.
 */

public class HttpContext {

    private Socket underlySocket;

    private HashMap<String, String> requestHeader;

    public HttpContext() {
        requestHeader = new HashMap<>();
    }

    public Socket getUnderlySocket() {
        return underlySocket;
    }

    public void setUnderlySocket(Socket underlySocket) {
        this.underlySocket = underlySocket;
    }

    public void addRequestHeader(String headerName, String headerValue) {
        requestHeader.put(headerName, headerValue);
    }

    public String getRequestHeaderValue(String headerName) {
        return requestHeader.get(headerName);
    }
}
