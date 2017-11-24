package com.sbai.finance.view.training;

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
import com.sbai.finance.utils.FinanceUtil;

import java.util.List;


/**
 * Created by ${wangJie} on 2017/8/8.
 */

public class ScoreProgressView extends LinearLayout {
    private static final String TAG = "ScoreProgressView";

    //等级区分数量  默认为5
    private int mGradeSize = 5;
    // 等级评价   "较差", "中等", "良好", "优秀", "极强"
    private String[] mGradeExPlain;
    //分数区间
    private int[] mScoreData = new int[]{0, 200, 400, 600, 800, 1000};

    private List<TrainAppraiseAndRemark> mAppraiseAndRemarkList;
    private UserEachTrainingScoreModel mUserEachTrainingScoreModel;

    //交易水平 超过多少人
    private TextView mTradeGradeTextView;
    //建议
    private TextView mAdviseTextView;


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
        mGradeExPlain = getResources().getStringArray(R.array.credit_grade);

        //上面分数等级
        LinearLayout creditGradeLinearLayout = new LinearLayout(getContext());
        creditGradeLinearLayout.setPadding(0, 0, px2dp(10), 0);
        LayoutParams layoutParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(0, 0, px2dp(6), px2dp(6));


        LinearLayout scoreLinearLayout = new LinearLayout(getContext());
        LayoutParams scoreLayoutParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        scoreLayoutParams.weight = 1;

        for (int i = 0; i < mGradeSize; i++) {
            TextView scoreGradeTextView = createScoreGradeTextView(mGradeExPlain[i]);
            creditGradeLinearLayout.addView(scoreGradeTextView, layoutParams);

            if (i == 0) {
                TextView scoreTextView = createScoreTextView(String.valueOf(mScoreData[0]));
                scoreLinearLayout.addView(scoreTextView);
                TextView scoreTextView1 = createScoreTextView(String.valueOf(mScoreData[1]));
                scoreLinearLayout.addView(scoreTextView1, scoreLayoutParams);
            } else {
                TextView scoreTextView = createScoreTextView(String.valueOf(mScoreData[i + 1]));
                scoreLinearLayout.addView(scoreTextView, scoreLayoutParams);
            }
        }

        addView(creditGradeLinearLayout);
        createCreditAreaView();
        addView(scoreLinearLayout);
        createTradeGradeTextView();
        createAdviseTextView();
        changeScoreArea(creditGradeLinearLayout);

    }

    private void createCreditAreaView() {
        LayoutParams progressLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, px2dp(6));
        progressLayoutParams.setMargins(0, 0,
                px2dp(10), px2dp(6));
        CreditAreaView creditAreaView = new CreditAreaView(getContext());
        if (mUserEachTrainingScoreModel != null &&
                mAppraiseAndRemarkList != null &&
                !mAppraiseAndRemarkList.isEmpty()) {

            TrainAppraiseAndRemark trainAppraiseAndRemark = mAppraiseAndRemarkList.get(mAppraiseAndRemarkList.size() - 1);
            int scoreEnd = trainAppraiseAndRemark.getSocreEnd();
            if (scoreEnd != 0) {
                float value = FinanceUtil.divide(mUserEachTrainingScoreModel.getUserTotalScore(), scoreEnd).floatValue();
                creditAreaView.setPercent(value);
            }
        }
        this.addView(creditAreaView, progressLayoutParams);
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

    private void changeScoreArea(LinearLayout linearLayout) {
        if (mUserEachTrainingScoreModel != null) {
            int userTotalScore = (int) mUserEachTrainingScoreModel.getUserTotalScore();
            for (int i = 0; i < mAppraiseAndRemarkList.size(); i++) {
                TrainAppraiseAndRemark result = mAppraiseAndRemarkList.get(i);
                if (result.getSocreStart() <= userTotalScore &&
                        userTotalScore <= result.getSocreEnd()) {
                    updateGrade(i, linearLayout);
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
                FinanceUtil.downToIntegerPercentage(mUserEachTrainingScoreModel.getRank())));
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
