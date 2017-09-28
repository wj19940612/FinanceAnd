package com.sbai.finance.activity.evaluation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.activity.RewardGetActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.levelevaluation.EvaluationResult;
import com.sbai.finance.utils.NumberFormatUtils;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.leveltest.ScoreView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EvaluationResultActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.accuracy)
    TextView mAccuracy;
    @BindView(R.id.accuracyHint)
    TextView mAccuracyHint;
    @BindView(R.id.accidence)
    TextView mAccidence;
    @BindView(R.id.primary)
    TextView mPrimary;
    @BindView(R.id.intermediate)
    TextView mIntermediate;
    @BindView(R.id.expert)
    TextView mExpert;
    @BindView(R.id.master)
    TextView mMaster;
    @BindView(R.id.result)
    CardView mResult;
    @BindView(R.id.score)
    ScoreView mScoreView;
    @BindView(R.id.going_train)
    TextView mGoingTrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_result);
        ButterKnife.bind(this);
        translucentStatusBar();
        EvaluationResult data = getIntent().getParcelableExtra(ExtraKeys.HISTORY_TEST_RESULT);
        updateUserResult(data);
    }

    private void updateUserResult(EvaluationResult data) {
        if (data == null) return;
        mScoreView.setData(data);
        setScoreView(data.getLevel());
        mAccuracyHint.setText(getString(R.string.accuracy_ranking,
                NumberFormatUtils.formatPercentStringEndReplaceZero(data.getAllAccuracy(), 2),
                NumberFormatUtils.formatPercentString(data.getPassPercent())));
        mAccuracy.setText(NumberFormatUtils.formatPercentStringEndReplaceZero(data.getAllAccuracy(), 2));
    }

    public void setScoreView(int result) {
        switch (result) {
            case 1:
                mAccidence.setTextColor(ContextCompat.getColor(this, R.color.yellowScoreGrade));
                mAccidence.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_level_accidence_pass, 0, 0);
                break;
            case 2:
                mPrimary.setTextColor(ContextCompat.getColor(this, R.color.yellowScoreGrade));
                mPrimary.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_level_primary_pass, 0, 0);
                break;
            case 3:
                mIntermediate.setTextColor(ContextCompat.getColor(this, R.color.yellowScoreGrade));
                mIntermediate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_level_intermediate_pass, 0, 0);
                break;
            case 4:
                mExpert.setTextColor(ContextCompat.getColor(this, R.color.yellowScoreGrade));
                mExpert.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_level_expert_pass, 0, 0);
                break;
            case 5:
                mMaster.setTextColor(ContextCompat.getColor(this, R.color.yellowScoreGrade));
                mMaster.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_level_master_pass, 0, 0);
                break;
        }
    }

    @OnClick(R.id.going_train)
    public void onViewClicked() {

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.putExtra(ExtraKeys.MAIN_PAGE_CURRENT_ITEM, 0);
        startActivity(intent);

        if (LocalUser.getUser().getUserInfo().isNewUser()) {
            int reward = LocalUser.getUser().getUserInfo().getRegisterRewardIngot();
            RewardGetActivity.show(getActivity(), reward);
            LocalUser.getUser().setNewUser(false);
        }

        finish();
    }
}
