package com.sbai.finance.activity.daily;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.DailyReport;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;
import com.sbai.httplib.CookieManger;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 乐米日报
 */

public class DailyReportActivity extends BaseActivity implements CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private int mPageSize = 20;
    private int mPageNo = 0;
    private HashSet<String> mSet;
    private DailyReportAdapter mDailyReportAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);
        ButterKnife.bind(this);
        initListHeader();
        initListView();
        requestDailyList();
    }

    private void initListHeader() {
        View view = new View(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Display.dp2Px(1, getResources()));
        view.setLayoutParams(params);
        mListView.addHeaderView(view);
    }

    private void initListView() {
        mSet = new HashSet<>();
        mDailyReportAdapter = new DailyReportAdapter(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mDailyReportAdapter);
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mDailyReportAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DailyReport dailyReport = (DailyReport) parent.getItemAtPosition(position);
                if (dailyReport!=null){
                    Launcher.with(getActivity(), DailyReportDetailActivity.class)
                            .putExtra(DailyReportDetailActivity.EX_FORMAT, dailyReport.getFormat())
                            .putExtra(DailyReportDetailActivity.EX_ID,dailyReport.getId())
                            .putExtra(DailyReportDetailActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                            .execute();
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        requestDailyList();
    }

    @Override
    public void onRefresh() {
        reset();
        requestDailyList();
    }

    private void requestDailyList() {
        Client.getDailyReportList(mPageNo).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<DailyReport>>, List<DailyReport>>() {
                    @Override
                    protected void onRespSuccessData(List<DailyReport> data) {
                        updateDailyList(data);
                    }
                }).fireFree();
    }

    private void updateDailyList(List<DailyReport> data) {
        stopRefreshAnimation();
        if (mSet.isEmpty()) {
            mDailyReportAdapter.clear();
        }
        for (DailyReport dailyReport : data) {
            if (mSet.add(dailyReport.getId())) {
                mDailyReportAdapter.add(dailyReport);
            }
        }
        if (data.size() < mPageSize) {
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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_daily_report, null, true);
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
            @BindView(R.id.click)
            TextView mClick;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.title)
            TextView mTitle;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(DailyReport item, Context context) {
                Glide.with(context).load(item.getCoverUrl()).into(mImage);
                mClick.setText(context.getString(R.string.read_count, item.getClicks()));
                mTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
                mTitle.setText(item.getTitle());
            }
        }
    }
}
