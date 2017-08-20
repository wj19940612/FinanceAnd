package com.sbai.finance.view.training.Kline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.chart.ChartSettings;
import com.sbai.chart.domain.KlineViewData;
import com.sbai.finance.R;
import com.sbai.finance.model.training.question.KData;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class MvKlineView extends RelativeLayout {

    private Kline mKline;
    private OverLayer mOverLayer;
    private LinearLayout mJudgeButtons;
    private Settings mSettings;
    private SparseArray<Kline.IntersectionPoint> mIntersectionPointArray;
    private Button mJudgeUpBtn;
    private Button mJudgeDownBtn;
    private Kline.IntersectionPoint mFocusedPoint;
    private OnAnswerSelectedListener mOnAnswerSelectedListener;
    private int mRightAnswers;
    private ImageView mFocusView;
    private PopupWindow mHintView;

    public interface OnAnswerSelectedListener {

        void onRightAnswerSelected(float accuracy);

        void onWrongAnswerSelected(String analysis);
    }

    public MvKlineView(Context context) {
        super(context);
        init();
    }

    public MvKlineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mOverLayer != null && !mOverLayer.isStarted()) {
            mOverLayer.start();
        }
    }

    private void init() {
        mIntersectionPointArray = new SparseArray<>();

        RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        mKline = new Kline(getContext());
        mSettings = new Settings();
        mSettings.setXAxis(60);
        mSettings.setBaseLines(7);
        mSettings.setMovingAverages(new int[]{5, 30});
        mKline.setSettings(mSettings);
        mKline.setIntersectionPointArray(mIntersectionPointArray);
        addView(mKline, params);

        params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mOverLayer = new OverLayer(getContext());
        mOverLayer.setIntersectionPointArray(mIntersectionPointArray);
        mOverLayer.setOnFocusIntersectionPointListener(new OverLayer.OnFocusIntersectionPointListener() {
            @Override
            public void onFocus(Kline.IntersectionPoint point) {
                mJudgeUpBtn.setEnabled(true);
                mJudgeDownBtn.setEnabled(true);
                mFocusedPoint = point;

                translateFocusView();
                showHintView();
                updateHint(R.string.forecast_up_down);
            }
        });
        addView(mOverLayer, params);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        mJudgeButtons = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_judge_buttons, null);
        params.addRule(ALIGN_PARENT_RIGHT);
        params.addRule(CENTER_VERTICAL);
        addView(mJudgeButtons, params);

        mJudgeUpBtn = (Button) mJudgeButtons.findViewById(R.id.judgeUpBtn);
        mJudgeDownBtn = (Button) mJudgeButtons.findViewById(R.id.judgeDownBtn);
        mJudgeUpBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mJudgeUpBtn.isSelected()) {
                    mJudgeUpBtn.setSelected(true);
                    mJudgeDownBtn.setEnabled(false);
                    if (mFocusedPoint.getType() == KData.TYPE_LONG) {
                        onRightAnswerSelected();
                        showHintView();
                        updateHint(R.string.judge_right);
                        v.postDelayed(new ResumeTask(), 1000);
                    } else {
                        showHintView();
                        updateHint(R.string.judge_wrong);
                        onWrongAnswerSelected();
                    }
                }
            }
        });
        mJudgeDownBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mJudgeDownBtn.isSelected()) {
                    mJudgeDownBtn.setSelected(true);
                    mJudgeUpBtn.setEnabled(false);
                    if (mFocusedPoint.getType() == KData.TYPE_SHORT) {
                        onRightAnswerSelected();
                        showHintView();
                        updateHint(R.string.judge_right);
                        v.postDelayed(new ResumeTask(), 1000);
                    } else {
                        showHintView();
                        updateHint(R.string.judge_wrong);
                        onWrongAnswerSelected();
                    }
                }
            }
        });

        mFocusView = new ImageView(getContext());
        mFocusView.setImageResource(R.drawable.ic_judge_focus);
        mFocusView.setVisibility(INVISIBLE);
        addView(mFocusView);

        initHintView();
    }

    private void initHintView() {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_judge_hint, null);
        mHintView = new PopupWindow(popupView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mHintView.setOutsideTouchable(false);
        mHintView.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mHintView.setClippingEnabled(true);
    }

    private void showHintView() {
        if (mHintView.isShowing()) {
            mHintView.dismiss();
        }

        if (!mHintView.isShowing()) {
            View popupView = mHintView.getContentView();
            popupView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int[] location = new int[2];
            getLocationOnScreen(location);
            int offsetX = (int) (location[0] + mFocusView.getTranslationX()
                    + mFocusView.getWidth() / 2 - popupView.getMeasuredWidth() / 2);
            int offsetY = (int) (location[1] + mFocusView.getTranslationY()
                    - popupView.getMeasuredHeight() - mOverLayer.dp2Px(5));
            mHintView.showAtLocation(this, Gravity.NO_GRAVITY, offsetX, offsetY);
        }
    }

    private void updateHint(int strRes) {
        View popupView = mHintView.getContentView();
        TextView hint = (TextView) popupView.findViewById(R.id.hint);
        hint.setText(strRes);
    }

    private void translateFocusView() {
        mFocusView.setTranslationX(mFocusedPoint.getPoint().x - mFocusView.getWidth() / 2);
        mFocusView.setTranslationY(mFocusedPoint.getPoint().y - mFocusView.getHeight() / 2);
        showFocusView();
    }

    private void showFocusView() {
        mFocusView.setVisibility(VISIBLE);
        mFocusView.setAlpha(0f);
        mFocusView.setScaleX(3f);
        mFocusView.setScaleY(3f);
        mFocusView.animate()
                .alpha(1).scaleX(1).scaleY(1)
                .setDuration(300)
                .start();
    }

    private void onWrongAnswerSelected() {
        if (mOnAnswerSelectedListener != null) {
            mOnAnswerSelectedListener.onWrongAnswerSelected(mFocusedPoint.getAnalysis());
        }
    }

    private void onRightAnswerSelected() {
        mRightAnswers++;
        float accuracy = mRightAnswers * 1.0f / mIntersectionPointArray.size();
        if (mOnAnswerSelectedListener != null) {
            mOnAnswerSelectedListener.onRightAnswerSelected(accuracy);
        }
    }

    public void setOnAnswerSelectedListener(OnAnswerSelectedListener onAnswerSelectedListener) {
        mOnAnswerSelectedListener = onAnswerSelectedListener;
    }

    public void resume() {
        post(new ResumeTask());
    }

    public void setDurationTime(long milliseconds) {
        mOverLayer.setAnimTime(milliseconds);
    }

    private class ResumeTask implements Runnable {

        @Override
        public void run() {
            mJudgeUpBtn.setEnabled(false);
            mJudgeUpBtn.setSelected(false);
            mJudgeDownBtn.setEnabled(false);
            mJudgeDownBtn.setSelected(false);
            mFocusView.setVisibility(INVISIBLE);
            mHintView.dismiss();
            mOverLayer.start();
        }
    }

    public void setDataList(List<KData> dataList) {
        new DataShowTask(dataList, mIntersectionPointArray, mKline, mSettings).execute();
    }

    static class DataShowTask extends AsyncTask<Void, Void, Void> {

        private Settings mSettings;
        private List<KData> mDataList;
        private SparseArray<Kline.IntersectionPoint> mPointSparseArray;
        private WeakReference<Kline> mRef;

        public DataShowTask(List<KData> dataList, SparseArray<Kline.IntersectionPoint> intersectionPointArray,
                            Kline kline, Settings settings) {
            mDataList = dataList;
            mPointSparseArray = intersectionPointArray;
            mSettings = settings;
            mRef = new WeakReference<>(kline);
        }

        @Override
        protected Void doInBackground(Void... params) {
            KlineUtils.calculateIntersectionPointsNum(mPointSparseArray, mDataList);
            KlineUtils.calculateStartAndEndPosition(mSettings, mDataList);
            KlineUtils.calculateMovingAverages(mSettings, mDataList);
            KlineUtils.calculateBaseLines(mSettings, mDataList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Kline kline = mRef.get();
            if (kline != null) {
                kline.setDataList(mDataList);
            }
        }
    }

    public static class KlineUtils {

        public static void calculateIntersectionPointsNum(
                SparseArray<Kline.IntersectionPoint> pointSparseArray, List<KData> dataList) {
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).isOption()) {
                    pointSparseArray.put(i, new Kline.IntersectionPoint());
                }
            }
        }

        public static void calculateStartAndEndPosition(Settings settings, List<KData> dataList) {
            if (dataList != null) {
                settings.start = dataList.size() - settings.getXAxis() < 0 ? 0 : (dataList.size() - settings.getXAxis());
                settings.length = Math.min(dataList.size(), settings.getXAxis());
                settings.end = settings.start + settings.length;
            }
        }

        public static void calculateMovingAverages(Settings settings, List<KData> dataList) {
            if (dataList != null && dataList.size() > 0) {
                for (int movingAverage : settings.movingAverages) {
                    for (int i = settings.start; i < settings.end; i++) {
                        int start = i - movingAverage + 1;
                        if (start < 0) continue;
                        float movingAverageValue = calculateMovingAverageValue(start, movingAverage, dataList);
                        dataList.get(i).getK().addMovingAverage(movingAverage, movingAverageValue);
                    }
                }
            }
        }

        private static float calculateMovingAverageValue(int start, int movingAverage, List<KData> dataList) {
            float result = 0;
            for (int i = start; i < start + movingAverage; i++) {
                result += dataList.get(i).getK().getClosePrice();
            }
            return result / movingAverage;
        }

        public static void calculateBaseLines(Settings settings, List<KData> dataList) {
            if (dataList != null && dataList.size() > 0) {
                float[] baselines = settings.getBaseLines();
                float max = Float.MIN_VALUE;
                float min = Float.MAX_VALUE;
                for (int i = settings.start; i < settings.end; i++) {
                    KlineViewData data = dataList.get(i).getK();
                    if (max < data.getMaxPrice()) max = data.getMaxPrice();
                    if (min > data.getMinPrice()) min = data.getMinPrice();
                }

                for (int mv : settings.movingAverages) {
                    for (int i = settings.start; i < settings.end; i++) {
                        int start = i - mv + 1;
                        if (start < 0) continue;
                        float mvValue = dataList.get(i).getK().getMovingAverage(mv);
                        if (max < mvValue) max = mvValue;
                        if (min > mvValue) min = mvValue;
                    }
                }

                float priceRange = BigDecimal.valueOf(max).subtract(new BigDecimal(min))
                        .divide(new BigDecimal(baselines.length - 1),
                                4, RoundingMode.HALF_EVEN).floatValue();

                baselines[0] = max;
                baselines[baselines.length - 1] = min;
                for (int i = baselines.length - 2; i > 0; i--) {
                    baselines[i] = baselines[i + 1] + priceRange;
                }
            }
        }
    }

    public static class Settings extends ChartSettings {
        int start;
        int length;
        int end;

        private int[] movingAverages;

        public int[] getMovingAverages() {
            return movingAverages;
        }

        public void setMovingAverages(int[] movingAverages) {
            this.movingAverages = movingAverages;
        }
    }
}
