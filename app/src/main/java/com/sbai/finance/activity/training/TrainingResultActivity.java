package com.sbai.finance.activity.training;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.AnimUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 训练结果页面
 */
public class TrainingResultActivity extends BaseActivity {

    private static final long ANIM_DURATION = 500;
    private static final long ENTER_OFFSET = (long) (0.4f * ANIM_DURATION);

    @BindView(R.id.myGrade)
    TextView mMyGrade;
    @BindView(R.id.failedMessage)
    TextView mFailedMessage;

    @BindView(R.id.card2)
    CardView mCard2;
    @BindView(R.id.card3)
    CardView mCard3;

    @BindView(R.id.recordTrainingExperience)
    TextView mRecordTrainingExperience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_result);
        ButterKnife.bind(this);

    }

    public void showResultsWithAnim() {
//        mCard1.startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION,
//                new AnimUtils.AnimEndListener() {
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        mCard1.setVisibility(View.VISIBLE);
//                    }
//                }));

        mCard2.startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION, ENTER_OFFSET,
                new AnimUtils.AnimEndListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mCard2.setVisibility(View.VISIBLE);
                    }
                }));

        mCard3.startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION, ENTER_OFFSET * 2,
                new AnimUtils.AnimEndListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mCard3.setVisibility(View.VISIBLE);
                    }
                }));
    }

    @OnClick(R.id.recordTrainingExperience)
    public void onViewClicked() {
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        showResultsWithAnim();
    }
}
