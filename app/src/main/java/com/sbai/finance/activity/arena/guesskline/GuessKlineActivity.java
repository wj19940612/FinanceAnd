package com.sbai.finance.activity.arena.guesskline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.battle.BattleRuleActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.fund.WalletActivity;
import com.sbai.finance.fragment.dialog.system.StartDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.model.klinebattle.GuessKline;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * K线对决选择页面
 */

public class GuessKlineActivity extends BaseActivity {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.fourPkDescribe)
    LinearLayout mFourPkDescribe;
    @BindView(R.id.fourPk)
    RelativeLayout mFourPk;
    @BindView(R.id.onePkDescribe)
    LinearLayout mOnePkDescribe;
    @BindView(R.id.onePk)
    RelativeLayout mOnePk;
    @BindView(R.id.exerciseDescribe)
    LinearLayout mExerciseDescribe;
    @BindView(R.id.exercise)
    RelativeLayout mExercise;
    @BindView(R.id.rank)
    TextView mRank;

    private ImageView mAvatar;
    private TextView mIngot;
    private TextView mRecharge;

    private UserFundInfo mUserFundInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_kline);
        ButterKnife.bind(this);
        initTitleView();
    }

    private void initTitleView() {
        View view = mTitleBar.getCustomView();
        mAvatar = view.findViewById(R.id.avatar);
        mIngot = view.findViewById(R.id.ingot);
        mRecharge = view.findViewById(R.id.recharge);
        TextView myBattleResult = view.findViewById(R.id.myBattleResult);
        mRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umengEventCount(UmengCountEventId.FUTURE_PK_RECHARGE);
                openWalletPage();
            }
        });
        mIngot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWalletPage();
            }
        });
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lookBattleResult();
            }
        });
        myBattleResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umengEventCount(UmengCountEventId.FUTURE_PK_RULE);
                lookBattleResult();
            }
        });
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lookBattleRule();
            }
        });
    }

    private void openWalletPage() {
        if (LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), WalletActivity.class).execute();
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    private void lookBattleRule() {
        Launcher.with(getActivity(), BattleRuleActivity.class).execute();
    }

    private void lookBattleResult() {
        if (LocalUser.getUser().isLogin()) {
            new StartDialogFragment().show(getSupportFragmentManager());
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    private void requestUserFindInfo() {
        Client.requestUserFundInfo()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserFundInfo>, UserFundInfo>() {
                    @Override
                    protected void onRespSuccessData(UserFundInfo data) {
                        updateUserFund(data);
                        mUserFundInfo = data;
                    }
                })
                .fireFree();
    }

    private void updateUserFund(UserFundInfo data) {
        if (data == null) return;
        mIngot.setText(getString(R.string.battle_list_ingot_number, StrFormatter.formIngotNumber(data.getYuanbao())));
    }

    private void updateAvatar() {
        if (LocalUser.getUser().isLogin()) {
            GlideApp.with(getActivity())
                    .load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .placeholder(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(mAvatar);
        } else {
            mAvatar.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (LocalUser.getUser().isLogin()) {
            requestUserFindInfo();
        } else {
            mIngot.setText(R.string.not_login);
        }
        updateAvatar();
    }

    @OnClick({R.id.fourPk, R.id.onePk, R.id.exercise, R.id.rank})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fourPk:
                Launcher.with(getActivity(), GuessKlineDetailActivity.class)
                        .putExtra(ExtraKeys.GUESS_TYPE, GuessKline.TYPE_4V4)
                        .execute();
                break;
            case R.id.onePk:
                Launcher.with(getActivity(), GuessKlineDetailActivity.class)
                        .putExtra(ExtraKeys.GUESS_TYPE, GuessKline.TYPE_1V1)
                        .execute();
                break;
            case R.id.exercise:
                Launcher.with(getActivity(), GuessKlineDetailActivity.class)
                        .putExtra(ExtraKeys.GUESS_TYPE, GuessKline.TYPE_EXERCISE)
                        .execute();
                break;
            case R.id.rank:
                // TODO: 2017-12-13  
                break;
        }
    }
}
