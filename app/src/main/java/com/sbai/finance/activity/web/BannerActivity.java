package com.sbai.finance.activity.web;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.WebSettings;
import com.sbai.finance.activity.WebActivity;

public class BannerActivity extends WebActivity {

    public static final String INFO_HTML_META = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\">";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "banner的内容" + mPureHtml);

        openWebView(mPureHtml);
    }

    @Override
    protected boolean isNeedViewTitle() {
        return false;
    }

    private void openWebView(String urlData) {
        String content;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            getWebView().getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            content = INFO_HTML_META + "<body>" + "<br></br>" + mPureHtml + "</body>";
        } else {
            getWebView().getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            content = getHtmlData(urlData);
        }
        getWebView().loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style>" + INFO_HTML_META + "</head>";
        return "<html>" + head + "<br></br>" + bodyHTML + "</html>";
    }
}
