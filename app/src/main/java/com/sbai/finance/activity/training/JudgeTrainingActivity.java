package com.sbai.finance.activity.training;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
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
import com.sbai.finance.model.training.TrainingDetail;
import com.sbai.finance.model.training.TrainingSubmit;
import com.sbai.finance.model.training.question.KData;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.training.Kline.MvKlineView;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JudgeTrainingActivity extends BaseActivity {

    @BindView(R.id.topArea)
    LinearLayout mTopArea;
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

    private TrainingDetail mTrainingDetail;
    private Training mTraining;
    private Question<KData> mQuestion;
    private CountDownTimer mCountDownTimer;
    private TrainingSubmit mTrainingSubmit;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_judge_training);
        ButterKnife.bind(this);

        translucentStatusBar();
        addStatusBarHeightTopPadding(mTopArea);

        initData(getIntent());

        Collections.reverse(mQuestion.getContent()); // first is the last data
        mKlineView.setOnAnswerSelectedListener(new MvKlineView.OnAnswerSelectedListener() {
            @Override
            public void onRightAnswerSelected(float accuracy) {
                mAccuracy.setText(FinanceUtil.formatToPercentage(accuracy, 0));
                mTrainingSubmit.setRate(accuracy);
            }

            @Override
            public void onWrongAnswerSelected(String analysis) {
                mAccuracyArea.setVisibility(View.INVISIBLE);
                mKnowledgeArea.setVisibility(View.VISIBLE);
                mKnowledge.setText(analysis);
            }
        });
        mKlineView.setOnFinishListener(new MvKlineView.OnFinishListener() {
            @Override
            public void onFinish() {
                mTrainingSubmit.setFinish(true);
                startTrainingResultPage();
            }
        });
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
                mTrainingSubmit.setTime((int) (pastTime / 1000));
            }

            @Override
            public void onFinish() {
                mTrainingSubmit.setFinish(false);
                startTrainingResultPage();
            }
        }.start();
    }

    private void startTrainingResultPage() {
        Launcher.with(getActivity(), TrainingResultActivity.class)
                .putExtra(ExtraKeys.TRAINING_DETAIL, mTrainingDetail)
                .putExtra(ExtraKeys.TRAINING_SUBMIT, mTrainingSubmit)
                .execute();
        finish();
    }

    private void initData(Intent intent) {
        mTrainingDetail = intent.getParcelableExtra(ExtraKeys.TRAINING_DETAIL);
        mQuestion = intent.getParcelableExtra(ExtraKeys.QUESTION);
        mTraining = mTrainingDetail.getTrain();
        mTrainingSubmit = new TrainingSubmit(mTraining.getId());
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
        float widthScale = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                ? 0.45f : SmartDialog.DEFAULT_SCALE;

        SmartDialog.single(getActivity(), getString(R.string.exit_train_will_not_save_train_record))
                .setTitle(getString(R.string.is_sure_exit_train))
                .setWidthScale(widthScale)
                .setNegative(R.string.exit_train, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setPositive(R.string.continue_train, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
