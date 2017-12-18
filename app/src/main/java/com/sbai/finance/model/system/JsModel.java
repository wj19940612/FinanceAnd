package com.sbai.finance.model.system;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by ${wangJie} on 2017/12/18.
 * AppJs中 controlTitleBarRightView 中传递过来的值
 */

public class JsModel {

    public static JsModel getJsModel(String content) {
        JsModel jsModel = null;
        try {
            jsModel = new Gson().fromJson(content, JsModel.class);
        } catch (JsonSyntaxException e) {

        }
        return jsModel;
    }

    //分享内容
    private String title;
    private String description;
    private String shareUrl;
    private String shareThumbnailUrl;
    private String shareChannel;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public String getShareThumbnailUrl() {
        return shareThumbnailUrl;
    }

    public String getShareChannel() {
        return shareChannel;
    }
}
