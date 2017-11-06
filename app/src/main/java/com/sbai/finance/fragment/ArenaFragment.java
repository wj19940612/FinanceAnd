package com.sbai.finance.fragment;

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

import com.sbai.finance.R;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.arena.ArenaActivity;
import com.sbai.finance.activity.battle.BattleListActivity;
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

    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.nameAndIngot)
    TextView mNameAndIngot;
    @BindView(R.id.textArenaKnowledge)
    TextView mTextArenaKnowledge;
    @BindView(R.id.iconArenaKnowledge)
    ImageView mIconArenaKnowledge;
    @BindView(R.id.stairHalo)
    ImageView mStairHalo;
    @BindView(R.id.moneyRewardArena)
    ImageView mMoneyRewardArena;
    @BindView(R.id.generalBattleBanner)
    ImageView mGeneralBattleBanner;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arena, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
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
                            Launcher.with(getActivity(), ArenaActivity.class).execute();
                        } else {
                            ToastUtil.show("活动未开启");
                        }
                    }
                })
                .fireFree();
    }

    private void updateUserStatus() {
        updateUserAvatar();
    }

    private void updateUserAvatar() {
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
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mStairHalo.clearAnimation();
    }

    private void startBreatheAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0);
        alphaAnimation.setRepeatCount(-1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setDuration(BREATHE_ANIMATION_DURATION);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mStairHalo.startAnimation(alphaAnimation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }


    @OnClick({R.id.textArenaKnowledge, R.id.iconArenaKnowledge, R.id.moneyRewardArena,
            R.id.generalBattleBanner, R.id.nameAndIngot})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.textArenaKnowledge:
            case R.id.iconArenaKnowledge:
                Launcher.with(getActivity(), WebActivity.class)
                        .putExtra(WebActivity.EX_URL, Client.ARENA_KNOWLEDGE)
                        .execute();
                break;
            case R.id.moneyRewardArena:
                updateArenaActivityStatus();
                break;
            case R.id.generalBattleBanner:
                Launcher.with(getActivity(), BattleListActivity.class).execute();
                break;
            case R.id.nameAndIngot:
                Launcher.with(getActivity(), WalletActivity.class).execute();
                break;

        }
    }

    public void updateIngotNumber(UserFundInfo userFundInfo) {
        long ingot = userFundInfo != null ? userFundInfo.getYuanbao() : 0;
        SpannableString nameAndIngot = StrUtil.mergeTextWithRatioColor(LocalUser.getUser().getUserInfo().getUserName(),
                "\n" + getString(R.string.my_ingot_, ingot)
                , 0.75f,
                ContextCompat.getColor(getActivity(), R.color.yellowAssist));
        mNameAndIngot.setText(nameAndIngot);
    }
}