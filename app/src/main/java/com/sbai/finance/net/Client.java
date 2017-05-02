package com.sbai.finance.net;

import com.android.volley.Request;
import com.sbai.httplib.ApiParams;


public class Client {

    private static final int POST = Request.Method.POST;

    /**
     * 获取服务器系统时间
     *
     * @return
     */
    public static API getSystemTime() {
        return new API("/user/user/getSystemTime.do");
    }

    public static API getSocketAddress() {
        return new API("/fut/ip/list.do",
                new ApiParams().put("type", "app"));
    }

    /**
     * 获取分时图数据
     *
     * @param code
     * @return
     */
    public static API getTrendData(String code) {
        return new API("/fut/k/timeSharing.do",
                new ApiParams().put("code", code));
    }

    /**
     * 获取 k 线数据
     *
     * @param varietyType
     * @param type
     * @return
     */
    public static API getKlineData(String varietyType, String type, String endTime) {
        return new API("/fut/k/data.do",
                new ApiParams()
                        .put("contractsCode", varietyType)
                        .put("limit", 100)
                        .put("endTime", endTime)
                        .put("type", type));
    }

    /**
     * 接口名称 快捷登入
     * 请求类型 post
     * 请求Url  /registerLogin/quickLogin.do
     *
     * @param msgCode 短信验证码
     * @param phone   手机
     * @return
     */
    public static API login(String msgCode, String phone) {
        return new API(POST, "/user/registerLogin/quickLogin.do", new ApiParams()
                .put("msgCode", msgCode)
                .put("phone", phone));
    }

    /**
     * 接口名称 获取验证码
     * 请求类型 get
     * 请求Url  /registerLogin/sendMsgCode.do
     *
     * @param phone
     * @return
     */

    public static API getAuthCode(String phone) {
        return new API("/user/registerLogin/sendMsgCode.do", new ApiParams().put("phone", phone));
    }

    /**
     * 请求类型 post
     * 请求Url  /user/updatePic.do
     * 接口描述 修改头像
     *
     * @param pic
     * @return
     */
    public static API updateUserHeadImage(String pic) {
        return new API(POST, "/user/user/updatePic.do", new ApiParams().put("pic", pic));
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

    /**
     * 请求类型 get
     * 请求Url  /out/logout.do
     * 接口描述 退出登入
     *
     * @return
     */
    public static API logout() {
        return new API("/user/out/logout.do", null);
    }

    /**
     * 接口名称 修改用户名
     * 请求类型 post
     * 请求Url  /user/updateUserName.do
     *
     * @param userName
     * @return
     */
    public static API updateUserNickNmae(String userName) {
        return new API(POST, "/user/user/updateUserName.do", new ApiParams().put("userName", userName));
    }

    /**
     * 接口名称 获取用户信息
     * 请求类型 get
     * 请求Url  /user/loadUserInfo.do
     *
     * @return
     */
    public static API requestDetailUserInfo() {
        return new API("/user/user/findUserInfo.do", null);
    }

    /**
     * 接口名称 历史消息
     * 请求类型 post
     * 请求Url  msg/msg/history.do
     *
     * @param classify 消息类型{1.系统消息 2.互助消息 3.经济圈消息}
     * @param autoRead 是否自动标记已读 默认为true
     * @return
     */
    public static API requestHistoryNews(boolean autoRead, int classify, int page, int pageSize) {
        return new API("/msg/msg/history.do", new ApiParams()
                .put("classify", classify)
                .put("page", page)
                .put("size", pageSize)
                .put("autoRead", autoRead));
    }

    /**
     * 接口名称 标记已读
     * 请求类型 get
     * 请求Url  /msg/msg/read.do
     * 接口描述 如果 不在获取消息列表的时候自动标记就需要在 用户点击消息的时候去 标记一下
     *
     * @param msgId
     * @return
     */
    public static API updateMsgReadStatus(int msgId) {
        return new API("/msg/msg/read.do", new ApiParams().put("msgId", msgId));
    }

    /**
     * 接口名称 修改用户信息
     * 请求类型 post
     * 请求Url  /user/updateUser
     * 接口描述 修改用户各种信息
     *
     * @param age
     * @param land
     * @param userSex
     * @return
     */

    public static API updateUserInfo(int age, String land, int userSex) {
        return new API(POST, "/user/user/updateUser.do", new ApiParams()
                .put("age", age)
                .put("land", land)
                .put("userSex", userSex));
    }

    /**
     * 获取首页的 banner
     *
     * @return
     */
    public static API getBannerData() {
        return new API(POST, "/user/news/findBannerList.do");
    }

    /**
     * @param page     页码
     * @param pageSize 页码大小
     * @return
     */
    public static API getBreakingNewsData(Integer page, Integer pageSize) {
        return new API("/user/breakingNews/findBreakingNewsList.do",
                new ApiParams()
                        .put("page", page)
                        .put("pageSize", pageSize));
    }

    /**
     * 接口名称 屏蔽或者取消屏蔽
     * 请求类型 post
     * 请求Url  coterie/userDetail/shield
     *
     * @param shieldId 屏蔽对象的ID
     * @param status   0屏蔽 1取消屏蔽
     * @return
     */
    public static API shieldOrRelieveShieldUser(int shieldId, int status) {
        return new API(POST, "/coterie/userDetail/shield.do", new ApiParams()
                .put("shieldId", shieldId)
                .put("status", status));
    }

    /**
     * 接口名称 查看屏蔽表
     * 请求类型 get
     * 请求Url  /user/followShield/myShield.do
     *
     * @param page
     * @param pageSize
     * @return
     */
    public static API getShieldList(int page, int pageSize) {
        return new API("/user/followShield/myShield.do", new ApiParams()
                .put("page", page)
                .put("pageSize", pageSize));
    }

    /**
     * 接口名称 查看用户关注表
     * 请求类型 get
     * 请求Url  /user/followShield/myFollow.do
     *
     * @return
     */
    public static API getUserAttentionList(int page, int pageSize) {
        return new API("/user/followShield/myFollow.do", new ApiParams()
                .put("page", page)
                .put("pageSize", pageSize));
    }

    /**
     * 接口名称 查看粉丝表
     * 请求类型 get
     * 请求Url  /user/followShield/followMe.do
     *
     * @param page
     * @param pageSize
     * @return
     */
    public static API getUserFansList(int page, int pageSize) {
        return new API("/user/followShield/followMe.do", new ApiParams()
                .put("page", page)
                .put("pageSize", pageSize));
    }

    /**
     * 接口名称 关注
     * 请求类型 post
     * 请求Url  coterie/userDetail/follow
     *
     * @param followId 关注对象的ID
     * @param status   0关注 1取消关注
     * @return
     */
    public static API attentionOrRelieveAttentionUser(int followId, int status) {
        return new API(POST, "/coterie/userDetail/follow.do", new ApiParams()
                .put("followId", followId)
                .put("status", status));
    }

    /**
     * 接口名称 关注,粉丝数量
     * 请求类型 get
     * 请求Url  coterie/userDetail/getAttentionFollow
     *
     * @param userId 点击查看的用户ID
     * @return
     */
    public static API getAttentionFollowUserNumber(int userId) {
        return new API("/coterie/userDetail/getStatistics.do", new ApiParams().put("userId", userId));
    }

    /**
     * 接口名称 未读消息的数量
     * 请求类型 post
     * 请求Url   msg/msg/count.do
     */
    public static API getNoReadMessageNumber() {
        return new API("/msg/msg/count.do");
    }

    /**
     * 接口名称 认证查询
     * 请求类型 get
     * 请求Url  /user/user/userCertification.do
     *
     * @return
     */
    public static API getUserCreditApproveStatus() {
        return new API("/user/user/userCertification.do");
    }

    /**
     * 接口名称 查看点击用户观点
     * 请求类型 post
     * 请求Url  /coterie/userInterest/queryClickUserViewPoint.do
     *
     * @param page
     * @param pageSize
     * @param userId   用户id
     * @return
     */
    public static API getUserPublishList(int page, int pageSize, int userId) {
        return new API("/coterie/userInterest/queryClickUserViewPoint.do", new ApiParams()
                .put("page", page)
                .put("pageSize", pageSize)
                .put("userId", userId));
    }

    /**
     * 大事件详情
     *
     * @param id
     * @return
     */
    public static API getBreakingNewsDetailData(String id) {
        return new API("/user/breakingNews/showDetail.do",
                new ApiParams()
                        .put("id", id));
    }

    /**
     * 大事件最新标题
     *
     * @return
     */
    public static API getBreakingNewsTitleData() {
        return new API("/user/breakingNews/findOneTitle.do");
    }

    /**
     * 获取主题
     *
     * @return
     */
    public static API getTopicData() {
        return new API("/coterie/subject/find.do");
    }

    /**
     * 获取主题详情
     *
     * @param id
     * @return
     */
    public static API getTopicDetailData(Integer id) {
        return new API("/coterie/subject/findCoterieInfo.do",
                new ApiParams()
                        .put("id", id));
    }

    /**
     * 获取期货品种
     *
     * @param page
     * @param pageSize
     * @param smallVarietyTypeCode
     * @return
     */
    public static API getFutureVariety(Integer page, Integer pageSize, String smallVarietyTypeCode) {
        return new API("/order/order/getVariety.do",
                new ApiParams()
                        .put("bigVarietyTypeCode", "future")
                        .put("page", page)
                        .put("pageSize", pageSize)
                        .put("smallVarietyTypeCode", smallVarietyTypeCode));
    }

    /**
     * 股票除指数品种
     *
     * @param page
     * @param pageSize
     * @return
     */
    public static API getStockVariety(Integer page, Integer pageSize) {
        return new API("/order/order/getStockVariety.do",
                new ApiParams()
                        .put("page", page)
                        .put("pageSize", pageSize));
    }

    /**
     * 股票指数品种
     *
     * @return
     */
    public static API getStockIndexVariety() {
        return new API("order/order/getStockExponentVariety.do");
    }

    /**
     * 查询自选gu
     *
     * @param bigVarietyTypeCode
     * @return
     */
    public static API getOptional(String bigVarietyTypeCode) {
        return new API("/order/optional/findOptional.do",
                new ApiParams()
                        .put("bigVarietyTypeCode", bigVarietyTypeCode));
    }

    /**
     * 添加自选股
     *
     * @param varietyId
     * @return
     */
    public static API addOptional(String varietyId) {
        return new API("/order/optional/addOptional.do",
                new ApiParams()
                        .put("varietyId", varietyId));
    }

    /**
     * 添加自选股
     *
     * @param varietyId
     * @return
     */
    public static API delOptional(Integer varietyId) {
        return new API("/order/optional/deleteOptional.do",
                new ApiParams()
                        .put("varietyId", varietyId));
    }

    /**
     * 获取经济圈首页列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    public static API getEconomicCircleList(int page, int pageSize) {
        return new API("/coterie/coterie/coterieList.do",
                new ApiParams()
                        .put("page", page)
                        .put("pageSize", pageSize));
    }

    /**
     * 发表观点
     *
     * @param bigVarietyTypeCode
     * @param content
     * @param direction
     * @param varietyId
     * @param varietyType
     * @return
     */
    public static API saveViewPoint(String bigVarietyTypeCode, String content, int direction, int varietyId, String varietyType) {
        return new API(POST, "/coterie/viewpoint/saveViewpoint.do",
                new ApiParams()
                        .put("bigVarietyTypeCode", bigVarietyTypeCode)
                        .put("content", content)
                        .put("direction", direction)
                        .put("varietyId", varietyId)
                        .put("varietyType", varietyType));
    }

    /**
     * 查询观点
     *
     * @param page
     * @param pageSize
     * @param varietyId
     * @return
     */
    public static API findViewpoint(int page, int pageSize, int varietyId) {
        return new API("/coterie/viewpoint/findViewpoint.do",
                new ApiParams()
                        .put("page", page)
                        .put("pageSize", pageSize)
                        .put("varietyId", varietyId));
    }


}
