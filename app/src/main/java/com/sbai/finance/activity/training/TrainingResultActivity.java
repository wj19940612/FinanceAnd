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
import com.sbai.finance.model.training.TrainingResult;
import com.sbai.finance.model.training.TrainingSubmit;
import com.sbai.finance.model.training.TrainingTarget;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AnimUtils;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.SecurityUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.training.TrainingAchievementView;
import com.sbai.httplib.BuildConfig;

import java.util.Collections;
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

    TrainingAchievementView[] mAchievementViews;

    @BindView(R.id.retry)
    TextView mRetry;

    private Training mTraining;
    private TrainingDetail mTrainingDetail;
    private TrainingSubmit mTrainingSubmit;
    private int mCount;

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

        mAchievementViews = new TrainingAchievementView[3];
        mAchievementViews[0] = (TrainingAchievementView) findViewById(R.id.achievement0);
        mAchievementViews[1] = (TrainingAchievementView) findViewById(R.id.achievement1);
        mAchievementViews[2] = (TrainingAchievementView) findViewById(R.id.achievement2);

        initData(getIntent());

        initView();

        submitTrainingResult();
    }

    private void initView() {
        if (mTrainingSubmit.isFinish()) {
            mRecordTrainingExperience.setVisibility(View.VISIBLE);
            mRetry.setVisibility(View.GONE);
            mMyGrade.setVisibility(View.VISIBLE);
            mFailedMessage.setVisibility(View.GONE);
            mMyGrade.setText(formatTime(mTrainingSubmit.getTime()));
        } else {
            mRecordTrainingExperience.setVisibility(View.GONE);
            mRetry.setVisibility(View.VISIBLE);
            mMyGrade.setVisibility(View.GONE);
            mFailedMessage.setVisibility(View.VISIBLE);
            mFailedMessage.setText(R.string.what_a_pity_you_do_not_finish);
        }

        updateTrainingResultDetail(mTrainingDetail.getTargets());
    }

    private void initData(Intent intent) {
        mTrainingDetail = intent.getParcelableExtra(ExtraKeys.TRAINING_DETAIL);
        mTrainingSubmit = intent.getParcelableExtra(ExtraKeys.TRAINING_SUBMIT);
        mTraining = mTrainingDetail.getTrain();
    }

    private void submitTrainingResult() {
        Client.submitTrainingResult(SecurityUtil.AESEncrypt(new Gson().toJson(mTrainingSubmit))).setTag(TAG)
                .setCallback(new Callback<Resp<TrainingResult>>() {

                    @Override
                    protected void onRespSuccess(Resp<TrainingResult> resp) {
                        if (BuildConfig.DEBUG) {
                            ToastUtil.show(resp.getData().toString());
                        }
                        //mCount = resp.getData().getLevel();
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                    }
                }).fireFree();

    }

    private void updateTrainingResultDetail(List<TrainingTarget> trainTargets) {
        Collections.sort(trainTargets);
        if (!trainTargets.isEmpty()) {
            int targetType = trainTargets.get(0).getType();
            switch (targetType) {
                case TrainingTarget.TYPE_FINISH:
                    mAchievementViews[0].setAchieved(mTrainingSubmit.isFinish());
                    mAchievementViews[0].setContent(R.string.mission_complete);
                    showResultsWithAnim(1);
                    break;
                case TrainingTarget.TYPE_RATE:
                    for (int i = 0; i < mAchievementViews.length; i++) {
                        mAchievementViews[i].setContent(getString(R.string.accuracy_to_,
                                FinanceUtil.formatToPercentage(trainTargets.get(i).getRate(), 0)));
                        if (mTrainingSubmit.getRate() >= trainTargets.get(i).getRate()) {
                            mAchievementViews[i].setAchieved(true);
                        } else {
                            mAchievementViews[i].setAchieved(false);
                        }
                    }
                    showResultsWithAnim(trainTargets.size());
                    break;
                case TrainingTarget.TYPE_TIME:
                    for (int i = 0; i < mAchievementViews.length; i++) {
                        if (trainTargets.get(i).getTime() % 60 == 0) {
                            mAchievementViews[i].setContent(getString(R.string.train_finish_time, formatTime(trainTargets.get(i).getTime())));
                        } else {
                            mAchievementViews[i].setContent(getString(R.string.train_finish_time_with_second, formatTime(trainTargets.get(i).getTime())));
                        }
                        if (mTrainingSubmit.getTime() > trainTargets.get(i).getTime()) {
                            mAchievementViews[i].setAchieved(false);
                        } else {
                            mAchievementViews[i].setAchieved(true);
                        }
                    }
                    showResultsWithAnim(trainTargets.size());
                    break;
            }
        }
    }

    public void showResultsWithAnim(int count) {
        if (count > 0) {
            mAchievementViews[0].startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION,
                    new AnimUtils.AnimEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mAchievementViews[0].setVisibility(View.VISIBLE);
                        }
                    }));
        }
        if (count > 1) {
            mAchievementViews[1].startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION, ENTER_OFFSET,
                    new AnimUtils.AnimEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mAchievementViews[1].setVisibility(View.VISIBLE);
                        }
                    }));
        }
        if (count > 2) {
            mAchievementViews[2].startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION, ENTER_OFFSET * 2,
                    new AnimUtils.AnimEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mAchievementViews[2].setVisibility(View.VISIBLE);
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
                Launcher.with(getActivity(), WriteExperienceActivity.class)
                        .putExtra(ExtraKeys.TRAINING, mTraining)
                        //.putExtra(ExtraKeys.TRAIN_LEVEL, mCount)
                        .execute();
                finish();
                break;
            case R.id.retry:
                Launcher.with(getActivity(), TrainingCountDownActivity.class)
                        .putExtra(ExtraKeys.TRAINING_DETAIL, mTrainingDetail)
                        .execute();
                finish();
                break;
        }
    }

    private String formatTime(int time) {
        int minute = time / 60;
        int seconds = time % 60;
        if (minute == 0) {
            return getString(R.string._seconds, seconds);
        }
        if (seconds == 0) {
            return getString(R.string._minutes, minute);
        }
        return getString(R.string._minutes_complete, minute, seconds);
    }
}
