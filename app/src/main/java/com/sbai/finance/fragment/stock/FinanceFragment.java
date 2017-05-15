package com.sbai.finance.fragment.stock;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.BottomTextViewLayout;
import com.sbai.finance.view.IconTextRow;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FinanceFragment extends BaseFragment {

    private static final String KEY_STOCK_CODE = "STOCK_CODE";
    @BindView(R.id.company)
    IconTextRow mCompany;
    @BindView(android.R.id.list)
    ListView mList;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;


    private Unbinder mBind;
    private CompanyFinanceAdapter mCompanyFinanceAdapter;
    private HashSet<String> mSet;
    //证券代码
    private String mStockCode;
    private int mPage = 0;
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
        mList.setEmptyView(mEmpty);
        mCompanyFinanceAdapter = new CompanyFinanceAdapter(getActivity());
        mList.setAdapter(mCompanyFinanceAdapter);
        requestCompanyAnnualReport(mPage);
        requestCompanyInfo();
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
                        mCompanyInfo = data;
                        mCompany.setText(data.getCompanyName());
                    }
                })
                .fireSync();
    }

    public void requestCompanyAnnualReport(final int page) {
        this.mPage = page;
        Client.getCompanyAnnualReport(mStockCode, mPage, Client.PAGE_SIZE, CompanyAnnualReportModel.TYPE_FINANCIAL_SUMMARY)
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
        if (data == null) {
            return;
        }

        //切换的时候刷新数据
        if (page == 0) {
            mSet.clear();
            mCompanyFinanceAdapter.clear();
        }

        if (mFootView == null) {
            mFootView = new TextView(getActivity());
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            mFootView.setPadding(padding, padding, padding, padding);
            mFootView.setText(getText(R.string.load_more));
            mFootView.setGravity(Gravity.CENTER);
            mFootView.setTextColor(ContextCompat.getColor(getActivity(), R.color.greyAssist));
            mFootView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.greyLightAssist));
            mFootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPage++;
                    requestCompanyAnnualReport(mPage);
                }
            });
            mList.addFooterView(mFootView);
        }

        if (data.size() < Client.PAGE_SIZE) {
            mList.removeFooterView(mFootView);
            mFootView = null;
        }

        for (CompanyAnnualReportModel companyAnnualReportData : data) {
            if (mSet.add(companyAnnualReportData.getId())) {
                mCompanyFinanceAdapter.add(companyAnnualReportData);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    class CompanyFinanceAdapter extends ArrayAdapter<CompanyAnnualReportModel> {

        private Context mContext;

        public CompanyFinanceAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_stock_company_finance, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position));
            return convertView;
        }

        class ViewHolder {
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
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(CompanyAnnualReportModel item) {
                mCompanyFinancePublishTime.setText(String.valueOf(item.getCreateDate()));
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
