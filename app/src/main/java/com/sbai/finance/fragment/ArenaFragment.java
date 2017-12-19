package com.sbai.finance.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.BuildConfig;
import com.sbai.finance.R;
import com.sbai.finance.activity.TestActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.arena.RewardActivity;
import com.sbai.finance.activity.arena.klinebattle.BattleKlineActivity;
import com.sbai.finance.activity.battle.BattleListActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.fund.WalletActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.arena.ArenaActivityAndUserStatus;
import com.sbai.finance.model.arena.ArenaInfo;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.OnTouchAlphaChangeImageView;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/10/24.
 * 竞技场
 */

public class ArenaFragment extends BaseFragment {

    private static final int BREATHE_ANIMATION_DURATION = 1500;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.nameAndIngot)
    TextView mNameAndIngot;
    @BindView(R.id.textArenaKnowledge)
    TextView mTextArenaKnowledge;
    @BindView(R.id.iconArenaKnowledge)
    ImageView mIconArenaKnowledge;
    @BindView(R.id.klineBattle)
    OnTouchAlphaChangeImageView mKlineBattle;
    @BindView(R.id.moneyRewardArena)
    OnTouchAlphaChangeImageView mMoneyRewardArena;
    @BindView(R.id.generalBattleBanner)
    OnTouchAlphaChangeImageView mGeneralBattleBanner;
    Unbinder mBinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arena, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Launcher.with(getActivity(), TestActivity.class).execute();
            }
        });

        if (!BuildConfig.FLAVOR.equalsIgnoreCase("dev")
                || !BuildConfig.FLAVOR.equalsIgnoreCase("alpha")
                || !BuildConfig.FLAVOR.equalsIgnoreCase("alpha")) {
            mTitleBar.setRightViewEnable(false);
            mTitleBar.setRightVisible(false);
        }

        mTitleBar.setRightVisible(true);
        mTitleBar.setRightViewEnable(true);
        mTitleBar.setRightText("活动入口" + "");
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Launcher.with(getActivity(), WebActivity.class)
                        .putExtra(WebActivity.EX_URL, Client.ACTIVITY_URL_GUESS_HAPPY)
                        .putExtra(WebActivity.TITLE_BAR_BACKGROUND, ContextCompat.getColor(getActivity(), R.color.guessKlinePrimary))
                        .putExtra(WebActivity.TITLE_BAR_BACK_ICON, R.drawable.ic_tb_back_white)
                        .putExtra(WebActivity.TITLE_BAR_HAS_BOTTOM_SPLIT_LINE, false)
                        .putExtra(WebActivity.TITLE_BAR_CENTER_TITLE_COLOR, ColorStateList.valueOf(Color.WHITE))
                        .execute();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startBreatheAnimation();
        updateUserStatus();
    }

    private void updateArenaActivityStatus() {
        Client.requestArenaInfo(ArenaActivityAndUserStatus.DEFAULT_ACTIVITY_CODE)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<ArenaInfo>, ArenaInfo>() {
                    @Override
                    protected void onRespSuccessData(ArenaInfo data) {
                        if (!data.isArenaActivityOver()) {
                            Launcher.with(getActivity(), RewardActivity.class).execute();
                        } else {
                            ToastUtil.show("活动未开启");
                        }
                    }
                })
                .fireFree();
    }

    private void updateUserStatus() {
        if (LocalUser.getUser().isLogin()) {
            GlideApp.with(getActivity())
                    .load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .circleCrop()
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(mAvatar);
        } else {
            GlideApp.with(getActivity())
                    .load(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(mAvatar);
            mNameAndIngot.setText(R.string.not_login);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void startBreatheAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0);
        alphaAnimation.setRepeatCount(-1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setDuration(BREATHE_ANIMATION_DURATION);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }


    @OnClick({R.id.textArenaKnowledge, R.id.iconArenaKnowledge, R.id.moneyRewardArena,
            R.id.generalBattleBanner, R.id.nameAndIngot, R.id.avatar, R.id.klineBattle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.textArenaKnowledge:
            case R.id.iconArenaKnowledge:
                umengEventCount(UmengCountEventId.ARENA_KNOWLEDGE);
                Launcher.with(getActivity(), WebActivity.class)
                        .putExtra(WebActivity.EX_URL, Client.ARENA_KNOWLEDGE)
                        .execute();
                break;
            case R.id.klineBattle:
                Launcher.with(getActivity(), BattleKlineActivity.class)
                        .execute();
                break;
            case R.id.moneyRewardArena:
                umengEventCount(UmengCountEventId.ARENA_MRPK);
                updateArenaActivityStatus();
                break;
            case R.id.generalBattleBanner:
                umengEventCount(UmengCountEventId.ARENA_FUTURE_PK);
                Launcher.with(getActivity(), BattleListActivity.class).execute();
                break;
            case R.id.avatar:
            case R.id.nameAndIngot:
                umengEventCount(UmengCountEventId.ARENA_INGOT);
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), WalletActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;

        }
    }

    public void updateIngotNumber(UserFundInfo userFundInfo) {
        long ingot = userFundInfo != null ? userFundInfo.getYuanbao() : 0;
        String name = LocalUser.getUser().getUserInfo() != null ? LocalUser.getUser().getUserInfo().getUserName() : "";
        SpannableString nameAndIngot = StrUtil.mergeTextWithRatioColor(name,
                "\n" + getString(R.string.my_ingot_, ingot)
                , 0.75f,
                ContextCompat.getColor(getActivity(), R.color.yellowAssist));
        mNameAndIngot.setText(nameAndIngot);
    }
}
