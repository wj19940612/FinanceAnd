package com.sbai.finance.view.stock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.sbai.chart.ChartSettings;
import com.sbai.chart.ChartView;
import com.sbai.finance.model.stock.StockTrendData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StockTrendChart extends ChartView {

    private static final float VOLUME_WIDTH_DP = 1.0f; //dp

    private List<StockTrendData> mDataList;
    private StockTrendData mUnstableData;
    private SparseArray<StockTrendData> mVisibleList;
    private int mFirstVisibleIndex;
    private int mLastVisibleIndex;

    private String[] mTimeLine;
    private float mVolumeWidth;
    private StockTrendView.Settings mSettings;

    public StockTrendChart(Context context) {
        super(context);
        init();
    }

    public StockTrendChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mTimeLine = new String[]{"09:30", "11:30", "13:00", "15:00"};
        mVolumeWidth = dp2Px(VOLUME_WIDTH_DP);

        mVisibleList = new SparseArray<>();
        mFirstVisibleIndex = Integer.MAX_VALUE;
        mLastVisibleIndex = Integer.MIN_VALUE;
    }

    protected void setRealTimeLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.BLACK.get()));
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(null);
    }

    private void setCandleBodyPaint(Paint paint, String color) {
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    protected void setDashLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.BASE.get()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 3}, 1));
    }

    protected void setTouchLineTextPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.WHITE.get()));
        paint.setTextSize(mBigFontSize);
        paint.setPathEffect(null);
    }

    protected void setRedRectBgPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.RED.get()));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    protected void setRedTouchLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.RED.get()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(null);
    }

    protected void setUnstablePricePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.WHITE.get()));
        paint.setTextSize(mBigFontSize);
        paint.setPathEffect(null);
    }

    protected void setUnstablePriceBgPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.BLACK.get()));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    public List<StockTrendData> getDataList() {
        return mDataList;
    }

    public void setDataList(List<StockTrendData> dataList) {
        mDataList = dataList;
        redraw();
    }

    public void setUnstableData(StockTrendData unstableData) {
        if (mUnstableData != null && unstableData != null
                && mUnstableData.isSameData(unstableData)) {
            return;
        }

        mUnstableData = unstableData;

        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup.getVisibility() != VISIBLE || getVisibility() != VISIBLE) {
            return;
        }

        if (enableDrawUnstableData()) {
            redraw();
        } else if (mSettings != null && mUnstableData != null) { // When unstable data > top || < bottom, still redraw
            float[] baseLines = mSettings.getBaseLines();
            if (mUnstableData.getClose_Price() > baseLines[0]
                    || mUnstableData.getClose_Price() < baseLines[baseLines.length - 1]) {
                redraw();
            }
        }
    }

    @Override
    public void setSettings(ChartSettings settings) {
        mSettings = (StockTrendView.Settings) settings;
        super.setSettings(settings);
        redraw();
    }

    public void setVisibleList(SparseArray<StockTrendData> visibleList) {
        mVisibleList = visibleList;
    }

    public SparseArray<StockTrendData> getVisibleList() {
        return mVisibleList;
    }

    public void setPriceAreaWidth(float priceAreaWidth) {
        mPriceAreaWidth = priceAreaWidth;
    }

    public float getPriceAreaWidth() {
        return mPriceAreaWidth;
    }

    public int getFirstVisibleIndex() {
        return mFirstVisibleIndex;
    }

    public int getLastVisibleIndex() {
        return mLastVisibleIndex;
    }

    @Override
    protected void calculateBaseLines(float[] baselines) {
        float preClosePrice = mSettings.getPreClosePrice();
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;

        if (mDataList != null) {
            for (StockTrendData stockTrendData : mDataList) {
                if (max < stockTrendData.getClose_Price()) {
                    max = stockTrendData.getClose_Price();
                }
                if (min > stockTrendData.getClose_Price()) {
                    min = stockTrendData.getClose_Price();
                }
            }
        }

        if (mUnstableData != null) {
            if (max < mUnstableData.getClose_Price()) {
                max = mUnstableData.getClose_Price();
            }
            if (min > mUnstableData.getClose_Price()) {
                min = mUnstableData.getClose_Price();
            }
        }

        if (max == Float.MIN_VALUE || min == Float.MAX_VALUE) {
            max = preClosePrice * (1 + 0.1f);
            min = preClosePrice * (1 - 0.1f);
        } else {
            if (preClosePrice != 0) {
                if (Math.abs(preClosePrice - max) > Math.abs(preClosePrice - min)) {
                    min = preClosePrice - Math.abs(preClosePrice - max);
                } else {
                    max = preClosePrice + Math.abs(preClosePrice - min);
                }
            }
        }

        float priceRange =
                BigDecimal.valueOf(max).subtract(new BigDecimal(min))
                        .divide(new BigDecimal(baselines.length - 1), mSettings.getNumberScale() + 2, RoundingMode.HALF_EVEN)
                        .floatValue();

        baselines[0] = max;
        baselines[baselines.length - 1] = min;
        for (int i = baselines.length - 2; i > 0; i--) {
            baselines[i] = baselines[i + 1] + priceRange;
        }
    }

    @Override
    protected void calculateIndexesBaseLines(long[] indexesBaseLines) {
        if (mDataList != null && mDataList.size() > 0) {
            mDataList.get(0).setBusinessVolume(mDataList.get(0).getNow_Volume());
            long maxVolume = mDataList.get(0).getBusinessVolume();
            for (int i = 1; i < mDataList.size(); i++) {
                long volume = mDataList.get(i).getNow_Volume() - mDataList.get(i - 1).getNow_Volume();
                mDataList.get(i).setBusinessVolume(volume);
                if (maxVolume < mDataList.get(i).getBusinessVolume()) {
                    maxVolume = mDataList.get(i).getBusinessVolume();
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

    @Override
    protected void drawBaseLines(boolean indexesEnable,
                                 float[] baselines, int left, int top, int width, int height,
                                 long[] indexesBaseLines, int left2, int top2, int width2, int height2,
                                 Canvas canvas) {
        if (baselines == null || baselines.length < 2) return;

        float verticalInterval = height * 1.0f / (baselines.length - 1);
        mPriceAreaWidth = calculatePriceWidth(baselines[0]);
        float topY = top;
        for (int i = 0; i < baselines.length; i++) {
            Path path = getPath();
            path.moveTo(left, topY);
            path.lineTo(left + (i == 0 ? width + getPaddingRight() : width), topY);
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

//        float preClosePrice = mSettings.getPreClosePrice();
//        if (preClosePrice < baselines[0] && preClosePrice > baselines[baselines.length - 1]) {
//            setDashLinePaint(sPaint);
//            topY = getChartY(preClosePrice);
//            Path path = getPath();
//            path.moveTo(left, topY);
//            path.lineTo(left + width, topY);
//            canvas.drawPath(path, sPaint);
//
//            setDefaultTextPaint(sPaint);
//            String preClosePriceStr = formatNumber(preClosePrice);
//            float x = left + mTextMargin;
//            float y = topY - mTextMargin - mFontHeight / 2 + mOffset4CenterText;
//            if (topY - mTextMargin - mFontHeight <= top) { // preClosePriceText beyond top
//                y = topY + mTextMargin + mFontHeight / 2 + mOffset4CenterText;
//            }
//            canvas.drawText(preClosePriceStr, x, y, sPaint);
//        }
    }


    @Override
    protected void drawRealTimeData(boolean indexesEnable,
                                    int left, int top, int width, int height,
                                    int left2, int top2, int width2, int height2, Canvas canvas) {
        float firstChartX = 0;
        if (mDataList != null && mDataList.size() > 0) {
            int size = mDataList.size();
            Path path = getPath();
            float chartX = 0;
            float chartY = 0;
            for (int i = 0; i < size; i++) {
                chartX = getChartX(i, mDataList.get(i));
                chartY = getChartY(mDataList.get(i).getClose_Price());
                if (path.isEmpty()) {
                    firstChartX = chartX;
                    path.moveTo(chartX, chartY);
                } else {
                    path.lineTo(chartX, chartY);
                }
            }

//            if (mUnstableData != null && mDataList.size() > 0) {
//                chartX = getChartX(mUnstableData);
//                chartY = getChartY(mUnstableData.getLastPrice());
//                path.lineTo(chartX, chartY);
//            }

            setRealTimeLinePaint(sPaint);
            canvas.drawPath(path, sPaint);

            // fill area
//            path.lineTo(chartX, top + height);
//            path.lineTo(firstChartX, top + height);
//            path.close();
//            setRealTimeFillPaint(sPaint);
//            canvas.drawPath(path, sPaint);
//            sPaint.setShader(null);

            if (indexesEnable) {
                for (int i = 0; i < size; i++) {
                    ChartColor color = ChartColor.RED;
                    chartX = getChartX(i);
                    if (i > 0 && mDataList.get(i).getClose_Price() < mDataList.get(i - 1).getClose_Price()) {
                        color = ChartColor.GREEN;
                    }
                    setCandleBodyPaint(sPaint, color.get());
                    RectF rectf = getRectF();
                    rectf.left = chartX - mVolumeWidth / 2;
                    rectf.top = getIndexesChartY(mDataList.get(i).getBusinessVolume());
                    rectf.right = chartX + mVolumeWidth / 2;
                    rectf.bottom = getIndexesChartY(0);
                    canvas.drawRect(rectf, sPaint);
                }
            }
        }
    }

    @Override
    protected void drawUnstableData(boolean indexesEnable,
                                    int left, int top, int width, int topPartHeight,
                                    int left2, int top2, int width1, int bottomPartHeight,
                                    Canvas canvas) {
        if (mUnstableData != null) {
            // last point connect to unstable point
            Path path = getPath();
            int unstableIndex = mDataList != null ? mDataList.size() : 0;
            float chartX = getChartX(unstableIndex, mUnstableData);
            float chartY = getChartY(mUnstableData.getClose_Price());

            if (mDataList != null && mDataList.size() > 0) {
                StockTrendData lastData = mDataList.get(mDataList.size() - 1);
                path.moveTo(getChartX(mDataList.size() - 1, lastData), getChartY(lastData.getClose_Price()));
                path.lineTo(chartX, chartY);
                setRealTimeLinePaint(sPaint);
                canvas.drawPath(path, sPaint);
            }

            // dash line
            path = getPath();
            path.moveTo(chartX, chartY);
            path.lineTo(left + width - mPriceAreaWidth, chartY);
            setDashLinePaint(sPaint);
            canvas.drawPath(path, sPaint);

            // unstable price
            setUnstablePricePaint(sPaint);
            String unstablePrice = formatNumber(mUnstableData.getClose_Price());
            float priceWidth = sPaint.measureText(unstablePrice);
            float priceMargin = (mPriceAreaWidth - priceWidth) / 2;
            float priceX = left + width - priceMargin - priceWidth;
            RectF blueRect = getBigFontBgRectF(priceX, chartY + mOffset4CenterBigText, priceWidth);
            //// the center of rect is connected to dashLine
            //// add offset and let the bottom of rect connect to dashLine
            float rectHeight = blueRect.height();
            blueRect.top -= rectHeight / 2;
            blueRect.bottom -= rectHeight / 2;
            setUnstablePriceBgPaint(sPaint);
            canvas.drawRoundRect(blueRect, 2, 2, sPaint);
            float priceY = chartY - rectHeight / 2 + mOffset4CenterBigText;
            setUnstablePricePaint(sPaint);
            canvas.drawText(unstablePrice, priceX, priceY, sPaint);
        }
    }

    private float getChartX(int index, StockTrendData data) {
        updateFirstLastVisibleIndex(index);
        mVisibleList.put(index, data);
        return super.getChartX(index);
    }

    @Override
    protected int calculateTouchIndex(MotionEvent e) {
        float touchX = e.getX();
        return getIndexOfXAxis(touchX);
    }

    private void updateFirstLastVisibleIndex(int indexOfXAxis) {
        mFirstVisibleIndex = Math.min(indexOfXAxis, mFirstVisibleIndex);
        mLastVisibleIndex = Math.max(indexOfXAxis, mLastVisibleIndex);
    }

    @Override
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {
        setDefaultTextPaint(sPaint);
        float textWidth = sPaint.measureText(mTimeLine[0]);
        float textY = top + mTextMargin * 2.5f + mFontHeight / 2 + mOffset4CenterText;
        float textX = left - textWidth / 2;
        canvas.drawText(mTimeLine[0], textX, textY, sPaint);

        textWidth = sPaint.measureText(mTimeLine[2]);
        textX = (left + width) / 2 - textWidth / 2;
        canvas.drawText(mTimeLine[2], textX, textY, sPaint);

        textWidth = sPaint.measureText(mTimeLine[3]);
        textX = left + width - textWidth / 2;
        canvas.drawText(mTimeLine[3], textX, textY, sPaint);
    }

    @Override
    protected void drawTouchLines(boolean indexesEnable, int touchIndex,
                                  int left, int top, int width, int height,
                                  int left2, int top2, int width2, int height2,
                                  Canvas canvas) {
        if (hasThisTouchIndex(touchIndex)) {
            StockTrendData data = mVisibleList.get(touchIndex);
            float touchX = getChartX(touchIndex);
            float touchY = getChartY(data.getClose_Price());
            float touchY2 = getIndexesChartY(data.getBusinessVolume());

            // draw cross line: vertical line and horizontal line
            setRedTouchLinePaint(sPaint);
            Path path = getPath();
            path.moveTo(touchX, top);
            path.lineTo(touchX, top + height);
            canvas.drawPath(path, sPaint);
            path = getPath();
            path.moveTo(left, touchY);
            path.lineTo(left + width - mPriceAreaWidth, touchY);
            canvas.drawPath(path, sPaint);

            // draw date connect to vertical line
            String date = getDate(touchIndex);
            setTouchLineTextPaint(sPaint);
            float dateWidth = sPaint.measureText(date);
            RectF redRect = getBigFontBgRectF(0, 0, dateWidth);
            float rectHeight = redRect.height();
            float rectWidth = redRect.width();
            redRect.left = touchX - rectWidth / 2;
            redRect.top = top + height;
            if (redRect.left < left) { // rect will touch left border
                redRect.left = left;
            }
            if (redRect.left + rectWidth > left + width) { // rect will touch right border
                redRect.left = left + width - rectWidth;
            }
            redRect.right = redRect.left + rectWidth;
            redRect.bottom = redRect.top + rectHeight;
            setRedRectBgPaint(sPaint);
            canvas.drawRoundRect(redRect, 2, 2, sPaint);
            float dateX = redRect.left + (rectWidth - dateWidth) / 2;
            float dateY = top + height + rectHeight / 2 + mOffset4CenterBigText;
            setTouchLineTextPaint(sPaint);
            canvas.drawText(date, dateX, dateY, sPaint);

            // draw price connect to horizontal line
            String price = formatNumber(data.getClose_Price());
            setTouchLineTextPaint(sPaint);
            float priceWidth = sPaint.measureText(price);
            float priceMargin = (mPriceAreaWidth - priceWidth) / 2;
            float priceX = left + width - priceMargin - priceWidth;
            redRect = getBigFontBgRectF(priceX, touchY + mOffset4CenterBigText, priceWidth);
            rectHeight = redRect.height();
            redRect.top -= rectHeight / 2;
            if (redRect.top < top) {
                redRect.top = top;
            }
            redRect.bottom = redRect.top + rectHeight;
            setRedRectBgPaint(sPaint);
            canvas.drawRoundRect(redRect, 2, 2, sPaint);
            float priceY = redRect.top + rectHeight / 2 + mOffset4CenterBigText;
            setTouchLineTextPaint(sPaint);
            canvas.drawText(price, priceX, priceY, sPaint);

            if (indexesEnable) {
                // draw cross line: vertical line and horizontal line
                setRedTouchLinePaint(sPaint);
                path = getPath();
                path.moveTo(touchX, top2);
                path.lineTo(touchX, top2 + height2);
                canvas.drawPath(path, sPaint);
                path = getPath();
                path.moveTo(left2, touchY2);
                path.lineTo(left2 + width2 - mPriceAreaWidth, touchY2);
                canvas.drawPath(path, sPaint);

                // draw volume connect to horizontal line
                String volume = String.valueOf(data.getBusinessVolume());
                setTouchLineTextPaint(sPaint);
                float volumeWidth = sPaint.measureText(volume);
                redRect = getBigFontBgRectF(0, 0, volumeWidth);
                rectHeight = redRect.height();
                rectWidth = redRect.width();
                float volumeMargin = (rectWidth - volumeWidth) / 2;
                float volumeX = left2 + width2 - volumeMargin - volumeWidth;
                redRect.top = touchY2 - rectHeight;
                redRect.left = left2 + width2 - rectWidth;
                if (redRect.top < top2) {
                    redRect.top = top2;
                }
                redRect.bottom = redRect.top + rectHeight;
                redRect.right = redRect.left + rectWidth;
                setRedRectBgPaint(sPaint);
                canvas.drawRoundRect(redRect, 2, 2, sPaint);
                float volumeY = redRect.top + rectHeight / 2 + mOffset4CenterBigText;
                setTouchLineTextPaint(sPaint);
                canvas.drawText(volume, volumeX, volumeY, sPaint);
            }
        }
    }

    private String getDate(int index) {
        int morningDataCount = getDiffMinutes(mTimeLine[0], mTimeLine[1]);
        if (index >= morningDataCount) {
            return getTimeFrom(mTimeLine[2], index - morningDataCount);
        }
        return getTimeFrom(mTimeLine[0], index);
    }

    /**
     * get diff minutes bewteen endDate and startDate. endDate - startDate
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public int getDiffMinutes(String startDate, String endDate) {
        long diff = 0;
        try {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            long start = parser.parse(startDate).getTime();
            long end = parser.parse(endDate).getTime();

            if (startDate.compareTo(endDate) <= 0) { // eg. 09:00 <= 09:10
                diff = end - start;
            } else { // eg. 21:00 ~ 01:00, we should change 01:00 to 25:00
                diff = end + 24 * 60 * 60 * 1000 - start;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return (int) (diff / (60 * 1000));
        }
    }

    /**
     *
     * @param startDate
     * @param count
     * @return startData + count min
     */
    public String getTimeFrom(String startDate, int count) {
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        long result = 0;
        try {
            long start = parser.parse(startDate).getTime();
            result = start + count * 60 * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return parser.format(new Date(result));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setBaseLinePaint(sPaint);
        canvas.drawLine(getWidth(), getTop(), getWidth(), getHeight(), sPaint);
    }

}
