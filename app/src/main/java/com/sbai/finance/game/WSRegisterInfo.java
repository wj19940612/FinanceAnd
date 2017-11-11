package com.sbai.finance.game;

import com.sbai.finance.BuildConfig;

public class WSRegisterInfo {

    /**
     * token1 : NHV4ZW5la29hanJmY3BlZ2Zua2Nnb2luaW5jcw==
     * token2 : NjgzZmUyMGQ5NjRmN2E5NjBiNWNkNWNjN2JhOTQwOTM=
     * host : var.esongbai.xyz
     */

    private String token1;
    private String token2;
    private String host;

    public WSRegisterInfo(String tokens) {
        String[] strings = processTokens(tokens);
        host = BuildConfig.HOST;
        if (strings.length == 2) {
            token1 = strings[0];
            token2 = strings[1];
        }
    }

    private String[] processTokens(String tokens) {
        String[] strings = tokens.split(";");
        for (int i = 0; i < strings.length; i++) {
            String str = strings[i];
            strings[i] = str.substring(str.indexOf("\"") + 1, str.lastIndexOf("\""));
        }
        return strings;
    }
}
