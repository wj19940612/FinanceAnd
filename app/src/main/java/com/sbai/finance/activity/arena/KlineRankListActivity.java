package com.sbai.finance.activity.arena;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.training.LookBigPictureActivity;
import com.sbai.finance.fragment.battle.KlineMeleeRankFragment;
import com.sbai.finance.fragment.battle.KlineOneByOneRankFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.KlineRank;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.slidingTab.SlidingTabTitle;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017\12\12 0012.
 */

public class KlineRankListActivity extends BaseActivity implements KlineOneByOneRankFragment.OnFragmentRecycleViewScrollListener {
    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.slidingTabLayout)
    SlidingTabTitle mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.winRate)
    TextView mWinRate;
    @BindView(R.id.rankPosition)
    TextView mRankPosition;
    @BindView(R.id.medal)
    TextView mMedal;
    @BindView(R.id.topNumber)
    TextView mTopNumber;
    @BindView(R.id.secondNumber)
    TextView mSecondNumber;
    @BindView(R.id.thirdNumber)
    TextView mThirdNumber;
    @BindView(R.id.header)
    LinearLayout mHeader;
    @BindView(R.id.toolBar)
    Toolbar mToolBar;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.tip)
    TextView mTip;

    public static final int FRAGMENT_ONE = 0;
    public static final int FRAGMENT_MELEE = 1;

    private int mPage;
    private PagerAdapter mPagerAdapter;
    private int mAppBarVerticalOffset = -1;
    private boolean mSwipEnabled = true;
    private int mSelectPosition;
    private KlineRank mKlineRank;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline_rank_list);
        ButterKnife.bind(this);
        translucentStatusBar();
        initView();
        refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAppBarLayout.removeOnOffsetChangedListener(mOnOffsetChangedListener);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void initView() {
        mAppBarLayout.addOnOffsetChangedListener(mOnOffsetChangedListener);
        initViewPager();
        initTabView();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                KlineOneByOneRankFragment oneByOneRankFragment = getOneByOneFragment();
                if (oneByOneRankFragment != null) {
                    oneByOneRankFragment.refresh();
                }

                KlineMeleeRankFragment meleeRankFragment = getMeleeFragment();
                if (meleeRankFragment != null) {
                    meleeRankFragment.refresh();
                }
            }
        });
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(0, false);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void initTabView() {
        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setTabLeftAndRightMargin((int) Display.dp2Px(80, getResources()));
        mTabLayout.setSelectedIndicatorPadding((int) Display.dp2Px(25, getResources()));
        mTabLayout.setSelectedIndicatorHeight(3);
        mTabLayout.setTabViewTextSize(16);
        mTabLayout.setSelectedIndicatorColors(Color.WHITE);
        mTabLayout.setTabViewTextColor(ContextCompat.getColorStateList(getActivity(), R.color.sliding_rank_tab_text));
        mTabLayout.setViewPager(mViewPager);
        if (mPage > 0) {
            mTabLayout.setTabIndex(mPage);
        }
        mTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSelectPosition = position;
                updateHeader();
                if (position == FRAGMENT_ONE) {
                    KlineOneByOneRankFragment oneByOneRankFragment = getOneByOneFragment();
                    if (oneByOneRankFragment != null) {
                        oneByOneRankFragment.initScrollState();
                    }
                } else if (position == FRAGMENT_MELEE) {
                    KlineMeleeRankFragment meleeRankFragment = getMeleeFragment();
                    if (meleeRankFragment != null) {
                        meleeRankFragment.initScrollState();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onSwipRefreshEnable(boolean enabled, int fragmentPosition) {
        mSwipEnabled = enabled;
        boolean b = enabled && mAppBarVerticalOffset > -1;
        if (mSwipeRefreshLayout.isEnabled() != b) {
            mSwipeRefreshLayout.setEnabled(b);
        }
    }

    private KlineOneByOneRankFragment getOneByOneFragment() {
        return (KlineOneByOneRankFragment) mPagerAdapter.getFragment(FRAGMENT_ONE);
    }

    private KlineMeleeRankFragment getMeleeFragment() {
        return (KlineMeleeRankFragment) mPagerAdapter.getFragment(FRAGMENT_MELEE);
    }

    public void refreshData() {

    }

    public void setKlineRank(KlineRank klineRank) {
        mSwipeRefreshLayout.setRefreshing(false);
        mKlineRank = klineRank;
        updateHeader();
    }

    public void updateHeader() {

        if (!LocalUser.getUser().isLogin()) {
            mHeader.setVisibility(View.GONE);
            mTip.setVisibility(View.GONE);
            return;
        }
        mHeader.setVisibility(View.VISIBLE);
        if (mSelectPosition == FRAGMENT_ONE) {
            mWinRate.setVisibility(View.VISIBLE);
            mTopNumber.setVisibility(View.GONE);
            mSecondNumber.setVisibility(View.GONE);
            mThirdNumber.setVisibility(View.GONE);
            mMedal.setVisibility(View.GONE);
            mTip.setText(R.string.battle_have_30);
            if (mKlineRank != null) {
                GlideApp.with(getActivity()).load(mKlineRank.getUserRank1v1().getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop().into(mAvatar);
                mName.setText(mKlineRank.getUserRank1v1().getUserName());
                mRankPosition.setText(mKlineRank.getUserRank1v1().getSort() < 0 ? "未排名" : String.valueOf(mKlineRank.getUserRank1v1().getSort()));
                String rate = "0";
                if (mKlineRank.getUserRank1v1().getRankingRate() != 0)
                    rate = String.valueOf(mKlineRank.getUserRank1v1().getRankingRate() * 100);
                mWinRate.setText("胜率: " + rate + "%");
            }
        } else {
            mWinRate.setVisibility(View.GONE);
            mTopNumber.setVisibility(View.VISIBLE);
            mSecondNumber.setVisibility(View.VISIBLE);
            mThirdNumber.setVisibility(View.VISIBLE);
            mMedal.setVisibility(View.VISIBLE);
            mTip.setText("");
            if (mKlineRank != null) {
                GlideApp.with(getActivity()).load(mKlineRank.getUserRank4v4().getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop().into(mAvatar);
                mName.setText(mKlineRank.getUserRank4v4().getUserName());
                mRankPosition.setText(mKlineRank.getUserRank4v4().getSort() < 0 ? "未排名" : String.valueOf(mKlineRank.getUserRank1v1().getSort()));
                mTopNumber.setText(String.valueOf(mKlineRank.getUserRank4v4().getOne()));
                mSecondNumber.setText(String.valueOf(mKlineRank.getUserRank4v4().getTwo()));
                mThirdNumber.setText(String.valueOf(mKlineRank.getUserRank4v4().getThree()));
            }
        }
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

    @OnClick(R.id.avatar)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.avatar:
                if (mKlineRank != null)
                    Launcher.with(this, LookBigPictureActivity.class).putExtra(Launcher.EX_PAYLOAD, mKlineRank.getUserRank1v1().getUserPortrait())
                            .execute();
                break;
        }
    }

    public static class PagerAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private FragmentManager mFragmentManager;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            mFragmentManager = fm;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.one_by_one_rank);
                case 1:
                    return mContext.getString(R.string.melee_rank);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new KlineOneByOneRankFragment();
                case 1:
                    return new KlineMeleeRankFragment();

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
