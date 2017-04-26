package com.sbai.finance.net;

import com.sbai.httplib.ApiParams;

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

    /**
     * 获取 k 线数据
     *
     * @param varietyType
     * @param type
     * @return
     */
    public static API getKlineData(String varietyType, String type, String endTime) {
        return new API("/quota/candlestickData/getCandlesticKData.do",
                new ApiParams()
                        .put("contractsCode", varietyType)
                        .put("limit", 100)
                        .put("endTime", endTime)
                        .put("type", type));
    }

    /**
     * 获取品种列表
     *
     * @param bigVarietyTypeCode
     * @param page
     * @param pageSize
     * @param smallVarietyTypeCode
     * @return
     */
    public static API getVarietyList(String bigVarietyTypeCode, int page, int pageSize, String smallVarietyTypeCode) {
        return new API("/order/order/getVariety.do",
                new ApiParams()
                        .put("bigVarietyTypeCode", bigVarietyTypeCode)
                        .put("page", page)
                        .put("pageSize", pageSize)
                        .put("smallVarietyTypeCode", smallVarietyTypeCode));
    }

}
