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
public class BattleKline extends KlineChart {

    private static final String TRANS_GREEN = "#262fcc9f";
    private static final String TRANS_RED = "#26f25b57";
    private List<BattleKlineData> mDataList;

    protected void setTranslucentBgPaint(Paint paint, String color) {
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    public BattleKline(Context context) {
        super(context);
    }

    public BattleKline(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean enableDrawTouchLines() {
        return false;
    }

    @Override
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {
    }

    public void addKlineData(BattleKlineData gameKlineData) {
        mDataList.add(gameKlineData);
        new DataShowTask(this, mDataList, mDataList.size() - 1).execute();
    }

    /**
     * 使用 39条数据初始化
     *
     * @param klineDataList
     */
    public void initKlineDataList(List<BattleKlineData> klineDataList) {
        mDataList = klineDataList;
        new DataShowTask(this, mDataList).execute();
    }

    @Override
    protected void calculateMovingAverages(boolean indexesEnable) {
    }

    @Override
    protected void drawRealTimeData(boolean indexesEnable, int left, int top, int width, int height, int left2, int top2, int width2, int height2, Canvas canvas) {
        super.drawRealTimeData(indexesEnable, left, top, width, height, left2, top2, width2, height2, canvas);
        // draw translucent background
        if (mDataList == null || mDataList.isEmpty()) return;
        /*
         * 透明背景绘制区：
         * buy 点 ~ sell 点
         * (start & HP) 点 ~ sell 点
         * (start & HP) 点 ~ (HP & end - 1) 点
         * buy 点 ~ (HP & end - 1) 点
         *
         * (HP & end - 1) 点的颜色需要遍历到 sell 点 或者最后一个 HP 点才能确定
         */
        RectF rectF = null;
        int i = getStart();
        for (; i < getEnd(); i++) {
            BattleKlineData data = mDataList.get(i);
            if (rectF != null && data.getMark().equalsIgnoreCase(BattleKlineData.MARK_SELL)) {
                rectF.right = getChartXOfScreen(i);
                String bgColor = data.getPositions() >= 0 ? TRANS_RED : TRANS_GREEN;
                setTranslucentBgPaint(sPaint, bgColor);
                canvas.drawRect(rectF, sPaint);
                rectF = null;
            } else if (rectF != null && data.getMark().equalsIgnoreCase(BattleKlineData.MARK_HOLD_PASS) && i == getEnd() - 1) {
                rectF.right = left + width;
            } else if (data.getMark().equalsIgnoreCase(BattleKlineData.MARK_HOLD_PASS) && i == getStart()) {
                rectF = getRectF();
                rectF.top = top;
                rectF.bottom = top + height;
                rectF.left = left;
            } else if (data.getMark().equalsIgnoreCase(BattleKlineData.MARK_BUY)) {
                rectF = getRectF();
                rectF.top = top;
                rectF.bottom = top + height;
                rectF.left = getChartXOfScreen(i);
            }
        } // i == end
        if (rectF != null) {
            String bgColor = TRANS_RED;
            for (; i < mDataList.size(); i++) {
                BattleKlineData data = mDataList.get(i);
                if (data.getMark().equalsIgnoreCase(BattleKlineData.MARK_SELL)) {
                    bgColor = data.getPositions() >= 0 ? TRANS_RED : TRANS_GREEN;
                    break;
                } else if (data.getMark().equalsIgnoreCase(BattleKlineData.MARK_HOLD_PASS)) {
                    bgColor = data.getPositions() >= 0 ? TRANS_RED : TRANS_GREEN;
                } else {
                    break;
                }
            }
            setTranslucentBgPaint(sPaint, bgColor);
            canvas.drawRect(rectF, sPaint);
        }
    }

    static class DataShowTask extends AsyncTask<Void, Void, Void> {

        private final List<BattleKlineData> mDataList;
        private WeakReference<BattleKline> mRef;
        private int[] movingAverages;
        private int mIndex;

        public DataShowTask(BattleKline battleKline, List<BattleKlineData> dataList, int index) {
            mRef = new WeakReference<>(battleKline);
            mDataList = dataList;
            movingAverages = battleKline.getMovingAverages();
            mIndex = index;
        }

        public DataShowTask(BattleKline battleKline, List<BattleKlineData> dataList) {
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
            BattleKline kline = mRef.get();
            if (kline != null) {
                int start = 0;
                for (int mv : movingAverages) {
                    start = Math.max(start, mv);
                }
                start = start - 1;
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
