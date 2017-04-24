package com.sbai.finance.net;

import com.sbai.httplib.ApiParams;

public class Client {

    /**
     * 获取服务器系统时间
     *
     * @return
     */
    public static APIBase getSystemTime() {
        return new APIBase("/user/user/getSystemTime.do");
    }

    public static APIBase getTrendData(String varietyType) {
        return new APIBase("/quotaStatus/" + varietyType + ".fst");
    }

    /**
     * 获取 k 线数据
     *
     * @param varietyType
     * @param type
     * @return
     */
    public static APIBase getKlineData(String varietyType, String type, String endTime) {
        return new APIBase("/quota/candlestickData/getCandlesticKData.do",
                new ApiParams()
                        .put("contractsCode", varietyType)
                        .put("limit", 100)
                        .put("endTime", endTime)
                        .put("type", type));
    }

}
