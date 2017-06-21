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
import com.sbai.finance.fragment.mine.ExChangeProductFragment;
import com.sbai.finance.model.mine.cornucopia.ExchangeDetailModel;
import com.sbai.finance.model.payment.UserFundInfoModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
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
    private ExchangeProductAdapter mExchangeProductAdapter;

    private UserFundInfoModel mUserFundInfoModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cornucopia);
        ButterKnife.bind(this);

        mExchangeProductAdapter = new ExchangeProductAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(mExchangeProductAdapter);
        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setSelectedIndicatorPadding(Display.dp2Px(60, getResources()));
        mTabLayout.setPadding(Display.dp2Px(13, getResources()));
        mTabLayout.setViewPager(mViewPager);

        updateCoinAndIntegrateNumber(null);
        requestUserFindInfo();
    }

    private void updateCoinAndIntegrateNumber(UserFundInfoModel userFundInfoModel) {
        if (userFundInfoModel == null)
            mUserFundInfoModel = new UserFundInfoModel();
        mCoin.setText(StrUtil.mergeTextWithRatioColor(getString(R.string.coin_number, mUserFundInfoModel.getYuanbao()), "\n" + getString(R.string.check_details), 0.66f, ContextCompat.getColor(getActivity(), R.color.unluckyText)));
        mIntegrate.setText(StrUtil.mergeTextWithRatioColor(getString(R.string.integrate_number, FinanceUtil.formatWithScale(mUserFundInfoModel.getCredit())), "\n" + getString(R.string.check_details), 0.66f, ContextCompat.getColor(getActivity(), R.color.unluckyText)));
    }


    private void requestUserFindInfo() {
        Client.requestUserFundInfo()
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserFundInfoModel>, UserFundInfoModel>() {
                    @Override
                    protected void onRespSuccessData(UserFundInfoModel data) {
                        updateCoinAndIntegrateNumber(mUserFundInfoModel);
                    }
                })
                .fireSync();
    }

    @OnClick({R.id.coin, R.id.integrate, R.id.exchange_rule})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.coin:
                Launcher.with(getActivity(), EarningsAndExpendDetailsActivity.class).putExtra(Launcher.EX_PAY_END, ExchangeDetailModel.TYPE_COIN).execute();
                break;
            case R.id.integrate:
                Launcher.with(getActivity(), EarningsAndExpendDetailsActivity.class).putExtra(Launcher.EX_PAY_END, ExchangeDetailModel.TYPE_INTEGRATE).execute();
                break;
            case R.id.exchange_rule:
                // TODO: 2017/6/20 兑换规则
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
            switch (position) {
                case 0:
                    return new ExChangeProductFragment();
                case 1:
                    return new ExChangeProductFragment();
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
                    return mContext.getString(R.string.exchange_coin);
                case 1:
                    return mContext.getString(R.string.exchange_integrate);
            }
            return super.getPageTitle(position);
        }
    }
}
