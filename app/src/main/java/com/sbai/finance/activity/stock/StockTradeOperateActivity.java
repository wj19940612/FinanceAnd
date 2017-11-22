package com.sbai.finance.activity.stock;

import android.os.Bundle;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockTradeOperateActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_trade_operate);
        ButterKnife.bind(this);

        initTitleBar();
    }

    private void initTitleBar() {

    }
}
