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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.battle.BattleActivity;
import com.sbai.finance.activity.battle.BattleHisRecordActivity;
import com.sbai.finance.activity.battle.BattleListActivity;
import com.sbai.finance.activity.battle.BattleRecordResultListActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.fund.VirtualProductExchangeActivity;
import com.sbai.finance.activity.mine.fund.WalletActivity;
import com.sbai.finance.activity.mine.userinfo.CreditApproveActivity;
import com.sbai.finance.fragment.battle.BattleListFragment;
import com.sbai.finance.fragment.battle.BattleRankingFragment;
import com.sbai.finance.game.PushCode;
import com.sbai.finance.game.WSMessage;
import com.sbai.finance.game.WSPush;
import com.sbai.finance.game.WsClient;
import com.sbai.finance.game.callback.WSCallback;
import com.sbai.finance.game.cmd.ArenaQuickMatchLauncher;
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
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.OnTouchAlphaChangeImageView;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.ShareDialog;
import com.sbai.finance.view.dialog.StartMatchDialog;
import com.sbai.finance.view.dialog.UserArenaExchangeResultDialog;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;
import com.sbai.glide.GlideApp;
import com.sbai.httplib.ApiError;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Modified by john on 06/12/2017
 * <p>
 * 赏金场（竞技场）列表页面
 */
public class RewardActivity extends BaseActivity implements View.OnClickListener, BattleListFragment.OnFragmentRecycleViewScrollListener {

    private static final int REQ_CODE_FUTURE_BATTLE = 3787;
    private static final int REQ_CODE_SUBMIT_EXCHANGE_AWARD = 8809;

    private static final int VIEWPAGER_BATTLE_LIST_POSITION = 0;

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
    @BindView(R.id.quickMatch)
    OnTouchAlphaChangeImageView mQuickMatch;
    @BindView(R.id.gift)
    ImageView mGift;
    @BindView(R.id.exchangeDetail)
    TextView mExchangeDetail;
    @BindView(R.id.myInfo)
    LinearLayout mMyInfo;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ArenaFragmentAdapter mArenaFragmentAdapter;
    private UserFundInfo mUserFundInfo;
    private TextView mIngot;
    private ArenaApplyRule mArenaApplyRule;

    private ArenaActivityAndUserStatus mArenaActivityAndUserStatus;
    private ImageView mAvatar;
    UserActivityScore mUserActivityScore;
    private Battle mBattle;
    private int mAppBarVerticalOffset = -1;
    private boolean mSwipEnabled = true;
    private int mViperPosition;
    private StringBuilder mRefuseBattleIdSb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        refreshData();
    }

    private void refreshData() {
        requestArenaActivityAndUserStatus();
        if (LocalUser.getUser().isLogin()) {
            requestArenaApplyRule(false);
            requestUserFundInfo();
            requestUserLastBattleInfo(false);
        } else {
            updateUserJoinArenaStatus(false);
            mIngot.setText(R.string.not_login);
        }
        updateUserAvatar();
    }


    private void updateUserAvatar() {
        if (LocalUser.getUser().isLogin()) {
            GlideApp.with(this)
                    .load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(mAvatar);
        } else {
            GlideApp.with(this)
                    .load(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(mAvatar);
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
                        mUserActivityScore = mArenaActivityAndUserStatus.getMyScoreVO();
                        ArenaInfo arenaInfo = mArenaActivityAndUserStatus.getActivityModel();
                        if (arenaInfo != null) {
                            updateArenaInfo(arenaInfo);
                        }
                        if (LocalUser.getUser().isLogin()) {
                            if (mUserActivityScore != null) {
                                updateUserActivityScore(mUserActivityScore);
                            }
                            updateUserJoinArenaStatus(data.isApplyed());
                        }

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                })
                .fireFree();
    }

    private void updateArenaInfo(ArenaInfo data) {
        if (data == null) return;
        String activityTime = getString(R.string.activity_time,
                DateUtil.format(data.getStartDate(), DateUtil.FORMAT_DATE_ARENA),
                DateUtil.format(data.getEndDate(), DateUtil.FORMAT_DATE_ARENA));
        mActivityTime.setText(activityTime);
        mActivityTime2.setText(activityTime);
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
            if (mViperPosition == VIEWPAGER_BATTLE_LIST_POSITION) {
                mQuickMatch.setVisibility(View.VISIBLE);
            }
            updateUserArenaActivityStatus(mArenaActivityAndUserStatus);
        } else {
            mJoinGameLL.setVisibility(View.VISIBLE);
            mGameInfoRl.setVisibility(View.GONE);
            mQuickMatch.setVisibility(View.GONE);
        }
    }

    private void updateUserActivityScore(UserActivityScore data) {
        String rank = data.getRank() == 0 ? "-" : String.valueOf(data.getRank());
        SpannableString ranking = StrUtil.mergeTextWithRatioColor(rank,
                "\n" + getString(R.string.ranking_now),
                0.6f, ContextCompat.getColor(getActivity(), R.color.unluckyText));
        mRanking.setText(ranking);

        String score = "";
        if (data.getScore() == 0) {
            score = "0";
        } else if (data.getScore() > 0) {
            score = getString(R.string.my_profit_count, data.getScore());
        } else {
            score = String.valueOf(data.getScore());
        }
        SpannableString
                myProfit = StrUtil.mergeTextWithRatioColor(score,
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
            mPredictGain.setVisibility(View.INVISIBLE);
            mAward.setTextColor(Color.WHITE);
            if (arenaActivityAndUserStatus.userCanExchangeAward()) {
                if (mUserActivityScore.getTotalCount() < 30) {
                    mAward.setText(R.string.battle_count_is_not_enough);
                } else {
                    mAward.setText(R.string.your_rank_is_not_exchange_reward);
                }
            } else {
                mAward.setText(R.string.ranking_low);
            }
        } else {
            ArenaActivityAwardInfo arenaActivityAwardInfo = arenaActivityAwardInfoList.get(0);
            mPredictGain.setVisibility(View.VISIBLE);
            if (arenaActivityAndUserStatus.userCanExchangeAward()) {
                mPredictGain.setText(R.string.get_award);
                mAward.setSelected(false);
                requestUserExchangeAwardInfo(false);
                if (arenaActivityAwardInfo != null) {
                    mAward.setText(arenaActivityAwardInfo.getPrizeName());
                }
            } else {
                mAward.setSelected(false);
                if (arenaActivityAwardInfo != null) {
                    mAward.setText(arenaActivityAwardInfo.getPrizeName());
                }
                mPredictGain.setText(R.string.predict_gain);
            }
        }
    }

    private void requestUserExchangeAwardInfo(final boolean ifShowResultDialog) {
        Client.requestUserExchangeAwardInfo(ArenaActivityAndUserStatus.DEFAULT_ACTIVITY_CODE)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<UserExchangeAwardInfo>>, List<UserExchangeAwardInfo>>() {
                    @Override
                    protected void onRespSuccessData(List<UserExchangeAwardInfo> data) {
                        updateUserExchangeAwardStatus(data);
                        if (data != null && !data.isEmpty() && ifShowResultDialog) {
                            showUserExchangeResultDialog(data);
                        }
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        if (failedResp.getCode() == Resp.CODE_ARENA_IS_OVER_OR_NOT_IS_EXCHANGE_TIME) {
                            // 未到时间或者已经结束
                            mGift.setVisibility(View.GONE);
                            ToastUtil.show(failedResp.getMsg());
                        }
                    }

                    @Override
                    protected boolean onErrorToast() {
                        return false;
                    }
                })
                .fireFree();
    }

    private void updateUserExchangeAwardStatus(List<UserExchangeAwardInfo> data) {
        //如果用户兑换列表为空，则表示用户还没有提交兑换申请
        if (data.isEmpty()) {
            mExchangeDetail.setVisibility(View.GONE);
            if (mArenaActivityAndUserStatus.userCanExchangeAward()) {
                mGift.setVisibility(View.VISIBLE);
            }
        } else {
            mGift.setVisibility(View.GONE);
            mExchangeDetail.setVisibility(View.VISIBLE);
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


    private void showUserExchangeResultDialog(List<UserExchangeAwardInfo> data) {
        UserExchangeAwardInfo userExchangeAwardInfo = data.get(0);
        if (userExchangeAwardInfo != null) {
            UserArenaExchangeResultDialog.single(getActivity(), userExchangeAwardInfo).show();
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
        Battle battle = (Battle) battleWSPush.getContent().getData();
        if (battle != null && (mBattle == null || mBattle.getId() != battle.getId())) {
            switch (battleWSPush.getContent().getType()) {
                case PushCode.QUICK_MATCH_TIMEOUT:
                case PushCode.QUICK_MATCH_FAILURE:
                    showMatchTimeoutDialog();
                    break;
                case PushCode.QUICK_MATCH_SUCCESS:
                    StartMatchDialog.dismiss(getActivity());
                    SmartDialog.dismiss(getActivity());
                    //防止出现多次推送
                    openBattlePage(battle);
                    mQuickMatch.setImageResource(R.drawable.btn_current_battle);
                    mBattle = battle;
                    break;
            }
        }
    }

    private void openBattlePage(Battle battle) {
        Launcher.with(getActivity(), BattleActivity.class)
                .putExtra(ExtraKeys.BATTLE, battle)
                .executeForResult(REQ_CODE_FUTURE_BATTLE);
    }

    private void requestUserFundInfo() {
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
    }

    private void initView() {
        setSupportActionBar(mToolBar);

        mRefuseBattleIdSb = new StringBuilder();
        mAppBarLayout.addOnOffsetChangedListener(mOnOffsetChangedListener);

        mArenaFragmentAdapter = new ArenaFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mArenaFragmentAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    umengEventCount(UmengCountEventId.MRPK_CURRENT_BATTLE);
                } else {
                    umengEventCount(UmengCountEventId.MRPK_LEARD_BOARD);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setSelectedIndicatorPadding((int) Display.dp2Px(60, getResources()));
        mTabLayout.setPadding(Display.dp2Px(12, getResources()));
        mTabLayout.setSelectedIndicatorColors(Color.WHITE);
        mTabLayout.setCustomTabView(R.layout.layout_arena_tablayout, R.id.tabText);
        mTabLayout.setHasBottomBorder(false);
        mTabLayout.setViewPager(mViewPager);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                BattleListFragment battleListFragment = getBattleListFragment();
                if (battleListFragment != null) {
                    battleListFragment.refresh();
                }

                BattleRankingFragment battleRankingFragment = getBattleRankingFragment();
                if (battleRankingFragment != null) {
                    battleRankingFragment.requestArenaAwardRankingData();
                }
            }
        });

        initTitleBar();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mViperPosition = position;
                if (mArenaActivityAndUserStatus != null && mArenaActivityAndUserStatus.isApplyed()) {
                    if (position == 0) {
                        mQuickMatch.setVisibility(View.VISIBLE);
                    } else {
                        mQuickMatch.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            mAppBarVerticalOffset = verticalOffset;
            boolean b = mSwipEnabled && mAppBarVerticalOffset > -1;
            if (mSwipeRefreshLayout.isEnabled() != b) {
                mSwipeRefreshLayout.setEnabled(b);
            }
        }
    };

    private BattleListFragment getBattleListFragment() {
        return (BattleListFragment) mArenaFragmentAdapter.getFragment(0);
    }

    private BattleRankingFragment getBattleRankingFragment() {
        return (BattleRankingFragment) mArenaFragmentAdapter.getFragment(1);
    }

    private void initTitleBar() {
        View customView = mTitleBar.getCustomView();
        mAvatar = customView.findViewById(R.id.avatar);
        mIngot = customView.findViewById(R.id.ingot);
        TextView recharge = customView.findViewById(R.id.recharge);
        TextView activityRule = customView.findViewById(R.id.activityRule);
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

        mIngot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), WalletActivity.class).execute();
                } else {
                    openLoginPage();
                }
            }
        });

        recharge.setOnClickListener(this);
        activityRule.setOnClickListener(this);
    }

    private void openLoginPage() {
        Launcher.with(getActivity(), LoginActivity.class).execute();
    }

    private void requestArenaShareInfo() {
        Client.requestShareData(Share.SHARE_CODE_ARENA)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<Share>, Share>() {
                    @Override
                    protected void onRespSuccessData(Share data) {
                        share(data);
                    }

                    @Override
                    public void onFailure(ApiError apiError) {
                        super.onFailure(apiError);
                    }
                })
                .fireFree();
    }

    public void share(Share data) {
        ShareDialog.with(getActivity())
                .setTitle(getString(R.string.share_title))
                .setShareTitle(data.getTitle())
                .setShareDescription(data.getContent())
                .setShareUrl(data.getShareLink() + "?inviteCode=" + LocalUser.getUser().getUserInfo().getInviteCode())
                .setShareThumbUrl(data.getShareLeUrl())
                .hasFeedback(false)
                .hasWeiBo(false)
                .setListener(new ShareDialog.OnShareDialogCallback() {
                    @Override
                    public void onSharePlatformClick(ShareDialog.SHARE_PLATFORM platform) {
                        switch (platform) {
                            case WECHAT_FRIEND:
                                umengEventCount(UmengCountEventId.MRPK_INVITE_FRIEND);
                                break;
                            case WECHAT_CIRCLE:
                                umengEventCount(UmengCountEventId.MRPK_INVITE_CIRCLE);
                                break;
                        }
                        Client.share().setTag(TAG).fireFree();
                    }

                    @Override
                    public void onFeedbackClick(View view) {

                    }
                })
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recharge:
                umengEventCount(UmengCountEventId.MRPK_RECHARAGE);
                if (LocalUser.getUser().isLogin()) {
                    openVirtualCurrencyPage();
                } else {
                    openLoginPage();
                }
                break;
            case R.id.activityRule:
                umengEventCount(UmengCountEventId.MRPK_RULE);
                Launcher.with(getActivity(), WebActivity.class)
                        .putExtra(WebActivity.EX_URL, Client.ARENA_RULE)
                        .executeForResult(500);
                break;
        }
    }

    private void openVirtualCurrencyPage() {
        Launcher.with(getActivity(), VirtualProductExchangeActivity.class)
                .putExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_INGOT)
                .putExtra(ExtraKeys.USER_FUND, mUserFundInfo != null ? mUserFundInfo.getMoney() : 0)
                .execute();
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
                umengEventCount(UmengCountEventId.MRPK_MY_RECORD);
                Launcher.with(getActivity(), BattleRecordResultListActivity.class)
                        .putExtra(ExtraKeys.BATTLE_HISTORY, BattleRecordResultListActivity.BATTLE_HISTORY_RECORD_TYPE_ARENA)
                        .execute();
                break;
            case R.id.quickMatch:
                umengEventCount(UmengCountEventId.MRPK_FAST_MATCH);
                requestUserLastBattleInfo(true);
                break;
            case R.id.gift:
                requestUserRealNameStatus();
                break;
            case R.id.exchangeDetail:
                requestUserExchangeAwardInfo(true);
                break;
        }
    }

    private void requestUserLastBattleInfo(final boolean needQuickMatch) {
        Client.requestLastArenaBattleInfo()
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback<Resp<Battle>>() {

                    @Override
                    protected void onRespSuccess(Resp<Battle> resp) {
                        mBattle = resp.getData();
                        if (mBattle != null && mBattle.isBattleStarted()) {
                            BattleListFragment battleListFragment = getBattleListFragment();
                            if (battleListFragment != null) {
                                battleListFragment.setCurrentBattle(mBattle);
                            }
                            mQuickMatch.setImageResource(R.drawable.btn_current_battle);
                            if (needQuickMatch) {
                                openBattlePage(mBattle);
                            }
                        } else {
                            mQuickMatch.setImageResource(R.drawable.btn_battle_matching);
                            if (needQuickMatch) {
                                quickMatchArena(ArenaQuickMatchLauncher.ARENA_MATCH_START);
                            }
                        }
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
                        } else {
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
                        dialog.dismiss();
                        Launcher.with(getActivity(), CreditApproveActivity.class).execute();
                    }
                })
                .setNegative(R.string.i_see)
                .show();
    }

    private void showRealNameIsCheckingDialog() {
        SmartDialog.single(getActivity(), getString(R.string.you_is_real_name_wait_please_wait))
                .setPositiveVisible(View.GONE)
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
                    } else {
                        ToastUtil.show(content.getMsg());
                    }
                }
            }

        }, TAG);
    }

    private void showThereWasNoOpeningHoursDialog(String msg) {
        SmartDialog.single(getActivity(), msg)
                .setNegativeVisible(View.GONE)
                .show();
    }

    private void showMatchFundNotEnoughDialog(String msg) {
        SmartDialog.with(getActivity(), msg)
                .setPositive(R.string.immediately_recharge, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        openVirtualCurrencyPage();
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
        StartMatchDialog.dismiss(RewardActivity.this);
    }

    private void showEnterForACompetitionConditionDialog(ArenaApplyRule data) {
        String entryFees = "";
        switch (data.getMoneyType()) {
            case ArenaApplyRule.PAY_FUND_TYPE_INGOT:
                entryFees = getString(R.string.number_ingot, String.valueOf((int) data.getMoney()));
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
                        requestUserFundInfo();
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        if (failedResp.isInsufficientFund()) {
                            String fundIsEnoughHint = getFundIsEnoughHint();
                            showInsufficientFundDialog(fundIsEnoughHint);
                        } else {
                            ToastUtil.show(failedResp.getMsg());
                        }
                    }

                    @Override
                    protected boolean onErrorToast() {
                        return false;
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
                        umengEventCount(UmengCountEventId.MRPK_RECHARAGE);
                        openVirtualCurrencyPage();
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
    protected void onDestroy() {
        super.onDestroy();
        mAppBarLayout.removeOnOffsetChangedListener(mOnOffsetChangedListener);
        dismissQuickMatchDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_FUTURE_BATTLE) {
            switch (resultCode) {
                case BattleActivity.RESULT_CODE_FIGHT_AGAIN:
                    requestUserLastBattleInfo(true);
                    break;
                case BattleActivity.RESULT_CODE_GO_2_NORMAL_BATTLE:
                    Launcher.with(getActivity(), BattleListActivity.class).execute();
                    finish();
                    break;
            }
        }
        if (requestCode == REQ_CODE_SUBMIT_EXCHANGE_AWARD && resultCode == RESULT_OK) {
            if (LocalUser.getUser().isLogin()) {
                requestArenaActivityAndUserStatus();
            }
        }
    }

    @Override
    public void onSwipRefreshEnable(boolean enabled, int fragmentPosition) {
//        Log.d(TAG, "onSwipRefreshEnable: " + enabled + "  " + mAppBarVerticalOffset);
        mSwipEnabled = enabled;
        boolean b = enabled && mAppBarVerticalOffset > -1;
        if (mSwipeRefreshLayout.isEnabled() != b) {
            mSwipeRefreshLayout.setEnabled(b);
        }
    }

    @Override
    public void onCurrentBattle(Battle battle) {
        if (battle != null) {
            if (battle.isBattleStarted()) {
                mQuickMatch.setImageResource(R.drawable.btn_current_battle);
            } else {
                mQuickMatch.setImageResource(R.drawable.btn_battle_matching);
            }
        }
    }


    static class ArenaFragmentAdapter extends FragmentPagerAdapter {

        private FragmentManager mFragmentManager;

        public ArenaFragmentAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
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

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
