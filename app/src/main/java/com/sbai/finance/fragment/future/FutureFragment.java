package com.sbai.finance.fragment.future;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.FutureHq;
import com.sbai.finance.model.VarietyModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class FutureFragment extends BaseFragment {
    @BindView(R.id.rate)
    TextView mRate;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    private Unbinder unbinder;
    private FutureListAdapter mFutureListAdapter;
    private List<FutureHq> mListHq;

    public static final int FOREIGN_FUTURE=0;
    public static final int CHINA_FUTURE=1;
    private int mfutureType;
    private Integer mPage = 0;
    private Integer mPageSize = 15;
    public static FutureFragment newInstance(int type){
        FutureFragment futureFragment = new FutureFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        futureFragment.setArguments(bundle);
        return futureFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mfutureType = this.getArguments().getInt("type");
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_future, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFutureListAdapter = new FutureListAdapter(getActivity());
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mFutureListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestFutureData();

    }

    private void requestFutureData() {
        String bigVarietyTypeCode = null;
        if (mfutureType == FOREIGN_FUTURE){
            bigVarietyTypeCode = "foreign";
        }else if (mfutureType == CHINA_FUTURE){
            bigVarietyTypeCode = "china";
        }
        Client.getFutureVariety(mPage,mPageSize,bigVarietyTypeCode).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<VarietyModel>>,List<VarietyModel>>() {
                    @Override
                    protected void onRespSuccessData(List<VarietyModel> data) {
                        updateFutureData((ArrayList<VarietyModel>) data);
                    }
                }).fire();

    }

    @OnClick(R.id.rate)
    public void onClick(View view) {
    }

    private void updateFutureData(ArrayList<VarietyModel> varietyList) {
        if (varietyList == null){
            return;
        }
        mFutureListAdapter.clear();
        mFutureListAdapter.addAll(varietyList);
        mFutureListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public static class FutureListAdapter extends ArrayAdapter<VarietyModel> {
        Context mContext;

        public FutureListAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_hq, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), position, mContext);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.futureName)
            TextView mFutureName;
            @BindView(R.id.futureCode)
            TextView mFutureCode;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.rate)
            TextView mRate;
            @BindView(R.id.stopTrade)
            TextView mStopTrade;
            @BindView(R.id.trade)
            LinearLayout mTrade;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(VarietyModel item, int position, Context context) {
                mFutureName.setText(item.getVarietyName());
                mFutureCode.setText(item.getContractsCode());
                mLastPrice.setText(item.getBigVarietyTypeCode());
//                mRate.setText("+" + item.getUpDropSpeed().toString() + "%");
                if (item.getExchangeStatus() == 0) {
                    mTrade.setVisibility(View.GONE);
                    mStopTrade.setVisibility(View.VISIBLE);
                } else {
                    mTrade.setVisibility(View.VISIBLE);
                    mStopTrade.setVisibility(View.GONE);
                }
            }
        }
    }
}
