package com.sbai.finance.fragment.focusnews;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.web.DailyReportDetailActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.DailyReport;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.httplib.CookieManger;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 要闻
 */

public class FocusNewsFragment extends BaseFragment implements
        CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    Unbinder unbinder;

    private int mPageNo = 0;
    private Set<String> mSet;
    private DailyReportAdapter mDailyReportAdapter;
    private long mLastTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_focus_news, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListView();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestAllDayNewsList();
//        startScheduleJob(5 * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRefreshAnimation();
//        stopScheduleJob();
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        requestLastNewsList();
    }

    private void initListView() {
        mSet = new HashSet<>();
        mDailyReportAdapter = new DailyReportAdapter(getContext());
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mDailyReportAdapter);
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mDailyReportAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DailyReport dailyReport = (DailyReport) parent.getItemAtPosition(position);
                if (dailyReport != null) {
                    Launcher.with(getActivity(), DailyReportDetailActivity.class)
                            .putExtra(DailyReportDetailActivity.EX_FORMAT, dailyReport.getFormat())
                            .putExtra(DailyReportDetailActivity.EX_ID, dailyReport.getId())
                            .putExtra(DailyReportDetailActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                            .executeForResult(DailyReportDetailActivity.READ_CODE);
                }
            }
        });
    }

    private void requestAllDayNewsList() {
        Client.getNewsList(DailyReport.NEWS, mPageNo).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<DailyReport>>, List<DailyReport>>() {
                    @Override
                    protected void onRespSuccessData(List<DailyReport> data) {
                        if (data.isEmpty()) return;
                        updateAllDayNewsList(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                }).fireFree();
    }

    private void requestLastNewsList() {
        if (mLastTime == 0) return;
        Client.getLastNewsList(DailyReport.NEWS, mLastTime)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<DailyReport>>, List<DailyReport>>() {
                    @Override
                    protected void onRespSuccessData(List<DailyReport> data) {
                        if (data.isEmpty()) return;
                        updateLastNews(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                })
                .fireFree();

    }

    private void updateLastNews(List<DailyReport> data) {
        mLastTime = data.get(0).getCreateTime();
        Collections.reverse(data);
        for (DailyReport dailyReport : data) {
            mDailyReportAdapter.insert(dailyReport, 0);
        }
        mDailyReportAdapter.notifyDataSetChanged();
    }

    private void updateAllDayNewsList(List<DailyReport> data) {
        if (mSet.isEmpty()) {
            mDailyReportAdapter.clear();
            mLastTime = data.get(0).getCreateTime();
        }
        for (DailyReport dailyReport : data) {
            if (mSet.add(dailyReport.getId())) {
                mDailyReportAdapter.add(dailyReport);
            }
        }
        if (data.size() < Client.DEFAULT_PAGE_SIZE) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPageNo++;
        }
        mDailyReportAdapter.notifyDataSetChanged();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onLoadMore() {
        requestAllDayNewsList();
    }

    @Override
    public void onRefresh() {
        reset();
        requestAllDayNewsList();
    }

    private void reset() {
        mPageNo = 0;
        mSet.clear();
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }

    static class DailyReportAdapter extends ArrayAdapter<DailyReport> {

        public DailyReportAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_focus_news, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.image)
            ImageView mImage;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.title)
            TextView mTitle;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(DailyReport item, Context context) {
                if (!TextUtils.isEmpty(item.getCoverUrl())) {
                    mImage.setVisibility(View.VISIBLE);
                    Glide.with(context).load(item.getCoverUrl()).into(mImage);
                } else {
                    mImage.setVisibility(View.GONE);
                }
                mTime.setText(DateUtil.formatDefaultStyleTime(item.getCreateTime()));
                mTitle.setText(item.getTitle());
            }
        }
    }
}
