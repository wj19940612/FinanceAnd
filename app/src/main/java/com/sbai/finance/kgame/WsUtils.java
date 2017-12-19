package com.sbai.finance.kgame;

import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.utils.DateUtil;
import com.sbai.socket.WsRequest;

import java.util.UUID;

/**
 * Modified by john on 15/12/2017
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class WsUtils {

    public static WsRequest getRequest(int code, Object o) {
        WsRequest wsRequest = new WsRequest(code, 0);
        long timestamp = SysTime.getSysTime().getSystemTimestamp();
        wsRequest.setTimestamp(String.valueOf(timestamp));
        wsRequest.setUuid(createUUID(timestamp));
        return wsRequest;
    }

    private static String createUUID(long timestamp) {
        String uuid = UUID.randomUUID().toString();
        String time = DateUtil.format(timestamp, "yyyyMMdd_HHmmss");
        return uuid + "-" + time;
    }
}
