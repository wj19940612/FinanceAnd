package com.sbai.finance.fragment.focusnews;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.DailyReport;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.StrUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.sbai.finance.utils.DateUtil.FORMAT_HOUR_MINUTE;

/**
 * 7x24小时
 */

public class InformationFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.week)
    TextView mWeek;
    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.dateArea)
    RelativeLayout mDateArea;
    @BindView(R.id.dataLayout)
    FrameLayout mDataLayout;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    Unbinder unbinder;
    @BindView(R.id.weekArea)
    LinearLayout mWeekArea;
    private int mPage = 0;
    private boolean mLoadMore = true;
    private InformationAdapter mInformationAdapter;
    private Set<String> mSet;
    private long mLastTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSet = new HashSet<>();
        initRecyclerView();
        requestNewsList(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                requestNewsList(true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startScheduleJob(5 * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRefreshAnimation();
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        requestLastNewsList();
    }

    private void initRecyclerView() {
        mInformationAdapter = new InformationAdapter(getActivity());
        mRecyclerView.setAdapter(mInformationAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                handleRecycleScroll(recyclerView);
            }
        });
    }

    private void requestNewsList(final boolean isRefresh) {
        Client.getNewsList(DailyReport.INFORMATION, mPage)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<DailyReport>>, List<DailyReport>>() {
                    @Override
                    protected void onRespSuccessData(List<DailyReport> data) {
                        updateDetailList(data, isRefresh);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                })
                .fireFree();

    }

    private void requestLastNewsList() {
        if (mLastTime == 0) return;
        Client.getLastNewsList(DailyReport.INFORMATION, mLastTime)
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
            mInformationAdapter.insert(0, dailyReport);
        }
        mInformationAdapter.notifyDataSetChanged();
    }

    private void updateDetailList(List<DailyReport> newsList, boolean isRefresh) {
        if (newsList == null || newsList.isEmpty() && mInformationAdapter.isEmpty()) {
            mDataLayout.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
            stopRefreshAnimation();
        } else {
            mDataLayout.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
            if (isRefresh) {
                mInformationAdapter.clear();
                mLastTime = newsList.get(0).getCreateTime();
                setFloatView(mLastTime);
            }
            if (newsList.size() < Client.DEFAULT_PAGE_SIZE) {
                mLoadMore = false;
            } else {
                mPage++;
                mLoadMore = true;
            }
            mInformationAdapter.addAll(newsList);
        }
    }

    private void setFloatView(long time) {
        int dayOfMonth = DateUtil.getDayOfMonth(time);
        if (dayOfMonth < 10) {
            mDate.setText("0" + dayOfMonth);
        } else {
            mDate.setText(String.valueOf(dayOfMonth));
        }
        if (DateUtil.isToday(time, mLastTime)) {
            mWeekArea.setSelected(true);
            mDate.setTextColor(Color.parseColor("#2B71FF"));
            mWeek.setText(getString(R.string.week_, DateUtil.getDayOfWeek(time)));
        } else {
            mWeekArea.setSelected(false);
            mDate.setTextColor(ContextCompat.getColor(getContext(), R.color.luckyText));
            mWeek.setText(getString(R.string.week_, DateUtil.getDayOfWeek(time)));
        }
    }

    //RecycleView 滑动的时候 根据tag 显示对应的吸顶文字
    private void handleRecycleScroll(RecyclerView recyclerView) {
        if (isSlideToBottom(mRecyclerView) && mLoadMore) {
            requestNewsList(false);
        }
        View stickyInfoView = recyclerView.findChildViewUnder(
                mDateArea.getMeasuredWidth() / 2, 5);

        if (stickyInfoView != null && stickyInfoView.getContentDescription() != null) {
            long time = Long.valueOf((String) stickyInfoView.getContentDescription());
            setFloatView(time);
        }

        View transInfoView = recyclerView.findChildViewUnder(
                mDateArea.getMeasuredWidth() / 2, mDateArea.getMeasuredHeight() + 1);
        if (transInfoView != null && transInfoView.getTag() != null) {
            int transViewStatus = (int) transInfoView.getTag();
            int dealtY = transInfoView.getTop() - mDateArea.getMeasuredHeight();

            if (transViewStatus == InformationAdapter.HAS_STICKY_VIEW) {
                if (transInfoView.getTop() > 0) {
                    mDateArea.setTranslationY(dealtY);
                } else {
                    mDateArea.setTranslationY(0);
                }
            } else if (transViewStatus == InformationAdapter.NONE_STICKY_VIEW) {
                mDateArea.setTranslationY(0);
            }
        }
    }

    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.ViewHolder> {

        public static final int HAS_STICKY_VIEW = 2;
        public static final int NONE_STICKY_VIEW = 3;
        private List<DailyReport> mNewsList;
        private Context mContext;

        public InformationAdapter(Context context) {
            this.mNewsList = new ArrayList<>();
            this.mContext = context;
        }

        public void addAll(List<DailyReport> detailArrayList) {
            mNewsList.addAll(detailArrayList);
            notifyDataSetChanged();
        }

        public void insert(int index, DailyReport dailyReport) {
            mNewsList.add(index, dailyReport);
        }

        public void clear() {
            mNewsList.clear();
            notifyDataSetChanged();
        }

        public boolean isEmpty() {
            return mNewsList.isEmpty();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_information, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mNewsList.get(position), isTheDifferentDate((position)), mContext);
            if (isTheDifferentDate(position)) {
                holder.itemView.setTag(HAS_STICKY_VIEW);
            } else {
                holder.itemView.setTag(NONE_STICKY_VIEW);
            }
            holder.itemView.setContentDescription(String.valueOf(mNewsList.get(position).getCreateTime()));
        }

        @Override
        public int getItemCount() {
            return mNewsList.size();
        }

        private boolean isTheDifferentDate(int position) {
            if (position == 0) {
                return true;
            }
            DailyReport pre = mNewsList.get(position - 1);
            DailyReport next = mNewsList.get(position);
            long preTime = pre.getCreateTime();
            long nextTime = next.getCreateTime();
            return !DateUtil.isInThisDay(nextTime, preTime);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.dateArea)
            RelativeLayout mDateArea;
            @BindView(R.id.weekArea)
            LinearLayout mWeekArea;
            @BindView(R.id.week)
            TextView mWeek;
            @BindView(R.id.date)
            TextView mDate;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.content)
            TextView mContent;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindDataWithView(DailyReport item, boolean theDifferentDate, Context context) {
                if (theDifferentDate) {
                    mDateArea.setVisibility(View.VISIBLE);
                    mWeekArea.setSelected(false);
                    mWeek.setText(getString(R.string.week_, DateUtil.getDayOfWeek(item.getCreateTime())));
                    int dayOfMonth = DateUtil.getDayOfMonth(item.getCreateTime());
                    if (dayOfMonth < 10) {
                        mDate.setText("0" + dayOfMonth);
                    } else {
                        mDate.setText(String.valueOf(dayOfMonth));
                    }
                } else {
                    mDateArea.setVisibility(View.GONE);
                }
                mTime.setText(DateUtil.formatDefaultStyleTime(item.getCreateTime()));
                if (TextUtils.isEmpty(item.getTitle())) {
                    mContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    mContent.setTextColor(ContextCompat.getColor(context, R.color.luckyText));
                    mContent.setText(Html.fromHtml(item.getContent().toString().trim()));
                } else {
                    mContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    mContent.setText(StrUtil.mergeTextWithRatioColorBold(item.getTitle(), Html.fromHtml(item.getContent()).toString().trim(), 0.86f,
                            ContextCompat.getColor(context, R.color.primaryText), ContextCompat.getColor(context, R.color.luckyText)));
                }
                mContent.setMaxLines(3);
                mContent.setEllipsize(TextUtils.TruncateAt.END);
                mContent.setOnClickListener(new View.OnClickListener() {
                    boolean flag = true;

                    @Override
                    public void onClick(View v) {
                        if (flag) {
                            flag = false;
                            mContent.setMaxLines(Integer.MAX_VALUE);
                            mContent.setEllipsize(null);
                        } else {
                            flag = true;
                            mContent.setMaxLines(3);
                            mContent.setEllipsize(TextUtils.TruncateAt.END);
                        }
                    }
                });
            }
        }
    }
}
