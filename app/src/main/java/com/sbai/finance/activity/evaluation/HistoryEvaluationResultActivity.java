package com.sbai.finance.activity.evaluation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.levelevaluation.EvaluationResult;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.NumberFormatUtils;
import com.sbai.finance.view.CustomSwipeRefreshLayout;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HistoryEvaluationResultActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mCustomSwipeRefreshLayout;
    private HistoryTestResultAdapter mHistoryTestResultAdapter;

    private HashSet<String> mSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_evaluation_result);
        ButterKnife.bind(this);
        mListView.setEmptyView(mEmpty);
        mListView.setDivider(null);
        mSet = new HashSet<>();

        mHistoryTestResultAdapter = new HistoryTestResultAdapter(this);
        mListView.setAdapter(mHistoryTestResultAdapter);
        mListView.setOnItemClickListener(this);

        mCustomSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mCustomSwipeRefreshLayout.setLoadMoreEnable(true);
                requestHistoryTestResultList();
            }
        });
        requestHistoryTestResultList();
    }

    private void requestHistoryTestResultList() {
        Client.requestHistoryTestResultList()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<EvaluationResult>>, List<EvaluationResult>>() {
                    @Override
                    protected void onRespSuccessData(List<EvaluationResult> data) {
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


    private void updateHistoryTestResultList(List<EvaluationResult> data) {
        if (data == null || data.isEmpty()) {
            stopRefreshAnimation();
            return;
        }
        if (data.size() < Client.DEFAULT_PAGE_SIZE) {
            mCustomSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {

        }

        if (mCustomSwipeRefreshLayout.isRefreshing()) {
            mHistoryTestResultAdapter.clear();
        }
        stopRefreshAnimation();
        for (EvaluationResult dataResult : data) {
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EvaluationResult evaluationResult = (EvaluationResult) parent.getAdapter().getItem(position);
        if (evaluationResult != null) {
            Launcher.with(getActivity(), EvaluationResultActivity.class)
                    .putExtra(ExtraKeys.HISTORY_TEST_RESULT, evaluationResult)
                    .execute();
        }
    }

    static class HistoryTestResultAdapter extends ArrayAdapter<EvaluationResult> {

        private Context mContext;

        public HistoryTestResultAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
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
            viewHolder.bindDataWithView(getItem(position), position, mContext);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.grade)
            TextView mGrade;
            @BindView(R.id.accuracy)
            TextView mAccuracy;
            @BindView(R.id.time)
            TextView mTime;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(EvaluationResult item, int position, Context context) {
                mTime.setText(DateUtil.format(item.getCreateTime(), DateUtil.DEFAULT_FORMAT, "yyyy-MM-dd"));
                mGrade.setText(getTestGrade(item.getLevel()));
                mAccuracy.setText(context.getString(R.string.accuracy_ranking,
                        NumberFormatUtils.formatPercentStringEndReplaceZero(item.getAllAccuracy(), 2),
                        NumberFormatUtils.formatPercentString(item.getPassPercent())));
            }


            private String getTestGrade(int grade) {
                switch (grade) {
                    case 1:
                        return "LV1 入门";
                    case 2:
                        return "LV2 初级";
                    case 3:
                        return "LV3 中级";
                    case 4:
                        return "LV4 高级";
                    case 5:
                        return "LV5 精通";
                    default:
                        return "";
                }
            }
        }

    }
}
