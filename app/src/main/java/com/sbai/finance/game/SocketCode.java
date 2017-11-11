package com.sbai.finance.game;

public interface SocketCode {

    int CODE_HEART = 1000;
    int CODE_REGISTER = 1100;
    int CODE_CMD = 1200;

    int CODE_RESP_HEART = 2000;
    int CODE_RESP_UNKNOWN_CMD = 2001; // 未知指令
    int CODE_RESP_JSON_ERROR = 2002;
    int CODE_RESP_UNKNOWN_ERROR = 2003;

    int CODE_RESP_UNREGISTER = 2010;
    int CODE_RESP_UNLOGIN = 2011;

    int CODE_RESP_REGISTER_SUCCESS = 2100;
    int CODE_RESP_REGISTER_FAILURE = 2101;

    int CODE_RESP_CMD_SUCCESS = 2200;
    int CODE_RESP_CMD_FAILURE = 2201;
    int CODE_RESP_CMD_GONE = 2202; // 命令无法找到

    int CODE_RESP_PARAMS_ERROR = 2210;

    int CODE_RESP_PUSH = 3000;
}
