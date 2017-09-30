package com.sbai.finance.net;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.sbai.finance.App;
import com.sbai.finance.Preference;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.levelevaluation.QuestionAnswer;
import com.sbai.finance.utils.AppInfo;
import com.sbai.httplib.ApiParams;


public class Client {

    private static final int POST = Request.Method.POST;
    public static final int DEFAULT_PAGE_SIZE = 20;

    //h5功能介绍网址
    public static final String ABOUT_US_PAGE_URL = API.getHost() + "/lm/introduce.html";
    //h5的用户协议界面网址
    public static final String WEB_USER_PROTOCOL_PAGE_URL = API.getHost() + "/lm/user_protocol.html";

    /**
     * @deprecated
     */
    // 期货分享地址
    public static final String SHARE_URL_FUTURE = API.getHost() + "/mobi/future/future_quota?varietyId=%d";
    /**
     * @deprecated
     */
    // 股票分享地址
    public static final String SHARE_URL_STOCK = API.getHost() + "/mobi/stock/stock_quota?varietyType=%s&varietyId=%d";
    // 提问分享地址
    public static final String SHARE_URL_QUESTION = API.getHost() + "/lm/commentary.html?questionId=%d";
    // 乐米日报分享地址
    public static final String SHARE_URL_REPORT = API.getHost() + "/lm/arcelor.html?id=%s";
    // 乐米训练心得分享地址
    public static final String SHARE_URL_TRAIN_EXPERIENCE = API.getHost() + "/lm/train.html?trainId=%d";

    //期货对战分享
    public static final String SHARE_URL_FUTURE_BATTLE = API.getHost() + "/lm/share.html";


    public static String getServiceQQ(String serviceQQ) {
//        if (qqType == ChannelServiceInfo.QQ_TYPE_NORMAL) {
//            return "mqqwpa://im/chat?chat_type=wpa&uin=" + serviceQQ + "&version=1";
//        }
        //return "mqqwpa://im/chat?chat_type=crm&uin=" + serviceQQ + "&version=1";
        return "mqqwpa://im/chat?chat_type=wpa&uin=" + serviceQQ + "&version=1";
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
     * 期货搜索
     */
    public static API searchFuture(String search) {
        return new API("/order/future/query/search.do",
                new ApiParams()
                        .put("search", search));
    }

    /**
     * 股票搜索
     */
    public static API searchStock(String search) {
        return new API("/order/stock/query/search.do",
                new ApiParams()
                        .put("search", search));
    }

    /**
     * 股票除指数品种
     *
     * @param page
     * @param pageSize
     * @return
     */
    public static API getStockVariety(int page, int pageSize, String search) {
        return new API("/order/order/getStockVariety.do",
                new ApiParams()
                        .put("page", page)
                        .put("pageSize", pageSize)
                        .put("search", search));
    }

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
     * 观点回复
     *
     * @param content
     * @param viewpointId
     * @return
     */
    public static API opinionReply(String content, int viewpointId) {
        return new API(POST, "/coterie/viewpoint/viewpointReply.do",
                new ApiParams()
                        .put("content", content)
                        .put("viewpointId", viewpointId));
    }

    /**
     * 观点点赞
     *
     * @param viewpointId
     * @return
     */
    public static API opinionPraise(int viewpointId) {
        return new API(POST, "/coterie/viewpoint/viewpointPraise.do",
                new ApiParams()
                        .put("viewpointId", viewpointId));
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
    public static API saveViewPoint(String bigVarietyTypeCode, int calcuId, String content, int direction, int varietyId, String varietyType) {
        return new API(POST, "/coterie/viewpoint/saveViewpoint.do",
                new ApiParams()
                        .put("bigVarietyTypeCode", bigVarietyTypeCode)
                        .put("calcuId", calcuId)
                        .put("content", content)
                        .put("direction", direction)
                        .put("varietyId", varietyId)
                        .put("varietyType", varietyType));
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

    /**
     * 检测是否已经预测'看涨'看跌'
     *
     * @param bigVarietyTypeCode
     * @param varietyId
     * @return
     */
    public static API checkViewpoint(String bigVarietyTypeCode, int varietyId) {
        return new API(POST, "/coterie/viewpoint/checkCalculate.do",
                new ApiParams()
                        .put("bigVarietyTypeCode", bigVarietyTypeCode)
                        .put("varietyId", varietyId));
    }

    /**
     * 获取品种简介
     *
     * @param varietyId
     * @return
     */
    public static API getVarietytradeIntrouce(int varietyId) {
        return new API("/order/order/getVarietytradeIntro.do",
                new ApiParams()
                        .put("varietyId", varietyId));
    }


    /**
     * 获取多个行情
     *
     * @param codes
     * @return
     */
    public static API getQuotaList(String codes) {
        return new API("/fut/quota/list.do",
                new ApiParams()
                        .put("codes", codes));
    }

    /**
     * 接口名称 快捷登入
     *
     * @param authCode 短信验证码
     * @param phone    手机
     *                 deviceId 设备id
     *                 platform 平台 0-安卓 1-ios
     * @return
     */
    public static API authCodeLogin(String phone, String authCode) {
        return new API(POST, "/user/registerLogin/quickLogin.do", new ApiParams()
                .put("phone", phone)
                .put("msgCode", authCode)
                .put("deviceId", Preference.get().getPushClientId())
                .put("platform", 0)
                .put("channel", AppInfo.getMetaData(App.getAppContext(), "UMENG_CHANNEL")));
    }

    /**
     * 接口名称 快捷登入(for 微信)
     *
     * @param authCode 短信验证码
     * @param phone    手机
     *                 deviceId 设备id
     *                 platform 平台 0-安卓 1-ios
     * @return
     */
    public static API authCodeLogin(String phone, String authCode, String openId, String name, String iconUrl, int sex) {
        return new API(POST, "/user/registerLogin/quickLogin.do", new ApiParams()
                .put("phone", phone)
                .put("msgCode", authCode)
                .put("deviceId", Preference.get().getPushClientId())
                .put("platform", 0)
                .put("source", AppInfo.getMetaData(App.getAppContext(), "UMENG_CHANNEL"))
                .put("openId", openId)
                .put("name", name)
                .put("iconUrl", iconUrl)
                .put("sex", sex));
    }

    /**
     * 常规登录
     *
     * @param phone
     * @param password
     * @return
     */
    public static API login(String phone, String password) {
        return new API(POST, "/user/registerLogin/login.do", new ApiParams()
                .put("phone", phone)
                .put("password", password)
                .put("deviceId", Preference.get().getPushClientId())
                .put("platform", 0));
    }

    /**
     * 微信登录
     *
     * @param openId
     * @return
     */
    public static API weChatLogin(String openId) {
        return new API(POST, "/user/registerLogin/wechatLogin.do", new ApiParams()
                .put("openId", openId)
                .put("deviceId", Preference.get().getPushClientId())
                .put("platform", 0)
                .put("source", AppInfo.getMetaData(App.getAppContext(), "UMENG_CHANNEL")));
    }

    /**
     * 绑定微信
     *
     * @param openId
     * @return
     */
    public static API bindWeChat(String openId, String name, String iconUrl, int sex) {
        return new API(POST, "/user/user/boundWechat.do", new ApiParams()
                .put("openId", openId)
                .put("name", name)
                .put("iconUrl", iconUrl)
                .put("sex", sex));
    }

    /**
     * 注册
     *
     * @param phone
     * @param password
     * @param code
     * @return
     */
    public static API register(String phone, String password, String code) {
        return new API(POST, "/user/registerLogin/reigster.do", new ApiParams()
                .put("phone", phone)
                .put("password", password)
                .put("code", code)
                .put("deviceId", Preference.get().getPushClientId())
                .put("platform", 0)
                .put("channel", AppInfo.getMetaData(App.getAppContext(), "UMENG_CHANNEL")));
    }

    /**
     * 注册和找回密码的情况下验证手机号
     *
     * @param phone
     * @param type  0 注册，1 忘记密码
     * @return
     */
    public static API checkPhone(String phone, int type) {
        return new API("/user/registerLogin/checkAccount.do", new ApiParams()
                .put("phone", phone)
                .put("type", type));
    }

    /**
     * 忘记密码：更新新密码
     *
     * @param phone
     * @param password
     * @param code
     * @return
     */
    public static API updateNewPassword(String phone, String password, String code) {
        return new API(POST, "/user/user/forgetPassword.do", new ApiParams()
                .put("phone", phone)
                .put("password", password)
                .put("code", code));
    }

    /**
     * 修改登录密码
     *
     * @param password
     * @return
     */
    public static API modifyPassword(String password, String oldPassword) {
        return new API(POST, "/user/user/modifyPassword.do", new ApiParams()
                .put("newPassword", password)
                .put("oldPassword", oldPassword));
    }

    /**
     * 第一次 设置登录密码
     *
     * @param password
     * @return
     */
    public static API setPassword(String password) {
        return new API(POST, "/user/user/setPassword.do", new ApiParams()
                .put("password", password));
    }

    /**
     * 接口名称 获取验证码
     *
     * @param phone
     * @return
     */
    public static API getAuthCode(String phone) {
        return getAuthCode(phone, null);
    }

    /**
     * 接口名称 获取验证码
     *
     * @param phone
     * @param imgCode
     * @return
     */
    public static API getAuthCode(String phone, String imgCode) {
        return new API(POST, "/user/registerLogin/sendMsgCode.do", new ApiParams()
                .put("phone", phone)
                .put("imgCode", imgCode));
    }

    /**
     * 请求类型 post
     *
     * @param pic
     * @return
     */
    public static API updateUserHeadImage(String pic) {
        return new API(POST, "/user/user/updatePic.do", new ApiParams()
                .put("pic", pic));
    }

    /**
     * 修改头像
     *
     * @param picPath String 图片网址
     * @return
     */
    public static API updateUserHeadImagePath(String picPath) {
        return new API("/user/user/updatePicLand.do", new ApiParams()
                .put("picLand", picPath));
    }

    /**
     * /user/upload/image.do
     * POST
     * 上传-图片上传（yhj）
     *
     * @param base64ImageString
     * @return
     */
    public static API uploadImage(String base64ImageString) {
        return new API(POST, "/user/upload/image.do", new ApiParams().put("picture", base64ImageString));
    }

    /**
     * 观点回复点赞
     *
     * @param replyId
     * @return
     */
    public static API opinionReplyPraise(int replyId) {
        return new API(POST, "/coterie/viewpoint/viewpointReplyPraise.do",
                new ApiParams()
                        .put("replyId", replyId));
    }

    /**
     * 获取品种列表
     *
     * @param bigVarietyTypeCode
     * @param page
     * @param smallVarietyTypeCode
     * @return
     */
    public static API getVarietyList(String bigVarietyTypeCode, int page, String smallVarietyTypeCode) {
        return new API("/order/order/getVariety.do",
                new ApiParams()
                        .put("bigVarietyTypeCode", bigVarietyTypeCode)
                        .put("page", page)
                        .put("pageSize", 15)
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
     * @param classify 消息类型 1系统 2观点 3借款
     * @param autoRead 是否自动标记已读 默认为true
     * @return
     */
    public static API requestHistoryNews(boolean autoRead, String classify, Integer page, Integer size, Integer status, Long createTime) {
        return new API("/msg/msg/history.do", new ApiParams()
                .put("classify", classify)
                .put("page", page)
                .put("size", size)
                .put("status", status)
                .put("autoRead", autoRead)
                .put("createTime", createTime));
    }

    /**
     * 获取新消息数量
     *
     * @return
     */
    public static API getNewMessageCount() {
        return new API(POST, "/msg/msg/count.do");
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
     * @param userSex 1女2男0未知
     *                longitude 经度
     *                latitude  经度
     * @return
     */
    public static API updateUserInfo(Integer age, String land, Integer userSex) {
        return new API(POST, "/user/user/updateUser.do", new ApiParams()
                .put("age", age)
                .put("land", land)
                .put("userSex", userSex)
                .put("longitude", LocalUser.getUser().getUserInfo().getLongitude())
                .put("latitude", LocalUser.getUser().getUserInfo().getLatitude())
        );
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
     * 获取首页礼物活动
     *
     * @return
     */
    public static API requestGiftActivity() {
        return new API("/user/news/getBanner.do");
    }

    /**
     * 接口名称 大事件列表
     *
     * @param page     页码
     * @param pageSize 页码大小
     * @return
     */
    public static API getBreakingNewsData(int page, int pageSize) {
        return new API("/user/breakingNews/findBreakingNewsList.do",
                new ApiParams()
                        .put("page", page)
                        .put("pageSize", pageSize));
    }

    /**
     * 接口名称 显示内容
     * 请求类型 post
     * 请求Url  user/breakingNews/showDetail.do
     *
     * @param id
     * @return
     */
    public static API getBigEventContent(String id) {
        return new API("/user/breakingNews/showDetail.do", new ApiParams().put("id", id));
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
        return new API(POST, "/coterie/userInterest/shield.do", new ApiParams()
                .put("shieldId", shieldId)
                .put("status", status));
    }

    /**
     * 接口名称 查看屏蔽表
     * 请求类型 get
     * 请求Url  /user/followShield/myShield.do
     *
     * @param
     * @param
     * @return
     */
    public static API getShieldList() {
        return new API("/user/followShield/myShield.do", null);
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
        return new API("/coterie/userInterest/follow.do", new ApiParams()
                .put("followId", followId)
                .put("status", status));
    }

    /**
     * 接口名称 关注,粉丝数量
     * 请求类型 get
     * 请求Url   /coterie/userInterest/getStatistics.do
     *
     * @param userId 点击查看的用户ID
     * @return
     */
    public static API getAttentionFollowUserNumber(Integer userId) {
        return new API("/coterie/userInterest/getStatistics.do", new ApiParams().put("userId", userId));
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
     * 接口名称 未读取意见反馈数量
     * 请求类型 get
     * 请求Url   /user/userFeedback/findFeedback.do
     */
    public static API getNoReadFeedbackNumber() {
        return new API("/user/userFeedback/findFeedback.do");
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
     * 接口名称 查询实名记录
     * 请求类型 get
     * 请求Url  /user/user/queryCertification.do
     *
     * @param id
     * @return
     */
    public static API queryCertification(int id) {
        return new API("/user/user/queryCertification.do", new ApiParams().put("id", id));
    }

    /**
     * 接口名称 填写实名认证信息提交
     * 请求类型 post
     * 请求Url  /user/user/saveUserCertification.do
     * 接口描述 实名认证
     *
     * @param Back     身份证反面
     * @param Positive 身份证正面
     * @param certCode 身份证号码
     * @param realName 真实姓名
     * @return
     */
    public static API submitUserCreditApproveInfo(String Back, String Positive, String certCode, String realName) {
        return new API(POST, "/user/user/saveUserCertification.do", new ApiParams()
                .put("Back", Back)
                .put("Positive", Positive)
                .put("certCode", certCode)
                .put("realName", realName));
    }

    public static API updateUserCreditApproveInfo(String Back, String Positive, String certCode, String realName) {
        return new API(POST, "/user/user/updateUserCertification.do", new ApiParams()
                .put("Back", Back)
                .put("Positive", Positive)
                .put("certCode", certCode)
                .put("realName", realName));
    }

    /**
     * 接口名称 查看点击用户观点
     * 请求类型 post
     * 请求Url  /coterie/userInterest/queryClickUserViewPoint.do
     *
     * @param page
     * @param userId 用户id
     * @return
     */
    public static API getUserPublishList(int page, Integer userId) {
        return new API("/coterie/userInterest/queryClickUserViewPoint.do", new ApiParams()
                .put("page", page)
                .put("pageSize", DEFAULT_PAGE_SIZE)
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
    public static API getTopicDetailData(int id) {
        return new API("/coterie/subject/findCoterieInfo.do",
                new ApiParams()
                        .put("id", id));
    }

    /**
     * 获取股票的当前行情数据
     *
     * @param stockCodes
     * @return
     */
    public static API getStockMarketData(String stockCodes) {
        return new API(POST, "/stk/stk/list.do",
                new ApiParams()
                        .put("codes", stockCodes));
    }

    /**
     * 获取多个期货合约行情数据
     *
     * @param codes
     * @return
     */
    public static API getFutureMarketData(String codes) {
        return new API("/fut/quota/list.do",
                new ApiParams()
                        .put("codes", codes));
    }

    /**
     * 品种-查询品种是否允许交易
     *
     * @return
     */
    public static API getFutureTradeStatus() {
        return new API("/user/dictionary/findDictionaryForJson.do?type=variety_deal");
    }


    /**
     * 获取股票实时行情接口
     *
     * @param stockCode
     * @return
     */
    public static API getStockRealtimeData(String stockCode) {
        return new API("/stk/stk/real.do",
                new ApiParams()
                        .put("code", stockCode));
    }

    /**
     * 获取股票列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    public static API getStockVariety(int page, int pageSize) {
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
        return new API("/order/order/getStockExponentVariety.do");
    }

    public static API stockSearch(String key) {
        return new API("/order/order/getStockVarietySearch.do",
                new ApiParams()
                        .put("search", key));
    }


    /**
     * 接口名称 根据股票代码查询股票信息
     * 请求类型 get
     * 请求Url  /order/stock/query/info.do
     *
     * @param code
     * @return
     */
    public static API getStockInfo(String code) {
        return new API("/order/stock/query/info.do", new ApiParams().put("code", code));
    }

    /**
     * 获取k线数据
     *
     * @param stockCode
     * @param period
     * @return
     */
    public static API getStockKlineData(String stockCode, int period) {
        return new API("/stock/kline", new ApiParams()
                .put("period", period)
                .put("stock_code", stockCode)
                .put("request_num", 100));
    }

    /**
     * 获取股票分时图数据
     *
     * @return
     */
    public static API getStockTrendData(String stockCode) {
        return new API("/stk/stk/trend.do",
                new ApiParams()
                        .put("code", stockCode));
    }

    /**
     * 接口名称 涨跌幅排行
     * 请求类型 get
     * 请求Url  /stock/sort
     *
     * @param direction  排序类别  1降序 0升序
     * @param exchangeId 市场代码
     * @return
     */
    public static API getStockSort(int direction, int exchangeId) {
        return new API("/stk/stk/sort.do", new ApiParams()
                .put("direction", direction)
                .put("exchangeId", exchangeId));
    }

    /**
     * 查询自选gu
     *
     * @return
     */
    public static API getOptional(int page) {
        return new API("/order/optional/findOptional.do",
                new ApiParams()
                        .put("page", page)
                        .put("pageSize", 10000));
    }

    /**
     * 检测是否已添加
     *
     * @param varietyId
     * @return
     */
    public static API checkOptional(int varietyId) {
        return new API("/order/optional/checkOptional.do",
                new ApiParams()
                        .put("varietyId", varietyId));
    }

    /**
     * 添加自选股
     *
     * @param varietyId
     * @return
     */
    public static API addOption(int varietyId) {
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
     * 获取经济圈列表
     *
     * @param createTime
     * @param pageSize
     * @return
     */
    public static API getEconomicCircleList(Long createTime, int pageSize) {
        return new API("/coterie/coterie/coterieList.do",
                new ApiParams()
                        .put("createTime", createTime)
                        .put("pageSize", pageSize));
    }

    /**
     * 获取观点详情
     *
     * @param viewpointId
     * @return
     */
    public static API getOpinionDetails(int viewpointId) {
        return new API(POST, "/coterie/viewpoint/findViewpointInfo.do",
                new ApiParams()
                        .put("viewpointId", viewpointId));
    }


    /**
     * 获取品种详情
     *
     * @param varietyId
     * @return
     */
    public static API getVarietyDetails(int varietyId) {
        return new API("/order/order/getVarietyInfoById.do",
                new ApiParams()
                        .put("varietyId", varietyId));
    }


    /**
     * 获取观点回复列表
     *
     * @param page
     * @param pageSize
     * @param viewpointId
     * @return
     */
    public static API getOpinionReplyList(int page, int pageSize, int viewpointId) {
        return new API("/coterie/viewpoint/findViewpointReply.do",
                new ApiParams()
                        .put("page", page)
                        .put("pageSize", pageSize)
                        .put("viewpointId", viewpointId));
    }

    /**
     * 获取观点回复列表,在消息里面的回复列表里的置顶回复id
     *
     * @param createTime
     * @param pageSize
     * @param viewpointId
     * @param replyId
     * @return
     */
    public static API getOpinionReplyList(Long createTime, int pageSize, int viewpointId, Integer replyId) {
        return new API("/coterie/viewpoint/findViewpointReply.do",
                new ApiParams()
                        .put("createTime", createTime)
                        .put("pageSize", pageSize)
                        .put("viewpointId", viewpointId)
                        .put("replyId", replyId));
    }

    /**
     * 获取用户资料
     *
     * @param userId
     * @return
     */
    public static API getUserData(int userId) {
        return new API(POST, "/coterie/userInterest/queryClickUserDetail.do",
                new ApiParams()
                        .put("userId", userId));
    }

    /**
     * 是否关注,屏蔽
     *
     * @param objId
     * @return
     */
    public static API whetherAttentionShieldOrNot(int objId) {
        return new API("/coterie/userInterest/findShieldOrFollow.do",
                new ApiParams()
                        .put("objId", objId));
    }


    /**
     * 乐米协议
     *
     * @param id 2 借款协议  3 用户协议  4 兑换规则
     * @return
     */
    public static API getArticleProtocol(int id) {
        return new API("/user/article/articleDetail.do",
                new ApiParams().put("id", id));
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
    public static API publishPoint(String bigVarietyTypeCode, String calcuId, String content, int direction,
                                   String lastPrice, String risePrice, String risePercent,
                                   int varietyId, String varietyType) {
        return new API(POST, "/coterie/viewpoint/saveViewpoint.do",
                new ApiParams()
                        .put("bigVarietyTypeCode", bigVarietyTypeCode)
                        .put("calcuId", calcuId)
                        .put("content", content)
                        .put("direction", direction)
                        .put("lastPrice", lastPrice)
                        .put("risePrice", risePrice)
                        .put("risePre", risePercent)
                        .put("varietyId", varietyId)
                        .put("varietyType", varietyType));
    }

    /**
     * 获取观点大神
     *
     * @return
     */
    public static API getViewPointMaster() {
        return new API("/statistics/statistics/viewpointGod.do");
    }


    /**
     * 查看反馈和回复
     *
     * @param page
     * @param pageSize
     * @return
     */
    public static API getFeedback(int page, int pageSize) {
        return new API("/user/userFeedback/seeFeedback.do",
                new ApiParams()
                        .put("pageSize", pageSize)
                        .put("page", page));
    }

    /**
     * 用户发送反馈
     *
     * @param content
     * @param contentType
     * @return
     */
    public static API sendFeedback(String content, int contentType) {
        return new API(POST, "/user/userFeedback/addFeedback.do",
                new ApiParams()
                        .put("content", content)
                        .put("contentType", contentType));
    }

    /**
     * /user/userAccount/userFlow.do
     * GET
     * 用户流水 新接口 （wms）
     *
     * @param type
     * @param page
     * @return
     */
    public static API requestAccountFundDetailList(int type, int page) {
        return new API("/user/userAccount/userFlow.do",
                new ApiParams()
                        .put("type", type)
                        .put("page", page)
                        .put("pageSize", Client.DEFAULT_PAGE_SIZE));
    }

    /**
     * 获取品种简介
     *
     * @param varietyId
     * @return
     */
    public static API getVarietyTradeIntroduce(int varietyId) {
        return new API("/order/order/getVarietytradeIntro.do",
                new ApiParams()
                        .put("varietyId", varietyId));
    }

    /**
     * 检测是否已经预测'看涨'看跌'
     *
     * @param bigVarietyTypeCode
     * @param varietyId
     * @return
     */
    public static API getPrediction(String bigVarietyTypeCode, int varietyId) {
        return new API("/coterie/viewpoint/checkCalculate.do",
                new ApiParams()
                        .put("bigVarietyTypeCode", bigVarietyTypeCode)
                        .put("varietyId", varietyId));
    }

    /**
     * 获取品种交易所状态
     *
     * @param exchangeId
     * @return
     */
    public static API getExchangeStatus(int exchangeId) {
        return new API("/order/order/getExchangeStatus.do",
                new ApiParams()
                        .put("exchangeId", exchangeId));
    }

    /**
     * 获取意向金金额
     *
     * @return
     */
    public static API getIntentionAmount() {
        return new API("/user/userpay/getUserLoanMoney.do");
    }

    /**
     * 获取可用平台
     * /user/finance/platform/selectUsablePlatformByType.do
     * GET
     * 可用支付平台（wms）
     *
     * @return
     */
    public static API getUsablePlatform(int type) {
        return new API("/user/finance/platform/selectUsablePlatformByType.do", new ApiParams().put("type", type));
    }

    /**
     * 获取支付路径
     *
     * @param orderId
     * @param platform
     * @return
     */
    public static API getPaymentPath(int orderId, String platform) {
        return new API("/user/userpay/payLoanMoney.do",
                new ApiParams()
                        .put("orderId", orderId)
                        .put("platform", platform));
    }

    /**
     * 选择好心人
     *
     * @param id
     * @param selectedId
     * @return
     */
    public static API chooseGoodPeople(int id, int selectedId) {
        return new API("/coterie/help/loan/selected.do",
                new ApiParams()
                        .put("id", id)
                        .put("selectedId", selectedId));
    }

    /**
     * 支付查询
     *
     * @param orderId
     * @return
     */
    public static API paymentQuery(int orderId) {
        return new API("/user/userpay/confirmPay.do",
                new ApiParams()
                        .put("orderId", orderId));
    }

    /**
     * 接口名称 公司简介，公告，报告
     * 请求类型 get
     * 请求Url  /crawler/crawler/selectCompanyProfile.do
     *
     * @param code 证券代码
     * @return
     */
    public static API getCompanyReportOrInfo(String code) {
        return new API("/crawler/crawler/selectCompanyProfile.do", new ApiParams().put("code", code));
    }

    /**
     * 接口名称 年度报表  接口名称 个股资讯
     * 请求类型 get
     * 请求Url  /crawler/crawler/selectData.do?type=financial_summary
     *
     * @param code
     * @param page
     * @param pageSize
     * @param type
     * @return
     */
    public static API getCompanyAnnualReport(String code, int page, int pageSize, String type) {
        return new API("/crawler/crawler/selectData.do", new ApiParams()
                .put("code", code)
                .put("page", page)
                .put("pageSize", pageSize)
                .put("type", type));
    }

    /**
     * 接口名称 单个新闻
     * 请求类型 get
     * 请求Url  /crawler/crawler/newsDetail.do
     *
     * @param id
     * @return
     */
    public static API getStockNewsInfo(String id) {
        return new API("/crawler/crawler/newsDetail.do", new ApiParams().put("id", id));
    }

    /**
     * 图片上传
     */
    public static API uploadPicture(String picture) {
        return new API(POST, "/user/upload/images.do", new ApiParams().put("picture", picture));
    }

    /**
     * GET 查看是否创建过密码（wms）
     *
     * @return
     */
    public static API getUserHasPassWord() {
        return new API("/user/userAccount/hasPassword.do", null);
    }

    /**
     * /user/userAccount/setPassword.do
     * POST
     * 设置安全密码
     *
     * @param password
     * @return
     */
    public static API submitSetPassword(String password) {
        return new API(POST, "/user/userAccount/setPassword.do", new ApiParams().put("password", password));
    }

    /**
     * /user/userAccount/updatePassword.do
     * POST
     * 修改安全密码（wms）
     *
     * @param newPassword
     * @return
     */
    public static API updatePassword(String newPassword, String oldPassword, String code) {
        return new API(POST, "/user/userAccount/updatePassword.do", new ApiParams()
                .put("password", newPassword)
                .put("oldPassword", oldPassword)
                .put("code", code));
    }

    /**
     * /user/userAccount/checkPassword.do
     * POST
     * 安全密码验证（wms）
     *
     * @param password
     * @return
     */
    public static API checkPassword(String password) {
        return new API(POST, "/user/userAccount/checkPassword.do", new ApiParams().put("password", password));
    }

    /**
     * 通过手机号 获取短信验证码
     *
     * @param phone
     * @return
     */
    public static API getAuthCodeForSecurityPsd(String phone) {
        return getAuthCodeForSecurityPsd(phone, null);
    }

    /**
     * 通过手机号和图片验证码 获取短信验证码
     *
     * @param phone
     * @param imgCode
     * @return
     */
    public static API getAuthCodeForSecurityPsd(String phone, String imgCode) {
        return new API(POST, "/user/userAccount/sendMsgCodeForPass.do",
                new ApiParams()
                        .put("phone", phone)
                        .put("imgCode", imgCode));
    }

    /**
     * /user/userAccount/forgetPassword.do
     * POST
     * 忘记密码（wms）
     *
     * @param code
     * @param phone
     * @return
     */
    public static API forgetPassWord(String code, String phone) {
        return new API("/user/userAccount/forgetPassword.do", new ApiParams()
                .put("code", code)
                .put("phone", phone));
    }

    /**
     * /user/bankCard/addBankCard.do
     * POST
     * 绑定银行卡（wms）
     *
     * @param realName        姓名
     * @param idCard          生份证号
     * @param cardNumber      银行卡号
     * @param cardPhone       手机号
     * @param issuingBankName 银行
     * @param bankId          银行ID
     * @return
     */
    public static API bindBankCard(String realName, String idCard, String cardNumber,
                                   String cardPhone, String issuingBankName, int bankId) {
        return new API(POST, "/user/bankCard/addBankCard.do", new ApiParams()
                .put("realName", realName)
                .put("idCard", idCard)
                .put("cardNumber", cardNumber)
                .put("cardPhone", cardPhone)
                .put("issuingBankName", issuingBankName)
                .put("bankId", bankId));
    }

    /**
     * /user/bankCard/updateBankCard.do
     * POST
     * 修改银行卡（wms）
     *
     * @param realName
     * @param idCard
     * @param cardNumber
     * @param cardPhone
     * @param issuingBankName
     * @param bankId
     * @return
     */
    public static API updateBankCard(String realName, String idCard, String cardNumber,
                                     String cardPhone, String issuingBankName, int bankId, int id) {
        return new API(POST, "/user/bankCard/updateBankCard.do", new ApiParams()
                .put("realName", realName)
                .put("idCard", idCard)
                .put("cardNumber", cardNumber)
                .put("cardPhone", cardPhone)
                .put("issuingBankName", issuingBankName)
                .put("bankId", bankId)
                .put("id", id));
    }


    /**
     * /user/userpayForDeposit/payDepositMoney.do
     * POST
     * 充值接口（nqc）
     * <p>
     * 提交至服务端充值信息
     *
     * @param platform
     * @param money
     * @param bankcardId
     * @return
     */
    public static API submitRechargeData(String platform, String money, Integer bankcardId) {
        return new API(POST, "/user/userpayForDeposit/payDepositMoney.do", new ApiParams()
                .put("platform", platform)
                .put("money", money)
                .put("bankcardId", bankcardId));
    }

    /**
     * user/userpayForDeposit/payCheckByQt.do
     * POST
     * 银行卡支付支付短信验证码确认（nqc）
     *
     * @param merchantOrderId 第三方订单id
     * @param checkCode       确认码
     * @return
     */
    public static API confirmBankPay(String merchantOrderId, String checkCode) {
        return new API("/user/userpayForDeposit/payCheckByQt.do", new ApiParams()
                .put("merchantOrderId", merchantOrderId)
                .put("checkCode", checkCode));
    }

    /**
     * user/userpayForDeposit/confirmPay.do
     * POST
     * 充值确认（nqc）
     *
     * @param merchantOrderId
     * @return
     */
    public static API queryConfirmPay(String merchantOrderId) {
        return new API("/user/userpayForDeposit/confirmPay.do", new ApiParams().put("merchantOrderId", merchantOrderId));
    }

    /**
     * /user/userAccount/userAccountInfo.do
     * POST
     * 个人资金信息（wms）
     *
     * @return
     */
    public static API requestUserFundInfo() {
        return new API(POST, "/user/userAccount/userAccountInfo.do");
    }

    /**
     * /user/userPay/alipayTrade.do
     * POST
     * app生成订单信息（wms）
     * 借款ID 充值传0
     *
     * @param money      订单金额
     * @param type       0 现金 1 元宝 2 积分
     * @param exchangeId 元宝兑换需要
     * @return
     */
    public static API requestAliPayOrderInfo(String money, int type, int exchangeId) {
        return new API(POST, "/user/userPay/alipayTrade.do", new ApiParams()
                .put("money", money)
                .put("orderId", 0)
                .put("exchangeId", exchangeId)
                .put("type", type));
    }

    /**
     * /user/userPay/alipayCheckSign.do
     * POST
     * 同步验签（wms）
     *
     * @param jsonStr
     * @return
     */
    public static API requestAliPayWebSign(String jsonStr) {
        return new API(POST, "/user/userPay/alipayCheckSign.do", new ApiParams().put("jsonStr", jsonStr));
    }


    /**
     * /user/bankCard/hasBindBankCard.do
     * GET
     * 是否绑定过银行卡（wms）
     *
     * @return
     */
    public static API requestUserBankCardInfo() {
        return new API("/user/bankCard/hasBindBankCard.do", null);
    }

    /**
     * /user/bankCard/getBank.do
     * GET
     * 查询可用的银行
     *
     * @return
     */
    public static API requestCanUseBankList() {
        return new API("/user/bankCard/getBank.do", null);
    }

    //提现手续费
    public static API requestWithDrawPoundage() {
        return new API(POST, "/user/withdraw/getFee.do");
    }

    /**
     * 提现
     * /user/withdraw/withdrawByBankcard.do
     *
     * @param money
     * @param BankcardId
     * @param password
     * @return
     */
    public static API withDraw(String money, int BankcardId, String password) {
        return new API(POST, "/user/withdraw/withdrawByBankcard.do", new ApiParams()
                .put("money", money)
                .put("BankcardId", BankcardId)
                .put("password", password));
    }

    /**
     * /user/bankCard/getPayRule.do
     * POST
     * 获取限额（wms）
     *
     * @param bankId
     * @return
     */
    public static API getBankLimit(int bankId) {
        return new API(POST, "/user/bankCard/getPayRule.do", new ApiParams().put("bankId", bankId));
    }

    public static API getVersusGaming(Long location) {
        return new API("/game/battle/selectBattleGaming.do", new ApiParams()
                .put("location", location)
                .put("pageSize", 15));
    }

    /**
     * 我的对战历史
     *
     * @param location
     * @return
     */
    public static API getMyVersusRecord(Long location) {
        return new API("/game/battle/myBattleGamed.do", new ApiParams()
                .put("location", location)
                .put("pageSize", 15));
    }

    /**
     * 所有对战历史
     *
     * @param location
     * @return
     */
    public static API getBattleHisRecord(Long location) {
        return new API("/game/battle/allBattleGamed.do", new ApiParams()
                .put("location", location)
                .put("pageSize", 15));
    }

    /**
     * 加入对战
     *
     * @param battleId 对战ID
     * @param userFrom 用户来源
     * @return
     */
    public static API joinBattle(int battleId, String userFrom) {
        return new API(POST, "/game/battle/joinBattle.do", new ApiParams()
                .put("battleId", battleId)
                .put("userFrom", userFrom));
    }

    /**
     * 用户当前对战
     *
     * @return
     */
    public static API getCurrentBattle() {
        return new API("/game/battle/userBattle.do");
    }

    /**
     * 应战者快速匹配
     */
    public static API quickMatchForAgainst(int type, String refuseIds) {
        return new API(POST, "/game/battle/quickSearchForAgainst.do", new ApiParams()
                .put("type", type)
                .put("refuseIds", refuseIds));
    }

    /**
     * 快速匹配结果查询
     *
     * @param type     1 查询房主 2查询应战者
     * @param battleId
     * @return
     */
    public static API getQuickMatchResult(int type, Integer battleId) {
        return new API("/game/battle/quickSearchForResult.do", new ApiParams()
                .put("type", type)
                .put("battleId", battleId));
    }

    /**
     * /user/userAccount/getExchangeConfig.do
     * GET
     * 获取快捷兑换接口（wms）
     *
     * @return
     */
    public static API getExchangeProduct() {
        return new API("/user/userAccount/getExchangeConfig.do", null);
    }

    /**
     * /user/userAccount/exchange.do
     * POST
     * 兑换接口（wms）
     *
     * @param exchangeId    兑换配置ID
     * @param password      密码
     * @param toMoney
     * @param fromRealMoney 兑换来源金额
     * @return
     */
    public static API exchange(int exchangeId, String password, double fromRealMoney, double toMoney) {
        return new API(POST, "/user/userAccount/exchange.do", new ApiParams()
                .put("exchangeId", exchangeId)
                .put("password", password)
                .put("fromRealMoney", fromRealMoney)
                .put("toMoney", toMoney));
    }

    /**
     * /user/appVersion/getUpdateVersion.do
     * GET
     * 获取App版本更新情况
     * app版本情况
     * platform  0 android
     *
     * @return
     */
    public static API updateVersion() {
        return new API("/user/appVersion/getUpdateVersion.do", new ApiParams().put("platform", 0));
    }

    /**
     * 获取期货对战品种列表
     *
     * @return
     */
    public static API getFutureBattleVarietyList() {
        return new API("/game/battleconfig/selectBattleVariety.do");
    }

    /**
     * 获取期货对战配置
     *
     * @return
     */
    public static API getFutureBattleConfig() {
        return new API("/game/battleconfig/findBattleConfig.do");
    }

    /**
     * 发起对战
     *
     * @param vartietyId
     * @param coinType
     * @param reward
     * @param endtime
     * @return
     */
    public static API launchBattle(int vartietyId, int coinType, double reward, int endtime) {
        return new API(POST, "/game/battle/createBattle.do",
                new ApiParams()
                        .put("vartietyId", vartietyId)
                        .put("coinType", coinType)
                        .put("reward", reward)
                        .put("endtime", endtime));
    }

    /**
     * 对战-游戏点赞
     *
     * @param battleId
     * @param praiseId
     * @return
     */
    public static API addBattlePraise(int battleId, int praiseId) {
        return new API(POST, "/game/battle/userPraise.do", new ApiParams()
                .put("battleId", battleId)
                .put("praiseId", praiseId));
    }

    /**
     * 房主快速匹配
     *
     * @param type     1 开始快速匹配 0 取消匹配 2 继续匹配
     * @param battleId
     * @return
     */
    public static API quickSearchForLaunch(int type, int battleId) {
        return new API(POST, "/game/battle/quickSearchForLaunch.do", new ApiParams()
                .put("type", type)
                .put("battleId", battleId));
    }

    /**
     * 取消对战
     *
     * @param battleId
     * @return
     */
    public static API cancelBattle(int battleId) {
        return new API(POST, "/game/battle/cancelBattle.do", new ApiParams()
                .put("battleId", battleId));
    }

    /**
     * 下单记录
     *
     * @param battleId
     * @return
     */
    public static API getOrderHistory(int battleId) {
        return new API("/game/battleorder/optLog.do", new ApiParams()
                .put("battleId", battleId));
    }

    /**
     * 创建订单
     *
     * @param battleId
     * @param direction 方向 0跌 1涨
     * @return
     */
    public static API createOrder(int battleId, int direction) {
        return new API(POST, "/game/battleorder/createOrder.do", new ApiParams()
                .put("direction", direction)
                .put("battleId", battleId));
    }

    /**
     * 平仓
     *
     * @param battleId
     * @param orderId
     * @return
     */
    public static API closePosition(int battleId, int orderId) {
        return new API(POST, "/game/battleorder/unwind.do", new ApiParams()
                .put("battleId", battleId)
                .put("orderId", orderId));
    }


    /**
     * 根据ID查询对战信息
     *
     * @param battleId
     * @param batchCode
     * @return
     */
    public static API getBattleInfo(int battleId, String batchCode) {
        return new API("/game/battle/findBattle.do", new ApiParams()
                .put("battleId", battleId)
                .put("batchCode", batchCode));
    }


    /**
     * 游戏大厅列表实时数据
     *
     * @param battleIds
     * @return
     */
    public static API getBattleGamingData(String battleIds) {
        return new API("/game/battle/selectBattleGamingData.do", new ApiParams()
                .put("battleIds", battleIds));
    }

    /**
     * 订阅对战
     *
     * @param battleId
     * @return
     */
    public static API requestSubscribeBattle(int battleId) {
        return new API("/game/battle/subscribeBattle.do", new ApiParams()
                .put("battleId", battleId));
    }

    /**
     * 取消订阅对战
     *
     * @param battleId
     * @return
     */
    public static API requestUnsubscribeBattle(int battleId) {
        return new API("/game/battle/unsubscribeBattle.do", new ApiParams()
                .put("battleId", battleId));
    }

    /**
     * 查询房间交易中的订单
     *
     * @param battleId
     * @return
     */
    public static API requestCurrentOrder(int battleId) {
        return new API("/game/battleorder/orders.do", new ApiParams()
                .put("battleId", battleId));
    }


    public static API requestVarietyPrice(int varietyId) {
        return new API("/order/future/query/infoPrice.do", new ApiParams()
                .put("varietyId", varietyId));
    }

    //请求参加水平测试人数

    /**
     * /train/evaluate/FinishCount.do
     * 完成测评的人数
     *
     * @return
     */
    public static API requestJoinTestedNumber() {
        return new API("/train/evaluate/FinishCount.do");
    }

    //请求测试题库

    /**
     * /train/evaluate/topic.do
     * GET
     * 获取题目
     *
     * @return
     */
    public static API requestExamQuestions() {
        return new API("/train/evaluate/topic.do");
    }

    //提交水平测试答案接口

    /**
     * /**
     * train/evaluate/End.do
     * POST
     * 提交测评
     *
     * @return
     */
    public static API confirmLevelTestResult(QuestionAnswer finishPO) {
        return new API(POST, "/train/evaluate/End.do", new Gson().toJson(finishPO));
    }

    /**
     * /train/evaluate/historyLog.do
     * GET
     * 历史测评
     *
     * @return
     */
    public static API requestHistoryTestResultList() {
        return new API("/train/evaluate/historyLog.do");
    }

    /**
     * 获取小姐姐列表
     * /**
     * 获取小姐姐列表
     *
     * @return
     */
    public static API getMissList() {
        return new API("/explain/customInfo/getCustomList.do");
    }

    /**
     * 打赏小姐姐
     *
     * @param customId
     * @param money
     * @param type
     * @return
     */
    public static API rewardMiss(int customId, double money, int type, String password) {
        return new API(POST, "/explain/customInfo/award.do", new ApiParams()
                .put("customId", customId)
                .put("money", money)
                .put("type", type)
                .put("password", password));
    }

    /**
     * 打赏问题
     *
     * @param quesionId
     * @param money
     * @param type
     * @return
     */
    public static API rewardQuestion(int quesionId, double money, int type, String password) {
        return new API(POST, "/explain/question/award.do", new ApiParams()
                .put("questionId", quesionId)
                .put("money", money)
                .put("type", type)
                .put("password", password));
    }

    /**
     * 添加评论/回复
     *
     * @param invitationUserId
     * @param replyParentId
     * @param content
     * @param dataId
     * @return
     */
    public static API addComment(int invitationUserId, String replyParentId, String content, int dataId) {
        return new API(POST, "/user/comment/insertComment.do", new ApiParams()
                .put("invitationUserId", invitationUserId)
                .put("replyParentId", replyParentId)
                .put("type", 1)
                .put("content", content)
                .put("dataId", dataId));
    }

    /**
     * 发布提问
     *
     * @param questionContext
     * @param appointCustomId
     * @return
     */
    public static API addQuestion(String questionContext, Integer appointCustomId) {
        return new API(POST, "/explain/question/addQuestion.do", new ApiParams()
                .put("questionContext", questionContext)
                .put("appointCustomId", appointCustomId));
    }

    /**
     * 姐说消息列表
     *
     * @return
     */

    public static API getQuestionMessageList() {
        return new API("/msg/msg/history.do", new ApiParams()
                .put("classify", 4)
                .put("size", 200)
                .put("autoRead", false));
    }

    /**
     * 读消息
     *
     * @param msgId
     * @return
     */
    public static API readMessage(int msgId) {
        return new API(POST, "/msg/msg/read.do", new ApiParams()
                .put("msgId", msgId));
    }

    /**
     * 乐米日报精选
     *
     * @return
     */
    public static API getDailyReport() {
        return new API("/user/dailyReport/findRecencyReport.do");
    }

    /**
     * 日报列表
     *
     * @param page
     * @return
     */
    public static API getDailyReportList(int page) {
        return new API("/user/dailyReport/findDailyReportList.do", new ApiParams()
                .put("page", page)
                .put("pageSize", 20));
    }

    /**
     * 日报收藏
     *
     * @return
     */
    public static API collect(String id) {
        return new API(POST, "/user/dailyReport/collect.do", new ApiParams()
                .put("id", id));
    }

    /**
     * 日报详情
     *
     * @return
     */
    public static API getDailyReportDetail(String id) {
        return new API(POST, "/user/dailyReport/showDetail.do", new ApiParams()
                .put("id", id));
    }

    /**
     * 获取热门提问列表
     *
     * @return
     */
    public static API getHotQuestionList() {
        return new API("/explain/question/hotQuestionList.do");
    }

    /**
     * 获取最新提问列表
     *
     * @return
     */
    public static API getLatestQuestionList(Long createTime, int pageSize) {
        return new API("/explain/question/questionList.do",
                new ApiParams()
                        .put("createTime", createTime)
                        .put("pageSize", pageSize));
    }

    /**
     * 小姐姐点赞
     *
     * @param questionId
     * @return
     */
    public static API prise(int questionId) {
        return new API("/explain/question/prise.do", new ApiParams()
                .put("questionId", questionId));
    }

    /**
     * 关注小姐姐
     *
     * @param customId
     * @return
     */
    public static API attention(int customId) {
        return new API("/explain/customInfo/attention.do", new ApiParams()
                .put("customId", customId));
    }

    /**
     * 获取小姐姐详情
     *
     * @param customId
     * @return
     */
    public static API getMissDetail(int customId) {
        return new API("/explain/customInfo/getCustomInfo.do", new ApiParams()
                .put("customId", customId));
    }

    /**
     * 获取小姐姐她的回答列表
     *
     * @param customId
     * @param createTime
     * @param pageSize
     * @return
     */
    public static API getHerAnswerList(int customId, Long createTime, int pageSize) {
        return new API("/explain/question/questionListByCustom.do",
                new ApiParams()
                        .put("customId", customId)
                        .put("createTime", createTime)
                        .put("pageSize", pageSize));
    }


    /**
     * 获取我的提问列表
     *
     * @param createTime
     * @param pageSize
     * @return
     */
    public static API getMyQuestionList(Long createTime, int pageSize) {
        return new API("/explain/question/myQuestionList.do",
                new ApiParams()
                        .put("createTime", createTime)
                        .put("pageSize", pageSize));
    }

    /**
     * 获取回复列表
     *
     * @param type
     * @param dataId
     * @param page
     * @param pageSize
     * @return
     */
    public static API getQuestionReplyList(int type, int dataId, int page, int pageSize, String replyMsgId) {
        return new API("/user/comment/replyList.do",
                new ApiParams()
                        .put("type", type)
                        .put("dataId", dataId)
                        .put("page", page)
                        .put("pageSize", pageSize)
                        .put("replyMsgId", replyMsgId)
        );
    }

    /**
     * 提问详情
     *
     * @param questionId
     * @return
     */
    public static API getQuestionDetails(int questionId) {
        return new API("/explain/question/questionInfo.do",
                new ApiParams()
                        .put("questionId", questionId)
        );
    }

    /**
     * http://var.esongbai.xyz/train/appraise/user.do
     * GET
     * 获得登录用户的评价分
     *
     * @return
     */
    public static API requestUserScore() {
        return new API("/train/appraise/user.do");
    }

    /**
     * http://var.esongbai.xyz/train/train/myList.do
     * GET
     * 我的训练列表
     *
     * @return
     */
    public static API requestMineTrainProjectList() {
        return new API("/train/train/myList.do", new ApiParams()
                .put("page", 0)
                .put("pageSize", 50));
    }

    /**
     * 获取我的训练记录
     *
     * @param trainId
     * @return
     */
    public static API getMyTrainingRecord(int trainId) {
        return new API("/train/train/userTrain.do", new ApiParams()
                .put("trainId", trainId));
    }

    /**
     * /train/train/recommendList.do
     * GET
     * 推荐列表
     *
     * @return
     */
    public static API requestTrainProjectList() {
        return new API("/train/train/recommendList.do",
                new ApiParams()
                        .put("page", 0)
                        .put("pageSize", 50));
    }

    /**
     * 学一学记录
     *
     * @return
     */
    public static API getMyStudyInfo() {
        return new API("/train/course/findTrainCourse.do");
    }

    /**
     * 听语音
     *
     * @param questionId
     * @return
     */

    public static API listen(int questionId) {
        return new API("/explain/question/listen.do",
                new ApiParams()
                        .put("questionId", questionId)
        );
    }


    /**
     * /train/appraise/suggest.do
     * GET
     * 评价建议
     */
    public static API requestScoreStageAndRemark() {
        return new API("/train/appraise/suggest.do");
    }

    /**
     * 获取训练列表
     *
     * @return
     */
    public static API getTrainingList() {
        return new API("/train/train/list.do",
                new ApiParams()
                        .put("page", 0)
                        .put("pageSize", 100));
    }

    /**
     * 获取 训练的内容
     *
     * @param trainId
     * @return
     */
    public static API getTrainingContent(int trainId) {
        return new API("/train/train/start.do", new ApiParams()
                .put("trainId", trainId));
    }

    /**
     * 提交 训练结果
     *
     * @param result
     * @return
     */
    public static API submitTrainingResult(String result) {
        return new API(POST, "/train/train/finish.do", new ApiParams()
                .put("data", result));
    }

    /**
     * @param deviceId
     * @return
     */
    public static API getTrainCourse(String deviceId) {
        return new API(POST, "/train/course/task.do",
                new ApiParams()
                        .put("deviceId", deviceId)
        );
    }

    /**
     * 更多训练项目换一批
     *
     * @param pageSize
     * @return
     */
    public static API getFeedBackTrainList(int pageSize) {
        return new API("/train/trainfeedback/changeBatchTrainMore.do",
                new ApiParams()
                        .put("pageSize", pageSize));
    }

    public static API commitFeedBackTrain(String ids, String content) {
        return new API(POST, "/train/trainfeedback/moreTrainFeedback.do",
                new ApiParams()
                        .put("ids", ids)
                        .put("content", content));
    }

    /**
     * 乐米分排行
     * profit gold  appraise
     * day week
     *
     * @param type
     * @param timeType
     * @return
     */

    public static API getleaderBoardList(String type, String timeType) {
        return new API("/train/rank/rank.do",
                new ApiParams()
                        .put("type", type)
                        .put("timeType", timeType));
    }

    /**
     * 获取热门心得列表
     *
     * @return
     */
    public static API getHotExperienceList(int trainId) {
        return new API("/train/perception/findHotPerception.do",
                new ApiParams().put("trainId", trainId));
    }

    /**
     * 获取最新心得列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    public static API getLatestExperienceList(int page, int pageSize, int trainId) {
        return new API("/train/perception/pageQueryRecentPerception.do",
                new ApiParams()
                        .put("page", page)
                        .put("pageSize", pageSize)
                        .put("trainId", trainId));
    }

    /**
     * 写心得
     *
     * @param invitationUserId
     * @param type
     * @param star
     * @param content
     * @param picture
     * @return
     */
    public static API writeExperience(int invitationUserId, int type, Integer star, String content, String picture) {
        return new API(POST, "/user/comment/insertComment.do?",
                new ApiParams()
                        .put("invitationUserId", invitationUserId)
                        .put("type", type)
                        .put("star", star)
                        .put("content", content)
                        .put("picture", picture));
    }

    /**
     * 提交试卷
     *
     * @param jsonStr
     * @return
     */

    public static API handInPaper(String jsonStr) {
        return new API(POST, "/train/course/finishTask.do", jsonStr);
    }

    /**
     * 膜拜
     *
     * @param targetId
     * @return
     */
    public static API worship(int targetId, String type, String timeType) {
        return new API(POST, "/train/rank/worship.do",
                new ApiParams()
                        .put("targetId", targetId)
                        .put("type", type)
                        .put("timeType", timeType));
    }

    /**
     * 排行列表数据
     */
    public static API getLeaderLists() {
        return new API("/train/rank/list.do");
    }

    /**
     * 批量对消息
     */
    public static API batchRead(String ids) {
        return new API(POST, "/msg/msg/readBatch.do",
                new ApiParams()
                        .put("ids", ids));
    }

    /**
     * 批量读消息
     * classify  1系统 4 姐说
     */
    public static API batchRead() {
        return new API(POST, "/msg/msg/readAll.do",
                new ApiParams()
                        .put("classify", 1));
    }

    /**
     * 获取训练详情
     *
     * @param trainId
     * @return
     */
    public static API getTrainingDetail(int trainId) {
        return new API("/train/train/detail.do",
                new ApiParams()
                        .put("trainId", trainId));
    }

    /**
     * 最近完成训练的人 以及训练记录
     *
     * @param page
     * @param pageSize
     * @param trainId
     * @return
     */
    public static API getTrainedUserRecords(int page, int pageSize, int trainId) {
        return new API("/train/train/finishUser.do",
                new ApiParams()
                        .put("page", page)
                        .put("pageSize", pageSize)
                        .put("trainId", trainId));
    }


    /**
     * 训练点赞
     *
     * @param praiseId
     * @param isPraise
     * @return
     */
    public static API trainExperiencePraise(String praiseId, int isPraise) {
        return new API(POST, "/train/perception/dealClickPraise.do", new ApiParams()
                .put("praiseId", praiseId)
                .put("isPraise", isPraise));
    }

    /**
     * 分享
     *
     * @return
     */
    public static API share() {
        return new API("/explain/question/share.do");
    }

    /**
     * @param trainId
     * @return
     */
    public static API isTrained(int trainId) {
        return new API("/train/perception/userIsPerception.do",
                new ApiParams().put("trainId", trainId));
    }

    /**
     * 训练意见反馈接口
     */
    public static API trainFeedback(int trainId, String content, int contentType) {
        return new API(POST, "/train/trainfeedback/trainAdviceFeedback.do?",
                new ApiParams()
                        .put("trainId", trainId)
                        .put("content", content)
                        .put("contentType", contentType));
    }

    /**
     * 训练意见反馈列表接口
     */
    public static API getTrainFeedbackList(int trainId, int page, int pageSize) {
        return new API("/train/trainfeedback/queryUserFeedBackHistory.do",
                new ApiParams()
                        .put("trainId", trainId)
                        .put("page", page)
                        .put("pageSize", pageSize));
    }

    //获取客服联系方式
    public static API requestServiceConnectWay() {
        return new API("/user/dictionary/findDictionaryForJson.do?type=aboutus");
    }

    /**
     * 活动兑换记录
     */
    public static API getSkinList(int activityId, int page) {
        return new API("/user/skin/queryExchangeLogByUser.do",
                new ApiParams()
                        .put("activityId", activityId)
                        .put("page", page)
                        .put("pageSize", 20));
    }

    /**
     * 启动弹窗
     */
    public static API getStart() {
        return new API("/user/start/findAll.do");
    }

    /**
     * /user/user/myQuestion.do
     * POST
     * 我的问答
     *
     * @return
     */
    public static API requestMineQuestionOrComment(int type, int page) {
        return new API("/user/user/myQuestion.do",
                new ApiParams()
                        .put("type", type)
                        .put("page", page)
                        .put("pageSize", DEFAULT_PAGE_SIZE));
    }

    /**
     * 我的收藏
     * /user/user/myCollect.do
     *
     * @return
     */
    public static API requestMyCollection(int type, int page) {
        return new API("/user/user/myCollect.do", new ApiParams()
                .put("pageSize", Client.DEFAULT_PAGE_SIZE)
                .put("type", type)
                .put("page", page));
    }

    /**
     * /user/dailyReport/collect.do
     * GET
     * 乐米日报收藏（wms）
     *
     * @return
     */
    public static API changeReportCollectionStatus(String id) {
        return new API(POST, "/user/dailyReport/collect.do", new ApiParams().put("id", id));
    }

    /**
     * 获取训练项目数量
     *
     * @return
     */
    public static API requestTrainCount() {
        return new API("/train/train/trainCount.do");
    }

    /**
     * /user/registerLogin/sensitizeApp.do
     * GET
     * 激活APP
     *
     * @return
     */
    public static API updatePageOpenTime() {
        return new API(POST, "/user/registerLogin/sensitizeApp.do",
                new ApiParams()
                        .put("deviceId", AppInfo.getDeviceHardwareId(App.getAppContext())));
    }

    /**
     * 图片验证码地址
     *
     * @param phone
     * @return
     */
    public static String getImageAuthCode(String phone) {
        return API.getHost() + "/user/registerLogin/getRegImage.do?userPhone=" + phone;
    }

    /**
     * push 过来的html 服务端说有长度限制  调用接口去获取banner
     *
     * @param id
     * @return
     */
    public static API requestBannerInfo(String id) {
        return new API("/user/news/findBannerById.do", new ApiParams().put("id", id));
    }
}
