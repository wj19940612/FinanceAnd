package com.sbai.finance.net;

import com.android.volley.Request;
import com.sbai.httplib.ApiParams;

/**
 * Created by ${wangJie} on 2017/4/24.
 */

public class API extends APIBase {

    private static final int GET = Request.Method.GET;

    public API(String uri) {
        super(uri);
    }

    public API(String uri, ApiParams apiParams) {
        super(uri, apiParams);
    }

    public API(int method, String uri, ApiParams apiParams) {
        super(method, uri, apiParams);
    }

    public API(String uri, ApiParams apiParams, int version) {
        super(uri, apiParams, version);
    }

    public API(int method, String uri, ApiParams apiParams, int version) {
        super(method, uri, apiParams, version);
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
        return new API("  /registerLogin/quickLogin.do", new ApiParams()
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
        return new API(GET, "/registerLogin/sendMsgCode.do", new ApiParams().put("phone", phone));
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
        return new API("/user/updatePic.do", new ApiParams().put("pic", pic));
    }

    /**
     * 请求类型 get
     * 请求Url  /out/logout.do
     * 接口描述 退出登入
     *
     * @return
     */
    public static API logout() {
        return new API(GET, "/out/logout.do", null);
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
        return new API("/user/updateUserName.do", new ApiParams().put("userName", userName));
    }

    /**
     * 接口名称 获取用户信息
     * 请求类型 get
     * 请求Url  /user/loadUserInfo.do
     *
     * @return
     */
    public static API requestDetailUserInfo() {
        return new API(GET, "/user/loadUserInfo.do", null);
    }

    /**
     * 接口名称 历史消息
     * 请求类型 post
     * 请求Url  msg/historyMsg.do
     *
     * @param classify
     * @return
     */
    public static API requestHistoryNews(int classify) {
        return new API("msg/historyMsg.do", new ApiParams().put("classify", classify));
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
        return new API("/user/updateUser",new ApiParams()
                .put("age",age)
                .put("land",land)
                .put("userSex",userSex));
    }
}
