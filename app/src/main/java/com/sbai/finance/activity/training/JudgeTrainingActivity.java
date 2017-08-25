package com.sbai.finance.activity.training;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
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
import com.sbai.finance.utils.RenderScriptGaussianBlur;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.dialog.TrainingRuleDialog;
import com.sbai.finance.view.training.Kline.MvKlineView;

import java.util.Collections;
import java.util.List;

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
    @BindView(R.id.bgImg)
    ImageView mBgImg;
    @BindView(R.id.content)
    RelativeLayout mContent;

    private TrainingDetail mTrainingDetail;
    private Training mTraining;
    private Question<KData> mQuestion;
    private CountDownTimer mCountDownTimer;
    private TrainingSubmit mTrainingSubmit;
    private RenderScriptGaussianBlur mRenderScriptGaussianBlur;

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
        mRenderScriptGaussianBlur = new RenderScriptGaussianBlur(this);
        mKlineView.setOnAnswerSelectedListener(new MvKlineView.OnAnswerSelectedListener() {
            @Override
            public void onRightAnswerSelected(float accuracy) {
                mAccuracy.setText(FinanceUtil.formatToPercentage(accuracy, 0));
                mTrainingSubmit.setRate(accuracy);
            }

            @Override
            public void onWrongAnswerSelected(float accuracy, String analysis) {
                mAccuracy.setText(FinanceUtil.formatToPercentage(accuracy, 0));
                mTrainingSubmit.setRate(accuracy);
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
        List<KData> dataList = mQuestion.getContent();
        Collections.reverse(dataList); // first is the last data
        mKlineView.setDataList(dataList);

        Glide.with(this).load(LocalUser.getUser().getUserInfo().getUserPortrait())
                .bitmapTransform(new GlideCircleTransform(getActivity()))
                .placeholder(R.drawable.ic_default_avatar_big)
                .into(mUserPortrait);
        mUsername.setText(LocalUser.getUser().getUserInfo().getUserName());

        mCountDownTimer = new CountDownTimer(mTraining.getTime() * 1000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                long pastTime = mTraining.getTime() * 1000 - millisUntilFinished;
                formatTime(pastTime);
                mTrainingSubmit.setTime((int) (pastTime / 1000));
            }

            @Override
            public void onFinish() {
                mTrainingSubmit.setFinish(false);
                startTrainingResultPage();
            }
        }.start();
    }

    private void formatTime(long pastTime) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            String format = DateUtil.format(pastTime, "mm:ss.SS");
            mTimer.setText(format);
        } else {
            String format = DateUtil.format(pastTime, "mm:ss.SSS");
            String substring = format.substring(0, format.length() - 1);
            mTimer.setText(substring);
        }
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
        mTrainingSubmit.addQuestionId(mQuestion.getId());
    }

    @OnClick({R.id.close, R.id.iSeeBtn, R.id.howToPlay})
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
            case R.id.howToPlay:
                showHowPlayDialog();
                break;
        }
    }

    private void showHowPlayDialog() {
        mBgImg.setVisibility(View.VISIBLE);
        mContent.setDrawingCacheEnabled(true);
        mContent.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        Bitmap bitmap = mContent.getDrawingCache();
        mBgImg.setImageBitmap(mRenderScriptGaussianBlur.gaussianBlur(25, bitmap));
        mContent.setVisibility(View.INVISIBLE);
        TrainingRuleDialog.with(getActivity(), mTraining)
                .setOnDismissListener(new TrainingRuleDialog.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mBgImg.setVisibility(View.GONE);
                        mContent.setVisibility(View.VISIBLE);
                    }
                })
                .show();
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
        showCloseDialog();
    }

    private void showCloseDialog() {
        mBgImg.setVisibility(View.VISIBLE);
        mContent.setDrawingCacheEnabled(true);
        mContent.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        Bitmap bitmap = mContent.getDrawingCache();
        mBgImg.setImageBitmap(mRenderScriptGaussianBlur.gaussianBlur(25, bitmap));
        mContent.setVisibility(View.INVISIBLE);

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
                .setOnDismissListener(new SmartDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Dialog dialog) {
                        dialog.dismiss();
                        mBgImg.setVisibility(View.GONE);
                        mContent.setVisibility(View.VISIBLE);
                    }
                })
                .setPositive(R.string.continue_train)
                .show();
    }
}
