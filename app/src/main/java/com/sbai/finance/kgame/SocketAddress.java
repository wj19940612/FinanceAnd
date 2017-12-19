package com.sbai.finance.kgame;

/**
 * Modified by john on 15/12/2017
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class SocketAddress {


    /**
     * port : 3107
     * host : 192.168.0.247
     */

    private String port;
    private String host;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String toUri() {
        return "ws://" + host + ":" + port;
    }

    @Override
    public String toString() {
        return "SocketAddress{" +
                "port='" + port + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
