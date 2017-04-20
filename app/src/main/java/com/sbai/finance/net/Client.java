package com.sbai.finance.net;

public class Client {

    /**
     * 获取服务器系统时间
     *
     * @return
     */
    public static API getSystemTime() {
        return new API("/user/user/getSystemTime.do");
    }

    public static API getTrendData(String varietyType) {
        return new API("/quotaStatus/" + varietyType + ".fst");
    }
}
