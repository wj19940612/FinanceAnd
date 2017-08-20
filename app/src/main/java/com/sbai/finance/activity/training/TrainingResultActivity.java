package com.sbai.finance.activity.training;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingDetail;
import com.sbai.finance.model.training.TrainingSubmit;
import com.sbai.finance.model.training.TrainingSubmitResult;
import com.sbai.finance.model.training.TrainingTarget;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AnimUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.SecurityUtil;
import com.sbai.finance.view.training.TrainingAchievementView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 训练结果页面
 */
public class TrainingResultActivity extends BaseActivity {

    private static final long ANIM_DURATION = 500;
    private static final long ENTER_OFFSET = (long) (0.4f * ANIM_DURATION);

    public static final String TYPE_TIME = "time";
    public static final String TYPE_FINISH = "finish";
    public static final String TYPE_RATE = "RATE";

    // Activity enter anim duration is 400ms.
    // And onEnterAnimationComplete is not work when user close screen before activity show completely
    private static final long ANIM_DELAY = 410;

    @BindView(R.id.myGrade)
    TextView mMyGrade;
    @BindView(R.id.failedMessage)
    TextView mFailedMessage;

    @BindView(R.id.recordTrainingExperience)
    TextView mRecordTrainingExperience;

    @BindView(R.id.achievement1)
    TrainingAchievementView mAchievement1;
    @BindView(R.id.achievement2)
    TrainingAchievementView mAchievement2;
    @BindView(R.id.achievement3)
    TrainingAchievementView mAchievement3;
    @BindView(R.id.retry)
    TextView mRetry;

    private Training mTraining;
    private TrainingSubmit mTrainingSubmit;
    private TrainingSubmitResult mTrainingSubmitResult;


    private int mConfirmCount;

    public static void show(Activity activity, Training training, int time, boolean isFinish) {
        Launcher.with(activity, TrainingResultActivity.class)
                .putExtra(ExtraKeys.TRAINING, training)
                .putExtra(TYPE_TIME, time)
                .putExtra(TYPE_FINISH, isFinish)
                .execute();

    }

    public static void show(Activity activity, Training training, int time, boolean isFinish, double rate) {
        Launcher.with(activity, TrainingResultActivity.class)
                .putExtra(ExtraKeys.TRAINING, training)
                .putExtra(TYPE_TIME, time)
                .putExtra(TYPE_FINISH, isFinish)
                .putExtra(TYPE_RATE, rate)
                .execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_result);
        ButterKnife.bind(this);

        initData(getIntent());

        submitTrainingResult();
    }

    private void initData(Intent intent) {
        mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);
        mTrainingSubmit = new TrainingSubmit();
        mTrainingSubmit.setTrainId(mTraining.getId());
        mTrainingSubmit.setTime(intent.getIntExtra(TYPE_TIME, -1));
        mTrainingSubmit.setFinish(intent.getBooleanExtra(TYPE_FINISH, false));
        mTrainingSubmit.setRate(intent.getDoubleExtra(TYPE_RATE, 0));
    }

    private void submitTrainingResult() {
        Client.submitTrainingResult(SecurityUtil.AESEncrypt(new Gson().toJson(mTrainingSubmit))).setTag(TAG)
                .setCallback(new Callback<Resp<TrainingSubmitResult>>() {

                    @Override
                    protected void onRespSuccess(Resp<TrainingSubmitResult> resp) {
                        if (resp.isSuccess()) {
                            mTrainingSubmitResult = resp.getData();
                            updateTrainTitle();
                            requestTrainDetail();
                        }
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                    }
                }).fireFree();

    }

    private void updateTrainTitle() {
        if (mTrainingSubmit.isFinish()) {
            mRecordTrainingExperience.setVisibility(View.VISIBLE);
            mRetry.setVisibility(View.GONE);
            mMyGrade.setVisibility(View.VISIBLE);
            mFailedMessage.setVisibility(View.GONE);
            mMyGrade.setText(getString(R.string.train_use_time, mTrainingSubmit.getTime() / 60, mTrainingSubmit.getTime() % 60));
        } else {
            mRecordTrainingExperience.setVisibility(View.GONE);
            mRetry.setVisibility(View.VISIBLE);
            mMyGrade.setVisibility(View.GONE);
            mFailedMessage.setVisibility(View.VISIBLE);
            mFailedMessage.setText(R.string.what_a_pity_you_do_not_finish);
        }
    }

    private void requestTrainDetail() {
        Client.getTrainingDetail(mTraining.getId()).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<TrainingDetail>, TrainingDetail>() {
                    @Override
                    protected void onRespSuccessData(TrainingDetail data) {
                        if (data.getTargets() != null && !data.getTargets().isEmpty() && mTrainingSubmitResult != null) {
                            updateTrainResult(data.getTargets());
                        }
                    }
                }).fire();
    }

    private void updateTrainResult(List<TrainingTarget> trainTargets) {
        if (mTrainingSubmitResult.getType() == TrainingTarget.TYPE_TIME
                || mTrainingSubmitResult.getType() == TrainingTarget.TYPE_FINISH) {
            int size = trainTargets.size();
            if (size > 0) {
                mAchievement1.setContent(getString(R.string.train_finish_less_minutes, trainTargets.get(0).getTime() / 60));
                if (trainTargets.get(0).getTime() >= mTrainingSubmitResult.getTime()) {
                    mAchievement1.setAchieved(true);
                } else {
                    mAchievement1.setAchieved(false);
                }
            }
            if (size > 1) {
                mAchievement2.setContent(getString(R.string.train_finish_less_minutes, trainTargets.get(1).getTime() / 60));
                if (trainTargets.get(1).getTime() >= mTrainingSubmitResult.getTime()) {
                    mAchievement2.setAchieved(true);
                } else {
                    mAchievement2.setAchieved(false);
                }
            }
            if (size > 2) {
                mAchievement3.setContent(getString(R.string.train_finish_less_minutes, trainTargets.get(2).getTime() / 60));
                if (trainTargets.get(2).getTime() >= mTrainingSubmitResult.getTime()) {
                    mAchievement3.setAchieved(true);
                } else {
                    mAchievement3.setAchieved(false);
                }
            }
            showResultsWithAnim(size);
        }
    }

    public void showResultsWithAnim(int count) {
        if (count > 0) {
            mAchievement1.startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION,
                    new AnimUtils.AnimEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mAchievement1.setVisibility(View.VISIBLE);
                        }
                    }));
        }
        if (count > 1) {
            mAchievement2.startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION, ENTER_OFFSET,
                    new AnimUtils.AnimEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mAchievement2.setVisibility(View.VISIBLE);
                        }
                    }));
        }
        if (count > 2) {
            mAchievement3.startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION, ENTER_OFFSET * 2,
                    new AnimUtils.AnimEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mAchievement3.setVisibility(View.VISIBLE);
                        }
                    }));
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_to_bottom);
    }

    @OnClick({R.id.recordTrainingExperience, R.id.retry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.recordTrainingExperience:
                // TODO: 15/08/2017 跳转写心得
                break;
            case R.id.retry:
                Launcher.with(getActivity(), TrainingCountDownActivity.class)
                        .putExtra(ExtraKeys.TRAINING, mTraining)
                        .execute();
                finish();
                break;
        }
    }
}
