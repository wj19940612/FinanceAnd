package com.sbai.finance.activity.battle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.battle.ChooseFuturesFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChooseFuturesActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, ChooseFuturesFragment.OnFutureBattleVarietyChooseListener {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.tabLayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private FuturePagesAdapter mFuturePagesAdapter;
    private int pagePosition;
    private String mContractsCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_futures);
        initData(getIntent());
        ButterKnife.bind(this);
        initView();
    }

    private void initData(Intent intent) {
        mContractsCode = intent.getStringExtra(Launcher.EX_PAYLOAD);
    }

    private void initView() {
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(0, false);
        mFuturePagesAdapter = new FuturePagesAdapter(getSupportFragmentManager(), getActivity(), mContractsCode);
        mViewPager.setAdapter(mFuturePagesAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setSelectedIndicatorPadding(Display.dp2Px(60, getResources()));
        mTabLayout.setPadding(Display.dp2Px(13, getResources()));
        mTabLayout.setViewPager(mViewPager);
        mTitleBar.setOnTitleBarClickListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        pagePosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        Fragment fragment = mFuturePagesAdapter.getFragment(pagePosition);
        if (fragment instanceof ChooseFuturesFragment) {
            ((ChooseFuturesFragment) fragment).scrollToTop();
        }
    }

    @Override
    public void onFutureBattleVarietyChoose(Variety variety) {
        Intent intent = new Intent();

        //代表来自期货对战列表页，初次创建
        if (TextUtils.isEmpty(mContractsCode)) {
            intent.putExtra(ExtraKeys.VARIETY, variety);
            intent.setClass(getActivity(), CreateBattleActivity.class);
            startActivity(intent);
        } else {
            intent.putExtra(ExtraKeys.VARIETY, variety);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    static class FuturePagesAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private FragmentManager mFragmentManager;
        private String mContractsCode;

        private FuturePagesAdapter(FragmentManager fm, Context context, String contractsCode) {
            super(fm);
            mContext = context;
            mFragmentManager = fm;
            mContractsCode = contractsCode;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.future_foreign);
                case 1:
                    return mContext.getString(R.string.future_china);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ChooseFuturesFragment.newInstance(Variety.FUTURE_FOREIGN, mContractsCode);
                case 1:
                    return ChooseFuturesFragment.newInstance(Variety.FUTURE_CHINA, mContractsCode);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        private Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
