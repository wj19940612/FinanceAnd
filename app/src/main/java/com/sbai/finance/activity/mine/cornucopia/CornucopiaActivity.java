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

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.fragment.mine.ExChangeProductFragment;
import com.sbai.finance.model.mine.cornucopia.CornucopiaProductModel;
import com.sbai.finance.model.mine.cornucopia.ExchangeDetailModel;
import com.sbai.finance.model.mutual.ArticleProtocol;
import com.sbai.finance.model.payment.UserFundInfoModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;
import com.sbai.httplib.CookieManger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CornucopiaActivity extends BaseActivity implements ExChangeProductFragment.OnUserFundChangeListener {

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
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    private ExchangeProductAdapter mExchangeProductAdapter;

    private int mSelectPosition;

    private UserFundInfoModel mUserFundInfoModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cornucopia);
        ButterKnife.bind(this);

        initView();

        updateCoinAndIntegrateNumber(null);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestUserFindInfo();
    }

    private void initView() {
        mExchangeProductAdapter = new ExchangeProductAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(mExchangeProductAdapter);
        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setSelectedIndicatorPadding(Display.dp2Px(60, getResources()));
        mTabLayout.setPadding(Display.dp2Px(13, getResources()));
        mTabLayout.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSelectPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = mExchangeProductAdapter.getFragment(mSelectPosition);
                if (fragment != null && fragment instanceof ExChangeProductFragment) {
                    ((ExChangeProductFragment) (fragment)).scrollToTop();
                }
            }
        });
    }

    private void updateCoinAndIntegrateNumber(UserFundInfoModel userFundInfoModel) {
        if (userFundInfoModel != null) {
            mCoin.setText(StrUtil.mergeTextWithRatioColor(getString(R.string.coin_number, FinanceUtil.formatWithScaleNoZero(userFundInfoModel.getYuanbao())), "\n" + getString(R.string.check_details), 0.66f, ContextCompat.getColor(getActivity(), R.color.unluckyText)));
            mIntegrate.setText(StrUtil.mergeTextWithRatioColor(getString(R.string.integrate_number, FinanceUtil.formatWithScale(userFundInfoModel.getCredit())), "\n" + getString(R.string.check_details), 0.66f, ContextCompat.getColor(getActivity(), R.color.unluckyText)));
        }
    }


    private void requestUserFindInfo() {
        Client.requestUserFundInfo()
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserFundInfoModel>, UserFundInfoModel>() {
                    @Override
                    protected void onRespSuccessData(UserFundInfoModel data) {
                        mUserFundInfoModel = data;
                        updateCoinAndIntegrateNumber(data);
                        ExChangeProductFragment exChangeProductFragment = (ExChangeProductFragment) mExchangeProductAdapter.getFragment(0);
                        ExChangeProductFragment exChangeProductFragment2 = (ExChangeProductFragment) mExchangeProductAdapter.getFragment(1);
                        exChangeProductFragment.setUserFundInfo(data);
                        exChangeProductFragment2.setUserFundInfo(data);
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
                openExchangeRulePage();
                break;
        }
    }

    private void openExchangeRulePage() {
        Client.getArticleProtocol(ArticleProtocol.PROTOCOL_EXCHANGE).setTag(TAG)
                .setCallback(new Callback2D<Resp<ArticleProtocol>, ArticleProtocol>(false) {
                    @Override
                    protected void onRespSuccessData(ArticleProtocol data) {
                        Launcher.with(getActivity(), WebActivity.class)
                                .putExtra(WebActivity.EX_TITLE, data.getTitle())
                                .putExtra(WebActivity.EX_HTML, data.getContent())
                                .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                .execute();
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        Launcher.with(getActivity(), WebActivity.class)
                                .putExtra(WebActivity.EX_TITLE, getString(R.string.protocol))
                                .putExtra(WebActivity.EX_URL, Client.WEB_USER_PROTOCOL_PAGE_URL)
                                .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                .execute();
                    }
                }).fire();
    }

    @Override
    public void onUserFundChange() {
        requestUserFindInfo();
    }

    class ExchangeProductAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private FragmentManager mFragmentManager;

        public ExchangeProductAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ExChangeProductFragment.newInstance(CornucopiaProductModel.TYPE_VCOIN);
                case 1:
                    return ExChangeProductFragment.newInstance(CornucopiaProductModel.TYPE_INTEGRATION);
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

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
