package com.sbai.finance.activity.mine.cornucopia;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CornucopiaActivity extends BaseActivity {

    @BindView(R.id.coin)
    TextView mCoin;
    @BindView(R.id.integrate)
    TextView mIntegrate;
    @BindView(R.id.exchange_rule)
    TextView mExchangeRule;
    @BindView(R.id.tabLayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cornucopia);
        ButterKnife.bind(this);

        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setSelectedIndicatorPadding(Display.dp2Px(60, getResources()));
        mTabLayout.setPadding(Display.dp2Px(13, getResources()));
        mTabLayout.setViewPager(mViewPager);
    }

    @OnClick({R.id.coin, R.id.integrate, R.id.exchange_rule})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.coin:
                break;
            case R.id.integrate:
                break;
            case R.id.exchange_rule:
                break;
        }
    }

    class ExchangeProductAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public ExchangeProductAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }
    }
}
