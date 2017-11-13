package com.sbai.finance.game;

public abstract class WSCmd {

    private String cmd;
    private Object parameters;

    public WSCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setParameters(Object parameters) {
        this.parameters = parameters;
    }
}
