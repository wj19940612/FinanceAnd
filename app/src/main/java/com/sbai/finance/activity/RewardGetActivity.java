package com.sbai.finance.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RewardGetActivity extends BaseActivity {

    private static final String EX_REWARD = "reward";
    @BindView(R.id.reward)
    TextView mReward;

    public static void show(Activity activity, int reward) {
        Launcher.with(activity, RewardGetActivity.class)
                .putExtra(EX_REWARD, reward).execute();
    }

    private int mRewardValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_get);
        ButterKnife.bind(this);

        initData(getIntent());

        String rewardStr = mRewardValue >= 0 ? "+" + mRewardValue : String.valueOf(mRewardValue);
        mReward.setText(rewardStr + getString(R.string.ingot));
    }

    private void initData(Intent intent) {
        mRewardValue = intent.getIntExtra(EX_REWARD, 0);
    }
}
