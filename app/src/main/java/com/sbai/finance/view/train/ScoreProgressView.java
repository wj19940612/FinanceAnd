package com.sbai.finance.view.train;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.training.TrainAppraiseAndRemark;
import com.sbai.finance.model.training.UserEachTrainingScoreModel;
import com.sbai.finance.utils.NumberFormatUtils;

import java.util.List;


/**
 * Created by ${wangJie} on 2017/8/8.
 */

public class ScoreProgressView extends LinearLayout {
    private static final String TAG = "ScoreProgressView";

    //等级区分数量  默认为5
    private int mGradeSize = 5;
    // 等级评价   "较差", "中等", "良好", "优秀", "极强"
    private String[] mGradeExPlain = new String[]{"较差", "中等", "良好", "优秀", "极强"};
    //分数区间
    private int[] mScoreData = new int[]{0, 200, 400, 600, 800, 1000};

    private List<TrainAppraiseAndRemark> mAppraiseAndRemarkList;
    private UserEachTrainingScoreModel mUserEachTrainingScoreModel;

    //交易水平 超过多少人
    private TextView mTradeGradeTextView;
    //建议
    private TextView mAdviseTextView;

    private int[] bg;

    public ScoreProgressView(Context context) {
        this(context, null);
    }

    public ScoreProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScoreProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        removeAllViews();
        setOrientation(VERTICAL);

        bg = new int[]{R.drawable.bg_score_poor, R.drawable.bg_score_middle, R.drawable.bg_score_good, R.drawable.bg_score_excellent, R.drawable.bg_score_very_strong};
        //上面分数等级
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setPadding(0, 0, px2dp(10), 0);
        LayoutParams layoutParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(0, 0, px2dp(6), 0);

        LayoutParams layoutParams1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(0, 0, px2dp(10), 0);
        LinearLayout progressLinearLayout = new LinearLayout(getContext());
        LayoutParams progressLayoutParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, px2dp(6));
        progressLayoutParams.weight = 1;
        progressLayoutParams.setMargins(0, px2dp(6), px2dp(1), 0);


        LinearLayout scoreLinearLayout = new LinearLayout(getContext());
        LayoutParams scoreLayoutParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        scoreLayoutParams.setMargins(0, px2dp(6), 0, px2dp(6));
        scoreLayoutParams.weight = 1;

        for (int i = 0; i < mGradeSize; i++) {
            TextView scoreGradeTextView = createScoreGradeTextView(mGradeExPlain[i]);
            linearLayout.addView(scoreGradeTextView, layoutParams);

            View progressView = createProgressView(i);
            progressLinearLayout.addView(progressView, progressLayoutParams);

            if (i == 0) {
                TextView scoreTextView = createScoreTextView(String.valueOf(mScoreData[0]));
                TextView scoreTextView1 = createScoreTextView(String.valueOf(mScoreData[1]));
                scoreLinearLayout.addView(scoreTextView);
                scoreLinearLayout.addView(scoreTextView1, scoreLayoutParams);
            } else {
                TextView scoreTextView = createScoreTextView(String.valueOf(mScoreData[i + 1]));
                scoreLinearLayout.addView(scoreTextView, scoreLayoutParams);
            }
        }

        addView(linearLayout);
        addView(progressLinearLayout, layoutParams1);
        addView(scoreLinearLayout);
        createTradeGradeTextView();
        createAdviseTextView();
        changeScoreArea(linearLayout, progressLinearLayout);

    }

    private void createAdviseTextView() {
        mAdviseTextView = new TextView(getContext());
        mAdviseTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.luckyText));
        mAdviseTextView.setTextSize(12);
        mAdviseTextView.setLineSpacing(px2dp(3), 1);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, px2dp(5), 0, px2dp(25));
        addView(mAdviseTextView, layoutParams);
    }

    private void createTradeGradeTextView() {
        mTradeGradeTextView = new TextView(getContext());
        mTradeGradeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.blackPrimary));
        mTradeGradeTextView.setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(0, px2dp(20), 0, 0);
        addView(mTradeGradeTextView, layoutParams);
    }

    private void changeScoreArea(LinearLayout linearLayout, LinearLayout progressLinearLayout) {
        if (mUserEachTrainingScoreModel != null) {
            int userTotalScore = (int) mUserEachTrainingScoreModel.getUserTotalScore();
            for (int i = 0; i < mAppraiseAndRemarkList.size(); i++) {
                TrainAppraiseAndRemark result = mAppraiseAndRemarkList.get(i);
                if (result.getSocreStart() <= userTotalScore &&
                        userTotalScore <= result.getSocreEnd()) {
                    updateGrade(i, linearLayout);
                    updateScoreProgress(i, progressLinearLayout);
                    updateTradeGrade(result);
                    updateAdvise(result);
                    break;
                }

            }
        }
    }

    private void updateAdvise(TrainAppraiseAndRemark result) {
        mAdviseTextView.setText(getContext().getString(R.string.advise, result.getRemark()));
    }

    private void updateTradeGrade(TrainAppraiseAndRemark result) {
        mTradeGradeTextView.setText(getContext().getString(R.string.trade_level,
                result.getAppraise(),
                NumberFormatUtils.formatPercentString(mUserEachTrainingScoreModel.getRank())));
    }

    private void updateScoreProgress(int i, LinearLayout progressLinearLayout) {
        for (int j = 0; j < progressLinearLayout.getChildCount(); j++) {
            if (i < j) {
                break;
            }
            if (i >= j) {
                View childAt = progressLinearLayout.getChildAt(j);
                childAt.setSelected(true);
            }
        }
    }

    private void updateGrade(int i, LinearLayout linearLayout) {
        for (int j = 0; j < linearLayout.getChildCount(); j++) {
            if (i == j) {
                View childAt = linearLayout.getChildAt(j);
                if (childAt instanceof TextView) {
                    ((TextView) childAt).setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                }
                break;
            }

        }
    }

    private int px2dp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private TextView createProgressView(int i) {
        TextView view = new TextView(getContext());
        view.setBackgroundResource(bg[i]);
        return view;
    }

    //创建较差 中等 良好
    private TextView createScoreGradeTextView(String data) {
        AppCompatTextView textView = new AppCompatTextView(getContext());
        textView.setText(data);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(12);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.unluckyText));
        return textView;
    }

    private TextView createScoreTextView(String score) {
        TextView textView = new TextView(getContext());
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.unluckyText));
        textView.setText(score);
        textView.setSelected(true);
        textView.setGravity(Gravity.RIGHT);
        textView.setTextSize(12);
        return textView;
    }

    public void setUserScoreGradeData(List<TrainAppraiseAndRemark> data, UserEachTrainingScoreModel userEachTrainingScoreModel) {
        this.mAppraiseAndRemarkList = data;
        this.mUserEachTrainingScoreModel = userEachTrainingScoreModel;
        if (data.size() > mGradeSize) {
            mGradeSize = 5;
        } else {
            mGradeSize = data.size();
        }
        String[] strings = new String[mGradeSize];
        for (int i = 0; i < mGradeSize; i++) {
            strings[i] = data.get(i).getAppraise();
        }

        int[] scoreGrade = new int[mGradeSize + 1];
        for (int i = 0; i < mGradeSize; i++) {
            TrainAppraiseAndRemark trainAppraiseAndRemark = data.get(i);
            if (trainAppraiseAndRemark != null) {
                if (i == 0) {
                    scoreGrade[0] = trainAppraiseAndRemark.getSocreStart();
                    scoreGrade[1] = trainAppraiseAndRemark.getSocreEnd();
                } else {
                    scoreGrade[i + 1] = trainAppraiseAndRemark.getSocreEnd();
                }
            }
        }
        mScoreData = scoreGrade;
        mGradeExPlain = strings;
        init();
    }
}
