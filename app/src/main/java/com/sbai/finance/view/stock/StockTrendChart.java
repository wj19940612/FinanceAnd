package com.sbai.finance.view.stock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.sbai.chart.ChartView;
import com.sbai.finance.model.stock.StockTrendData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class StockTrendChart extends ChartView {

    private static final float VOLUME_WIDTH_DP = 1.0f; //dp

    private List<StockTrendData> mDataList;
    private StockTrendData mUnstableData;
    private String[] mTimeLine;
    private float mVolumeWidth;

    public StockTrendChart(Context context) {
        super(context);
        mTimeLine = new String[]{"9:30", "11:30", "15:00"};
        mVolumeWidth = dp2Px(VOLUME_WIDTH_DP);
    }

    public StockTrendChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTimeLine = new String[]{"9:30", "11:30", "15:00"};
        mVolumeWidth = dp2Px(VOLUME_WIDTH_DP);
    }

    protected void setRealTimeLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.BLUE.get()));
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(null);
    }

    private void setCandleBodyPaint(Paint paint, String color) {
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    public void setDataList(List<StockTrendData> dataList) {
        mDataList = dataList;
        redraw();
    }

    @Override
    protected void calculateBaseLines(float[] baselines) {
        if (mDataList != null && mDataList.size() > 0) {
            float max = Float.MIN_VALUE;
            float min = Float.MAX_VALUE;
            for (StockTrendData stockTrendData : mDataList) {
                if (max < stockTrendData.getLastPrice()) {
                    max = stockTrendData.getLastPrice();
                }
                if (min > stockTrendData.getLastPrice()) {
                    min = stockTrendData.getLastPrice();
                }
            }

            if (mUnstableData != null) {
                if (max < mUnstableData.getLastPrice()) {
                    max = mUnstableData.getLastPrice();
                }
                if (min > mUnstableData.getLastPrice()) {
                    min = mUnstableData.getLastPrice();
                }
            }

            // the chart need a min height
//            double delta = new BigDecimal(max).subtract(new BigDecimal(min)).doubleValue();
//            float limitUp = mSettings.getLimitUp();
//            if (delta < limitUp) {
//                max = new BigDecimal(min).add(new BigDecimal(limitUp)).floatValue();
//            }

            float pricePadding = (max - min) * 1.0f / (baselines.length - 1);
            /** expand max ~ min to not let trend line touch top and bottom **/
            max = max + pricePadding;
            min = min - pricePadding;

            float priceRange = BigDecimal.valueOf(max).subtract(new BigDecimal(min))
                    .divide(new BigDecimal(baselines.length - 1), RoundingMode.HALF_EVEN)
                    .floatValue();

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
            mDataList.get(0).setBusinessVolume(mDataList.get(0).getBusinessAmount());
            long maxVolume = mDataList.get(0).getBusinessVolume();
            for (int i = 1; i < mDataList.size(); i++) {
                long volume = mDataList.get(i).getBusinessAmount() - mDataList.get(i - 1).getBusinessAmount();
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
            path.lineTo(left + width, topY);
            setBaseLinePaint(sPaint);
            canvas.drawPath(path, sPaint);

            if (i == 0) {
                setDefaultTextPaint(sPaint);
                String baseLineValue = formatNumber(baselines[i]);
                float textWidth = sPaint.measureText(baseLineValue);
                float x = left + width - mPriceAreaWidth + (mPriceAreaWidth - textWidth) / 2;
                float y = topY + mTextMargin + mFontHeight / 2 + mOffset4CenterText;
                canvas.drawText(baseLineValue, x, y, sPaint);

            } else if (i != 0 && i % 2 == 0) {
                setDefaultTextPaint(sPaint);
                String baseLineValue = formatNumber(baselines[i]);
                float textWidth = sPaint.measureText(baseLineValue);
                float x = left + width - mPriceAreaWidth + (mPriceAreaWidth - textWidth) / 2;
                float y = topY - mTextMargin - mFontHeight / 2 + mOffset4CenterText;
                canvas.drawText(baseLineValue, x, y, sPaint);
            }

            topY += verticalInterval;
        }
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
                chartX = getChartX(i);
                chartY = getChartY(mDataList.get(i).getLastPrice());
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
                    if (i > 0 && mDataList.get(i).getLastPrice() < mDataList.get(i - 1).getLastPrice()) {
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
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {
        setDefaultTextPaint(sPaint);
        float textY = top + mTextMargin + mFontHeight / 2 + mOffset4CenterText;
        float textX = left + mTextMargin;
        canvas.drawText(mTimeLine[0], textX, textY, sPaint);

        float textWidth = sPaint.measureText(mTimeLine[1]);
        textX = (left + width) / 2 - textWidth / 2;
        canvas.drawText(mTimeLine[1], textX, textY, sPaint);

        textWidth = sPaint.measureText(mTimeLine[2]);
        textX = left + width - textWidth;
        canvas.drawText(mTimeLine[2], textX, textY, sPaint);
    }


}
