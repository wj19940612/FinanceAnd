package com.sbai.finance.activity.training;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.training.Question;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.question.KData;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.view.training.Kline.MvKlineView;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JudgeTrainingActivity extends BaseActivity {

    @BindView(R.id.close)
    ImageView mClose;
    @BindView(R.id.timer)
    TextView mTimer;

    @BindView(R.id.klineView)
    MvKlineView mKlineView;

    @BindView(R.id.accuracyArea)
    LinearLayout mAccuracyArea;
    @BindView(R.id.userPortrait)
    ImageView mUserPortrait;
    @BindView(R.id.username)
    TextView mUsername;
    @BindView(R.id.accuracy)
    TextView mAccuracy;

    @BindView(R.id.knowledgeArea)
    RelativeLayout mKnowledgeArea;
    @BindView(R.id.knowledge)
    TextView mKnowledge;
    @BindView(R.id.iSeeBtn)
    TextView mISeeBtn;

    private Training mTraining;
    private Question<KData> mQuestion;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_judge_training);
        ButterKnife.bind(this);

        initData(getIntent());

        Collections.reverse(mQuestion.getContent()); // first is the last data
        mKlineView.setOnAnswerSelectedListener(new MvKlineView.OnAnswerSelectedListener() {
            @Override
            public void onRightAnswerSelected(float accuracy) {
                mAccuracy.setText(FinanceUtil.formatToPercentage(accuracy, 0));
            }

            @Override
            public void onWrongAnswerSelected(String analysis) {
                mAccuracyArea.setVisibility(View.INVISIBLE);
                mKnowledgeArea.setVisibility(View.VISIBLE);
                mKnowledge.setText(analysis);
            }
        });
        mKlineView.setDurationTime(mTraining.getTime() * 1000 / 3); // seconds to milliseconds
        mKlineView.setDataList(mQuestion.getContent());

        Glide.with(this).load(LocalUser.getUser().getUserInfo().getUserPortrait())
                .bitmapTransform(new GlideCircleTransform(getActivity()))
                .placeholder(R.drawable.ic_default_avatar_big)
                .into(mUserPortrait);
        mUsername.setText(LocalUser.getUser().getUserInfo().getUserName());

        mCountDownTimer = new CountDownTimer(mTraining.getTime() * 1000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                long pastTime = mTraining.getTime() * 1000 - millisUntilFinished;
                mTimer.setText(DateUtil.format(pastTime, "mm:ss.SS"));
            }

            @Override
            public void onFinish() {
                // TODO: 20/08/2017 跳转结果页面
            }
        }.start();
    }

    private void initData(Intent intent) {
        mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);
        mQuestion = intent.getParcelableExtra(ExtraKeys.QUESTION);
    }


    @OnClick({R.id.close, R.id.iSeeBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close:
                onBackPressed();
                break;
            case R.id.iSeeBtn:
                mAccuracyArea.setVisibility(View.VISIBLE);
                mKnowledgeArea.setVisibility(View.GONE);
                mKlineView.resume();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // TODO: 20/08/2017 退出确认弹框
    }
}
