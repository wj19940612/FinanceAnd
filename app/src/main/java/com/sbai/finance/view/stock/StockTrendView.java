package com.sbai.finance.view.stock;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.chart.ChartSettings;
import com.sbai.chart.TrendView;
import com.sbai.finance.R;
import com.sbai.finance.model.stock.StockRTData;
import com.sbai.finance.model.stock.StockTrendData;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockTrendView extends LinearLayout {

    @BindView(R.id.askPrice5)
    TextView mAskPrice5;
    @BindView(R.id.askVolume5)
    TextView mAskVolume5;
    @BindView(R.id.askPrice4)
    TextView mAskPrice4;
    @BindView(R.id.askVolume4)
    TextView mAskVolume4;
    @BindView(R.id.askPrice3)
    TextView mAskPrice3;
    @BindView(R.id.askVolume3)
    TextView mAskVolume3;
    @BindView(R.id.askPrice2)
    TextView mAskPrice2;
    @BindView(R.id.askVolume2)
    TextView mAskVolume2;
    @BindView(R.id.askPrice1)
    TextView mAskPrice1;
    @BindView(R.id.askVolume1)
    TextView mAskVolume1;
    @BindView(R.id.bidPrice1)
    TextView mBidPrice1;
    @BindView(R.id.bidVolume1)
    TextView mBidVolume1;
    @BindView(R.id.bidPrice2)
    TextView mBidPrice2;
    @BindView(R.id.bidVolume2)
    TextView mBidVolume2;
    @BindView(R.id.bidPrice3)
    TextView mBidPrice3;
    @BindView(R.id.bidVolume3)
    TextView mBidVolume3;
    @BindView(R.id.bidPrice4)
    TextView mBidPrice4;
    @BindView(R.id.bidVolume4)
    TextView mBidVolume4;
    @BindView(R.id.bidPrice5)
    TextView mBidPrice5;
    @BindView(R.id.bidVolume5)
    TextView mBidVolume5;

    private StockTrendChart mTrendView;
    private StockTouchView mTouchView;
    //private StockTwinkleView mTwinkleView;

    private View mFivePriceView;

    public StockTrendView(Context context) {
        super(context);
        init();
    }

    public StockTrendView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);

        mTrendView = new StockTrendChart(getContext());
        int padding = (int) mTrendView.dp2Px(14);
        mTrendView.setPadding(padding, 0, padding, 0);

        mTouchView = new StockTouchView(getContext(), mTrendView);
        mTouchView.setPadding(padding, 0, padding, 0);

//        mTwinkleView = new StockTwinkleView(getContext(), mTrendView);
//        mTwinkleView.setPadding(padding, 0, padding, 0);

        mFivePriceView = LayoutInflater.from(getContext()).inflate(R.layout.view_five_price, null);

        FrameLayout container = new FrameLayout(getContext());
        container.addView(mTrendView);
        container.addView(mTouchView);
//        container.addView(mTwinkleView);
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        addView(container, params);

        int width = (int) dp2Px(125f, getResources());
        params = new LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mFivePriceView, params);

        ButterKnife.bind(this, mFivePriceView);
    }

    private float dp2Px(float value, Resources res) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, res.getDisplayMetrics());
    }

    public void setSettings(ChartSettings settings) {
        mTrendView.setSettings(settings);
        mTouchView.setSettings(settings);
        //mTwinkleView.setSettings(settings);
    }

    public Settings getSettings() {
        return mTrendView.getSettings();
    }

    public void setDataList(List<StockTrendData> dataList) {
        filterInvalidData(dataList);
        mTrendView.setDataList(dataList);
        //mTwinkleView.setDataList(dataList);
    }

    private void filterInvalidData(List<StockTrendData> dataList) {
        if (dataList != null) {
            String[] openMarketTimes = getSettings().getOpenMarketTimes();
            Iterator<StockTrendData> iterator = dataList.iterator();
            while (iterator.hasNext()) {
                StockTrendData data = iterator.next();
                if (!TrendView.Util.isValidDate(data.getHHmm(), openMarketTimes)) {
                    iterator.remove();
                }
            }
        }
    }

    public void setUnstableData(StockTrendData unstableData) {
        mTrendView.setUnstableData(unstableData);
        //mTwinkleView.setUnstableData(unstableData);
        mTouchView.setUnstableData(unstableData);
    }

    public void setHasFiveMarketView(boolean hasFiveMarketView) {
        if (hasFiveMarketView) {
            mFivePriceView.setVisibility(VISIBLE);
        } else {
            mFivePriceView.setVisibility(GONE);
        }
    }

    public void setStockRTData(StockRTData stockRTData) {
        if (mFivePriceView.getVisibility() == VISIBLE) {
            mAskPrice1.setText(stockRTData.getAskPrice());
            mAskPrice2.setText(stockRTData.getAskPrice2());
            mAskPrice3.setText(stockRTData.getAskPrice3());
            mAskPrice4.setText(stockRTData.getAskPrice4());
            mAskPrice5.setText(stockRTData.getAskPrice5());

            mBidPrice1.setText(stockRTData.getBidPrice());
            mBidPrice2.setText(stockRTData.getBidPrice2());
            mBidPrice3.setText(stockRTData.getBidPrice3());
            mBidPrice4.setText(stockRTData.getBidPrice4());
            mBidPrice5.setText(stockRTData.getBidPrice5());

            mAskVolume1.setText(getFormattedVolume(stockRTData.getAskVolume()));
            mAskVolume2.setText(getFormattedVolume(stockRTData.getAskVolume2()));
            mAskVolume3.setText(getFormattedVolume(stockRTData.getAskVolume3()));
            mAskVolume4.setText(getFormattedVolume(stockRTData.getAskVolume4()));
            mAskVolume5.setText(getFormattedVolume(stockRTData.getAskVolume5()));

            mBidVolume1.setText(getFormattedVolume(stockRTData.getBidVolume()));
            mBidVolume2.setText(getFormattedVolume(stockRTData.getBidVolume2()));
            mBidVolume3.setText(getFormattedVolume(stockRTData.getBidVolume3()));
            mBidVolume4.setText(getFormattedVolume(stockRTData.getBidVolume4()));
            mBidVolume5.setText(getFormattedVolume(stockRTData.getBidVolume5()));
        }
    }

    private String getFormattedVolume(String ask_volume) {
        long askVolume = 0;
        try {
            askVolume = Long.valueOf(ask_volume).longValue();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            askVolume = askVolume / 100;
            if (askVolume > 10000 || askVolume < -10000) {
                return formatWithUnit(askVolume);
            }
            return String.valueOf(askVolume);
        }
    }

    private String formatWithUnit(long askVolume) {
        BigDecimal decimal = BigDecimal.valueOf(askVolume)
                .divide(new BigDecimal(10000), 1, BigDecimal.ROUND_HALF_EVEN);
        return decimal.toString() + "万";
    }

    public static class Settings extends ChartSettings {

        private String mOpenMarketTimes;
        private String mDisplayMarketTimes;
        private boolean mCalculateXAxisFromOpenMarketTime;
        private boolean mXAxisRefresh;

        public void setOpenMarketTimes(String openMarketTimes) {
            mOpenMarketTimes = openMarketTimes;
        }

        public void setDisplayMarketTimes(String displayMarketTimes) {
            mDisplayMarketTimes = displayMarketTimes;
        }

        public String[] getOpenMarketTimes() {
            String[] result = new String[0];
            if (!TextUtils.isEmpty(mOpenMarketTimes)) {
                return mOpenMarketTimes.split(";");
            }
            return result;
        }

        public String[] getDisplayMarketTimes() {
            String[] result = new String[0];
            if (TextUtils.isEmpty(mDisplayMarketTimes)) {
                return mDisplayMarketTimes.split(";");
            }
            return result;
        }

        public void setCalculateXAxisFromOpenMarketTime(boolean value) {
            mCalculateXAxisFromOpenMarketTime = value;
            mXAxisRefresh = true;
        }

        @Override
        public int getXAxis() {
            if (mCalculateXAxisFromOpenMarketTime) {
                if (mXAxisRefresh) {
                    String[] openMarketTime = getOpenMarketTimes();
                    int size = openMarketTime.length % 2 == 0 ?
                            openMarketTime.length : openMarketTime.length - 1;
                    int xAxis = 0;
                    for (int i = 0; i < size; i += 2) {
                        xAxis += TrendView.Util.getDiffMinutes(openMarketTime[i], openMarketTime[i + 1]);
                    }
                    setXAxis(xAxis - 1);
                    mXAxisRefresh = false;

                }
                return super.getXAxis();
            }
            return super.getXAxis();
        }
    }
}
