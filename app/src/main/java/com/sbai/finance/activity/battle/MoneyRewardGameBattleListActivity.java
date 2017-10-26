package com.sbai.finance.activity.battle;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.fund.VirtualProductExchangeActivity;
import com.sbai.finance.fragment.battle.BattleListFragment;
import com.sbai.finance.fragment.battle.BattleRankingFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.arena.ArenaInfo;
import com.sbai.finance.model.arena.UserBattleResult;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MoneyRewardGameBattleListActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.arenaTitle)
    TextView mArenaTitle;
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
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private MoneyRewardGameBattleFragmentAdapter mMoneyRewardGameBattleFragmentAdapter;
    private UserFundInfo mUserFundInfo;
    private TextView mIngot;

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
        requestUserArenaInfo();
        requestUserFundInfo();
    }


    private void requestUserArenaInfo() {
        if (LocalUser.getUser().isLogin()) {
            Client.requestUserJoinArenaInfo()
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<UserBattleResult>, UserBattleResult>() {
                        @Override
                        protected void onRespSuccessData(UserBattleResult data) {
                            updateUserBattleResult(data);
                        }

                        @Override
                        public void onFailure(VolleyError volleyError) {
                            super.onFailure(volleyError);
                            UserBattleResult userBattleResult = new UserBattleResult();
                            userBattleResult.setStatus(1);
                            userBattleResult.setRanking(2000);
                            userBattleResult.setBattleCount(3000);
                            userBattleResult.setProfit(8454654.66);
                            updateUserBattleResult(userBattleResult);
                        }
                    })
                    .fireFree();
        } else {
            mJoinGameLL.setVisibility(View.VISIBLE);
            mGameInfoRl.setVisibility(View.GONE);
        }
    }

    private void updateUserBattleResult(UserBattleResult data) {
        if (data.userIsJoinArena()) {
            mJoinGameLL.setVisibility(View.GONE);
            mGameInfoRl.setVisibility(View.VISIBLE);

            SpannableString ranking = StrUtil.mergeTextWithRatioColor(String.valueOf(data.getRanking()),
                    "\n"+getString(R.string.ranking_now),
                    0.8f, ContextCompat.getColor(getActivity(), R.color.split));
            mRanking.setText(ranking);

            SpannableString myProfit = StrUtil.mergeTextWithRatioColor(getString(R.string.my_profit_count, data.getProfit()),
                   "\n"+ getString(R.string.my_profit),
                    0.8f, ContextCompat.getColor(getActivity(), R.color.split));
            mMyProfit.setText(myProfit);

            SpannableString battleCount = StrUtil.mergeTextWithRatioColor(String.valueOf(data.getBattleCount()),
                    "\n"+getString(R.string.battle_count),
                    0.8f, Color.WHITE);
            mGameCount.setText(battleCount);

        } else {
            mJoinGameLL.setVisibility(View.VISIBLE);
            mGameInfoRl.setVisibility(View.GONE);
        }
    }

    private void requestGameInfo() {
        // TODO: 2017/10/25 请求游戏的信息
        Client.requestArenaInfo()
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ArenaInfo>, ArenaInfo>() {
                    @Override
                    protected void onRespSuccessData(ArenaInfo data) {
                        updateArenaInfo(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        // TODO: 2017/10/25 模拟数据
                        ArenaInfo arenaInfo = new ArenaInfo();
                        arenaInfo.setTitle("操 盘 对 战 P K 赛\n奖金过万 等你来战");
                        arenaInfo.setReward("王者荣耀皮肤");
                        arenaInfo.setStartTime(System.currentTimeMillis());
                        arenaInfo.setEndTime(System.currentTimeMillis());
                        updateArenaInfo(arenaInfo);
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
        mArenaTitle.setText(data.getTitle());
        String activityTime = getString(R.string.activity_time,
                DateUtil.format(data.getStartTime(), DateUtil.FORMAT_DATE_ARENA),
                DateUtil.format(data.getEndTime(), DateUtil.FORMAT_DATE_ARENA));
        mActivityTime.setText(activityTime);
        mActivityTime2.setText(activityTime);
        mAward.setText(data.getReward());
    }

    private void initView() {
        mMoneyRewardGameBattleFragmentAdapter = new MoneyRewardGameBattleFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMoneyRewardGameBattleFragmentAdapter);
        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setSelectedIndicatorPadding((int) Display.dp2Px(60, getResources()));
        mTabLayout.setPadding(Display.dp2Px(12, getResources()));
//        mTabLayout.setTabViewTextColor(Color.WHITE);
        mTabLayout.setSelectedIndicatorColors(Color.WHITE);
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
                // TODO: 2017/10/25 分享

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recharge:
                Launcher.with(getActivity(),VirtualProductExchangeActivity.class)
                        .putExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_INGOT)
                        .putExtra(ExtraKeys.USER_FUND, mUserFundInfo != null ? mUserFundInfo.getMoney() : 0)
                        .execute();
                break;
            case R.id.activityRule:
                // TODO: 2017/10/25  h5的规则页面

                break;
        }
    }

    @OnClick({R.id.enterForACompetition, R.id.gameCount})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.enterForACompetition:
                break;
            case R.id.gameCount:
                break;
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
