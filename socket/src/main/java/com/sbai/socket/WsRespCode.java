package com.sbai.socket;

/**
 * Modified by john on 15/12/2017
 *
 *
 *
 */
public interface WsRespCode {

    int HEART = 2000;

    int UNKNOWN_REQUEST = 2001; // 未知指令
    int JSON_ERROR = 2002;
    int UNKNOWN_ERROR = 2003;

    int UNREGISTER = 2010;
    int UNLOGIN = 2011;

    int REGISTER_SUCCESS = 2100;
    int REGISTER_FAILURE = 2101;

    int MSG_OFFLINE_SUCCESS = 2103;
    int MSG_ACK_SUCCESS = 2104;

    int REQUEST_SUCCESS = 2200;
    int REQUEST_FAILURE = 2201;
    int REQUEST_GONE = 2202; // 命令无法找到

    int PARAMS_ERROR = 2210; // 参数异常

    int PUSH = 3000;
}
