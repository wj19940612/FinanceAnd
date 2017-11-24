package com.sbai.finance.activity.miss;

import android.content.Intent;
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
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.userinfo.ModifyUserInfoActivity;
import com.sbai.finance.fragment.MineFragment;
import com.sbai.finance.fragment.miss.MissProfileQuestionFragment;
import com.sbai.finance.fragment.miss.MissProfileRadioFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.model.miss.Attention;
import com.sbai.finance.model.miss.Miss;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;
import com.sbai.finance.view.slidingTab.SlidingTabTitle;
import com.sbai.glide.GlideApp;
import com.sbai.httplib.ApiError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017\11\22 0022.
 */

public class MissProfileDetailActivity extends BaseActivity implements MissProfileQuestionFragment.OnFragmentRecycleViewScrollListener {
    public static final String CUSTOM_ID = "custom_id";

    public static final int REQ_SUBMIT_QUESTION_LOGIN = 1001;

    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.profileIntroduce)
    TextView mProfileIntroduce;
    @BindView(R.id.fansNumber)
    TextView mFansNumber;
    @BindView(R.id.follow)
    TextView mFollow;
    @BindView(R.id.noFollow)
    TextView mNoFollow;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.toolBar)
    Toolbar mToolBar;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.empty)
    LinearLayout mEmpty;


    private ProfileFragmentAdapter mProfileFragmentAdapter;

    private int mCustomId;
    private boolean mSwipEnabled = true;
    private int mAppBarVerticalOffset = -1;
    private Miss mMiss;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss_profile_detail);
        ButterKnife.bind(this);
        translucentStatusBar();
        initData(getIntent());
        initView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        refreshData();
    }

    private void initData(Intent intent) {
        mCustomId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
    }

    private void initView() {
        setSupportActionBar(mToolBar);

        mAppBarLayout.addOnOffsetChangedListener(mOnOffsetChangedListener);

        mProfileFragmentAdapter = new ProfileFragmentAdapter(getSupportFragmentManager(), mCustomId);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mProfileFragmentAdapter);

        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setPadding((int) Display.dp2Px(45, getResources()), 0, (int) Display.dp2Px(45, getResources()), 0);
        mTabLayout.setSelectedIndicatorPadding((int) Display.dp2Px(48, getResources()));
        mTabLayout.setPadding(Display.dp2Px(10, getResources()));
        mTabLayout.setSelectedIndicatorColors(Color.BLACK);
        mTabLayout.setTabViewTextColor(ContextCompat.getColorStateList(getActivity(), R.color.sliding_tab_text));
        mTabLayout.setTabViewTextSize(15);
        mTabLayout.setSelectedIndicatorHeight(2);
        mTabLayout.setHasBottomBorder(false);
        mTabLayout.setViewPager(mViewPager);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                MissProfileQuestionFragment missProfileQuestionFragment = getMissProfileQuestionFragment();
                if (missProfileQuestionFragment != null) {
                    missProfileQuestionFragment.refresh();
                }

                MissProfileRadioFragment missProfileRadioFragment = getMissProfileRadioFragment();
                if (missProfileRadioFragment != null) {
                    missProfileRadioFragment.refresh();
                }
            }
        });

        initTitleBar();
    }

    private MissProfileQuestionFragment getMissProfileQuestionFragment() {
        return (MissProfileQuestionFragment) mProfileFragmentAdapter.getFragment(0);
    }

    private MissProfileRadioFragment getMissProfileRadioFragment() {
        return (MissProfileRadioFragment) mProfileFragmentAdapter.getFragment(1);
    }

    private void initTitleBar() {
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 分享接口
            }
        });
    }

    private void stopVoice() {
        MissProfileQuestionFragment missProfileQuestionFragment = getMissProfileQuestionFragment();
        if (missProfileQuestionFragment != null) {
            missProfileQuestionFragment.stopVoice();
        }
    }

    public void refreshData() {
        Client.getMissDetail(mCustomId).setTag(TAG)
                .setCallback(new Callback2D<Resp<Miss>, Miss>() {
                    @Override
                    protected void onRespSuccessData(Miss miss) {
                        updateMissDetail(miss);
                        setMiss(miss);
                    }

                    @Override
                    public void onFailure(ApiError apiError) {
                        super.onFailure(apiError);
                        mFansNumber.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }).fire();
    }

    private void updateMissDetail(Miss miss) {
        GlideApp.with(this).load(miss.getPortrait())
                .placeholder(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(mAvatar);

        mName.setText(miss.getName());

        setFansNumber(miss.getTotalAttention());

        if (!TextUtils.isEmpty(miss.getBriefingText())) {
            mProfileIntroduce.setText(miss.getBriefingText());
        } else {
            mProfileIntroduce.setText(R.string.no_miss_introduce);
        }
        if (LocalUser.getUser().getUserInfo() != null && miss.getId() == LocalUser.getUser().getUserInfo().getCustomId()) {
            //是小姐姐自己
            mFollow.setVisibility(View.GONE);
            mNoFollow.setVisibility(View.VISIBLE);
            mNoFollow.setText(R.string.edit_profile);
        } else {
            if (miss.isAttention() == 0) {
                mFollow.setVisibility(View.VISIBLE);
                mNoFollow.setVisibility(View.GONE);
            } else {
                mFollow.setVisibility(View.GONE);
                mNoFollow.setVisibility(View.VISIBLE);
                mNoFollow.setText(R.string.is_attention);
            }
        }
    }

    private void setFansNumber(int totalPrise) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("粉丝");
        stringBuilder.append("  ");
        if (totalPrise > 9999) {
            float newTotalPraise = (float) (totalPrise * 1.0 / 10000);
            stringBuilder.append(newTotalPraise);
            stringBuilder.append("万");
        } else {
            stringBuilder.append(totalPrise);
        }

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.white));
        spannableStringBuilder.setSpan(foregroundColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        ForegroundColorSpan foregroundColorSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.yellowAssist));
        spannableStringBuilder.setSpan(foregroundColorSpan2, 4, stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        mFansNumber.setText(spannableStringBuilder);
    }

    public void setMiss(Miss miss) {
        mMiss = miss;
        MissProfileQuestionFragment missProfileQuestionFragment = getMissProfileQuestionFragment();
        if (missProfileQuestionFragment != null) {
            missProfileQuestionFragment.setMiss(miss);
        }
    }

    public void gotoQuestionDetail(Question item, Question playingItem) {
        if (item != null) {
            if (playingItem != null) {
                Launcher.with(this, QuestionDetailActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, item.getId())
                        .putExtra(ExtraKeys.PLAYING_ID, playingItem.getId())
                        .putExtra(ExtraKeys.PLAYING_URL, playingItem.getAnswerContext())
                        .putExtra(ExtraKeys.PLAYING_AVATAR, playingItem.getCustomPortrait())
                        .executeForResult(REQ_QUESTION_DETAIL);
            } else {
                Launcher.with(this, QuestionDetailActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, item.getId())
                        .executeForResult(REQ_QUESTION_DETAIL);
            }
        }
    }

    public void praiseAdd(int praiseCount) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SUBMIT_QUESTION_LOGIN && resultCode == RESULT_OK) {
            Launcher.with(getActivity(), SubmitQuestionActivity.class)
                    .putExtra(Launcher.EX_PAYLOAD, mCustomId)
                    .execute();
        }
        if (requestCode == REQ_QUESTION_DETAIL && resultCode == RESULT_OK) {
            MissProfileQuestionFragment missProfileQuestionFragment = (MissProfileQuestionFragment) mProfileFragmentAdapter.getFragment(0);
            missProfileQuestionFragment.dealOtherActivityResule(REQ_QUESTION_DETAIL, data);
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

    @Override
    public void onSwipRefreshEnable(boolean enabled, int fragmentPosition) {
        mSwipEnabled = enabled;
        boolean b = enabled && mAppBarVerticalOffset > -1;
        if (mSwipeRefreshLayout.isEnabled() != b) {
            mSwipeRefreshLayout.setEnabled(b);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAppBarLayout.removeOnOffsetChangedListener(mOnOffsetChangedListener);
    }

    @OnClick({R.id.follow, R.id.noFollow})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.follow:
                attention();
                break;
            case R.id.noFollow:
                if (mMiss != null) {
                    if (LocalUser.getUser().getUserInfo() != null && mMiss.getId() == LocalUser.getUser().getUserInfo().getCustomId()) {
                        Launcher.with(getActivity(), ModifyUserInfoActivity.class).execute();
                    } else {
                        attention();
                    }
                } else {
                    ToastUtil.show(getString(R.string.no_miss));
                }
                break;
        }
    }

    private void attention() {
        if (mMiss != null) {
            if (LocalUser.getUser().isLogin()) {
                umengEventCount(UmengCountEventId.MISS_TALK_ATTENTION);

                Client.attention(mMiss.getId()).setCallback(new Callback2D<Resp<Attention>, Attention>() {

                    @Override
                    protected void onRespSuccessData(Attention attention) {
                        if (attention.getIsAttention() == 0) {
                            mFollow.setVisibility(View.VISIBLE);
                            mNoFollow.setVisibility(View.GONE);
                        } else {
                            mFollow.setVisibility(View.GONE);
                            mNoFollow.setVisibility(View.VISIBLE);
                        }
                        setFansNumber(attention.getAttentionCount());
                    }
                }).fire();
            } else {
                stopVoice();
                Launcher.with(getActivity(), LoginActivity.class).execute();
            }
        } else {
            ToastUtil.show(getString(R.string.no_miss));
        }
    }

    static class ProfileFragmentAdapter extends FragmentPagerAdapter {

        private FragmentManager mFragmentManager;
        private int mCustomId;

        public ProfileFragmentAdapter(FragmentManager fm, int customId) {
            super(fm);
            mFragmentManager = fm;
            mCustomId = customId;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MissProfileQuestionFragment.newInstance(mCustomId);
                case 1:
                    return MissProfileRadioFragment.newInstance(mCustomId);
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
                    return "问答";
                case 1:
                    return "电台";
            }
            return super.getPageTitle(position);
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }

}