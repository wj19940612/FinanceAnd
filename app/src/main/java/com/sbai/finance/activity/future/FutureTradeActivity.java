package com.sbai.finance.activity.future;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.chart.KlineView;
import com.sbai.chart.TrendView;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.Product;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.TradeFloatButtons;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FutureTradeActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.todayOpen)
    TextView mTodayOpen;
    @BindView(R.id.preClose)
    TextView mPreClose;
    @BindView(R.id.highest)
    TextView mHighest;
    @BindView(R.id.lowest)
    TextView mLowest;

    @BindView(R.id.marketArea)
    LinearLayout mMarketArea;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.trendView)
    TrendView mTrendView;
    @BindView(R.id.klineView)
    KlineView mKlineView;

    @BindView(R.id.tradeFloatButtons)
    TradeFloatButtons mTradeFloatButtons;

    private Product mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_trade);
        ButterKnife.bind(this);
        translucentStatusBar();
        if (Build.VERSION.SDK_INT >= 19) {
            addTopPaddingWithStatusBar(mTitleBar);
        }
    }
}
