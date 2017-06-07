package com.sbai.finance.view.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by ${wangJie} on 2017/6/6.
 */

public class CameraTopRectView extends View {

    private static final String TAG = "CameraTopRectView";

    private int mScreenWidth;
    private int mScreenHeight;
    private Context mContext;

    private int viewWidth;
    private int viewHeight;

    public int rectWidth;
    public int rectHeght;

    private int rectTop;
    private int rectLeft;
    private int rectRight;
    private int rectBottom;

    private int lineLen;
    private int lineWidht;
    private static final int LINE_WIDTH = 2;
    private static final int TOP_BAR_HEIGHT = 50;
    private static final int BOTTOM_BTN_HEIGHT = 66;

//    private static final int TOP_BAR_HEIGHT = Constant.RECT_VIEW_TOP;
//    private static final int BOTTOM_BTN_HEIGHT = Constant.RECT_VIEW_BOTTOM;

    private static final int LEFT_PADDING = 10;
    private static final int RIGHT_PADDING = 10;
    private static final String TIPS = "请将身份证放入规定区域内进行拍摄";

    private Paint linePaint;
    private Paint wordPaint;
    private Rect rect;
    private int baseline;


    public CameraTopRectView(Context context) {
        this(context, null);
    }

    public CameraTopRectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraTopRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getScreenMetrics((Activity) context);
        viewHeight = mScreenHeight;
        //viewHeight,界面的高,viewWidth,界面的宽
        viewWidth = mScreenWidth;

        /*rectWidth = panelWidth
                - UnitUtils.getInstance(activity).dip2px(
                        LEFT_PADDING + RIGHT_PADDING);*/

        rectWidth = mScreenWidth - (int) dp2px(context, LEFT_PADDING + RIGHT_PADDING);

        rectHeght = (int) (rectWidth * 54 / 85.6);
        // 相对于此view
        rectTop = (viewHeight - rectHeght) / 2;
        rectLeft = (viewWidth - rectWidth) / 2;

        rectBottom = rectTop + rectHeght;
        rectRight = rectLeft + rectWidth;
        Log.d(TAG, "rectWidth: " + rectWidth + " rectHeght " + rectHeght + "  rectTop " + rectTop + " rectLeft " + rectLeft + " rectBottom " + rectBottom + " rectRight " + rectRight);
        //边框的红色线长度
        lineLen = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.rgb(0xdd, 0x42, 0x2f));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(LINE_WIDTH);// 设置线宽
        linePaint.setAlpha(255);

        wordPaint = new Paint();
        wordPaint.setAntiAlias(true);
        wordPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wordPaint.setStrokeWidth(3);
        wordPaint.setTextSize(35);

        rect = new Rect(rectLeft, rectTop - 80, rectRight, rectTop - 10);
        Paint.FontMetricsInt fontMetrics = wordPaint.getFontMetricsInt();
        baseline = rect.top + (rect.bottom - rect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        wordPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void getScreenMetrics(Activity context) {
        WindowManager windowManager = context.getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        wordPaint.setColor(Color.TRANSPARENT);
        canvas.drawRect(rect, wordPaint);

        //画蒙层
        wordPaint.setColor(Color.parseColor("#8D8f92"));
        Log.d(TAG, "onDraw: " + " viewHeight " + viewHeight / 2 + " rectHeght " + rectHeght / 2);
        rect = new Rect(0, viewHeight / 2 + rectHeght / 2, viewWidth, viewHeight);
        canvas.drawRect(rect, wordPaint);

        rect = new Rect(0, 0, viewWidth, viewHeight / 2 - rectHeght / 2);
        canvas.drawRect(rect, wordPaint);

        rect = new Rect(0, viewHeight / 2 - rectHeght / 2, (viewWidth - rectWidth) / 2, viewHeight / 2 + rectHeght / 2);
        canvas.drawRect(rect, wordPaint);

        rect = new Rect(viewWidth - (viewWidth - rectWidth) / 2, viewHeight / 2 - rectHeght / 2, viewWidth, viewHeight / 2 + rectHeght / 2);
        canvas.drawRect(rect, wordPaint);

        rect = new Rect(rectLeft, viewHeight / 2 - rectHeght / 2, rectRight, 0);
        Log.d(TAG, "rectLeft : " + rectLeft + "  rectBottom " + rectBottom + " rectRight " + rectRight + " rectBottom " + rectBottom);
        wordPaint.setColor(Color.WHITE);
        wordPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        canvas.drawText(TIPS, rect.centerX(), rectBottom + 60, wordPaint);

        canvas.drawLine(rectLeft, rectTop, rectLeft + lineLen, rectTop,
                linePaint);
        Log.d(TAG, "rectTop: " + rectTop);
        canvas.drawLine(rectRight - lineLen, rectTop, rectRight, rectTop,
                linePaint);
        canvas.drawLine(rectLeft, rectTop, rectLeft, rectTop + lineLen,
                linePaint);
        canvas.drawLine(rectRight, rectTop, rectRight, rectTop + lineLen,
                linePaint);
        canvas.drawLine(rectLeft, rectBottom, rectLeft + lineLen, rectBottom,
                linePaint);
        canvas.drawLine(rectRight - lineLen, rectBottom, rectRight, rectBottom,
                linePaint);
        canvas.drawLine(rectLeft, rectBottom - lineLen, rectLeft, rectBottom,
                linePaint);
        canvas.drawLine(rectRight, rectBottom - lineLen, rectRight, rectBottom,
                linePaint);

    }

    public int getRectLeft() {
        return rectLeft;
    }

    public int getRectTop() {
        return rectTop;
    }

    public int getRectRight() {
        return rectRight;
    }

    public int getRectBottom() {
        return rectBottom;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }
}
