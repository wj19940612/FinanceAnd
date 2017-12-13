package com.sbai.finance.view.klinebattle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.sbai.chart.KlineChart;
import com.sbai.chart.domain.KlineViewData;
import com.sbai.finance.model.klinebattle.BattleKlineData;

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
        addData(gameKlineData);
    }

    public void setKlineDataList(List<BattleKlineData> klineDataList) {
        mDataList = klineDataList;
        setDataList(new ArrayList<KlineViewData>(klineDataList));
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
}
