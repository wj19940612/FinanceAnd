package com.sbai.finance.activity.trade.trade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.stock.StockTradeOperateActivity;
import com.sbai.finance.fragment.battle.BattleListFragment;
import com.sbai.finance.fragment.trade.stock.StockBusinessFragment;
import com.sbai.finance.fragment.trade.stock.StockEntrustFragment;
import com.sbai.finance.fragment.trade.stock.StockPositionFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.mutual.ArticleProtocol;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.model.stock.StockUser;
import com.sbai.finance.model.stocktrade.Position;
import com.sbai.finance.model.stocktrade.PositionRecords;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.Network;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.FundAndHoldingInfoView;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.picker.StockActivityPickerPopWin;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.activity.stock.StockTradeOperateActivity.TRADE_TYPE;
import static com.sbai.finance.activity.stock.StockTradeOperateActivity.TRADE_TYPE_BUY;
import static com.sbai.finance.activity.stock.StockTradeOperateActivity.TRADE_TYPE_SELL;
import static com.sbai.finance.utils.Network.registerNetworkChangeReceiver;
import static com.sbai.finance.utils.Network.unregisterNetworkChangeReceiver;

/**
 * 股票订单页
 */

public class StockOrderActivity extends BaseActivity implements BattleListFragment.OnFragmentRecycleViewScrollListener {
    public static final String ACTION_SWITCH_ACCOUNT = "233";
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
    private TextView mStockAccountName;
    private int mAppBarVerticalOffset = -1;
    private PagerAdapter mPagerAdapter;
    private boolean mSwipeEnabled = true;
    private PositionRecords mPositionRecords;
    private StockUser mCurrentStockUser;
    private List<StockUser> mStockUsers;
    private BroadcastReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
                requestStockAccount();
            }
        }
    };
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
        requestStockAccount();
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

    private void initSwipeView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestStockAccount();
            }
        });
    }

    private void initTitleBar() {
        View customView = mTitleBar.getCustomView();
        mStockAccountName = customView.findViewById(R.id.stockGame);
        if (mStockAccountName != null) {
            mStockAccountName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showActivityPickerDialog();
                }
            });
        }
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Client.getArticleProtocol(ArticleProtocol.PROTOCOL_STOCK_SIMULATE).setTag(TAG)
                        .setCallback(new Callback2D<Resp<ArticleProtocol>, ArticleProtocol>() {
                            @Override
                            protected void onRespSuccessData(ArticleProtocol data) {
                                Launcher.with(getActivity(), WebActivity.class)
                                        .putExtra(WebActivity.EX_TITLE, data.getTitle())
                                        .putExtra(WebActivity.EX_HTML, data.getContent())
                                        .execute();
                            }

                        }).fire();
            }
        });
    }

    private void initFundInfoView() {
        mFundInfo.setOnOrderClickListener(new FundAndHoldingInfoView.OnOrderClickListener() {
            @Override
            public void buy() {
                Launcher.with(getActivity(), StockTradeOperateActivity.class)
                        .putExtra(TRADE_TYPE, TRADE_TYPE_BUY)
                        .execute();
            }

            @Override
            public void sell() {
                Launcher.with(getActivity(), StockTradeOperateActivity.class)
                        .putExtra(TRADE_TYPE, TRADE_TYPE_SELL)
                        .execute();
            }

            @Override
            public void fetchFund() {
                showFetchFundDescribeDialog();
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        StockUser newUser = LocalUser.getUser().getStockUser();
        if (newUser != null && mCurrentStockUser != null
                && !newUser.getAccount().equalsIgnoreCase(mCurrentStockUser.getAccount())) {
            setCurrentStockUser(newUser);
        }
        registerNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    private void requestStockAccount() {
        Client.getStockAccountList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<StockUser>>, List<StockUser>>() {
                    @Override
                    protected void onRespSuccessData(List<StockUser> data) {
                        if (!data.isEmpty()) {
                            updateStockAccount(data);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                }).fireFree();
    }

    private void setCurrentStockUser(StockUser stockUser) {
        if (stockUser != null) {
            mStockAccountName.setText(stockUser.getAccountName());
            if (mCurrentStockUser != null && !mCurrentStockUser.getAccount().equalsIgnoreCase(stockUser.getAccount())) {
                mFundInfo.resetView();
            }
        }
        mCurrentStockUser = stockUser;
        LocalUser.getUser().setStockUser(stockUser);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(ACTION_SWITCH_ACCOUNT));
    }

    private void requestSwitchAccount(final StockUser stockUser) {
        Client.requestSwitchAccount(stockUser.getId(), stockUser.getAccount())
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        setCurrentStockUser(stockUser);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();
    }

    private void updateStockAccount(List<StockUser> data) {
        mStockUsers = data;
        for (StockUser stockUser : mStockUsers) {
            if (stockUser.getActive() == StockUser.ACCOUNT_ACTIVE) {
                mCurrentStockUser = stockUser;
                break;
            }
        }
        if (mCurrentStockUser == null) {
            requestSwitchAccount(data.get(0));
        } else {
            setCurrentStockUser(mCurrentStockUser);
        }
    }

    public void updateAssetAndPosition(List<StockData> result, Map<String, Position> positionMap) {
        double totalMarket = 0.00;
        double floatProfit = 0.00;
        double todayProfit = 0.00;
        for (StockData data : result) {
            Position position = positionMap.get(data.getInstrumentId());
            if (position != null) {
                double lastPrice = Double.valueOf(data.getLastPrice());
                int todayBusinessAmount = position.getTotalQty() - position.getUsableQty();
                totalMarket += position.getTotalQty() * lastPrice;
                floatProfit += position.getTotalQty() * (lastPrice - position.getAvgBuyPrice());
                todayProfit += (lastPrice - Double.valueOf(data.getPreClsPrice())) * (position.getTodayBargainCount() - todayBusinessAmount + position.getUsableQty())
                        + (lastPrice - position.getTodayAvgPrice()) * todayBusinessAmount;
            }
        }
        mFundInfo.setTotalMarket(totalMarket);
        mFundInfo.setHoldingFloat(floatProfit);
        mFundInfo.setTodayProfit(todayProfit);
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
        mFundInfo.setTotalFund(mPositionRecords.getUsableMoney());

    }

    private void showFetchFundDescribeDialog() {
        SmartDialog.single(getActivity())
                .setGravity(Gravity.CENTER)
                .setTitle(R.string.tips)
                .setMessage(getString(R.string.fetch_fund_describe))
                .setNegative(R.string.know)
                .setPositiveVisible(View.GONE)
                .show();
    }

    private void showActivityPickerDialog() {
        if (mStockUsers == null || mStockUsers.isEmpty()) return;
        ArrayList<String> arrayList = new ArrayList<>();
        for (StockUser stockUser : mStockUsers) {
            arrayList.add(stockUser.getAccountName());
        }
        new StockActivityPickerPopWin.Builder(getActivity(),
                new StockActivityPickerPopWin.OnPickedListener() {
                    @Override
                    public void onPickCompleted(int position) {
                        if (mStockUsers.indexOf(mCurrentStockUser) == position) return;
                        if (position < mStockUsers.size()) {
                            requestSwitchAccount(mStockUsers.get(position));
                        }
                    }
                })
                .colorCancel(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .colorConfirm(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .dataChose(mStockUsers.indexOf(mCurrentStockUser))
                .dataList(arrayList)
                .build()
                .showPopWin(getActivity());
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
