package com.sbai.finance.activity.trade;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by linrongfang on 2017/5/2.
 */

public class TradeWebActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_web);
        ButterKnife.bind(this);
        initWebviewSetting();
        loadTestData();
    }

    private void initWebviewSetting() {
        mWebView.getSettings().setJavaScriptEnabled(true);
    }

    private void loadTestData() {
        String testUrl = "https://www.dajiexin.com/";
        mWebView.loadUrl(testUrl);
    }
}
