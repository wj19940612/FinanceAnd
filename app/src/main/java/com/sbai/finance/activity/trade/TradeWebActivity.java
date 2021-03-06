package com.sbai.finance.activity.trade;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.R;
import com.sbai.finance.activity.WebActivity;

/**
 * Created by linrongfang on 2017/5/2.
 */

public class TradeWebActivity extends WebActivity {

    private static final String TRADE_URL = "https://gf1.dajiexin.com";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpTitleBar();
        loadUrl();
    }

    private void setUpTitleBar() {
        getTitleBar().setTitle(getString(R.string.quick_trade));
    }

    @Override
    protected boolean isNeedViewTitle() {
        return false;
    }

    private void loadUrl() {
        getWebView().loadUrl(TRADE_URL);
    }
}
