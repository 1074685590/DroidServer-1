package com.gnepux.droidserver;

/**
 * Created by xupeng on 17/2/14.
 */

public class WebConfiguration {

    // 端口
    private int port;

    // 最大并发数
    private int maxParallels;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxParallels() {
        return maxParallels;
    }

    public void setMaxParallels(int maxParallels) {
        this.maxParallels = maxParallels;
    }
}
