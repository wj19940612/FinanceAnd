package com.sbai.finance.kgame;

import com.sbai.finance.BuildConfig;

/**
 * Modified by john on 15/12/2017
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class RegisterInfo {

    private String host;
    private String channel;
    private String token1;
    private String token2;
    private String device;

    public RegisterInfo(String tokens) {
        String[] strings = processTokens(tokens);
        host = BuildConfig.HOST;
        if (strings.length == 2) {
            token1 = strings[0];
            token2 = strings[1];
        }
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    private String[] processTokens(String tokens) {
        String[] strings = tokens.split(";");
        for (int i = 0; i < strings.length; i++) {
            String str = strings[i];
            strings[i] = str.substring(str.indexOf("\"") + 1, str.lastIndexOf("\""));
        }
        return strings;
    }


    @Override
    public String toString() {
        return "RegisterInfo{" +
                "host='" + host + '\'' +
                ", channel='" + channel + '\'' +
                ", token1='" + token1 + '\'' +
                ", token2='" + token2 + '\'' +
                ", device='" + device + '\'' +
                '}';
    }
}
