package com.sbai.finance.activity.battle;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.fund.VirtualProductExchangeActivity;
import com.sbai.finance.fragment.battle.BattleListFragment;
import com.sbai.finance.fragment.battle.BattleRankingFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.arena.ArenaActivityAwardInfo;
import com.sbai.finance.model.arena.ArenaInfo;
import com.sbai.finance.model.arena.UserActivityScore;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.ShareDialog;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MoneyRewardGameBattleListActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.arenaTitle)
    ImageView mArenaTitle;
    @BindView(R.id.activityTime)
    TextView mActivityTime;
    @BindView(R.id.enterForACompetition)
    AppCompatButton mEnterForACompetition;
    @BindView(R.id.joinGameLL)
    LinearLayout mJoinGameLL;
    @BindView(R.id.predictGain)
    TextView mPredictGain;
    @BindView(R.id.award)
    TextView mAward;
    @BindView(R.id.activityTime2)
    TextView mActivityTime2;
    @BindView(R.id.ranking)
    TextView mRanking;
    @BindView(R.id.myProfit)
    TextView mMyProfit;
    @BindView(R.id.gameCount)
    TextView mGameCount;
    @BindView(R.id.gameInfoRl)
    RelativeLayout mGameInfoRl;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.toolBar)
    Toolbar mToolBar;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.quickMatch)
    TextView mQuickMatch;
    private MoneyRewardGameBattleFragmentAdapter mMoneyRewardGameBattleFragmentAdapter;
    private UserFundInfo mUserFundInfo;
    private TextView mIngot;
    private ArenaInfo mArenaInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_reward_game_battle_list);
        ButterKnife.bind(this);
        translucentStatusBar();

        initView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestGameInfo();
        if (LocalUser.getUser().isLogin()) {
            requestUserJoinArenaActivityStatus();
            requestUserFundInfo();
            requestArenaActivityExchangeAward();
            requestUserNowBattle();
        } else {
            updateUserJoinArenaStatus(false);
        }
    }

    private void requestUserNowBattle() {
        Client.requestUserArenaNowBattle()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Battle>, Battle>() {
                    @Override
                    protected void onRespSuccessData(Battle data) {
                        //当前对战
                    }
                })
                .fireFree();
    }

    private void requestArenaActivityExchangeAward() {
        Client.requestArenaActivityExchangeAward(ArenaInfo.DEFAULT_ACTIVITY_CODE)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<ArenaActivityAwardInfo>, ArenaActivityAwardInfo>() {
                    @Override
                    protected void onRespSuccessData(ArenaActivityAwardInfo data) {
                        mAward.setText(data.getPrizeName());
                    }
                })
                .setTag(TAG)
                .fireFree();
    }

    private void requestUserJoinArenaActivityStatus() {
        Client.requestUserJoinArenaActivityStatus(ArenaInfo.DEFAULT_ACTIVITY_CODE)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                    @Override
                    protected void onRespSuccessData(Boolean data) {
                        updateUserJoinArenaStatus(data);
                    }
                })
                .fireFree();
    }

    private void updateUserJoinArenaStatus(Boolean data) {
        if (data) {
            requestUserActivityScore();
            mJoinGameLL.setVisibility(View.GONE);
            mGameInfoRl.setVisibility(View.VISIBLE);

            mGameCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Launcher.with(getActivity(), BattleRecordResultListActivity.class)
                            .putExtra(ExtraKeys.BATTLE_HISTORY, BattleHisRecordActivity.BATTLE_HISTORY_RECORD_TYPE_ARENA)
                            .execute();
                }
            });
            //快速匹配出现
            // TODO: 2017/10/30 需要确认下
            mQuickMatch.setVisibility(View.VISIBLE);
        } else {
            mJoinGameLL.setVisibility(View.VISIBLE);
            mGameInfoRl.setVisibility(View.GONE);
            mQuickMatch.setVisibility(View.GONE);
        }
    }

    private void requestUserActivityScore() {
        Client.requestUserActivityScore(ArenaInfo.DEFAULT_ACTIVITY_CODE)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<UserActivityScore>, UserActivityScore>() {
                    @Override
                    protected void onRespSuccessData(UserActivityScore data) {
                        updateUserActivityScore(data);
                    }
                })
                .fireFree();
    }


    private void updateUserActivityScore(UserActivityScore data) {
        SpannableString ranking = StrUtil.mergeTextWithRatioColor(String.valueOf(data.getRank()),
                "\n" + getString(R.string.ranking_now),
                0.6f, ContextCompat.getColor(getActivity(), R.color.unluckyText));
        mRanking.setText(ranking);

        SpannableString myProfit = StrUtil.mergeTextWithRatioColor(getString(R.string.my_profit_count, data.getScore()),
                "\n" + getString(R.string.my_profit),
                0.6f, ContextCompat.getColor(getActivity(), R.color.unluckyText));
        mMyProfit.setText(myProfit);

        SpannableString battleCount = StrUtil.mergeTextWithRatioColor(String.valueOf(data.getCount()),
                "\n" + getString(R.string.battle_count),
                0.6f, Color.WHITE);
        mGameCount.setText(battleCount);
    }

    private void requestGameInfo() {
        Client.requestArenaInfo(ArenaInfo.DEFAULT_ACTIVITY_CODE)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ArenaInfo>, ArenaInfo>() {
                    @Override
                    protected void onRespSuccessData(ArenaInfo data) {
                        mArenaInfo = data;
                        updateArenaInfo(data);
                    }

                }).fireFree();
    }

    private void requestUserFundInfo() {
        if (LocalUser.getUser().isLogin()) {
            Client.requestUserFundInfo()
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<UserFundInfo>, UserFundInfo>() {
                        @Override
                        protected void onRespSuccessData(UserFundInfo data) {
                            mUserFundInfo = data;
                            mIngot.setText(getString(R.string.ingot_, StrFormatter.formIngotNumber(data.getYuanbao())));
                        }
                    })
                    .fireFree();
        } else {
            mIngot.setText(R.string.not_login);
        }
    }

    private void updateArenaInfo(ArenaInfo data) {
        if (data == null) return;
        String activityTime = getString(R.string.activity_time,
                DateUtil.format(data.getStartDate(), DateUtil.FORMAT_DATE_ARENA),
                DateUtil.format(data.getEndDate(), DateUtil.FORMAT_DATE_ARENA));
        mActivityTime.setText(activityTime);
        mActivityTime2.setText(activityTime);
    }

    private void initView() {
        setSupportActionBar(mToolBar);
        mMoneyRewardGameBattleFragmentAdapter = new MoneyRewardGameBattleFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMoneyRewardGameBattleFragmentAdapter);
        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setSelectedIndicatorPadding((int) Display.dp2Px(60, getResources()));
        mTabLayout.setPadding(Display.dp2Px(12, getResources()));
        mTabLayout.setSelectedIndicatorColors(Color.WHITE);
        mTabLayout.setCustomTabView(R.layout.layout_arena_tablayout, R.id.tabText);
        mTabLayout.setHasBottomBorder(false);
        mTabLayout.setViewPager(mViewPager);

        initTitleBar();
    }

    private void initTitleBar() {
        View customView = mTitleBar.getCustomView();
        ImageView avatar = (ImageView) customView.findViewById(R.id.avatar);
        mIngot = (TextView) customView.findViewById(R.id.ingot);
        TextView recharge = (TextView) customView.findViewById(R.id.recharge);
        TextView activityRule = (TextView) customView.findViewById(R.id.activityRule);
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        if (LocalUser.getUser().isLogin()) {
            GlideApp.with(this)
                    .load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(avatar);
        }


        recharge.setOnClickListener(this);
        activityRule.setOnClickListener(this);
    }

    public void share() {
        // TODO: 2017/10/26 分享
        ShareDialog.with(getActivity())
                .hasWeiBo(true)
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recharge:
                Launcher.with(getActivity(), VirtualProductExchangeActivity.class)
                        .putExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_INGOT)
                        .putExtra(ExtraKeys.USER_FUND, mUserFundInfo != null ? mUserFundInfo.getMoney() : 0)
                        .execute();
                break;
            case R.id.activityRule:
                Launcher.with(getActivity(), WebActivity.class)
                        .putExtra(WebActivity.EX_URL, Client.ARENA_RULE)
                        .execute();
                break;
        }
    }

    @OnClick({R.id.enterForACompetition, R.id.gameCount, R.id.quickMatch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.enterForACompetition:
                if (LocalUser.getUser().isLogin()) {
                    showEnterForACompetitionConditionDialog();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.gameCount:

                break;
            case R.id.quickMatch:
                matchArena();
                break;
        }
    }

    private void matchArena() {

    }

    private void showEnterForACompetitionConditionDialog() {
//        double entryFees = mArenaInfo != null ? mArenaInfo.getEntryFees() : 0;
        // TODO: 2017/10/30 接口请求
        double entryFees = 0;
        SmartDialog.with(getActivity(), getString(R.string.enter_arena_condition, String.valueOf(entryFees)))
                .setPositive(R.string.to_sign_up, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        enterForACompetition();
                    }
                })
                .setNegative(R.string.me_need_consider, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void enterForACompetition() {
        Client.enterForACompetition(ArenaInfo.DEFAULT_ACTIVITY_CODE)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        requestUserJoinArenaActivityStatus();
                        ToastUtil.show(resp.getMsg());
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        if (failedResp.isInsufficientFund()) {
                            showInsufficientFundDialog();
                        }
                        ToastUtil.show(failedResp.getMsg());
                    }
                })
                .fireFree();
    }

    private void showInsufficientFundDialog() {
        SmartDialog.with(getActivity())
                .setNegative(R.string.immediately_recharge, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Launcher.with(getActivity(), VirtualProductExchangeActivity.class)
                                .putExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_INGOT)
                                .putExtra(ExtraKeys.USER_FUND, mUserFundInfo != null ? mUserFundInfo.getMoney() : 0)
                                .execute();
                    }
                })
                .setPositive(R.string.invite_friend, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        share();
                    }
                }).show();
    }


    static class MoneyRewardGameBattleFragmentAdapter extends FragmentPagerAdapter {

        public MoneyRewardGameBattleFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new BattleListFragment();
                case 1:
                    return new BattleRankingFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "正在对战";
                case 1:
                    return "排行榜";
            }
            return super.getPageTitle(position);
        }
    }
}
