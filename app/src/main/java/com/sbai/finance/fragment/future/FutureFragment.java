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
        updateFutureForeignHqList();

    }

    @OnClick(R.id.rate)
    public void onClick(View view) {
    }

    private void updateFutureForeignHqList() {
        if (mListHq == null) {
            mListHq = new ArrayList<>();
        }
        for (int i = 0; i < 10; i++) {
            FutureHq futureHq = new FutureHq();
            futureHq.setCodeName("美原油");
            futureHq.setInstrumentId("CL1709");
            futureHq.setLastPrice(66.66);
            futureHq.setUpDropSpeed(25.00);
            mListHq.add(futureHq);
        }
        mFutureListAdapter.clear();
        mFutureListAdapter.addAll(mListHq);
        mFutureListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public static class FutureListAdapter extends ArrayAdapter<FutureHq> {
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

            private void bindDataWithView(FutureHq item, int position, Context context) {
                mFutureName.setText(item.getCodeName());
                mFutureCode.setText(item.getInstrumentId());
                mLastPrice.setText(item.getLastPrice().toString());
                mRate.setText("+" + item.getUpDropSpeed().toString() + "%");
                if (position == 2) {
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
