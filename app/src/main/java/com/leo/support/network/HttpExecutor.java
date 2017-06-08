package com.leo.support.network;

/**
 * Created by leo on 2017/6/8.
 */

public interface HttpExecutor {

    public static class ProxyHost {
        public String mHost;
        public int mPort;

        public ProxyHost() {}

        public ProxyHost(String host, int port) {
            this.mHost = host;
            this.mPort = port;
        }
    }
}
