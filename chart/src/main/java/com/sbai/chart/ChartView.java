package com.sbai.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public abstract class ChartView extends View {

    protected enum ChartColor {

        BASE("#dddddd"),
        TEXT("#999999"),
        WHITE("#ffffff"),
        GREEN("#2fcc9f"),
        BLACK("#222222"),
        DASH("#979797"),
        RED("#f25b57");

        private String value;

        ChartColor(String color) {
            value = color;
        }

        public String get() {
            return value;
        }
    }

    private enum Action {
        NONE,
        TOUCH, // touch line
        DRAG,
        ZOOM
    }

    private static final int FONT_SIZE_DP = 10;
    private static final int FONT_BIG_SIZE_DP = 10;
    private static final int FONT_MA_TITLE_DP = 9;
    private static final int TEXT_MARGIN_WITH_LINE_DP = 3;
    private static final int RECT_PADDING_DP = 3;
    private static final int MIDDLE_EXTRA_SPACE_DP = 2;
    private static final int HEIGHT_TIME_LINE_DP = 30;
    private static final float RATIO_OF_TOP = 0.73f;
    private static final float BASELINE_WIDTH = 0.5f;

    private static final int WHAT_LONG_PRESS = 1;
    private static final int WHAT_ONE_CLICK = 2;
    private static final int DELAY_LONG_PRESS = 500;
    private static final int DELAY_ONE_CLICK = 100;
    private static final float CLICK_PIXELS = 1;

    public static Paint sPaint;
    private Path mPath;
    private Paint.FontMetrics mFontMetrics;
    private RectF mRectF;
    private StringBuilder mStringBuilder;
    private Handler mHandler;

    protected ChartSettings mSettings;
    protected ColorCfg mColorCfg;

    protected float mFontSize;
    protected int mFontHeight;
    protected float mOffset4CenterText; // center y of text + this will draw the text in center you want
    protected float mBigFontSize;
    protected int mBigFontHeight;
    protected float mOffset4CenterBigText;
    protected float mMaTitleSize;
    protected int mMaTitleHeight;
    protected float mOffset4CenterMaTitle;
    protected float mPriceAreaWidth;
    protected float mOneXAxisWidth;

    protected int mMiddleExtraSpace; // The middle space between two parts
    protected int mTextMargin; // The margin between text and baseline

    private int mXRectPadding;
    private int mYRectPadding;
    private int mTimeLineHeight;
    private int mCenterPartHeight;
    private float mBaseLineWidth;

    private int mTouchIndex; // The position of cross when touch view
    private float mDownX;
    private float mDownY;
    private Action mAction;
    private long mElapsedTime;

    private float mTransactionX;
    private float mMaxTransactionX;
    private float mPreviousTransactionX;
    private float mStartX;
    private int mStartPointOffset;

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();
        mRectF = new RectF();
        mStringBuilder = new StringBuilder();
        mHandler = new ChartHandler();

        mSettings = new ChartSettings();

        // text font
        mFontSize = sp2Px(FONT_SIZE_DP);
        sPaint.setTextSize(mFontSize);
        mFontMetrics = sPaint.getFontMetrics();
        sPaint.getFontMetrics(mFontMetrics);
        mFontHeight = (int) (mFontMetrics.bottom - mFontMetrics.top);
        mOffset4CenterText = calOffsetY4TextCenter();

        // big text font
        mBigFontSize = sp2Px(FONT_BIG_SIZE_DP);
        sPaint.setTextSize(mBigFontSize);
        sPaint.getFontMetrics(mFontMetrics);
        mBigFontHeight = (int) (mFontMetrics.bottom - mFontMetrics.top);
        mOffset4CenterBigText = calOffsetY4TextCenter();

        // ma title text font
        mMaTitleSize = sp2Px(FONT_MA_TITLE_DP);
        sPaint.setTextSize(mMaTitleSize);
        sPaint.getFontMetrics(mFontMetrics);
        mMaTitleHeight = (int) (mFontMetrics.bottom - mFontMetrics.top);
        mOffset4CenterMaTitle = calOffsetY4TextCenter();

        // constant
        mTextMargin = (int) dp2Px(TEXT_MARGIN_WITH_LINE_DP);
        mXRectPadding = (int) dp2Px(RECT_PADDING_DP);
        mYRectPadding = mXRectPadding / 2;
        mMiddleExtraSpace = (int) dp2Px(MIDDLE_EXTRA_SPACE_DP);
        mTimeLineHeight = (int) dp2Px(HEIGHT_TIME_LINE_DP);
        mCenterPartHeight = mMiddleExtraSpace + mTimeLineHeight;
        mBaseLineWidth = dp2Px(BASELINE_WIDTH);

        // gesture
        mTouchIndex = -1;
        mAction = Action.NONE;
    }

    private class ChartHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_LONG_PRESS || msg.what == WHAT_ONE_CLICK) {
                if (enableDrawTouchLines()) {
                    mAction = Action.TOUCH;
                    MotionEvent e = (MotionEvent) msg.obj;
                    triggerTouchLinesRedraw(e);
                }
            }
        }
    }

    public ChartSettings getSettings() {
        return mSettings;
    }

    public void setSettings(ChartSettings settings) {
        mSettings = settings;
        mColorCfg = mSettings.getColorCfg();
        redraw();
    }

    protected void setBaseLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.BASE.get()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mBaseLineWidth);
        applyColorConfiguration(paint, ColorCfg.BASE_LINE);
    }

    protected void setDefaultTextPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.TEXT.get()));
        paint.setTextSize(mFontSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
        applyColorConfiguration(paint, ColorCfg.DEFAULT_TXT);
    }

    protected void applyColorConfiguration(Paint paint, String elementName) {
        if (mColorCfg == null) return;

        String color = mColorCfg.get(elementName);
        if (color != null) {
            paint.setColor(Color.parseColor(color));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int topPartHeight = getTopPartHeight();
        int bottomPartHeight = getBottomPartHeight();

        if (enableDragChart()) {
            mOneXAxisWidth = calculateOneXAxisWidth();
            mMaxTransactionX = calculateMaxTransactionX();
        }

        if (enableCalculateMovingAverages()) {
            calculateMovingAverages(mSettings.isIndexesEnable());
        }

        calculateBaseLines(mSettings.getBaseLines());

        int top2 = -1;
        if (mSettings.isIndexesEnable()) {
            top2 = top + getTopPartHeight() + mCenterPartHeight;

            calculateIndexesBaseLines(mSettings.getIndexesBaseLines());
        }

        drawBaseLines(mSettings.isIndexesEnable(),
                mSettings.getBaseLines(), left, top, width, topPartHeight,
                mSettings.getIndexesBaseLines(), left, top2, width, bottomPartHeight,
                canvas);

        drawRealTimeData(mSettings.isIndexesEnable(),
                left, top, width, topPartHeight,
                left, top2, width, bottomPartHeight,
                canvas);

        if (enableDrawMovingAverages()) {
            drawMovingAverageLines(mSettings.isIndexesEnable(),
                    left, top, width, topPartHeight,
                    left, top2, width, bottomPartHeight,
                    canvas);
        }

        if (enableDrawUnstableData()) {
            drawUnstableData(mSettings.isIndexesEnable(),
                    left, top, width, topPartHeight,
                    left, top2, width, bottomPartHeight,
                    canvas);
        }

        drawTimeLine(left, top + topPartHeight, width, canvas);

        if (enableDrawMovingAverages()) {
            drawTitleAboveBaselines(mSettings.getBaseLines(), left, top, width, topPartHeight,
                    mSettings.getIndexesBaseLines(), left, top2, width, bottomPartHeight,
                    mTouchIndex, canvas);
        }

        if (mTouchIndex >= 0) {
            if (enableDrawTouchLines()) {
                drawTouchLines(mSettings.isIndexesEnable(), mTouchIndex,
                        left, top, width, topPartHeight,
                        left, top2, width, bottomPartHeight,
                        canvas);

                onTouchLinesAppear(mTouchIndex);
            }
        } else {
            onTouchLinesDisappear();
        }
    }


    protected float calculateMaxTransactionX() {
        return 0;
    }

    protected void calculateMovingAverages(boolean indexesEnable) {
    }

    protected float calculateOneXAxisWidth() {
        return 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mTouchIndex >= 0) { // touchLines appear
            getParent().requestDisallowInterceptTouchEvent(true);
        } else {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mAction == Action.NONE) {
                    Message message = mHandler.obtainMessage(WHAT_LONG_PRESS, event);
                    mHandler.sendMessageDelayed(message, DELAY_LONG_PRESS);
                } else if (mAction == Action.TOUCH) {
                    Message message = mHandler.obtainMessage(WHAT_ONE_CLICK, event);
                    mHandler.sendMessageDelayed(message, DELAY_ONE_CLICK);
                }

                mDownX = event.getX();
                mDownY = event.getY();

                mStartX = event.getX() - mPreviousTransactionX;

                return true;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(mDownX - event.getX()) < CLICK_PIXELS
                        || Math.abs(mDownY - event.getY()) < CLICK_PIXELS) {
                    return false;
                }

                mHandler.removeMessages(WHAT_LONG_PRESS);
                mHandler.removeMessages(WHAT_ONE_CLICK);

                if (mAction == Action.TOUCH) {
                    return triggerTouchLinesRedraw(event);
                }

                if (enableDragChart() && (mAction == Action.NONE || mAction == Action.DRAG)) {
                    double distance = Math.abs(event.getX() - (mStartX + mPreviousTransactionX));
                    if (distance > mOneXAxisWidth) {
                        mAction = Action.DRAG;
                        mTransactionX = event.getX() - mStartX;
                        if (mTransactionX > mMaxTransactionX) {
                            mTransactionX = mMaxTransactionX;
                        }
                        if (mTransactionX < 0) {
                            mTransactionX = 0;
                        }
                        int newStartPointOffset = calculatePointOffset();
                        if (mStartPointOffset != newStartPointOffset) {
                            mStartPointOffset = newStartPointOffset;
                            redraw();
                        }
                        return true;
                    }
                }

                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mAction == Action.NONE && mHandler.hasMessages(WHAT_LONG_PRESS)) {
                    mHandler.removeMessages(WHAT_LONG_PRESS);
                } else if (mAction == Action.TOUCH && mHandler.hasMessages(WHAT_ONE_CLICK)) {
                    mHandler.removeMessages(WHAT_ONE_CLICK);
                    mAction = Action.NONE;
                    if (mTouchIndex != -1) {
                        mTouchIndex = -1;
                        redraw();
                    }
                } else if (mAction == Action.DRAG) {
                    mAction = Action.NONE;
                    mPreviousTransactionX = mTransactionX;
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean triggerTouchLinesRedraw(MotionEvent event) {
        int newTouchIndex = calculateTouchIndex(event);
        if (newTouchIndex != mTouchIndex && hasThisTouchIndex(newTouchIndex)) {
            mTouchIndex = newTouchIndex;
            redraw();
            return true;
        }
        return false;
    }

    protected boolean hasThisTouchIndex(int touchIndex) {
        return false;
    }

    protected boolean enableCalculateMovingAverages() {
        return false;
    }

    protected boolean enableDrawMovingAverages() {
        return false;
    }

    protected boolean enableDrawUnstableData() {
        return false;
    }

    protected boolean enableDrawTouchLines() {
        return false;
    }

    protected boolean enableDragChart() {
        return false;
    }

    protected int calculateTouchIndex(MotionEvent e) {
        return -1;
    }

    protected int calculatePointOffset() {
        return (int) (mTransactionX / mOneXAxisWidth);
    }

    /**
     * draw touch lines, total area of top without middle
     *
     * @param indexesEnable
     * @param touchIndex
     * @param left
     * @param top
     * @param width
     * @param height
     * @param left2
     * @param top2
     * @param width2
     * @param height2
     * @param canvas
     */
    protected void drawTouchLines(boolean indexesEnable, int touchIndex,
                                  int left, int top, int width, int height,
                                  int left2, int top2, int width2, int height2,
                                  Canvas canvas) {
    }

    protected void onTouchLinesAppear(int touchIndex) {
    }

    protected void onTouchLinesDisappear() {
    }

    /**
     * draw moving averages post real time data draw, specifically for kline
     *
     * @param indexesEnable
     * @param left
     * @param top
     * @param width
     * @param topPartHeight
     * @param left1
     * @param top2
     * @param width1
     * @param bottomPartHeight
     * @param canvas
     */
    protected void drawMovingAverageLines(boolean indexesEnable,
                                          int left, int top, int width, int topPartHeight,
                                          int left1, int top2, int width1, int bottomPartHeight,
                                          Canvas canvas) {
    }

    /**
     * the title above content area and indexes area <br/>if indexes is disable, top2 is -1
     *
     * @param left
     * @param top        content draw area top
     * @param top2       indexes draw area top
     * @param touchIndex
     * @param canvas
     */
    protected void drawTitleAboveBaselines(float[] baselines, int left, int top, int width, int height,
                                           long[] indexesBaseLines, int left2, int top2, int width2, int height2,
                                           int touchIndex, Canvas canvas) {

    }

    protected abstract void calculateBaseLines(float[] baselines);

    protected abstract void calculateIndexesBaseLines(long[] indexesBaseLines);

    /**
     * draw top baselines and bottom indexes baselines
     *
     * @param indexesEnable
     * @param baselines
     * @param left
     * @param top
     * @param width
     * @param height
     * @param indexesBaseLines
     * @param left2
     * @param top2
     * @param width2
     * @param height2
     * @param canvas
     */
    protected abstract void drawBaseLines(boolean indexesEnable,
                                          float[] baselines, int left, int top, int width, int height,
                                          long[] indexesBaseLines, int left2, int top2, int width2, int height2,
                                          Canvas canvas);

    /**
     * draw real time data
     *
     * @param indexesEnable
     * @param left
     * @param top
     * @param width
     * @param height
     * @param left2
     * @param top2
     * @param width2
     * @param height2
     * @param canvas
     */
    protected abstract void drawRealTimeData(boolean indexesEnable,
                                             int left, int top, int width, int height,
                                             int left2, int top2, int width2, int height2,
                                             Canvas canvas);

    /**
     * draw real unstable data
     *
     * @param indexesEnable
     * @param left
     * @param top
     * @param width
     * @param topPartHeight
     * @param left2
     * @param top2
     * @param width1
     * @param bottomPartHeight
     * @param canvas
     */
    protected void drawUnstableData(boolean indexesEnable,
                                    int left, int top, int width, int topPartHeight,
                                    int left2, int top2, int width1, int bottomPartHeight,
                                    Canvas canvas) {

    }


    /**
     * draw time line
     *
     * @param left   the left(x) coordinate of time line text
     * @param top    the top(y coordinate）of time line text
     * @param width
     * @param canvas
     */
    protected abstract void drawTimeLine(int left, int top, int width, Canvas canvas);

    protected int getTopPartHeight() {
        int originalHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int topPartHeight = originalHeight - mTimeLineHeight;
        if (mSettings.isIndexesEnable()) {
            return (int) ((topPartHeight - mMiddleExtraSpace) * RATIO_OF_TOP);
        }
        return topPartHeight;
    }

    protected int getBottomPartHeight() {
        int originalHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        if (mSettings.isIndexesEnable()) {
            return originalHeight - mCenterPartHeight - getTopPartHeight();
        }
        return 0;
    }

    protected Path getPath() {
        if (mPath == null) {
            mPath = new Path();
        }
        mPath.reset();
        return mPath;
    }

    public int getStartPointOffset() {
        return mStartPointOffset;
    }

    protected RectF getRectF() {
        if (mRectF == null) {
            mRectF = new RectF();
        }
        mRectF.setEmpty();
        return mRectF;
    }

    protected StringBuilder getStringBuilder() {
        if (mStringBuilder == null) {
            mStringBuilder = new StringBuilder();
        }
        mStringBuilder.setLength(0);
        return mStringBuilder;
    }

    protected float getChartY(float y) {
        // When values beyond baselines, eg. mv. return -1
        float[] baselines = mSettings.getBaseLines();
        if (y > baselines[0] || y < baselines[baselines.length - 1]) {
            return -1;
        }

        int height = getTopPartHeight();
        y = (baselines[0] - y) / (baselines[0] - baselines[baselines.length - 1]) * height;
        return y + getPaddingTop();
    }

    protected float getIndexesChartY(long y) {
        // When values beyond indexes baselines, eg. mv. return -1
        long[] indexesBaseLines = mSettings.getIndexesBaseLines();
        if (y > indexesBaseLines[0] || y < indexesBaseLines[indexesBaseLines.length - 1]) {
            return -1;
        }

        int height = getBottomPartHeight();

        float chartY = (indexesBaseLines[0] - y) * 1.0f /
                (indexesBaseLines[0] - indexesBaseLines[indexesBaseLines.length - 1]) * height;

        return chartY + getPaddingTop() + getTopPartHeight() + mCenterPartHeight;
    }

    protected float getChartX(int index) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        return getPaddingLeft() + index * width * 1.0f / mSettings.getXAxis();
    }

    /**
     * this is the inverse operation of getCharX(index)
     *
     * @param chartX
     * @return
     */
    protected int getIndexOfXAxis(float chartX) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        chartX = chartX - getPaddingLeft();
        return (int) (chartX * mSettings.getXAxis() / width);
    }

    /**
     * the startXY of drawText is at baseline, this is used to add some offsetY for text center
     * centerY = y + offsetY
     *
     * @return offsetY
     */
    protected float calOffsetY4TextCenter() {
        Paint.FontMetrics fontMetrics = sPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        return fontHeight / 2 - fontMetrics.bottom;
    }

    /**
     * this method is used to calculate a rectF for the text that will be drew
     * we add some left-right padding for the text, just for nice
     *
     * @param textX     left of text
     * @param textY     y of text baseline
     * @param textWidth
     * @return
     */
    protected RectF getBigFontBgRectF(float textX, float textY, float textWidth) {
        mRectF.left = textX - mXRectPadding;
        mRectF.top = textY + mFontMetrics.top - mYRectPadding;
        mRectF.right = textX + textWidth + mXRectPadding;
        mRectF.bottom = textY + mFontMetrics.bottom + mYRectPadding;
        return mRectF;
    }

    /**
     * this method is used to calculate the width of the big font size price text with rect bg
     *
     * @param price
     * @return
     */
    protected float calculatePriceWidth(float price) {
        String preClosePrice = formatNumber(price);
        sPaint.setTextSize(mBigFontSize);
        float priceWidth = sPaint.measureText(preClosePrice);
        return getBigFontBgRectF(0, 0, priceWidth).width();
    }

    public float dp2Px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    public float sp2Px(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }

    public String formatNumber(float value) {
        return formatNumber(value, mSettings.getNumberScale());
    }

    protected String formatNumber(double value, int numberScale) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();

        decimalFormat.setMaximumFractionDigits(numberScale);
        decimalFormat.setMinimumFractionDigits(numberScale);
        decimalFormat.setMinimumIntegerDigits(1);
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);

        String v = decimalFormat.format(value);
        return v;
    }

    protected void redraw() {
        invalidate(0, 0, getWidth(), getHeight());
    }

    protected void resetChart() {
        mTouchIndex = -1; // The position of cross when touch view
        mDownX = 0;
        mDownY = 0;
        mAction = Action.NONE;
        mElapsedTime = 0;

        mTransactionX = 0;
        mMaxTransactionX = 0;
        mPreviousTransactionX = 0;
        mStartX = 0;
        mStartPointOffset = 0;
    }
}
