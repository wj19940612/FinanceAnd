package com.sbai.finance.net;

import com.sbai.httplib.ApiParams;
import com.android.volley.Request;

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

    public static API getBannerData(){
        return new API(Request.Method.POST,"/user/news/findBannerList.do");
    }

    /**
     * @param page  页码
     * @param pageSize 页码大小
     * @return
     */
    public static API getBreakingNewsData(Integer page,Integer pageSize){
        return new API("/user/breakingNews/findBreakingNewsList.do",
                new ApiParams()
                        .put("page", page)
                        .put("pageSize", pageSize));
    }

    /**
     * 大事件详情
     * @param id
     * @return
     */
    public static API getBreakingNewsDetailData(String id){
        return new API("/user/breakingNews/showDetail.do",
                new ApiParams()
                        .put("id", id));
    }

    /**
     * 大事件最新标题
     * @return
     */
    public static API getBreakingNewsTitleData(){
        return new API("/user/breakingNews/findOneTitle.do");
    }

    /**
     * 获取主题
     * @return
     */
    public static API getTopicData(){
        return new API("/coterie/subject/find.do");
    }

    /**
     * 获取主题详情
     * @param id
     * @return
     */
    public static API getTopicDetailData(Integer id){
        return new API("/coterie/subject/findCoterieInfo.do",
                new ApiParams()
                        .put("id", id));
    }
}
