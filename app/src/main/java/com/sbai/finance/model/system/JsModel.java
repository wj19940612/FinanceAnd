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
    private String shareTitle;
    private String shareContent;
    private String shareUrl;
    private String shareImage;

    public String getTitle() {
        return shareTitle;
    }

    public String getDescription() {
        return shareContent;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public String getShareThumbnailUrl() {
        return shareImage ;
    }

    @Override
    public String toString() {
        return "JsModel{" +
                "shareTitle='" + shareTitle + '\'' +
                ", shareContent='" + shareContent + '\'' +
                ", shareUrl='" + shareUrl + '\'' +
                ", shareImage='" + shareImage + '\'' +
                '}';
    }
}
