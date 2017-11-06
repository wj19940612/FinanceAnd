package com.sbai.finance.activity.arena;

import android.app.Dialog;
import android.content.Intent;
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
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.BuildConfig;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.battle.BattleHisRecordActivity;
import com.sbai.finance.activity.battle.BattleRecordResultListActivity;
import com.sbai.finance.activity.battle.FutureBattleActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.fund.VirtualProductExchangeActivity;
import com.sbai.finance.activity.mine.userinfo.CreditApproveActivity;
import com.sbai.finance.fragment.battle.BattleListFragment;
import com.sbai.finance.fragment.battle.BattleRankingFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.arena.ArenaActivityAndUserStatus;
import com.sbai.finance.model.arena.ArenaActivityAwardInfo;
import com.sbai.finance.model.arena.ArenaApplyRule;
import com.sbai.finance.model.arena.ArenaInfo;
import com.sbai.finance.model.arena.UserActivityScore;
import com.sbai.finance.model.arena.UserExchangeAwardInfo;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.model.mine.UserIdentityCardInfo;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.model.system.Share;
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
import com.sbai.finance.view.dialog.StartMatchDialog;
import com.sbai.finance.view.dialog.UserArenaExchangeResultDialog;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;
import com.sbai.finance.websocket.PushCode;
import com.sbai.finance.websocket.WSMessage;
import com.sbai.finance.websocket.WSPush;
import com.sbai.finance.websocket.WsClient;
import com.sbai.finance.websocket.callback.WSCallback;
import com.sbai.finance.websocket.cmd.ArenaQuickMatchLauncher;
import com.sbai.glide.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MoneyRewardGameBattleListActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQ_CODE_FUTURE_BATTLE = 3787;
    private static final int REQ_CODE_SUBMIT_EXCHANGE_AWARD = 8809;

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
    @BindView(R.id.gift)
    ImageView mGift;
    @BindView(R.id.exchangeDetail)
    TextView mExchangeDetail;
    private MoneyRewardGameBattleFragmentAdapter mMoneyRewardGameBattleFragmentAdapter;
    private UserFundInfo mUserFundInfo;
    private TextView mIngot;
    private ArenaApplyRule mArenaApplyRule;

    private ArenaActivityAndUserStatus mArenaActivityAndUserStatus;

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
        if (LocalUser.getUser().isLogin()) {
            requestArenaApplyRule(false);
            requestArenaActivityAndUserStatus();
            requestUserFundInfo();
            requestUserNowBattle();
        } else {
            updateUserJoinArenaStatus(false);
        }
    }

    private void requestArenaActivityAndUserStatus() {
        Client.requestArenaActivityAndUserStatus(ArenaActivityAndUserStatus.DEFAULT_ACTIVITY_CODE)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ArenaActivityAndUserStatus>, ArenaActivityAndUserStatus>() {
                    @Override
                    protected void onRespSuccessData(ArenaActivityAndUserStatus data) {
                        mArenaActivityAndUserStatus = data;
                        updateUserJoinArenaStatus(data.isApplyed());
                        UserActivityScore userActivityScore = mArenaActivityAndUserStatus.getMyScoreVO();
                        ArenaInfo arenaInfo = mArenaActivityAndUserStatus.getActivityModel();
                        if (arenaInfo != null) {
                            updateArenaInfo(arenaInfo);
                        }
                        if (userActivityScore != null) {
                            updateUserActivityScore(userActivityScore);
                        }

                    }
                })
                .fireFree();
    }

    private void updateUserJoinArenaStatus(boolean isJoinActivity) {
        if (isJoinActivity) {
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
            mQuickMatch.setVisibility(View.VISIBLE);
            updateUserArenaActivityStatus(mArenaActivityAndUserStatus);
        } else {
            mJoinGameLL.setVisibility(View.VISIBLE);
            mGameInfoRl.setVisibility(View.GONE);
            mQuickMatch.setVisibility(View.GONE);
        }
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

        SpannableString battleCount = StrUtil.mergeTextWithRatioColor(String.valueOf(data.getTotalCount()),
                "\n" + getString(R.string.battle_count),
                0.6f, Color.WHITE);
        mGameCount.setText(battleCount);
    }

    private void updateUserArenaActivityStatus(ArenaActivityAndUserStatus arenaActivityAndUserStatus) {
        List<ArenaActivityAwardInfo> arenaActivityAwardInfoList = arenaActivityAndUserStatus.getPrizeModels();
        //如果可兑换数据为空 则代表排名低 没有进入排名
        if (arenaActivityAwardInfoList.isEmpty()) {
            mAward.setVisibility(View.GONE);
            mPredictGain.setText(R.string.ranking_low);
        } else {
            mAward.setVisibility(View.VISIBLE);
            ArenaActivityAwardInfo arenaActivityAwardInfo = arenaActivityAwardInfoList.get(0);
            if (arenaActivityAndUserStatus.userCanExchangeAward()) {
                if (arenaActivityAwardInfo != null) {
                    mAward.setText(arenaActivityAwardInfo.getPrizeName());
                }
                mPredictGain.setText(R.string.get_award);
            }
            requestUserExchangeAwardInfo(arenaActivityAwardInfo);
        }
    }

    private void requestUserExchangeAwardInfo(final ArenaActivityAwardInfo arenaActivityAwardInfo) {
        Client.requestUserExchangeAwardInfo(ArenaActivityAndUserStatus.DEFAULT_ACTIVITY_CODE)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<UserExchangeAwardInfo>>, List<UserExchangeAwardInfo>>() {
                    @Override
                    protected void onRespSuccessData(List<UserExchangeAwardInfo> data) {
                        updateUserExchangeAwardStatus(arenaActivityAwardInfo, data);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        if (failedResp.getCode() == Resp.CODE_ARENA_IS_OVER_OR_NOT_IS_EXCHANGE_TIME) {
                            // 未到时间或者已经结束
                            mGift.setVisibility(View.GONE);
                            showOverExchangeTimeDialog();
                        }
                    }
                })
                .fireFree();
    }

    private void showOverExchangeTimeDialog() {
        SmartDialog.single(getActivity(), getString(R.string.you_miss_exchange_time))
                .setPositive(R.string.i_see)
                .setNegativeVisible(View.GONE)
                .show();
    }

    private void showUserExchangeResultDialog(List<UserExchangeAwardInfo> data) {
        UserExchangeAwardInfo userExchangeAwardInfo = data.get(0);
        if (userExchangeAwardInfo != null) {
            UserArenaExchangeResultDialog.single(getActivity(), userExchangeAwardInfo).show();
        }
    }

    private void updateUserExchangeAwardStatus(ArenaActivityAwardInfo arenaActivityAwardInfo, List<UserExchangeAwardInfo> data) {
        //如果用户兑换列表为空，则表示用户还没有提交兑换申请
        if (data.isEmpty()) {
            mExchangeDetail.setVisibility(View.GONE);
            mGift.setVisibility(View.VISIBLE);
            if (arenaActivityAwardInfo != null) {
                mAward.setText(arenaActivityAwardInfo.getPrizeName());
            }
            mPredictGain.setText(R.string.get_award);
        } else {
            mGift.setVisibility(View.GONE);
            mExchangeDetail.setVisibility(View.VISIBLE);
            if (arenaActivityAwardInfo != null) {
                mAward.setText(arenaActivityAwardInfo.getPrizeName());
            }
            mPredictGain.setText(R.string.get_award);

            UserExchangeAwardInfo userExchangeAwardInfo = data.get(0);
            if (userExchangeAwardInfo != null) {
                switch (userExchangeAwardInfo.getStatus()) {
                    case UserExchangeAwardInfo.EXCHANGE_STATUS_APPLY_FOR:
                        mExchangeDetail.setText(R.string.exchange_details);
                        break;
                    case UserExchangeAwardInfo.EXCHANGE_STATUS_SUCCESS:
                        mExchangeDetail.setText(R.string.exchange_success);
                        break;
                    case UserExchangeAwardInfo.EXCHANGE_STATUS_FAIL:
                        mExchangeDetail.setText(R.string.exchange_fail);
                        break;
                }
            }
        }
    }

    /**
     * @param isRequestEnterACompetition 作为是否请求加入比赛的标志
     */
    private void requestArenaApplyRule(final boolean isRequestEnterACompetition) {
        Client.requestArenaApplyRule(ArenaInfo.DEFAULT_ACTIVITY_CODE)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<ArenaApplyRule>, ArenaApplyRule>() {
                    @Override
                    protected void onRespSuccessData(ArenaApplyRule data) {
                        mArenaApplyRule = data;
                        if (isRequestEnterACompetition) {
                            showEnterForACompetitionConditionDialog(data);
                        }
                    }
                })
                .fireFree();
    }


    @Override
    protected void onBattlePushReceived(WSPush<Battle> battleWSPush) {
        super.onBattlePushReceived(battleWSPush);
        switch (battleWSPush.getContent().getType()) {
            case PushCode.QUICK_MATCH_TIMEOUT:
            case PushCode.QUICK_MATCH_FAILURE:
                showMatchTimeoutDialog();
                break;
            case PushCode.QUICK_MATCH_SUCCESS:
                Battle battle = (Battle) battleWSPush.getContent().getData();
                if (battle != null) {
                    Launcher.with(getActivity(), FutureBattleActivity.class)
                            .putExtra(ExtraKeys.BATTLE, battle)
                            .executeForResult(REQ_CODE_FUTURE_BATTLE);
                }
                break;
            case PushCode.BATTLE_OVER:
                if (BuildConfig.DEBUG) {
                    ToastUtil.show("对战结束");
                }
//                requestCurrentBattle();
                break;
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
                if (LocalUser.getUser().isLogin()) {
                    requestArenaShareInfo();
                } else {
                    openLoginPage();
                }
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

    private void openLoginPage() {
        Launcher.with(getActivity(), LoginActivity.class).execute();
    }

    private void requestArenaShareInfo() {
        if (LocalUser.getUser().isLogin()) {
            Client.requestShareData(Share.SHARE_CODE_ARENA)
                    .setIndeterminate(this)
                    .setTag(TAG)
                    .setCallback(new Callback2D<Resp<Share>, Share>() {
                        @Override
                        protected void onRespSuccessData(Share data) {
                            share(data);
                        }

                        @Override
                        public void onFailure(VolleyError volleyError) {
                            super.onFailure(volleyError);
                        }
                    })
                    .fireFree();
        } else {
            openLoginPage();
        }
    }

    public void share(Share data) {
        ShareDialog.with(getActivity())
                .setTitle(R.string.share_to)
                .setShareTitle(data.getTitle())
                .setShareDescription(data.getContent())
                .setShareUrl(data.getShareLink() + "?inviteCode=%s" + LocalUser.getUser().getUserInfo().getInviteCode())
                .setShareThumbUrl(data.getShareLeUrl())
                .hasFeedback(false)
                .hasWeiBo(false)
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
                        .executeForResult(500);
                break;
        }
    }

    @OnClick({R.id.enterForACompetition, R.id.gameCount, R.id.quickMatch, R.id.gift, R.id.exchangeDetail})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.enterForACompetition:
                if (LocalUser.getUser().isLogin()) {
                    requestArenaApplyRule(true);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.gameCount:
                Launcher.with(getActivity(), BattleRecordResultListActivity.class)
                        .putExtra(ExtraKeys.BATTLE_HISTORY, BattleRecordResultListActivity.BATTLE_HISTORY_RECORD_TYPE_ARENA)
                        .execute();
                break;
            case R.id.quickMatch:
                requestUserLastBattleInfo();
                break;
            case R.id.gift:
                requestUserRealNameStatus();
                break;
            case R.id.exchangeDetail:
                requestUserExchangeDetail();
                break;
        }
    }

    private void requestUserExchangeDetail() {
        Client.requestUserExchangeAwardInfo(ArenaActivityAndUserStatus.DEFAULT_ACTIVITY_CODE)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<UserExchangeAwardInfo>>, List<UserExchangeAwardInfo>>() {
                    @Override
                    protected void onRespSuccessData(List<UserExchangeAwardInfo> data) {
                        if (data != null && !data.isEmpty()) {
                            showUserExchangeResultDialog(data);
                        }
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        if (failedResp.getCode() == Resp.CODE_ARENA_IS_OVER_OR_NOT_IS_EXCHANGE_TIME) {
                            // 未到时间或者已经结束
                            mGift.setVisibility(View.GONE);
                            showOverExchangeTimeDialog();
                        }
                    }
                })
                .fireFree();
    }

    private void requestUserLastBattleInfo() {
        Client.requestLastArenaBattleInfo()
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<Battle>, Battle>() {
                    @Override
                    protected void onRespSuccessData(Battle battle) {
                        if (battle.isBattleStarted()) {
                            Launcher.with(getActivity(), FutureBattleActivity.class)
                                    .putExtra(ExtraKeys.BATTLE, battle)
                                    .executeForResult(REQ_CODE_FUTURE_BATTLE);
                        } else {
                            quickMatchArena(ArenaQuickMatchLauncher.ARENA_MATCH_START);
                        }
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        quickMatchArena(ArenaQuickMatchLauncher.ARENA_MATCH_START);
                    }
                })
                .fireFree();
    }

    private void requestUserCanExchangeAward() {
        Client.requestArenaActivityExchangeAward(ArenaActivityAndUserStatus.DEFAULT_ACTIVITY_CODE)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<ArenaActivityAwardInfo>>, List<ArenaActivityAwardInfo>>() {
                    @Override
                    protected void onRespSuccessData(List<ArenaActivityAwardInfo> data) {
                        if (!data.isEmpty()) {
                            ArenaActivityAwardInfo arenaActivityAwardInfo = data.get(0);
                            if (arenaActivityAwardInfo.awardIsVirtual()) {
                                showExchangeVirtualProductInputInfoDialog(arenaActivityAwardInfo);
                            } else {
                                showExchangeEntityAwardDialog(arenaActivityAwardInfo);
                            }
                        }
                    }
                })
                .fireFree();
    }

    private void showExchangeVirtualProductInputInfoDialog(ArenaActivityAwardInfo arenaActivityAwardInfo) {
        Launcher.with(getActivity(), ArenaVirtualAwardExchangeActivity.class)
                .putExtra(ExtraKeys.ARENA_EXCHANGE_AWARD_ID, arenaActivityAwardInfo.getId())
                .executeForResult(REQ_CODE_SUBMIT_EXCHANGE_AWARD);
    }

    private void showExchangeEntityAwardDialog(ArenaActivityAwardInfo arenaActivityAwardInfo) {
        Launcher.with(getActivity(), ArenaEntityExchangeInfoInputActivity.class)
                .putExtra(ExtraKeys.ARENA_EXCHANGE_AWARD_ID, arenaActivityAwardInfo.getId())
                .executeForResult(REQ_CODE_SUBMIT_EXCHANGE_AWARD);
    }

    private void requestUserRealNameStatus() {
        Client.getUserCreditApproveStatus()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<UserIdentityCardInfo>>() {

                    @Override
                    protected void onRespSuccess(Resp<UserIdentityCardInfo> resp) {
                        if (resp.getData() != null) {
                            updateUserCreditStatus(resp.getData().getStatus());
                        }else {
                            showIsNotRealNameDialog();
                        }
                    }
                })
                .fireFree();
    }

    private void updateUserCreditStatus(Integer status) {
        switch (status) {
            case UserInfo.CREDIT_IS_ALREADY_APPROVE:
                requestUserCanExchangeAward();
                break;
            case UserInfo.CREDIT_IS_APPROVE_ING:
                showRealNameIsCheckingDialog();
                break;
            default:
                showIsNotRealNameDialog();
                break;
        }
    }

    private void showIsNotRealNameDialog() {
        SmartDialog.single(getActivity(), getString(R.string.you_not_real_name_not_can_exchange_ward))
                .setPositive(R.string.go_approve, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        Launcher.with(getActivity(), CreditApproveActivity.class).execute();
                        finish();
                    }
                })
                .setNegative(R.string.i_see)
                .show();
    }

    private void showRealNameIsCheckingDialog() {
        SmartDialog.single(getActivity(), getString(R.string.you_is_real_name_wait_please_wait))
                .setPositiveVisable(View.GONE)
                .setNegative(R.string.i_see)
                .show();
    }

    public void quickMatchArena(final int matchType) {
        WsClient.get().send(new ArenaQuickMatchLauncher(matchType), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {
                Resp content = respWSMessage.getContent();
                if (content != null) {
                    if (content.getCode() == Resp.SUCCESS) {
                        if (matchType == ArenaQuickMatchLauncher.ARENA_MATCH_START) {
                            showMatchDialog();
                        }

                        if (matchType == ArenaQuickMatchLauncher.ARENA_MATCH_CANCEL) {
                            SmartDialog.dismiss(getActivity());
                        }
                    } else if (content.getCode() == Resp.CODE_EXCHANGE_FUND_IS_NOT_ENOUGH) {
                        showMatchFundNotEnoughDialog(content.getMsg());
                    } else if (content.getCode() == Resp.ACTIVITY_IS_NOT_YET_OPEN) {
                        showThereWasNoOpeningHoursDialog(content.getMsg());
                    }
                }
            }

        }, TAG);
    }

    private void showThereWasNoOpeningHoursDialog(String msg) {
        SmartDialog.single(getActivity(), msg)
                .show();
    }

    private void showMatchFundNotEnoughDialog(String msg) {
        SmartDialog.with(getActivity(), msg)
                .setPositive(R.string.immediately_recharge, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Launcher.with(getActivity(), VirtualProductExchangeActivity.class)
                                .putExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_INGOT)
                                .putExtra(ExtraKeys.USER_FUND, mUserFundInfo != null ? mUserFundInfo.getMoney() : 0)
                                .execute();
                    }
                })
                .setDialogDeleteShow(true)
                .setNegative(R.string.invite_friend, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestArenaShareInfo();
                    }
                }).show();
    }


    private void showMatchDialog() {
        StartMatchDialog.get(this, new StartMatchDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                StartMatchDialog.dismiss(getActivity());
                showCancelMatchDialog();
            }
        });
    }

    private void showCancelMatchDialog() {
        SmartDialog.single(getActivity(), getString(R.string.cancel_tip))
                .setTitle(getString(R.string.cancel_matching))
                .setPositive(R.string.no_waiting, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        quickMatchArena(ArenaQuickMatchLauncher.ARENA_MATCH_CANCEL);
                    }
                })
                .setNegative(R.string.continue_match, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        showMatchDialog();
                    }
                })
                .setGravity(Gravity.CENTER)
                .show();
    }

    private void showMatchTimeoutDialog() {
        dismissQuickMatchDialog();

        SmartDialog.single(getActivity(), getString(R.string.match_overtime))
                .setTitle(getString(R.string.match_failed))
                .setCancelableOnTouchOutside(false)
                .setPositive(R.string.rematch, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
//                        requestQuickMatch();
                        quickMatchArena(ArenaQuickMatchLauncher.ARENA_MATCH_START);
                    }
                })
                .setNegative(R.string.later_try_again, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void dismissQuickMatchDialog() {
        StartMatchDialog.dismiss(MoneyRewardGameBattleListActivity.this);
    }

    private void showEnterForACompetitionConditionDialog(ArenaApplyRule data) {
        String entryFees = "";
        switch (data.getMoneyType()) {
            case ArenaApplyRule.PAY_FUND_TYPE_INGOT:
                entryFees = getString(R.string.number_ingot, String.valueOf((int) data.getMoney()));
                break;
            case ArenaApplyRule.PAY_FUND_TYPE_CREDIT:
                entryFees = getString(R.string.number_score, String.valueOf(data.getMoney()));
                break;
            case ArenaApplyRule.PAY_FUND_TYPE_CRASH:
                entryFees = getString(R.string.RMB, String.valueOf(data.getMoney()));
                break;
        }

        SmartDialog.with(getActivity(), getString(R.string.enter_arena_condition, entryFees))
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
                        requestArenaActivityAndUserStatus();
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        if (failedResp.isInsufficientFund()) {
                            String fundIsEnoughHint = getFundIsEnoughHint();
                            showInsufficientFundDialog(fundIsEnoughHint);
                        }
                        ToastUtil.show(failedResp.getMsg());
                    }
                })
                .fireFree();
    }

    private String getFundIsEnoughHint() {
        if (mArenaApplyRule != null) {
            switch (mArenaApplyRule.getMoneyType()) {
                case ArenaApplyRule.PAY_FUND_TYPE_INGOT:
                    return getString(R.string.ingot_is_not_enough,
                            getString(R.string.ingot),
                            String.valueOf((int) mArenaApplyRule.getMoney() + "个"));

                case ArenaApplyRule.PAY_FUND_TYPE_CREDIT:
                    return getString(R.string.ingot_is_not_enough,
                            getString(R.string.integral),
                            String.valueOf((int) mArenaApplyRule.getMoney()));

                case ArenaApplyRule.PAY_FUND_TYPE_CRASH:
                    return getString(R.string.ingot_is_not_enough,
                            getString(R.string.cash),
                            getString(R.string.RMB,
                                    String.valueOf(mArenaApplyRule.getMoney())));
            }
        }
        return "";
    }

    private void showInsufficientFundDialog(String msg) {
        SmartDialog.with(getActivity(), msg)
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
                .setDialogDeleteShow(true)
                .setPositive(R.string.invite_friend, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestArenaShareInfo();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_FUTURE_BATTLE && resultCode == RESULT_OK) {
            requestUserLastBattleInfo();
        }
        if (requestCode == REQ_CODE_SUBMIT_EXCHANGE_AWARD && resultCode == RESULT_OK) {
            if (LocalUser.getUser().isLogin()) {
                requestArenaActivityAndUserStatus();
            }
        }
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
