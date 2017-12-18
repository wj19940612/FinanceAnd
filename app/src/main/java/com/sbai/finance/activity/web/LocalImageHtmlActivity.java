package com.sbai.finance.activity.web;

/**
 * Created by ${wangJie} on 2017/12/14.
 * 加载本地html  自己设置imageStyle
 */

public class LocalImageHtmlActivity extends BannerActivity {

    @Override
    protected String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{width: 100%; height: auto;border:none;vertical-align:top;bottom:0;left:0;display:block} *{margin: 0;padding: 0;}</style>" + INFO_HTML_META + "</head>";
        return "<html>" + head + bodyHTML + "</html>";
    }
}
