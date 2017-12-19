package com.sbai.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.sbai.chart.domain.KlineViewData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class KlineChart extends ChartView {

    private static final int CANDLES_WIDTH_DP = 6; //dp

    private static final String MA_BLUE = "#6a96ef";
    private static final String MA_PURPLE = "#dc6aef";
    private static final String MA_YELLOW = "#efc86a";

    public static final String DATE_FORMAT_DAY_MIN = "HH:mm";

    private List<KlineViewData> mDataList;
    private SparseArray<KlineViewData> mVisibleList;
    private int mFirstVisibleIndex;
    private int mLastVisibleIndex;

    private Settings mSettings;
    private SimpleDateFormat mDateFormat;
    private boolean mIsDayLine;
    private Date mDate;
    private int[] mMovingAverages;
    private OnTouchLinesAppearListener mOnTouchLinesAppearListener;
    private KlineView.OnReachBorderListener mOnReachBorderListener;
    private KlineView.OnReachBorderListener mReachBorderListener;

    // visible points index range
    private int mStart;
    private int mEnd;
    private int mLength;

    private float mCandleWidth;
    private boolean mInitData;

    public KlineChart(Context context) {
        super(context);
        init();
    }

    public KlineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected boolean enableDragChart() {
        return true;
    }

    @Override
    protected boolean enableDrawTouchLines() {
        return true;
    }

    @Override
    protected boolean enableCalculateMovingAverages() {
        return true;
    }

    @Override
    protected boolean enableDrawMovingAverages() {
        return true;
    }

    public int getFirstVisibleIndex() {
        return mFirstVisibleIndex;
    }

    public int getLastVisibleIndex() {
        return mLastVisibleIndex;
    }

    private void init() {
        mVisibleList = new SparseArray<>();
        mDateFormat = new SimpleDateFormat(DATE_FORMAT_DAY_MIN);
        mDate = new Date();
        mCandleWidth = dp2Px(CANDLES_WIDTH_DP);
        mMovingAverages = new int[]{5, 10, 20};

        mFirstVisibleIndex = Integer.MAX_VALUE;
        mLastVisibleIndex = Integer.MIN_VALUE;
    }

    protected int[] getMovingAverages() {
        return mMovingAverages;
    }

    protected int getStart() {
        return mStart;
    }

    protected int getEnd() {
        return mEnd;
    }

    protected List<KlineViewData> getDataList() {
        return mDataList;
    }

    public void addData(KlineViewData data) {
        mDataList.add(data);
        redraw();
    }

    public void setDataList(List<KlineViewData> dataList) {
        mDataList = dataList;
        mInitData = true;
        redraw();
    }

    public void addHistoryData(List<KlineViewData> dataList) {
        mDataList.addAll(0, dataList);
        redraw();
    }

    public void setVisibleList(SparseArray<KlineViewData> visibleList) {
        mVisibleList = visibleList;
    }

    public SparseArray<KlineViewData> getVisibleList() {
        return mVisibleList;
    }

    public void setDayLine(boolean dayLine) {
        mIsDayLine = dayLine;
    }

    public void setOnTouchLinesAppearListener(OnTouchLinesAppearListener onTouchLinesAppearListener) {
        mOnTouchLinesAppearListener = onTouchLinesAppearListener;
    }

    public void setOnReachBorderListener(KlineView.OnReachBorderListener onReachBorderListener) {
        mOnReachBorderListener = onReachBorderListener;
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

    protected void setTouchLineTextPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.WHITE.get()));
        paint.setTextSize(mBigFontSize);
        paint.setPathEffect(null);
    }

    protected void setMovingAveragesPaint(Paint paint, int movingAverage) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setPathEffect(null);
        if (movingAverage == mMovingAverages[0]) {
            paint.setColor(Color.parseColor(MA_BLUE));
        } else if (movingAverage == mMovingAverages[1]) {
            paint.setColor(Color.parseColor(MA_PURPLE));
        } else if (movingAverage == mMovingAverages[2]) {
            paint.setColor(Color.parseColor(MA_YELLOW));
        }
    }

    private void setMovingAveragesTextPaint(Paint paint, int movingAverage) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        paint.setPathEffect(null);
        paint.setTextSize(mMaTitleSize);
        if (movingAverage == mMovingAverages[0]) {
            paint.setColor(Color.parseColor(MA_BLUE));
        } else if (movingAverage == mMovingAverages[1]) {
            paint.setColor(Color.parseColor(MA_PURPLE));
        } else if (movingAverage == mMovingAverages[2]) {
            paint.setColor(Color.parseColor(MA_YELLOW));
        }
    }

    protected void setTouchLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.BLACK.get()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(null);
        applyColorConfiguration(paint, ColorCfg.TOUCH_LINE);
    }

    protected void setTouchLineTextBgPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.RED.get()));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
        applyColorConfiguration(paint, ColorCfg.TOUCH_LINE_TXT_BG);
    }

    private void calculateStartAndEndPosition() {
        if (mDataList != null) {
            mStart = mDataList.size() - mSettings.getXAxis() < 0
                    ? 0 : (mDataList.size() - mSettings.getXAxis() - getStartPointOffset());
            mLength = Math.min(mDataList.size(), mSettings.getXAxis());
            mEnd = mStart + mLength;
        }
    }

    @Override
    protected void calculateMovingAverages(boolean indexesEnable) {
        if (mDataList != null && mDataList.size() > 0) {
            for (int movingAverage : mMovingAverages) {
                for (int i = mStart; i < mEnd; i++) {
                    int start = i - movingAverage + 1;
                    if (start < 0) continue;
                    float movingAverageValue = calculateMovingAverageValue(start, movingAverage);
                    mDataList.get(i).addMovingAverage(movingAverage, movingAverageValue);
                }
            }
        }
    }

    @Override
    protected float calculateMaxTransactionX() {
        if (mDataList != null) {
            return Math.max((mDataList.size() - mSettings.getXAxis()) * mOneXAxisWidth, 0);
        }
        return super.calculateMaxTransactionX();
    }

    @Override
    protected float calculateOneXAxisWidth() {
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mPriceAreaWidth;
        return width / mSettings.getXAxis();
    }

    @Override
    protected void calculateBaseLines(float[] baselines) {
        if (mDataList != null && mDataList.size() > 0) {
            float max = Float.MIN_VALUE;
            float min = Float.MAX_VALUE;
            for (int i = mStart; i < mEnd; i++) {
                KlineViewData data = mDataList.get(i);
                max = Math.max(max, data.getMaxPrice());
                min = Math.min(min, data.getMinPrice());

                for (int movingAverage : mMovingAverages) {
                    Float movingAverageValue = mDataList.get(i).getMovingAverage(movingAverage);
                    if (movingAverageValue != null) {
                        max = Math.max(max, movingAverageValue.floatValue());
                        min = Math.min(min, movingAverageValue.floatValue());
                    }
                }
            }

            float priceRange = BigDecimal.valueOf(max).subtract(new BigDecimal(min))
                    .divide(new BigDecimal(baselines.length - 1),
                            mSettings.getNumberScale() + 1, RoundingMode.HALF_EVEN).floatValue();

            // 额外扩大最大值 最小值
            max += priceRange;
            //min -= priceRange;
            priceRange = BigDecimal.valueOf(max).subtract(new BigDecimal(min))
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
    protected void calculateIndexesBaseLines(long[] indexesBaseLines) {
        if (mDataList != null && mDataList.size() > 0) {
            long maxVolume = mDataList.get(mStart).getNowVolume();
            for (int i = mStart; i < mEnd; i++) {
                if (maxVolume < mDataList.get(i).getNowVolume()) {
                    maxVolume = mDataList.get(i).getNowVolume();
                }
            }
            long volumeRange = maxVolume / (indexesBaseLines.length - 1);
            indexesBaseLines[0] = maxVolume;
            indexesBaseLines[indexesBaseLines.length - 1] = 0;
            for (int i = indexesBaseLines.length - 2; i > 0; i--) {
                indexesBaseLines[i] = indexesBaseLines[i + 1] + volumeRange;
            }
        }
    }

    private float calculateMovingAverageValue(int start, int movingAverage) {
        float result = 0;
        for (int i = start; i < start + movingAverage; i++) {
            result += mDataList.get(i).getClosePrice();
        }
        return result / movingAverage;
    }

    @Override
    protected void drawTitleAboveBaselines(float[] baselines, int left, int top, int width, int height,
                                           long[] indexesBaseLines, int left2, int top2, int width2, int height2,
                                           int touchIndex, Canvas canvas) {
        float textX = left + mTextMargin;
        float verticalInterval = mTextMargin * 2;
        if (mVisibleList != null && mVisibleList.size() > 0) {
            KlineViewData data = mVisibleList.get(mVisibleList.size() - 1);
            if (hasThisTouchIndex(touchIndex)) {
                data = mVisibleList.get(touchIndex);
            }
            for (int movingAverage : mMovingAverages) {
                setMovingAveragesTextPaint(sPaint, movingAverage);
                Float movingAverageValue = data.getMovingAverage(movingAverage);
                String maText = "MA" + movingAverage + ":--";
                if (movingAverageValue != null) {
                    maText = "MA" + movingAverage + ":" + formatNumber(movingAverageValue);
                }
                float textWidth = sPaint.measureText(maText);
                canvas.drawText(maText, textX, top + verticalInterval + mOffset4CenterMaTitle, sPaint);
                textX += textWidth + mTextMargin * 5;
            }
        } else {
            for (int movingAverage : mMovingAverages) {
                setMovingAveragesTextPaint(sPaint, movingAverage);
                String maText = "MA" + movingAverage + ":--";
                float textWidth = sPaint.measureText(maText);
                canvas.drawText(maText, textX, top + verticalInterval + mOffset4CenterMaTitle, sPaint);
                textX += textWidth + mTextMargin * 5;
            }
        }
    }

    public void setPriceAreaWidth(float priceAreaWidth) {
        mPriceAreaWidth = priceAreaWidth;
    }

    public float getPriceAreaWidth() {
        return mPriceAreaWidth;
    }

    @Override
    protected void drawBaseLines(boolean indexesEnable, float[] baselines, int left, int top, int width, int height,
                                 long[] indexesBaseLines, int left2, int top2, int width2, int height2, Canvas canvas) {
        if (baselines == null || baselines.length < 2) return;

        float verticalInterval = height * 1.0f / (baselines.length - 1);
        mPriceAreaWidth = calculatePriceWidth(baselines[0]);
        float topY = top;
        for (int i = 0; i < baselines.length; i++) {
            Path path = getPath();
            path.moveTo(left, topY);
            path.lineTo(left + width, topY);
            setBaseLinePaint(sPaint);
            canvas.drawPath(path, sPaint);

            if (i != 0) {
                setDefaultTextPaint(sPaint);
                String baseLineValue = formatNumber(baselines[i]);
                float textWidth = sPaint.measureText(baseLineValue);
                float x = left + width - mPriceAreaWidth + (mPriceAreaWidth - textWidth) / 2;
                float y = topY - mTextMargin - mFontHeight / 2 + mOffset4CenterText;
                canvas.drawText(baseLineValue, x, y, sPaint);
            }

            topY += verticalInterval;
        }

        if (indexesEnable && indexesBaseLines.length >= 2) {
//            topY = top2;
//            verticalInterval = height2 * 1.0f / (indexesBaseLines.length - 1);
//            for (int i = 0; i < indexesBaseLines.length; i++) {
//                setBaseLinePaint(sPaint);
//                canvas.drawLine(left2, topY, left2 + width2, topY, sPaint);
//
//                setDefaultTextPaint(sPaint);
//                String baseLineValue = formatIndexesNumber(indexesBaseLines[i]);
//                float textWidth = sPaint.measureText(baseLineValue);
//                float x = left + width - mPriceAreaWidth + (mPriceAreaWidth - textWidth) / 2;
//                float y = topY - mTextMargin - mFontHeight / 2 + mOffset4CenterText;
//                canvas.drawText(baseLineValue, x, y, sPaint);
//
//                topY += verticalInterval;
//            }
        }
    }

    protected String formatIndexesNumber(long value) {
        if (mSettings.getIndexesType() == Settings.INDEXES_VOL) {
            formatNumber(value, 0);
        }
        return value + "";
    }

    @Override
    protected void drawRealTimeData(boolean indexesEnable, int left, int top, int width, int height,
                                    int left2, int top2, int width2, int height2, Canvas canvas) {
        if (mDataList != null && mDataList.size() > 0) {
            for (int i = mStart; i < mEnd; i++) {
                KlineViewData data = mDataList.get(i);
                float chartX = getChartXOfScreen(i, data);
                drawCandle(chartX, data, canvas);
                if (indexesEnable) {
                    drawIndexes(chartX, data, canvas);
                }
            }
        }
    }

    @Override
    protected void drawMovingAverageLines(boolean indexesEnable, int left, int top, int width, int topPartHeight,
                                          int left1, int top2, int width1, int bottomPartHeight,
                                          Canvas canvas) {
        for (int movingAverage : mMovingAverages) {
            setMovingAveragesPaint(sPaint, movingAverage);
            float startX = -1;
            float startY = -1;
            for (int i = mStart; i < mEnd; i++) {
                Float movingAverageValue = mDataList.get(i).getMovingAverage(movingAverage);
                if (movingAverageValue == null) continue;
                float chartX = getChartXOfScreen(i);
                float chartY = getChartY(movingAverageValue.floatValue());
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

    private void drawCandle(float chartX, KlineViewData data, Canvas canvas) {
        // default line is positive line
        ChartColor color = ChartColor.RED;
        float topPrice = data.getClosePrice();
        float bottomPrice = data.getOpenPrice();
        if (data.getClosePrice() < data.getOpenPrice()) { // negative line
            color = ChartColor.GREEN;
            topPrice = data.getOpenPrice();
            bottomPrice = data.getClosePrice();
        }
        drawTopCandleLine(data.getMaxPrice(), topPrice, color.get(), chartX, canvas);
        drawCandleBody(topPrice, bottomPrice, color.get(), chartX, canvas);
        drawBottomCandleLine(data.getMinPrice(), bottomPrice, color.get(), chartX, canvas);
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


    private void drawIndexes(float chartX, KlineViewData data, Canvas canvas) {
        if (mSettings.getIndexesType() == Settings.INDEXES_VOL) {
            drawVolumes(chartX, data, canvas);
        }
    }

    private void drawVolumes(float chartX, KlineViewData data, Canvas canvas) {
        ChartColor color = ChartColor.RED;
        if (data.getClosePrice() < data.getOpenPrice()) {
            color = ChartColor.GREEN;
        }
        setCandleBodyPaint(sPaint, color.get());
        RectF rectf = getRectF();
        rectf.left = chartX - mCandleWidth / 2;
        rectf.top = getIndexesChartY(data.getNowVolume());
        rectf.right = chartX + mCandleWidth / 2;
        rectf.bottom = getIndexesChartY(0);
        canvas.drawRect(rectf, sPaint);
    }


    @Override
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {
        float textY = top + mTextMargin * 2.5f + mFontHeight / 2 + mOffset4CenterText;
        for (int i = mStart; i < mEnd; i += 8) {
            KlineViewData data = mDataList.get(i);
            setDefaultTextPaint(sPaint);
            if (i == mStart) {
                String timeStr = data.getDay();
                canvas.drawText(timeStr, left - mTextMargin * 2, textY, sPaint);
            } else {
                String displayTime = formatTimestamp(data);
                float textWidth = sPaint.measureText(displayTime);
                float textX = getChartXOfScreen(i) - textWidth / 2;
                canvas.drawText(displayTime, textX, textY, sPaint);
            }
        }
    }

    protected float getChartXOfScreen(int index) {
        index = index - mStart; // visible index 0 ~ 39
        return getChartX(index);
    }

    protected float getChartXOfScreen(int index, KlineViewData data) {
        index = index - mStart; // visible index 0 ~ 39
        updateFirstLastVisibleIndex(index);
        mVisibleList.put(index, data);
        return getChartX(index);
    }

    private void updateFirstLastVisibleIndex(int indexOfXAxis) {
        mFirstVisibleIndex = Math.min(indexOfXAxis, mFirstVisibleIndex);
        mLastVisibleIndex = Math.max(indexOfXAxis, mLastVisibleIndex);
    }

    @Override
    protected float getChartX(int index) {
        float offset = mCandleWidth / 2;
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mPriceAreaWidth;
        float chartX = getPaddingLeft() + index * width * 1.0f / mSettings.getXAxis();
        return chartX + offset;
    }

    @Override
    protected int getIndexOfXAxis(float chartX) {
        float offset = mCandleWidth / 2;
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mPriceAreaWidth;
        chartX = chartX - offset - getPaddingLeft();
        return (int) (chartX * mSettings.getXAxis() / width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calculateStartAndEndPosition();
        super.onDraw(canvas);
        if (mOnReachBorderListener != null && mDataList != null &&
                mDataList.size() > mSettings.getXAxis() && mStart == 0) {
            mOnReachBorderListener.onReachLeftBorder(mDataList.get(mStart), mDataList);
        }
        if (mOnReachBorderListener != null && mDataList != null &&
                mDataList.size() > mSettings.getXAxis() && mEnd == mDataList.size()) {
            if (mInitData) {
                mInitData = false;
                return;
            }
            mOnReachBorderListener.onReachRightBorder(mDataList.get(mEnd - 1), mDataList);
        }
    }

    private String formatTimestamp(KlineViewData data) {
        if (mIsDayLine) {
            String timeStr = data.getDay();
            timeStr = timeStr.substring(timeStr.indexOf("-") + 1);
            return timeStr.replaceAll("-", "/");
        } else {
            mDate.setTime(data.getTimeStamp());
            return mDateFormat.format(mDate);
        }
    }

    public boolean isLastDataVisible() {
        return getStartPointOffset() == 0;
    }

    public void clearData() {
        mStart = 0;
        mEnd = 0;
        mLength = 0;
        mVisibleList.clear();

        mFirstVisibleIndex = Integer.MAX_VALUE;
        mLastVisibleIndex = Integer.MIN_VALUE;

        resetChart();
        setDataList(null);
    }

    @Override
    protected void onTouchLinesAppear(int touchIndex) {
        if (mOnTouchLinesAppearListener != null) {
            KlineViewData curData = mVisibleList.get(touchIndex);
            KlineViewData preData = null;
            int previousIndex = touchIndex - 1;
            if (previousIndex >= 0) {
                preData = mVisibleList.get(previousIndex);
            }
            mOnTouchLinesAppearListener.onAppear(curData, preData, touchIndex < mSettings.getXAxis() / 2);
        }
    }

    @Override
    protected void onTouchLinesDisappear() {
        if (mOnTouchLinesAppearListener != null) {
            mOnTouchLinesAppearListener.onDisappear();
        }
    }

    @Override
    protected void drawTouchLines(boolean indexesEnable, int touchIndex,
                                  int left, int top, int width, int height,
                                  int left2, int top2, int width2, int height2,
                                  Canvas canvas) {
        if (hasThisTouchIndex(touchIndex)) {
            KlineViewData data = mVisibleList.get(touchIndex);
            float touchX = getChartX(touchIndex);
            float touchY = getChartY(data.getClosePrice());

            // draw cross line: vertical line and horizontal line
            setTouchLinePaint(sPaint);
            canvas.drawLine(touchX, top, touchX, top + height, sPaint);
            canvas.drawLine(left, touchY, left + width, touchY, sPaint);

            if (indexesEnable) {
                float touchY2 = getIndexesChartY(data.getNowVolume());

                // draw volume connect to horizontal line
                String volume = String.valueOf(data.getNowVolume());
                setTouchLineTextPaint(sPaint);
                float volumeWidth = sPaint.measureText(volume);
                RectF redRect = getBigFontBgRectF(0, 0, volumeWidth);
                float rectHeight = redRect.height();
                float rectWidth = redRect.width();
                float volumeMargin = (rectWidth - volumeWidth) / 2;
                float volumeX = left2 + width2 - volumeMargin - volumeWidth;
                redRect.top = touchY2 - rectHeight;
                redRect.left = left2 + width2 - rectWidth;
                if (redRect.top < top2) {
                    redRect.top = top2;
                }
                redRect.bottom = redRect.top + rectHeight;
                redRect.right = redRect.left + rectWidth;
                setTouchLineTextBgPaint(sPaint);
                canvas.drawRoundRect(redRect, 2, 2, sPaint);
                float volumeY = redRect.top + rectHeight / 2 + mOffset4CenterBigText;
                setTouchLineTextPaint(sPaint);
                canvas.drawText(volume, volumeX, volumeY, sPaint);

                setTouchLinePaint(sPaint);
                canvas.drawLine(touchX, top2, touchX, top2 + height2, sPaint);
                canvas.drawLine(left2, touchY2, left2 + width, touchY2, sPaint);
            }
        }
    }

    @Override
    protected int calculateTouchIndex(MotionEvent e) {
        float touchX = e.getX();
        int touchIndex = getIndexOfXAxis(touchX);
        if (getVisibleList() != null && getVisibleList().size() > 0) {
            touchIndex = Math.max(touchIndex, getFirstVisibleIndex());
            touchIndex = Math.min(touchIndex, getLastVisibleIndex());
        }
        return touchIndex;
    }

    @Override
    protected boolean hasThisTouchIndex(int touchIndex) {
        if (touchIndex != -1 && mVisibleList != null && mVisibleList.get(touchIndex) != null) {
            return true;
        }
        return super.hasThisTouchIndex(touchIndex);
    }

    @Override
    public void setSettings(ChartSettings settings) {
        mSettings = (Settings) settings;
        super.setSettings(settings);
    }

    public static class Settings extends ChartSettings {
        public static final int INDEXES_VOL = 1;

        private int indexesType;

        public Settings() {
            super();
            indexesType = INDEXES_VOL;
        }

        public int getIndexesType() {
            return indexesType;
        }

        public void setIndexesType(int indexesType) {
            this.indexesType = indexesType;
        }
    }

    public interface OnTouchLinesAppearListener {
        /**
         * @param data         current kline data
         * @param previousData previous data of current data, n & n - 1
         * @param isLeftArea   true means left area of view
         */
        void onAppear(KlineViewData data, KlineViewData previousData, boolean isLeftArea);

        void onDisappear();
    }
}
