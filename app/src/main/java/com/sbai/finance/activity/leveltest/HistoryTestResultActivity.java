package com.sbai.finance.activity.leveltest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.leveltest.TestResultModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HistoryTestResultActivity extends BaseActivity {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mCustomSwipeRefreshLayout;
    private HistoryTestResultAdapter mHistoryTestResultAdapter;

    private int mPage = 0;
    private HashSet<Long> mSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_test_result);
        ButterKnife.bind(this);
        mListView.setEmptyView(mEmpty);
        mListView.setDivider(null);
        mSet = new HashSet<>();

        mHistoryTestResultAdapter = new HistoryTestResultAdapter(this);
        mListView.setAdapter(mHistoryTestResultAdapter);

        mCustomSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mPage = 0;
                mCustomSwipeRefreshLayout.setLoadMoreEnable(true);
                requestHistoryTestResultList();
            }
        });
        requestHistoryTestResultList();
    }

    private void requestHistoryTestResultList() {
        Client.requestHistoryTestResultList()
                .setTag(TAG)
                .setCallback(new Callback2D<List<TestResultModel>, List<TestResultModel>>() {
                    @Override
                    protected void onRespSuccessData(List<TestResultModel> data) {
                        updateHistoryTestResultList(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void updateHistoryTestResultList(List<TestResultModel> data) {
        if (data == null || data.isEmpty()) {
            stopRefreshAnimation();
            return;
        }
        if (data.size() < Client.DEFAULT_PAGE_SIZE) {
            mCustomSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }

        if (mCustomSwipeRefreshLayout.isRefreshing()) {
            mHistoryTestResultAdapter.clear();
        }
        stopRefreshAnimation();
        for (TestResultModel dataResult : data) {
            if (mSet.add(dataResult.getCreateTime())) {
                mHistoryTestResultAdapter.add(dataResult);
            }
        }
    }


    private void stopRefreshAnimation() {
        if (mCustomSwipeRefreshLayout.isRefreshing()) {
            mCustomSwipeRefreshLayout.setRefreshing(false);
        }
        if (mCustomSwipeRefreshLayout.isLoading()) {
            mCustomSwipeRefreshLayout.setLoading(false);
        }
    }

    static class HistoryTestResultAdapter extends ArrayAdapter<TestResultModel> {

        public HistoryTestResultAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history_test_result, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), position);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.grade)
            TextView mGrade;
            @BindView(R.id.accuracy)
            TextView mAccuracy;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.empty)
            Space mEmpty;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(TestResultModel item, int position) {
                if (position == 0) {
                    mEmpty.setVisibility(View.VISIBLE);
                } else {
                    mEmpty.setVisibility(View.GONE);
                }
                mGrade.setText("LV1 入门");
                mAccuracy.setText(item.getAccuracy());
                mTime.setText(DateUtil.format(item.getCreateTime(), "yyyy-MM-dd"));
            }
        }

    }
}
