package com.sbai.finance.fragment.stock;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sbai.finance.R;
import com.sbai.finance.activity.stock.CompanyIntroActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.stock.CompanyAnnualReportModel;
import com.sbai.finance.model.stock.CompanyInfo;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.BottomTextViewLayout;
import com.sbai.finance.view.IconTextRow;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.R.attr.padding;

public class FinanceFragment extends BaseFragment {

    private static final String KEY_STOCK_CODE = "STOCK_CODE";
    @BindView(R.id.company)
    IconTextRow mCompany;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    //    @BindView(android.R.id.empty)

    AppCompatTextView mEmpty;


    private Unbinder mBind;
    private CompanyFinanceAdapter mCompanyFinanceAdapter;
    private HashSet<String> mSet;
    //证券代码
    private String mStockCode;
    private int mPage = 0;
    private int mPageSize = 10;
    private TextView mFootView;

    private CompanyInfo mCompanyInfo;

    public FinanceFragment() {

    }

    public static FinanceFragment newInstance(String code) {
        FinanceFragment fragment = new FinanceFragment();
        Bundle args = new Bundle();
        args.putString(KEY_STOCK_CODE, code);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStockCode = getArguments().getString(KEY_STOCK_CODE);
            Log.d(TAG, "onCreate: 股票代码 " + mStockCode);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finance, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSet = new HashSet<>();
        mCompanyFinanceAdapter = new CompanyFinanceAdapter(R.layout.row_stock_company_finance);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mCompanyFinanceAdapter);
        requestCompanyAnnualReport(mPage);
        requestCompanyInfo();
    }

    private AppCompatTextView getEmptyView() {
        mEmpty = new AppCompatTextView(getActivity());
        mEmpty.setText(getText(R.string.quick_publish));
        mEmpty.setPadding(0, 10 * padding, 0, 0);
        mEmpty.setGravity(Gravity.CENTER_HORIZONTAL);
        mEmpty.setTextColor(ContextCompat.getColor(getActivity(), R.color.assistText));
        mEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        mEmpty.setCompoundDrawablePadding(padding);
        mEmpty.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.img_no_message, 0, 0);
        return mEmpty;
    }

    @OnClick(R.id.company)
    public void onViewClicked() {
        if (mCompanyInfo != null) {
            Launcher.with(getActivity(), CompanyIntroActivity.class).putExtra(Launcher.EX_PAYLOAD, mCompanyInfo).execute();
        }
    }

    private void requestCompanyInfo() {
        Client.getCompanyReportOrInfo(mStockCode)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<CompanyInfo>, CompanyInfo>() {
                    @Override
                    protected void onRespSuccessData(CompanyInfo data) {
                        mCompany.setVisibility(View.VISIBLE);
                        mCompanyInfo = data;
                        mCompany.setText(data.getCompanyName());
                    }
                })
                .fireSync();
    }

    public void requestCompanyAnnualReport(final int page) {
        this.mPage = page;
        Client.getCompanyAnnualReport(mStockCode, mPage, mPageSize, CompanyAnnualReportModel.TYPE_FINANCIAL_SUMMARY)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ArrayList<CompanyAnnualReportModel>>, ArrayList<CompanyAnnualReportModel>>() {
                    @Override
                    protected void onRespSuccessData(ArrayList<CompanyAnnualReportModel> data) {
                        for (CompanyAnnualReportModel model : data) {
                            Log.d(TAG, "onRespSuccessData: 年报  " + model.toString());
                        }
                        updateCompanyAnnualReportList(data, page);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                    }
                })
                .fire();
    }

    private void updateCompanyAnnualReportList(ArrayList<CompanyAnnualReportModel> data, int page) {

        if (data == null || data.isEmpty()) {

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    class CompanyFinanceAdapter extends BaseQuickAdapter<CompanyAnnualReportModel, BaseViewHolder> {

        public CompanyFinanceAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, CompanyAnnualReportModel item) {
            bindDataWithView(helper, item);
        }

        private void bindDataWithView(BaseViewHolder helper, CompanyAnnualReportModel item) {
            AppCompatTextView mCompanyFinancePublishTime = helper.getView(R.id.companyFinancePublishTime);
            BottomTextViewLayout mOneStockNetAsset = helper.getView(R.id.oneStockNetAsset);
            BottomTextViewLayout mOneStockEarnings = helper.getView(R.id.one_stock_earnings);
            BottomTextViewLayout mOneStockCashContent = helper.getView(R.id.one_stock_cash_content);
            BottomTextViewLayout mOneStockCapitalAccumulationFund = helper.getView(R.id.one_stock_capital_accumulation_fund);
            BottomTextViewLayout mFixationCapitalCount = helper.getView(R.id.fixation_capital_count);
            BottomTextViewLayout mFlowCapitalCount = helper.getView(R.id.flow_capital_count);
            BottomTextViewLayout mCapitalCount = helper.getView(R.id.capital_count);
            BottomTextViewLayout mLongLiabilitiesCount = helper.getView(R.id.long_liabilities_count);
            BottomTextViewLayout mNormalBusinessEarnings = helper.getView(R.id.normal_business_earnings);
            BottomTextViewLayout mFinanceCharge = helper.getView(R.id.finance_charge);
            BottomTextViewLayout mEarnProfit = helper.getView(R.id.earn_profit);

            mCompanyFinancePublishTime.setText(DateUtil.getFormatTime(item.getCreateDate()));
            mOneStockNetAsset.setInfoText(item.getMeigujinzichan());
            mOneStockEarnings.setInfoText(item.getMeigushouyi());
            mOneStockCashContent.setInfoText(item.getMeiguxianjinhanliang());
            mOneStockCapitalAccumulationFund.setInfoText(item.getMeiguzibengongjijin());
            mFixationCapitalCount.setInfoText(item.getGudingzichanheji());
            mFlowCapitalCount.setInfoText(item.getLiudongzichanheji());
            mCapitalCount.setInfoText(item.getZichanzongji());
            mLongLiabilitiesCount.setInfoText(item.getChangqifuzaiheji());
            mNormalBusinessEarnings.setInfoText(item.getZhuyingyewushouru());
            mFinanceCharge.setInfoText(item.getCaiwufeiyong());
            mEarnProfit.setInfoText(item.getJinlirun());
        }
    }
}
