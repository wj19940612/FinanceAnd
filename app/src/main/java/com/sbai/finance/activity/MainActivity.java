package com.sbai.finance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.fragment.DiscoveryFragment;
import com.sbai.finance.fragment.MineFragment;
import com.sbai.finance.fragment.MissTalkFragment;
import com.sbai.finance.fragment.TrainingFragment;
import com.sbai.finance.fragment.dialog.system.RegisterInviteDialogFragment;
import com.sbai.finance.fragment.dialog.system.UpdateVersionDialogFragment;
import com.sbai.finance.model.AppVersion;
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

    @BindView(R.id.viewPager)
    ScrollableViewPager mViewPager;
    @BindView(R.id.bottomTabs)
    BottomTabs mBottomTabs;

    private MainFragmentsAdapter mMainFragmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
//        translucentStatusBar();
        checkVersion();
        requestServiceConnectWay();

        if (Preference.get().showRegisterInviteDialog()) {
            RegisterInviteDialogFragment registerInviteDialogFragment = new RegisterInviteDialogFragment();
            registerInviteDialogFragment.show(getSupportFragmentManager());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //从测评结果页打开主页，要特殊处理
        boolean booleanExtra = intent.getBooleanExtra(Launcher.EX_PAYLOAD, false);
        if (booleanExtra) {
            mViewPager.setCurrentItem(0, false);
        }
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
                        if (data.isForceUpdate()) {
                            UpdateVersionDialogFragment.newInstance(data, data.isForceUpdate()).show(getSupportFragmentManager());
                        } else if (data.isNeedUpdate()) {
                            UpdateVersionDialogFragment.newInstance(data, data.isForceUpdate()).show(getSupportFragmentManager());
                        }
                    }
                })
                .fireFree();
    }


    @Override
    protected void onDestroy() {
        WsClient.get().close();
        MarketSubscriber.get().disconnect();
        super.onDestroy();
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


    @Override
    public void onNoReadNewsNumber(int index, int count) {
        mBottomTabs.setPointNum(count);
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
                    return new TrainingFragment();
                case 1:
                    return new MissTalkFragment();
                case 2:
                    return new DiscoveryFragment();
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