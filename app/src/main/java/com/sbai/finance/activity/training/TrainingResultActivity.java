package com.sbai.finance.activity.training;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.AnimUtils;
import com.sbai.finance.view.training.TrainingAchievementView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 训练结果页面
 */
public class TrainingResultActivity extends BaseActivity {

    private static final long ANIM_DURATION = 500;
    private static final long ENTER_OFFSET = (long) (0.4f * ANIM_DURATION);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_result);
        ButterKnife.bind(this);

        mAchievement1.postDelayed(new Runnable() {
            @Override
            public void run() {
                showAchievementViews();
            }
        }, ANIM_DELAY);
    }

    private void showAchievementViews() {
        // TODO: 15/08/2017 init data
        showResultsWithAnim();
    }

    public void showResultsWithAnim() {
        mAchievement1.startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION,
                new AnimUtils.AnimEndListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mAchievement1.setVisibility(View.VISIBLE);
                    }
                }));

        mAchievement2.startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION, ENTER_OFFSET,
                new AnimUtils.AnimEndListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mAchievement2.setVisibility(View.VISIBLE);
                    }
                }));

        mAchievement3.startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION, ENTER_OFFSET * 2,
                new AnimUtils.AnimEndListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mAchievement3.setVisibility(View.VISIBLE);
                    }
                }));
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
                break;
        }
    }
}
