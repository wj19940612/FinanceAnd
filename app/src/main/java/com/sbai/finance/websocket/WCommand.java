package com.sbai.finance.websocket;

public class WCommand<T> {

    private String cmd;
    private T parameters;

    public String getCmd() {
        return cmd;
    }

    public void setParameters(T parameters) {
        this.parameters = parameters;
    }
}
