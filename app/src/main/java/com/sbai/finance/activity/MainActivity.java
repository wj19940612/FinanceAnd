package com.sbai.finance.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.sbai.finance.R;
import com.sbai.finance.fragment.EconomicCircleFragment;
import com.sbai.finance.fragment.HomeFragment;
import com.sbai.finance.fragment.MineFragment;
import com.sbai.finance.fragment.dialog.system.UpdateVersionDialogFragment;
import com.sbai.finance.model.AppVersionModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.netty.Netty;
import com.sbai.finance.utils.AppInfo;
import com.sbai.finance.utils.OnNoReadNewsListener;
import com.sbai.finance.view.BottomTabs;
import com.sbai.finance.websocket.WSClient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements OnNoReadNewsListener {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.bottomTabs)
    BottomTabs mBottomTabs;

    private MainFragmentsAdapter mMainFragmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        checkVersion();

    }

    private void checkVersion() {
        Client.updateVersion()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<AppVersionModel>, AppVersionModel>() {
                    @Override
                    protected void onRespSuccessData(AppVersionModel data) {
                        Log.d(TAG, "onRespSuccessData: " + data.toString());
                        data.setLastVersion("1.0-dev");
                        Log.d(TAG, "onRespSuccessData: " + AppInfo.getVersionName(getApplicationContext()));
                        if (data.isLatestVersion()) {
                            return;
                        }
                        data.setForceUpdateAllPreVersions(0);
                        data.setDownloadUrl("http://ucan.25pp.com/Wandoujia_web_seo_baidu_homepage.apk");
                        UpdateVersionDialogFragment.newInstance(data, data.isForceUpdate()).show(getSupportFragmentManager());
                    }
                })
                .fireFree();
    }

    @Override
    protected void onDestroy() {
        Netty.get().shutdown();
        WSClient.get().close();
        super.onDestroy();
    }

    private void initView() {
        mMainFragmentsAdapter = new MainFragmentsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMainFragmentsAdapter);
        mViewPager.setOffscreenPageLimit(2);
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
                    return new HomeFragment();
                case 1:
                    return new EconomicCircleFragment();
                case 2:
                    return new MineFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}