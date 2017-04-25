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
     * 接口名称 快捷登入
     * 请求类型 post
     * 请求Url  /registerLogin/quickLogin.do
     *
     * @param msgCode 短信验证码
     * @param phone   手机
     * @return
     */
    public static API login(String msgCode, String phone) {
        return new API("/user/registerLogin/quickLogin.do", new ApiParams()
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
        return new API("/registerLogin/sendMsgCode.do", new ApiParams().put("phone", phone));
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
     * 请求Url  msg/historyMsg.do
     *
     * @param classify
     * @return
     */
    public static API requestHistoryNews(int classify, int page, int pageSize) {
        return new API("/msg/historyMsg.do", new ApiParams()
                .put("classify", classify)
                .put("page", page)
                .put("pageSize", pageSize));
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

}
