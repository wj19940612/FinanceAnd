package com.sbai.finance.view.training.Kline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.sbai.chart.ChartSettings;
import com.sbai.chart.ChartView;
import com.sbai.chart.domain.KlineViewData;
import com.sbai.finance.model.training.question.KData;

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
    private SparseArray<IntersectionPoint> mIntersectionPointArray;
    private MvKlineView.Settings mSettings;

    private float mCandleWidth;
    private float mBaseLineWidth;
    private float mButtonsAreaWidth;
    private float mMvWidth;
    private Line[] mLines;

    protected void setBaseLinePaint(Paint paint) {
        paint.setColor(Color.parseColor("#2a2a2a"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mBaseLineWidth);
    }

    private void setMovingAveragesPaint(Paint paint, int movingAverage) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mMvWidth);
        paint.setPathEffect(null);
        if (movingAverage == mSettings.getMovingAverages()[0]) {
            paint.setColor(Color.parseColor(COLOR_VIOLET));
        } else if (movingAverage == mSettings.getMovingAverages()[1]) {
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
        mBaseLineWidth = dp2Px(0.5f);
        mButtonsAreaWidth = dp2Px(BUTTONS_AREA_WIDTH);
        mMvWidth = dp2Px(MV_WIDTH);
        mLines = new Line[2];
        for (int i = 0; i < mLines.length; i++) {
            mLines[i] = new Line();
        }
    }

    @Override
    protected void calculateBaseLines(float[] baselines) {
    }

    @Override
    protected void calculateIndexesBaseLines(long[] indexesBaseLines) {
    }

    @Override
    protected float getChartX(int index) {
        index = index - mSettings.start;
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
            for (int i = mSettings.start; i < mSettings.end; i++) {
                KData data = mDataList.get(i);
                float chartX = getChartX(i);
                drawCandle(chartX, data, canvas);
            }
            drawMovingAverageLines(canvas);
        }
    }

    private void drawMovingAverageLines(Canvas canvas) {
        for (int movingAverage : mSettings.getMovingAverages()) {
            setMovingAveragesPaint(sPaint, movingAverage);
            float startX = -1;
            float startY = -1;
            for (int i = mSettings.start; i < mSettings.end; i++) {
                int start = i - movingAverage + 1;
                if (start < 0) continue;
                KData kData = mDataList.get(i);
                float chartX = getChartX(i);
                float movingAverageValue = kData.getK().getMovingAverage(movingAverage);
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

    public void setDataList(List<KData> dataList) {
        mDataList = dataList;
        redraw();
    }

    public void setIntersectionPointArray(SparseArray<IntersectionPoint> pointSparseArray) {
        mIntersectionPointArray = pointSparseArray;
    }

    public SparseArray<IntersectionPoint> getIntersectionPointArray() {
        return mIntersectionPointArray;
    }

    @Override
    public void setSettings(ChartSettings settings) {
        mSettings = (MvKlineView.Settings) settings;
        super.setSettings(settings);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateIntersectionPointList();

//        for (int i = 0; i < mIntersectionPointArray.size(); i++) {
//            sPaint.setColor(Color.parseColor("#ffffff"));
//            sPaint.setStyle(Paint.Style.STROKE);
//            sPaint.setStrokeWidth(1);
//            IntersectionPoint point = mIntersectionPointArray.valueAt(i);
//            canvas.drawLine(point.getPoint().x, 0, point.getPoint().x, getHeight(), sPaint);
//        }
    }

    private void updateIntersectionPointList() {
        if (mDataList != null && mDataList.size() > 0) {
            for (int i = mSettings.start + 1; i < mSettings.end; i++) {
                KData kData = mDataList.get(i);
                if (kData.isOption()) {
                    KData preKData = mDataList.get(i - 1);
                    for (int j = 0; j < mLines.length; j++) {
                        int ma = mSettings.getMovingAverages()[j];
                        if (i - ma < 0) break; // (i - 1) no MA data
                        mLines[j].start.set(getChartX(i - 1), getChartY(preKData.getK().getMovingAverage(ma)));
                        mLines[j].end.set(getChartX(i), getChartY(kData.getK().getMovingAverage(ma)));
                    }
                    if (Line.isIntersected(mLines[0], mLines[1])) {
                        IntersectionPoint point = mIntersectionPointArray.get(i);
                        if (point != null) {
                            Line.updateIntersectionPoint(mLines[0], mLines[1], point);
                            point.setType(kData.getType());
                            point.setAnalysis(kData.getRemark());
                        }
                    }
                }
            }
        }
    }

    private static class Line {
        PointF start;
        PointF end;

        public Line() {
            start = new PointF();
            end = new PointF();
        }

        private float maxX() {
            return Math.max(start.x, end.x);
        }

        private float minX() {
            return Math.min(start.x, end.x);
        }

        private float maxY() {
            return Math.max(start.y, end.y);
        }

        private float minY() {
            return Math.min(start.y, end.y);
        }

        public static boolean isIntersected(Line line1, Line line2) {
            if (line1.maxX() < line2.minX()) {
                return false;
            }
            if (line1.maxY() < line2.minY()) {
                return false;
            }
            if (line2.maxX() < line1.minX()) {
                return false;
            }
            if (line2.maxY() < line1.minY()) {
                return false;
            }
            if (mult(line2.start, line1.end, line1.start) * mult(line1.end, line2.end, line1.start) < 0) {
                return false;
            }
            return mult(line1.start, line2.end, line2.start) * mult(line2.end, line1.end, line2.start) >= 0;
        }

        /**
         * 向量积 向量ca * 向量cb
         *
         * @param a
         * @param b
         * @param c
         * @return
         */
        private static float mult(PointF a, PointF b, PointF c) {
            return (a.x - c.x) * (b.y - c.y) - (b.x - c.x) * (a.y - c.y);
        }

        public static void updateIntersectionPoint(Line line1, Line line2, IntersectionPoint intersectionPoint) {
            // line1 方程 y - y1 = k1 * (x - x1) [x1, y1] 可以是 line1 其中一个点
            float k1 = getLineSlope(line1);
            float y1 = line1.start.y;
            float x1 = line1.start.x;
            // line2 方程 y - y2 = k2 * (x - x2) 同理
            float k2 = getLineSlope(line2);
            float y2 = line2.start.y;
            float x2 = line2.start.x;
            // 连解方程
            float x = (y2 - y1 - k2 * x2 + k1 * x1) / (k1 - k2);
            float y = k1 * x - k1 * x1 + y1;
            intersectionPoint.getPoint().set(x, y);
        }

        private static float getLineSlope(Line line) {
            return (line.end.y - line.start.y) / (line.end.x - line.start.x);
        }
    }

    public static class IntersectionPoint {
        private PointF point;
        private int type;
        private String analysis;

        public IntersectionPoint() {
            this.point = new PointF();
        }

        public PointF getPoint() {
            return point;
        }

        public void setPoint(PointF point) {
            this.point = point;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getAnalysis() {
            return analysis;
        }

        public void setAnalysis(String analysis) {
            this.analysis = analysis;
        }
    }
}
