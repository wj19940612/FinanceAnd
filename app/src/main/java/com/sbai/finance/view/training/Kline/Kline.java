package com.sbai.finance.view.training.Kline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.sbai.chart.ChartSettings;
import com.sbai.chart.ChartView;
import com.sbai.chart.domain.KlineViewData;
import com.sbai.finance.model.training.question.KData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Kline extends ChartView {

    private static final int CANDLES_WIDTH_DP = 6; //dp
    private static final int BUTTONS_AREA_WIDTH = 80; //dp
    private static final int MV_WIDTH = 3; //dp

    // color
    private static final String COLOR_RED = "#802624";
    private static final String COLOR_GREEN = "#00564B";
    private static final String COLOR_YELLOW = "#FFC336";
    private static final String COLOR_VIOLET = "#694FC8";

    private List<KData> mDataList;
    private int[] mMovingAverages;

    private int mStart;
    private int mLength;
    private int mEnd;
    private float mCandleWidth;
    private float mMaxBaseLine;
    private float mMinBaseLine;

    private float mBaseLineWidth;
    private float mButtonsAreaWidth;
    private float mMvWidth;

    protected void setBaseLinePaint(Paint paint) {
        paint.setColor(Color.parseColor("#2a2a2a"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mBaseLineWidth);
    }

    private void setMovingAveragesPaint(Paint paint, int movingAverage) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mMvWidth);
        paint.setPathEffect(null);
        if (movingAverage == mMovingAverages[0]) {
            paint.setColor(Color.parseColor(COLOR_VIOLET));
        } else if (movingAverage == mMovingAverages[1]) {
            paint.setColor(Color.parseColor(COLOR_YELLOW));
        }
    }

    private void setCandleLinePaint(Paint paint, String color) {
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setPathEffect(null);
    }

    private void setCandleBodyPaint(Paint paint, String color) {
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    public Kline(Context context) {
        super(context);
        init();
    }

    public Kline(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mCandleWidth = dp2Px(CANDLES_WIDTH_DP);
        mMovingAverages = new int[]{5, 30};

        mMaxBaseLine = Float.MIN_VALUE;
        mMinBaseLine = Float.MAX_VALUE;

        mBaseLineWidth = dp2Px(0.5f);
        mButtonsAreaWidth = dp2Px(BUTTONS_AREA_WIDTH);
        mMvWidth = dp2Px(MV_WIDTH);

        ChartSettings settings = new ChartSettings();
        settings.setXAxis(60);
        settings.setBaseLines(7);
        setSettings(settings);
    }

    @Override
    protected void calculateBaseLines(float[] baselines) {
        if (mDataList != null && mDataList.size() > 0) {
            float max = mMaxBaseLine;
            float min = mMinBaseLine;
            for (int i = mStart; i < mEnd; i++) {
                KlineViewData data = mDataList.get(i).getK();
                if (max < data.getMaxPrice()) max = data.getMaxPrice();
                if (min > data.getMinPrice()) min = data.getMinPrice();
            }

            float priceRange = BigDecimal.valueOf(max).subtract(new BigDecimal(min))
                    .divide(new BigDecimal(baselines.length - 1),
                            mSettings.getNumberScale() + 1, RoundingMode.HALF_EVEN).floatValue();

            baselines[0] = max;
            baselines[baselines.length - 1] = min;
            for (int i = baselines.length - 2; i > 0; i--) {
                baselines[i] = baselines[i + 1] + priceRange;
            }
        }
    }

    @Override
    protected boolean enableMovingAverages() {
        return true;
    }

    @Override
    protected void calculateMovingAverages(boolean indexesEnable) {
        if (mDataList != null && mDataList.size() > 0) {
            float max = Float.MIN_VALUE;
            float min = Float.MAX_VALUE;
            for (int movingAverage : mMovingAverages) {
                for (int i = mStart; i < mEnd; i++) {
                    int start = i - movingAverage + 1;
                    if (start < 0) continue;
                    float movingAverageValue = calculateMovingAverageValue(start, movingAverage);
                    mDataList.get(i).getK().addMovingAverage(movingAverage, movingAverageValue);
                    if (max < movingAverageValue) max = movingAverageValue;
                    if (min > movingAverageValue) min = movingAverageValue;
                }
            }
            mMaxBaseLine = max;
            mMinBaseLine = min;
        }
    }

    private float calculateMovingAverageValue(int start, int movingAverage) {
        float result = 0;
        for (int i = start; i < start + movingAverage; i++) {
            result += mDataList.get(i).getK().getClosePrice();
        }
        return result / movingAverage;
    }

    @Override
    protected void calculateIndexesBaseLines(long[] indexesBaseLines) {

    }

    @Override
    protected float getChartX(int index) {
        index = index - mStart;
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mButtonsAreaWidth;
        float offset = width * 1.0f / mSettings.getXAxis() / 2;
        float chartX = getPaddingLeft() + index * width * 1.0f / mSettings.getXAxis();
        return chartX + offset;
    }

    @Override
    protected void drawBaseLines(boolean indexesEnable,
                                 float[] baselines, int left, int top, int width, int height,
                                 long[] indexesBaseLines, int left2, int top2, int width2, int height2,
                                 Canvas canvas) {
        if (baselines == null || baselines.length < 2) return;

        float verticalInterval = height * 1.0f / (baselines.length - 1);
        float topY = top;
        for (int i = 0; i < baselines.length; i++) {
            Path path = getPath();
            path.moveTo(left, topY);
            path.lineTo(left + width, topY);
            setBaseLinePaint(sPaint);
            canvas.drawPath(path, sPaint);
            topY += verticalInterval;
        }
    }

    @Override
    protected void drawRealTimeData(boolean indexesEnable,
                                    int left, int top, int width, int height,
                                    int left2, int top2, int width2, int height2,
                                    Canvas canvas) {
        if (mDataList != null && mDataList.size() > 0) {
            for (int i = mStart; i < mEnd; i++) {
                KData data = mDataList.get(i);
                float chartX = getChartX(i);
                drawCandle(chartX, data, canvas);
            }
            drawMovingAverageLines(canvas);
        }
    }

    private void drawMovingAverageLines(Canvas canvas) {
        for (int movingAverage : mMovingAverages) {
            setMovingAveragesPaint(sPaint, movingAverage);
            float startX = -1;
            float startY = -1;
            for (int i = mStart; i < mEnd; i++) {
                int start = i - movingAverage + 1;
                if (start < 0) continue;
                float chartX = getChartX(i);
                float movingAverageValue = mDataList.get(i).getK().getMovingAverage(movingAverage);
                float chartY = getChartY(movingAverageValue);
                if (startX == -1 && startY == -1) { // start
                    startX = chartX;
                    startY = chartY;
                } else {
                    canvas.drawLine(startX, startY, chartX, chartY, sPaint);
                    startX = chartX;
                    startY = chartY;
                }
            }
        }
    }

    private void drawCandle(float chartX, KData kData, Canvas canvas) {
        // default line is positive line
        String color = COLOR_RED;
        KlineViewData data = kData.getK();
        float topPrice = data.getClosePrice();
        float bottomPrice = data.getOpenPrice();
        if (data.getClosePrice() < data.getOpenPrice()) { // negative line
            color = COLOR_GREEN;
            topPrice = data.getOpenPrice();
            bottomPrice = data.getClosePrice();
        }
        if (kData.isOption()) {
            color = "#ffffff"; // TODO: 18/08/2017 remove
        }
        drawTopCandleLine(data.getMaxPrice(), topPrice, color, chartX, canvas);
        drawCandleBody(topPrice, bottomPrice, color, chartX, canvas);
        drawBottomCandleLine(data.getMinPrice(), bottomPrice, color, chartX, canvas);
    }

    private void drawTopCandleLine(Float maxPrice, float topPrice, String color, float chartX, Canvas canvas) {
        setCandleLinePaint(sPaint, color);
        canvas.drawLine(chartX, getChartY(maxPrice), chartX, getChartY(topPrice), sPaint);
    }

    private void drawCandleBody(float topPrice, float bottomPrice, String color, float chartX, Canvas canvas) {
        if (topPrice == bottomPrice) {
            setCandleLinePaint(sPaint, color);
            canvas.drawLine(chartX - mCandleWidth / 2, getChartY(topPrice),
                    chartX + mCandleWidth / 2, getChartY(bottomPrice),
                    sPaint);
        } else {
            setCandleBodyPaint(sPaint, color);
            RectF rectf = getRectF();
            rectf.left = chartX - mCandleWidth / 2;
            rectf.top = getChartY(topPrice);
            rectf.right = chartX + mCandleWidth / 2;
            rectf.bottom = getChartY(bottomPrice);
            canvas.drawRect(rectf, sPaint);
        }
    }

    private void drawBottomCandleLine(Float minPrice, float bottomPrice, String color, float chartX, Canvas canvas) {
        setCandleLinePaint(sPaint, color);
        canvas.drawLine(chartX, getChartY(bottomPrice),
                chartX, getChartY(minPrice),
                sPaint);
    }

    @Override
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {
    }

    private void calculateStartAndEndPosition() {
        if (mDataList != null) {
            mStart = mDataList.size() - mSettings.getXAxis() < 0
                    ? 0 : (mDataList.size() - mSettings.getXAxis() - getStartPointOffset());
            mLength = Math.min(mDataList.size(), mSettings.getXAxis());
            mEnd = mStart + mLength;
        }
    }

    public void setDataList(List<KData> dataList) {
        mDataList = dataList;
        redraw();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calculateStartAndEndPosition();
        super.onDraw(canvas);
    }
}
