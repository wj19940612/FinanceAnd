package com.sbai.finance.activity.future;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sbai.chart.KlineView;
import com.sbai.chart.TrendView;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.Product;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.TradeFloatButtons;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.R.id.trendView;

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

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(trendView)
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

        mProduct = new Gson().fromJson("{\"displayMarketTimes\": \"06:00;12:00;18:00;00:00;05:00\",\"decimalScale\": 0.2,\"sign\": \"$\",\"varietyType\": \"CL\",\"baseline\": 9,\"isDomestic\": 0,\"tags\": 0,\"exchangeId\": 9,\"openMarketTime\": \"06:00;05:00\",\"flashChartPriceInterval\": 0.2,\"varietyId\": 10,\"exchangeStatus\": 1,\"contractsCode\": \"CL1706\",\"advertisement\": \" \",\"currency\": \"USD\",\"marketPoint\": 2,\"varietyName\": \"美原油\",\"eachPointMoney\": 1000,\"currencyUnit\": \"美元\",\"ratio\": 7.3}", Product.class);

        initTabLayout();
        initChartViews();
    }

    private void initTabLayout() {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.trend_chart));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.day_k_line));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.sixty_min_k));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.thirty_min_k));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.fifteen_min_k));
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
    }

    private void initChartViews() {
        mTrendView.clearData();
        TrendView.Settings settings = new TrendView.Settings();
        settings.setBaseLines(mProduct.getBaseline());
        settings.setNumberScale(mProduct.getPriceDecimalScale());
        settings.setOpenMarketTimes(mProduct.getOpenMarketTime());
        settings.setDisplayMarketTimes(mProduct.getDisplayMarketTimes());
        settings.setLimitUpPercent((float) mProduct.getLimitUpPercent());
        settings.setCalculateXAxisFromOpenMarketTime(true);
        mTrendView.setSettings(settings);

        Client.getTrendData(mProduct.getVarietyType()).setTag(TAG)
                .setCallback(new Callback<String>() {
                    @Override
                    protected void onRespSuccess(String resp) {
                        TrendView.Settings settings = mTrendView.getSettings();
                        mTrendView.setDataList(TrendView.Util.createDataList(resp, settings.getOpenMarketTimes()));
                    }
                }).fireSync();
    }

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabLayout.removeOnTabSelectedListener(mOnTabSelectedListener);
    }
}
