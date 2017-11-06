package com.sbai.finance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.fragment.ArenaFragment;
import com.sbai.finance.fragment.HomePageFragment;
import com.sbai.finance.fragment.MineFragment;
import com.sbai.finance.fragment.MissTalkFragment;
import com.sbai.finance.fragment.dialog.system.StartDialogFragment;
import com.sbai.finance.fragment.dialog.system.UpdateVersionDialogFragment;
import com.sbai.finance.model.ActivityModel;
import com.sbai.finance.model.AppVersion;
import com.sbai.finance.model.Banner;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.model.system.ServiceConnectWay;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.OnNoReadNewsListener;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.BottomTabs;
import com.sbai.finance.view.ScrollableViewPager;
import com.sbai.finance.websocket.WsClient;
import com.sbai.finance.websocket.market.MarketSubscriber;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements OnNoReadNewsListener {

    private static final int REQ_CODE_FEEDBACK_LOGIN = 23333;

    @BindView(R.id.viewPager)
    ScrollableViewPager mViewPager;
    @BindView(R.id.bottomTabs)
    BottomTabs mBottomTabs;

    private MainFragmentsAdapter mMainFragmentsAdapter;
    private StartDialogFragment mStartDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
//        translucentStatusBar();
        checkVersion();
        requestServiceConnectWay();
    }

    private void requestStartActivities() {
        if (Preference.get().canShowStartPage()) {
            Preference.get().setTodayFirstOpenAppTime(System.currentTimeMillis());
            Client.getStart().setTag(TAG)
                    .setCallback(new Callback2D<Resp<ActivityModel>, ActivityModel>() {
                        @Override
                        protected void onRespSuccessData(ActivityModel data) {
                            if (data.getLinkType().equalsIgnoreCase(ActivityModel.LINK_TYPE_MODEL)
                                    && LocalUser.getUser().isLogin()) {
                                return;
                            }
                            mStartDialogFragment = StartDialogFragment.newInstance(data);
                            mStartDialogFragment.show(getSupportFragmentManager());
                        }
                    }).fireFree();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int currentItem = intent.getIntExtra(ExtraKeys.MAIN_PAGE_CURRENT_ITEM, 0);
        if (0 <= currentItem && currentItem < mViewPager.getChildCount()) {
            mViewPager.setCurrentItem(currentItem, false);
        }

        boolean ifOpenFeedBackPage = intent.getBooleanExtra(ExtraKeys.PUSH_FEEDBACK, false);
        if (ifOpenFeedBackPage) {
            if (LocalUser.getUser().isLogin()) {
                Launcher.with(getActivity(), FeedbackActivity.class).execute();
            } else {
                Launcher.with(getActivity(), LoginActivity.class).executeForResult(REQ_CODE_FEEDBACK_LOGIN);
            }
        }

        Banner banner = intent.getParcelableExtra(ExtraKeys.ACTIVITY);
        //banner用接口去查询  如果url用数据 则是h5直接打开连接
        if (banner != null) {
            if (TextUtils.isEmpty(banner.getId())) {
                Launcher.with(getActivity(), WebActivity.class)
                        .putExtra(WebActivity.EX_URL, banner.getContent())
                        .execute();
            } else {
                requestPushBannerInfo(banner);
            }
        }
    }

    private void openActivityPage(Banner banner) {
        if (banner.isH5Style()) {
            Launcher.with(getActivity(), WebActivity.class)
                    .putExtra(WebActivity.EX_URL, banner.getContent())
                    .execute();
        } else {
            Launcher.with(getActivity(), WebActivity.class)
                    .putExtra(WebActivity.EX_HTML, banner.getContent())
                    .putExtra(WebActivity.EX_TITLE, banner.getTitle())
                    .execute();
        }
    }

    private void requestPushBannerInfo(Banner banner) {
        Client.requestBannerInfo(banner.getId())
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<Banner>, Banner>() {
                    @Override
                    protected void onRespSuccessData(Banner data) {
                        openActivityPage(data);
                    }
                })
                .fireFree();
    }

    private void requestServiceConnectWay() {
        Client.requestServiceConnectWay()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ServiceConnectWay>, ServiceConnectWay>() {
                    @Override
                    protected void onRespSuccessData(ServiceConnectWay data) {
                        Preference.get().setServiceConnectWay(data);
                    }
                })
                .fireFree();
    }

    private void checkVersion() {
        Client.updateVersion()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<AppVersion>, AppVersion>() {
                    @Override
                    protected void onRespSuccessData(AppVersion data) {
                        if (data.isForceUpdate() || data.isNeedUpdate()) {
                            UpdateVersionDialogFragment.newInstance(data, data.isForceUpdate())
                                    .setOnDismissListener(new UpdateVersionDialogFragment.OnDismissListener() {
                                        @Override
                                        public void onDismiss() {
                                            requestStartActivities();
                                        }
                                    })
                                    .show(getSupportFragmentManager());
                        } else {
                            requestStartActivities();
                        }
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        requestStartActivities();
                    }
                })
                .fireFree();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (LocalUser.getUser().isLogin()) {
            requestUserFund();
        }
    }

    private void requestUserFund() {
        Client.requestUserFundInfo()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserFundInfo>, UserFundInfo>() {
                    @Override
                    protected void onRespSuccessData(UserFundInfo data) {
                        ArenaFragment arenaFragment = (ArenaFragment) mMainFragmentsAdapter.getFragment(1);
                        if (arenaFragment != null) {
                            arenaFragment.updateIngotNumber(data);
                        }

                        MineFragment mineFragment = (MineFragment) mMainFragmentsAdapter.getFragment(3);
                        if (mineFragment != null) {
                            mineFragment.updateIngotNumber(data);
                        }
                    }
                })
                .fireFree();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mStartDialogFragment != null) {
            mStartDialogFragment.dismissAllowingStateLoss();
        }
    }

    @Override
    protected void onDestroy() {
        WsClient.get().close();
        MarketSubscriber.get().disconnect();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_FEEDBACK_LOGIN:
                    Launcher.with(getActivity(), FeedbackActivity.class).execute();
                    break;
            }
        }
    }

    private void initView() {
        mMainFragmentsAdapter = new MainFragmentsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMainFragmentsAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setScrollable(false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mBottomTabs.selectTab(position);
                refreshNotReadMessageCount();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setCurrentItem(0);

        mBottomTabs.setOnTabClickListener(new BottomTabs.OnTabClickListener() {
            @Override
            public void onTabClick(int position) {
                mBottomTabs.selectTab(position);
                mViewPager.setCurrentItem(position, false);
                if (position == 1) {
                    umengEventCount(UmengCountEventId.MISS_TALK_NAVIGATION);
                }
            }
        });
    }

    private void refreshNotReadMessageCount() {
        if (LocalUser.getUser().isLogin()) {
            MineFragment mineFragment = (MineFragment) mMainFragmentsAdapter.getFragment(3);
            if (mineFragment != null) {
                mineFragment.refreshNotReadMessageCount();
            }
        }
    }

    @Override
    public void onNoReadNewsNumber(int count) {
        mBottomTabs.setPointNum(count);
    }

    public boolean isMissTalkFragment() {
        return mViewPager.getCurrentItem() == 1;
    }


    private static class MainFragmentsAdapter extends FragmentPagerAdapter {

        FragmentManager mFragmentManager;

        public MainFragmentsAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HomePageFragment();
                case 1:
                    return new ArenaFragment();
                case 2:
                    return new MissTalkFragment();
                case 3:
                    return new MineFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}