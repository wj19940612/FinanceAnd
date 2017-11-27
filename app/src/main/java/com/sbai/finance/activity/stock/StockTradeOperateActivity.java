package com.sbai.finance.activity.stock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.stock.StockTradeOperateFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.model.stock.StockRTData;
import com.sbai.finance.model.stock.StockUser;
import com.sbai.finance.model.stocktrade.Position;
import com.sbai.finance.model.stocktrade.PositionRecords;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.StockUtil;
import com.sbai.finance.utils.TextViewUtils;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Modified by john on 24/11/2017
 * <p>
 * 股票交易操作页面，买入卖出，如果从股票详情页面进入，当前账户为模拟，如果从账户页面进入，依赖于当前保存是什么账户
 */
public class StockTradeOperateActivity extends BaseActivity implements StockTradeOperateFragment.OnTradeSuccessListener {

    public static final String TRADE_TYPE = "trade_type";
    public static final int TRADE_TYPE_BUY = 80;
    public static final int TRADE_TYPE_SELL = 81;

    @BindView(R.id.lastAndCostPrice)
    TextView mLastAndCostPrice;

    @BindView(R.id.slidingTab)
    SlidingTabLayout mSlidingTab;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.listView)
    ListView mListView;

    private StockTradeAdapter mStockTradeAdapter;

    private int mTradeType;
    private Variety mVariety;
    private StockRTData mStockRTData;

    private StockUser mStockUser;

    private HoldingPositionsAdapter mHoldingPositionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_trade_operate);
        ButterKnife.bind(this);

        initData(getIntent());

        initSlidingTab();
        iniListView();

        StockUser stockUser = LocalUser.getUser().getStockUser();
        if (stockUser != null) {
            mStockUser = stockUser;
            updateTradeVolume();
            requestStockHoldingList();
        } else {
            requestStockUser(String.valueOf(StockUser.ACCOUNT_TYPE_MOCK), null);
        }

        requestStockRTData();
    }

    private void initData(Intent intent) {
        mTradeType = intent.getIntExtra(TRADE_TYPE, TRADE_TYPE_BUY);
        mVariety = intent.getParcelableExtra(ExtraKeys.VARIETY);
    }

    private void iniListView() {
        mHoldingPositionsAdapter = new HoldingPositionsAdapter(getActivity());
        mListView.setAdapter(mHoldingPositionsAdapter);
    }

    private void updateTradeVolume() {
        for (int i = 0; i < mStockTradeAdapter.getCount(); i++) {
            Fragment fragment = mStockTradeAdapter.getFragment(i);
            if (fragment instanceof StockTradeOperateFragment) {
                ((StockTradeOperateFragment) fragment).updateTradeVolume();
            }
        }
    }

    private void updateRealTimeData() {
        for (int i = 0; i < mStockTradeAdapter.getCount(); i++) {
            Fragment fragment = mStockTradeAdapter.getFragment(i);
            if (fragment instanceof StockTradeOperateFragment) {
                ((StockTradeOperateFragment) fragment).updateRealTimeData(mStockRTData);
            }
        }
    }

    private void initSlidingTab() {
        mStockTradeAdapter = new StockTradeAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(mStockTradeAdapter);

        mSlidingTab.setDistributeEvenly(true);
        mSlidingTab.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mSlidingTab.setSelectedIndicatorPadding((int) Display.dp2Px(60, getResources()));
        mSlidingTab.setPadding(Display.dp2Px(13, getResources()));
        mSlidingTab.setViewPager(mViewPager);

        if (mTradeType == TRADE_TYPE_SELL) {
            mViewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onStockTradeSuccess() {
        StockUser stockUser = LocalUser.getUser().getStockUser();
        if (stockUser != null) {
            requestStockUser(String.valueOf(stockUser.getType()), stockUser.getActivityCode());
        }
    }

    private class StockTradeAdapter extends FragmentPagerAdapter {

        FragmentManager mFragmentManager;
        Context mContext;

        public StockTradeAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentManager = fm;
            mContext = context;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.buy_in);
                case 1:
                    return mContext.getString(R.string.sell_out);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return StockTradeOperateFragment.newInstance(mVariety, TRADE_TYPE_BUY);
                case 1:
                    return StockTradeOperateFragment.newInstance(mVariety, TRADE_TYPE_SELL);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }

    static class HoldingPositionsAdapter extends BaseAdapter {

        private Context mContext;
        private List<Position> mPositionList;
        private HashMap<String, StockData> mStockDataList;

        public void setPositionList(List<Position> positionList) {
            mPositionList = positionList;
            notifyDataSetChanged();
        }

        public void addStockData(List<StockData> stockDataList) {
            for (StockData data : stockDataList) {
                mStockDataList.put(data.getInstrumentId(), data);
            }
            notifyDataSetChanged();
        }

        public HoldingPositionsAdapter(Context context) {
            mContext = context;
            mPositionList = new ArrayList<>();
            mStockDataList = new HashMap<>();
        }

        public List<Position> getPositionList() {
            return mPositionList;
        }

        @Override
        public int getCount() {
            return mPositionList.size();
        }

        @Override
        public Object getItem(int position) {
            return mPositionList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_stock_position, parent);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Position pos = (Position) getItem(position);
            viewHolder.bindingData(pos, mStockDataList, mContext);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.stockName)
            TextView mStockName;
            @BindView(R.id.positionValue)
            TextView mPositionValue;
            @BindView(R.id.positionAmount)
            TextView mPositionAmount;
            @BindView(R.id.enableAmount)
            TextView mEnableAmount;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.costPrice)
            TextView mCostPrice;
            @BindView(R.id.floatValue)
            TextView mFloatValue;
            @BindView(R.id.floatRate)
            TextView mFloatRate;
            @BindView(R.id.positionArea)
            LinearLayout mPositionArea;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(Position pos, HashMap<String, StockData> stockDataList, Context context) {
                mStockName.setText(pos.getVarietyName());
                mPositionAmount.setText(String.valueOf(pos.getTotalQty()));
                mEnableAmount.setText(String.valueOf(pos.getUsableQty()));
                mCostPrice.setText(FinanceUtil.formatWithScale(pos.getAvgBuyPrice(), 3));

                StockData stockData = stockDataList.get(pos.getVarietyCode());
                if (stockData != null) {
                    int color = ContextCompat.getColor(context, R.color.primaryText);
                    String sign = "";
                    mLastPrice.setText(StockUtil.getStockDecimal(stockData.getLastPrice(), 3));
                    if (TextUtils.isEmpty(stockData.getLastPrice())) {
                        mPositionValue.setText(StockUtil.NULL_VALUE);
                        mFloatValue.setText(StockUtil.NULL_VALUE);
                        mFloatRate.setText(StockUtil.NULL_VALUE);
                    } else {
                        double lastPrice = Double.parseDouble(stockData.getLastPrice());
                        mPositionValue.setText(FinanceUtil.formatWithScale(lastPrice * pos.getTotalQty()));
                        double floatPrice = FinanceUtil.subtraction(lastPrice, pos.getAvgBuyPrice()).doubleValue();
                        if (floatPrice > 0) {
                            color = ContextCompat.getColor(context, R.color.redPrimary);
                            sign = "+";
                        } else if (floatPrice < 0) {
                            color = ContextCompat.getColor(context, R.color.greenAssist);
                        }
                        String floatValue = sign + FinanceUtil.formatWithScale(floatPrice * pos.getTotalQty());
                        mFloatValue.setText(floatValue);
                        String floatRate = sign + FinanceUtil.formatToPercentage(floatPrice / pos.getAvgBuyPrice());
                        mFloatRate.setText(floatRate);
                    }
                    TextViewUtils.setTextViewColor(mPositionArea, color);
                }
            }
        }
    }

    private void requestStockHoldingList() {
        Client.getStockHoldingList(mStockUser.getType(), mStockUser.getAccount(),
                mStockUser.getActivityCode()).setTag(TAG)
                .setCallback(new Callback2D<Resp<PositionRecords>, PositionRecords>() {
                    @Override
                    protected void onRespSuccessData(PositionRecords data) {
                        mHoldingPositionsAdapter.setPositionList(data.getList());
                    }
                }).fireFree();
    }


    private void requestStockUser(String accountType, String activityCode) {
        Client.getStockAccount(accountType, activityCode)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<StockUser>>, List<StockUser>>() {
                    @Override
                    protected void onRespSuccessData(List<StockUser> data) {
                        if (!data.isEmpty()) {
                            mStockUser = data.get(0);
                            LocalUser.getUser().setStockUser(mStockUser);
                            updateTradeVolume();
                            requestStockHoldingList();
                        }
                    }
                }).fireFree();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        startScheduleJob(1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    @Override
    public void onTimeUp(int count) {
        if (count % TimerHandler.STOCK_RT_PULL_TIME == 0) {
            requestStockRTData();
            requestStockHoldingListMarketData();
        }
    }

    private void requestStockHoldingListMarketData() {
        if (mHoldingPositionsAdapter != null) {
            List<Position> positionList = mHoldingPositionsAdapter.getPositionList();
            if (!positionList.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (Position position : positionList) {
                    stringBuilder.append(position.getVarietyCode()).append(",");
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                Client.getStockMarketData(stringBuilder.toString())
                        .setCallback(new Callback2D<Resp<List<StockData>>, List<StockData>>() {
                            @Override
                            protected void onRespSuccessData(List<StockData> result) {
                                mHoldingPositionsAdapter.addStockData(result);
                            }
                        }).fireFree();
            }
        }
    }

    private void requestStockRTData() {
        if (mVariety == null) return;
        Client.getStockRealtimeData(mVariety.getVarietyType())
                .setCallback(new Callback2D<Resp<StockRTData>, StockRTData>() {
                    @Override
                    protected void onRespSuccessData(StockRTData result) {
                        mStockRTData = result;
                        updateRealTimeData();
                    }

                    @Override
                    protected boolean onErrorToast() {
                        return false;
                    }
                }).fireFree();
    }
}
