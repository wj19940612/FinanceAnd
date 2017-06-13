package com.sbai.finance.activity.trade;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.R;
import com.sbai.finance.activity.WebActivity;

/**
 * Created by linrongfang on 2017/5/2.
 */

public class TradeWebActivity extends WebActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTestData();
        setUpTitleBar();
    }

    private void setUpTitleBar() {
        getTitleBar().setTitle(getString(R.string.crude_oil_future));
    }


    private void loadTestData() {
        String testUrl = "https://www.dajiexin.com/";
        getWebView().loadUrl(testUrl);
    }
}
