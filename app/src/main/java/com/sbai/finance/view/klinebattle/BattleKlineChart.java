package com.sbai.finance.view.klinebattle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;

import com.sbai.chart.KlineChart;
import com.sbai.chart.domain.KlineViewData;
import com.sbai.finance.model.klinebattle.BattleKlineData;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Modified by john on 11/12/2017
 * <p>
 * k 线对决的 k 线图
 */
public class BattleKlineChart extends KlineChart {

    private static final String TRANS_GREEN = "#262fcc9f";
    private static final String TRANS_RED = "#26f25b57";
    private List<BattleKlineData> mDataList;
    private double mBuyPrice;

    protected void setTranslucentBgPaint(Paint paint, String color) {
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    public BattleKlineChart(Context context) {
        super(context);
    }

    public BattleKlineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean enableDrawTouchLines() {
        return false;
    }

    @Override
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {
    }

    @Override
    protected boolean enableCalculateMovingAverages() {
        return false;
    }

    public void addKlineData(BattleKlineData gameKlineData) {
        if (mDataList == null || gameKlineData == null) return;
        mDataList.add(gameKlineData);
        new DataShowTask(this, mDataList, mDataList.size() - 1).execute();
    }

    public BattleKlineData getLastData() {
        if (mDataList != null) {
            return mDataList.get(mDataList.size() - 1);
        }
        return null;
    }

    /**
     * 使用 39条数据初始化
     *
     * @param klineDataList
     */
    public void initKlineDataList(List<BattleKlineData> klineDataList) {
        if (klineDataList == null) return;
        mDataList = klineDataList;
        new DataShowTask(this, mDataList).execute();
    }

    @Override
    protected void drawRealTimeData(boolean indexesEnable, int left, int top, int width, int height, int left2, int top2, int width2, int height2, Canvas canvas) {
        super.drawRealTimeData(indexesEnable, left, top, width, height, left2, top2, width2, height2, canvas);

        // draw translucent background
        List<KlineViewData> dataList = getDataList();
        if (dataList == null || dataList.isEmpty()) return;
        RectF rectF = null;
        for (int i = 0; i < dataList.size(); i++) {
            BattleKlineData data = (BattleKlineData) getDataList().get(i);
            if (data.getMark().equalsIgnoreCase(BattleKlineData.MARK_BUY)) { // buy point
                mBuyPrice = data.getClosePrice();

                if (i < getStart()) { // 屏幕外，左边
                    rectF = getRectF();
                    rectF.left = left;
                } else if (i < getEnd()) { // 屏幕内
                    rectF = getRectF();
                    rectF.left = getChartXOfScreen(i);
                }

                if (rectF != null) {
                    rectF.top = top;
                    rectF.bottom = top + height;
                    rectF.right = left + width - mPriceAreaWidth;
                }

            } else if (rectF != null && data.getMark().equalsIgnoreCase(BattleKlineData.MARK_SELL)) { // sell point
                if (i < getStart()) { // 屏幕外，左边, 不画
                    rectF = null;
                } else if (i < getEnd()) { // 屏幕内，更新 right
                    rectF.right = getChartXOfScreen(i);
                }

                if (rectF != null) {
                    String bgColor = data.getClosePrice() - mBuyPrice >= 0 ? TRANS_RED : TRANS_GREEN;
                    setTranslucentBgPaint(sPaint, bgColor);
                    canvas.drawRect(rectF, sPaint);
                    rectF = null;
                }
            } else if (rectF != null && i == dataList.size() - 1) { // 最后的数据，包括 Y，N
                if (i >= getStart() && i < getEnd()) {
                    rectF.right = getChartXOfScreen(i);
                }
                String bgColor = data.getClosePrice() - mBuyPrice >= 0 ? TRANS_RED : TRANS_GREEN;
                setTranslucentBgPaint(sPaint, bgColor);
                canvas.drawRect(rectF, sPaint);
                rectF = null;
            }
        }
    }

    static class DataShowTask extends AsyncTask<Void, Void, Void> {

        private final List<BattleKlineData> mDataList;
        private WeakReference<BattleKlineChart> mRef;
        private int[] movingAverages;
        private int mIndex;

        public DataShowTask(BattleKlineChart battleKline, List<BattleKlineData> dataList, int index) {
            mRef = new WeakReference<>(battleKline);
            mDataList = dataList;
            movingAverages = battleKline.getMovingAverages();
            mIndex = index;
        }

        public DataShowTask(BattleKlineChart battleKline, List<BattleKlineData> dataList) {
            mRef = new WeakReference<>(battleKline);
            mDataList = dataList;
            movingAverages = battleKline.getMovingAverages();
            mIndex = -1;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (mIndex >= 0) {
                KlineUtils.calculateMovingAverages(mIndex, mDataList, movingAverages);
            } else {
                KlineUtils.calculateMovingAverages(mDataList, movingAverages);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            BattleKlineChart kline = mRef.get();
            if (kline != null) {
                int start = 0;
                for (int mv : movingAverages) {
                    start = Math.max(start, mv);
                }
                start = start - 1;
                kline.clearData();
                kline.setDataList(new ArrayList<KlineViewData>(mDataList.subList(start, mDataList.size())));
            }
        }
    }

    static class KlineUtils {

        public static void calculateMovingAverages(List<BattleKlineData> dataList, int[] movingAverages) {
            if (dataList != null && dataList.size() > 0) {
                for (int movingAverage : movingAverages) {
                    for (int i = 0; i < dataList.size(); i++) {
                        int start = i - movingAverage + 1;
                        if (start < 0) continue;
                        float movingAverageValue = calculateMovingAverageValue(start, movingAverage, dataList);
                        dataList.get(i).addMovingAverage(movingAverage, movingAverageValue);
                    }
                }
            }
        }

        public static void calculateMovingAverages(int index, List<BattleKlineData> dataList, int[] movingAverages) {
            if (dataList != null && index < dataList.size()) {
                for (int movingAverage : movingAverages) {
                    int start = index - movingAverage + 1;
                    if (start < 0) continue;
                    float movingAverageValue = calculateMovingAverageValue(start, movingAverage, dataList);
                    dataList.get(index).addMovingAverage(movingAverage, movingAverageValue);
                }
            }
        }

        private static float calculateMovingAverageValue(int start, int movingAverage,
                                                         List<BattleKlineData> dataList) {
            float result = 0;
            for (int i = start; i < start + movingAverage; i++) {
                result += dataList.get(i).getClosePrice();
            }
            return result / movingAverage;
        }
    }
}
