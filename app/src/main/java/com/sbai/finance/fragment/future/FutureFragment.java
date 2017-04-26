package com.sbai.finance.fragment.future;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.future.FutureTradeActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;

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
    private List<Variety> mVarietyList;

    private String mFutureType;
    private int mPage;
    private int mPageSize;

    public static FutureFragment newInstance(String type) {
        FutureFragment futureFragment = new FutureFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        futureFragment.setArguments(bundle);
        return futureFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFutureType = getArguments().getString("type");
        }
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
        mPage = 0;
        mPageSize = 10;

        initView();
    }

    private void initView() {
        mFutureListAdapter = new FutureListAdapter(getActivity());
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mFutureListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Variety variety = (Variety) parent.getItemAtPosition(position);
                if (variety != null) {
                    Launcher.with(getActivity(), FutureTradeActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requestVarietyList();
    }

    @OnClick(R.id.rate)
    public void onClick(View view) {

    }

    private void updateVarietyList() {
        mFutureListAdapter.clear();
        mFutureListAdapter.addAll(mVarietyList);
        mFutureListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void requestVarietyList() {
        Client.getVarietyList(Variety.VAR_FUTURE, mPage, mPageSize, mFutureType)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        mVarietyList = data;
                        updateVarietyList();
                    }
                }).fireSync();
    }


    public static class FutureListAdapter extends ArrayAdapter<Variety> {

        public FutureListAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_variey, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), position, getContext());
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

            private void bindDataWithView(Variety item, int position, Context context) {
                mFutureName.setText(item.getVarietyName());
                mFutureCode.setText(item.getContractsCode());
                mLastPrice.setText("");
                mRate.setText("+" + "0.00%");
                if (item.getExchangeStatus() == Variety.EXCHANGE_STATUS_CLOSE) {
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
