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
    @BindView(android.R.id.empty)
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
    private ArrayList<CompanyAnnualReportModel> mCompanyAnnualReportModels;

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
        mCompanyAnnualReportModels = new ArrayList<>();
        mCompanyFinanceAdapter = new CompanyFinanceAdapter(mCompanyAnnualReportModels);
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
        if (page == 0) {
            mCompanyAnnualReportModels.clear();
        }
        Client.getCompanyAnnualReport(mStockCode, mPage, mPageSize, CompanyAnnualReportModel.TYPE_FINANCIAL_SUMMARY)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ArrayList<CompanyAnnualReportModel>>, ArrayList<CompanyAnnualReportModel>>() {
                    @Override
                    protected void onRespSuccessData(ArrayList<CompanyAnnualReportModel> data) {
                        for (CompanyAnnualReportModel a : data) {
                            Log.d("wangjie222", "onRespSuccessData: 财务 " + a.toString());
                        }
                        updateCompanyAnnualReportList(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        updateCompanyAnnualReportList(null);
                    }
                })
                .fire();
    }

    private void updateCompanyAnnualReportList(ArrayList<CompanyAnnualReportModel> data) {
        if (data == null || data.isEmpty() && mCompanyAnnualReportModels.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
            mCompanyFinanceAdapter.addAll(data);
        }

    }

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    class CompanyFinanceAdapter extends RecyclerView.Adapter<CompanyFinanceAdapter.ViewHolder> {

        ArrayList<CompanyAnnualReportModel> mCompanyAnnualReportModels;

        public CompanyFinanceAdapter(ArrayList<CompanyAnnualReportModel> companyAnnualReportModels) {
            mCompanyAnnualReportModels = companyAnnualReportModels;
        }

        public void addAll(ArrayList<CompanyAnnualReportModel> companyAnnualReportModels) {
            mCompanyAnnualReportModels.addAll(companyAnnualReportModels);
            notifyDataSetChanged();
        }

        public void clear() {
            mCompanyAnnualReportModels.clear();
            notifyItemRangeRemoved(0, mCompanyAnnualReportModels.size());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_stock_company_finance, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mCompanyAnnualReportModels.get(position));
        }

        @Override
        public int getItemCount() {
            return mCompanyAnnualReportModels.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
//            @BindView(R.id.companyFinancePublishTime)
//            AppCompatTextView mCompanyFinancePublishTime;
//            @BindView(R.id.oneStockNetAsset)
//            AutoCompleteTextView mOneStockNetAsset;
//            @BindView(R.id.one_stock_earnings)
//            AutoCompleteTextView mOneStockEarnings;
//            @BindView(R.id.one_stock_cash_content)
//            AutoCompleteTextView mOneStockCashContent;
//            @BindView(R.id.one_stock_capital_accumulation_fund)
//            AutoCompleteTextView mOneStockCapitalAccumulationFund;
//            @BindView(R.id.fixation_capital_count)
//            AutoCompleteTextView mFixationCapitalCount;
//            @BindView(R.id.flow_capital_count)
//            AutoCompleteTextView mFlowCapitalCount;
//            @BindView(R.id.capital_count)
//            AutoCompleteTextView mCapitalCount;
//            @BindView(R.id.long_liabilities_count)
//            AutoCompleteTextView mLongLiabilitiesCount;
//            @BindView(R.id.normal_business_earnings)
//            AutoCompleteTextView mNormalBusinessEarnings;
//            @BindView(R.id.finance_charge)
//            AutoCompleteTextView mFinanceCharge;
//            @BindView(R.id.earn_profit)
//            AutoCompleteTextView mEarnProfit;

            @BindView(R.id.companyFinancePublishTime)
            AppCompatTextView mCompanyFinancePublishTime;
            @BindView(R.id.oneStockNetAsset)
            BottomTextViewLayout mOneStockNetAsset;
            @BindView(R.id.one_stock_earnings)
            BottomTextViewLayout mOneStockEarnings;
            @BindView(R.id.one_stock_cash_content)
            BottomTextViewLayout mOneStockCashContent;
            @BindView(R.id.one_stock_capital_accumulation_fund)
            BottomTextViewLayout mOneStockCapitalAccumulationFund;
            @BindView(R.id.fixation_capital_count)
            BottomTextViewLayout mFixationCapitalCount;
            @BindView(R.id.flow_capital_count)
            BottomTextViewLayout mFlowCapitalCount;
            @BindView(R.id.capital_count)
            BottomTextViewLayout mCapitalCount;
            @BindView(R.id.long_liabilities_count)
            BottomTextViewLayout mLongLiabilitiesCount;
            @BindView(R.id.normal_business_earnings)
            BottomTextViewLayout mNormalBusinessEarnings;
            @BindView(R.id.finance_charge)
            BottomTextViewLayout mFinanceCharge;
            @BindView(R.id.earn_profit)
            BottomTextViewLayout mEarnProfit;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(CompanyAnnualReportModel item) {
                if (item == null) return;
                mCompanyFinancePublishTime.setText(DateUtil.getYearQuarter(item.getJiezhiriqi()));
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
}
