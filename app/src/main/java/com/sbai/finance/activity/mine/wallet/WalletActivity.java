package com.sbai.finance.activity.mine.wallet;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.setting.UpdateSecurityPassActivity;
import com.sbai.finance.fragment.mine.AccountFundDetailFragment;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.model.payment.UserBankCardInfoModel;
import com.sbai.finance.model.payment.UserFundInfoModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WalletActivity extends BaseActivity {

    private static final int REQ_CODE_ADD_SAFETY_PASS = 5120;
    private static final int REQ_CODE_BIND_BANK = 1445;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.tabLayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private UserBankCardInfoModel mUserBankCardInfoModel;
    private FundFragmentAdapter mFundFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);

        translucentStatusBar();

        initViewPager();
    }

    private void initViewPager() {
        mFundFragmentAdapter = new FundFragmentAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(mFundFragmentAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setSelectedIndicatorPadding((int) Display.dp2Px(30, getResources()));
        mTabLayout.setPadding(Display.dp2Px(12, getResources()));
        mTabLayout.setTabViewTextColor(Color.WHITE);
        mTabLayout.setSelectedIndicatorColors(Color.WHITE);
        mTabLayout.setHasBottomBorder(false);
        mTabLayout.setViewPager(mViewPager);
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestUserFindInfo();
    }


    private void updateUserFund(UserFundInfoModel fund) {
        if (fund != null) {
            AccountFundDetailFragment ingotFragment = (AccountFundDetailFragment) mFundFragmentAdapter.getFragment(0);
            if (ingotFragment != null) {
                ingotFragment.updateUserFund(fund.getYuanbao());
            }

            AccountFundDetailFragment accountCrashFragment = (AccountFundDetailFragment) mFundFragmentAdapter.getFragment(1);
            if (accountCrashFragment != null) {
                accountCrashFragment.updateUserFund(fund.getMoney());
            }

            AccountFundDetailFragment scoreFragment = (AccountFundDetailFragment) mFundFragmentAdapter.getFragment(2);
            if (scoreFragment != null) {
                scoreFragment.updateUserFund(fund.getCredit());
            }
        }
    }

    private void requestUserFindInfo() {
        Client.requestUserFundInfo()
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserFundInfoModel>, UserFundInfoModel>() {
                    @Override
                    protected void onRespSuccessData(UserFundInfoModel data) {
                        updateUserFund(data);
                    }
                })
                .fireFree();
    }
//
//    @OnClick({R.id.balance, R.id.recharge, R.id.withdraw, R.id.market_detail, R.id.bankCard})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.balance:
//                break;
//            case R.id.recharge:
//                umengEventCount(UmengCountEventIdUtils.WALLET_RECHARGE);
//                Launcher.with(getActivity(), RechargeActivity.class)
//                        .execute();
//                break;
//            case R.id.withdraw:
//                umengEventCount(UmengCountEventIdUtils.WALLET_WITHDRAW);
//                requestUserHasPassWord();
//                break;
//            case R.id.market_detail:
//                umengEventCount(UmengCountEventIdUtils.WALLET_DETAILS);
//                Launcher.with(getActivity(), FundDetailActivity.class).execute();
//                break;
//            case R.id.bankCard:
//                umengEventCount(UmengCountEventIdUtils.WALLET_BANK_CARD);
//                Client.requestUserBankCardInfo()
//                        .setTag(TAG)
//                        .setIndeterminate(this)
//                        .setCallback(new Callback<Resp<List<UserBankCardInfoModel>>>() {
//                            @Override
//                            protected void onRespSuccess(Resp<List<UserBankCardInfoModel>> resp) {
//                                if (resp.isSuccess()) {
//                                    if (resp.hasData()) {
//                                        mUserBankCardInfoModel = resp.getData().get(0);
//                                    }
//                                    Launcher.with(getActivity(), BindBankCardActivity.class)
//                                            .putExtra(Launcher.EX_PAY_END, mUserBankCardInfoModel)
//                                            .executeForResult(REQ_CODE_BIND_BANK);
//                                }
//                            }
//                        })
//                        .fire();
//                break;
//        }
//    }

    private void requestUserHasPassWord() {
        Client.getUserHasPassWord()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                    @Override
                    protected void onRespSuccessData(Boolean data) {
                        if (!data) {
                            Launcher.with(getActivity(), UpdateSecurityPassActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, data.booleanValue())
                                    .executeForResult(REQ_CODE_ADD_SAFETY_PASS);
                        } else {
                            openWithDrawPage();
                        }
                    }
                })
                .fire();
    }

    private void showOpenBindCardDialog() {
        SmartDialog.with(getActivity(), R.string.you_not_bind_bank_card)
                .setPositive(R.string.go_to_bind_card, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Launcher.with(getActivity(), BindBankCardActivity.class)
                                .putExtra(Launcher.EX_PAY_END, mUserBankCardInfoModel)
                                .executeForResult(BindBankCardActivity.REQ_CODE_BIND_CARD);
                    }
                })
                .show();
    }

    private void openWithDrawPage() {
        Client.requestUserBankCardInfo()
                .setTag(TAG)
                .setIndeterminate(WalletActivity.this)
                .setCallback(new Callback2D<Resp<List<UserBankCardInfoModel>>, List<UserBankCardInfoModel>>() {
                    @Override
                    protected void onRespSuccessData(List<UserBankCardInfoModel> data) {
                        if (data != null && !data.isEmpty()) {
                            mUserBankCardInfoModel = data.get(0);
                            if (mUserBankCardInfoModel != null) {
                                if (mUserBankCardInfoModel.isNotConfirmBankInfo()) {
                                    showOpenBindCardDialog();
                                } else {
//                                    Launcher.with(getActivity(), WithDrawActivity.class)
//                                            .putExtra(Launcher.EX_PAY_END, mUserBankCardInfoModel)
//                                            .putExtra(Launcher.EX_PAYLOAD, money)
//                                            .execute();
                                }
                            }
                        }
                    }
                })
                .fire();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //添加安全密码后回调
                case REQ_CODE_ADD_SAFETY_PASS:
//                    openWithDrawPage();
                    break;
                case BindBankCardActivity.REQ_CODE_BIND_CARD:
                    mUserBankCardInfoModel = data.getParcelableExtra(Launcher.EX_PAYLOAD);
                    Launcher.with(getActivity(), WithDrawActivity.class)
                            .execute();
                    break;
                case REQ_CODE_BIND_BANK:
                    boolean firstConfirm = data.getBooleanExtra(Launcher.EX_PAY_END, false);
                    if (firstConfirm) {
                        SmartDialog.with(getActivity(), R.string.bind_bank_success_hint)
                                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeVisible(View.GONE)
                                .show();
                    }
                    break;
            }
        }
    }


    private static class FundFragmentAdapter extends FragmentPagerAdapter {

        private FragmentManager mFragmentManager;
        private Context mContext;

        public FundFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentManager = fm;
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return AccountFundDetailFragment.newInstance(AccountFundDetail.TYPE_INGOT);
                case 1:
                    return AccountFundDetailFragment.newInstance(AccountFundDetail.TYPE_CRASH);
                case 2:
                    return AccountFundDetailFragment.newInstance(AccountFundDetail.TYPE_SCORE);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.ingot);
                case 1:
                    return mContext.getString(R.string.cash);
                case 2:
                    return mContext.getString(R.string.score);
            }
            return super.getPageTitle(position);
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
