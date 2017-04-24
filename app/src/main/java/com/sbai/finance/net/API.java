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


}
