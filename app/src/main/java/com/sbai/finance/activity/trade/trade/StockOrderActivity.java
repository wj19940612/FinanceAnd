package com.sbai.finance.activity.trade.trade;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.battle.BattleListFragment;
import com.sbai.finance.fragment.trade.stock.StockBusinessFragment;
import com.sbai.finance.fragment.trade.stock.StockEntrustFragment;
import com.sbai.finance.fragment.trade.stock.StockPositionFragment;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.model.stocktrade.FundAndPosition;
import com.sbai.finance.model.stocktrade.Position;
import com.sbai.finance.model.stocktrade.PositionRecords;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.FundAndHoldingInfoView;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.picker.StockActivityPickerPopWin;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 股票订单页
 */

public class StockOrderActivity extends BaseActivity implements BattleListFragment.OnFragmentRecycleViewScrollListener {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.fundInfo)
    FundAndHoldingInfoView mFundInfo;
    @BindView(R.id.tabLayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    private int mAppBarVerticalOffset = -1;
    private PagerAdapter mPagerAdapter;
    private boolean mSwipeEnabled = true;
    private PositionRecords mPositionRecords;

    AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            mAppBarVerticalOffset = verticalOffset;
            boolean b = mSwipeEnabled && mAppBarVerticalOffset > -1;
            if (mSwipeRefreshLayout.isEnabled() != b) {
                mSwipeRefreshLayout.setEnabled(b);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_order);
        ButterKnife.bind(this);
        translucentStatusBar();
        mAppBarLayout.addOnOffsetChangedListener(mOnOffsetChangedListener);
        initTitleBar();
        initFundInfoView();
        initViewPager();
        initSwipeView();
        initTabView();
    }

    private void initSwipeView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //  requestAsset();
            }
        });
    }

    public void updateAssetAndPosition(List<StockData> result, Map<String, Position> positionMap) {
        double totalMarket = 0.00;
        double floatProfit = 0.00;
        for (StockData data : result) {
            Position position = positionMap.get(data.getInstrumentId());
            if (position != null) {
                totalMarket += position.getTotalQty() * Double.valueOf(data.getLastPrice());
                floatProfit += position.getTotalQty() * (Double.valueOf(data.getLastPrice()) - position.getAvgBuyPrice());
            }
        }
        mFundInfo.setTotalMarket(totalMarket);
        mFundInfo.setHoldingFloat(floatProfit);
        if (mPositionRecords != null) {
            mFundInfo.setTotalFund(mPositionRecords.getUsableMoney() + totalMarket);
        }
    }

    public void updateEnableAndFetchFund(PositionRecords data) {
        mPositionRecords = data;
        mFundInfo.setEnableFund(data.getUsableMoney());
        if (data.getUsableDraw() > 0) {
            mFundInfo.setFetchFund(data.getUsableDraw());
        }
    }

    private void initFundInfoView() {
        mFundInfo.setOnOrderClickListener(new FundAndHoldingInfoView.OnOrderClickListener() {
            @Override
            public void buy() {
                // TODO: 2017-11-21
            }

            @Override
            public void sell() {
                // TODO: 2017-11-21
            }

            @Override
            public void fetchFund() {
                showFetchFundDescribeDialog();
            }
        });
    }

    private void showFetchFundDescribeDialog() {
        SmartDialog.single(getActivity())
                .setGravity(Gravity.CENTER)
                .setTitle(R.string.tips)
                .setMessage(getString(R.string.fetch_fund_describe))
                .setNegative(R.string.know)
                .setPositiveVisable(View.GONE)
                .show();
    }

    private void initTitleBar() {
        View customView = mTitleBar.getCustomView();
        TextView mStockGame = customView.findViewById(R.id.stockGame);
        if (mStockGame != null) {
            mStockGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showActivityPickerDialog();
                }
            });
        }
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2017-11-21  
            }
        });
    }

    private void showActivityPickerDialog() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("111");
        arrayList.add("22");
        arrayList.add("3333333333");
        arrayList.add("5555");
        new StockActivityPickerPopWin.Builder(getActivity(),
                new StockActivityPickerPopWin.OnPickedListener() {
                    @Override
                    public void onPickCompleted(int position) {

                    }
                })
                .textConfirm(getString(R.string.ok)) //text of confirm button
                .textCancel(getString(R.string.cancel)) //text of cancel button
                .colorCancel(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .colorConfirm(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .btnTextSize(16) // button text size
                .viewTextSize(10) // pick view text size
                .dataChose(2) // date chose when init popwindow
                .dataList(arrayList)
                .build()
                .showPopWin(getActivity());
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0, false);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void initTabView() {
        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setPadding(Display.dp2Px(10, getResources()));
        mTabLayout.setSelectedIndicatorPadding(50);
        mTabLayout.setSelectedIndicatorHeight(2);
        mTabLayout.setViewPager(mViewPager);
        mTabLayout.setTabViewTextSize(16);
        mTabLayout.setTabViewTextColor(ContextCompat.getColorStateList(getActivity(), R.color.sliding_tab_text));
    }

    @Override
    public void onSwipRefreshEnable(boolean enabled, int fragmentPosition) {
        mSwipeEnabled = enabled;
        boolean b = enabled && mAppBarVerticalOffset > -1;
        if (mSwipeRefreshLayout.isEnabled() != b) {
            mSwipeRefreshLayout.setEnabled(b);
        }
    }

    @Override
    public void onCurrentBattle(Battle battle) {

    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    static class PagerAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private FragmentManager mFragmentManager;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            mFragmentManager = fm;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.position);
                case 1:
                    return mContext.getString(R.string.entrust);
                case 2:
                    return mContext.getString(R.string.history);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new StockPositionFragment();
                case 1:
                    return new StockEntrustFragment();
                case 2:
                    return new StockBusinessFragment();

            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
