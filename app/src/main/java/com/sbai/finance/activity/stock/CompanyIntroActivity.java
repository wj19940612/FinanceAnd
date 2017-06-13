package com.sbai.finance.activity.stock;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.stock.CompanyInfo;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.BottomTextViewLayout;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompanyIntroActivity extends BaseActivity {

    @BindView(R.id.company_name)
    BottomTextViewLayout mCompanyName;
    @BindView(R.id.register_capital)
    BottomTextViewLayout mRegisterCapital;
    @BindView(R.id.market_date)
    BottomTextViewLayout mMarketDate;
    @BindView(R.id.publish_prise)
    BottomTextViewLayout mPublishPrise;
    @BindView(R.id.company_address)
    BottomTextViewLayout mCompanyAddress;
    @BindView(R.id.business_scope)
    BottomTextViewLayout mBusinessScope;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.nestedScrollView)
    NestedScrollView mNestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_intro);
        ButterKnife.bind(this);
        mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNestedScrollView.scrollTo(0, 0);
            }
        });
        CompanyInfo companyInfo = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
        mCompanyName.setInfoText(companyInfo.getCompanyName());
        mRegisterCapital.setInfoText(companyInfo.getZhuceziben());
        mMarketDate.setInfoText(companyInfo.getShangshiriqi());
        mPublishPrise.setInfoText(companyInfo.getFaxingjiage());
        mCompanyAddress.setInfoText(companyInfo.getBangongdizhi());
        mBusinessScope.setInfoText(companyInfo.getJingyingfanwei());
    }
}
