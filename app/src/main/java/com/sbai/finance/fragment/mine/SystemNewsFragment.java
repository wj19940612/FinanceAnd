package com.sbai.finance.fragment.mine;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mine.HistoryNewsModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SystemNewsFragment extends BaseFragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Unbinder mBind;
    private TextView mFootView;
    private SystemNewsAdapter mSystemNewsAdapter;

    private int mPage;
    private int mSize = 15;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_refresh_listview, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEmpty.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bigevent_pic_noevent, 0, 0);
        mListView.setEmptyView(mEmpty);
        mSystemNewsAdapter = new SystemNewsAdapter(getActivity());
        mListView.setAdapter(mSystemNewsAdapter);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestSystemNewsList();
            }
        });

        requestSystemNewsList();
    }

    private void requestSystemNewsList() {
        Client.requestHistoryNews(HistoryNewsModel.NEW_TYPE_MUTUAL_HELP, mPage, mSize)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<HistoryNewsModel>>, List<HistoryNewsModel>>() {
                    @Override
                    protected void onRespSuccessData(List<HistoryNewsModel> data) {
                        for (HistoryNewsModel his : data) {
                            Log.d(TAG, " 系统消息" + his.toString());
                        }
                        updateSystemNewsListData(data);
                    }
                })
                .fire();
//        ArrayList<HistoryNewsModel> systemNewsModels = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            systemNewsModels.add(new HistoryNewsModel());
//        }
//        updateSystemNewsListData(systemNewsModels);
    }

    private void updateSystemNewsListData(List<HistoryNewsModel> systemNewsModels) {
        if (systemNewsModels == null) {
            stopRefreshAnimation();
            return;
        }
        if (mFootView == null) {
            mFootView = new TextView(getActivity());
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            mFootView.setPadding(padding, padding, padding, padding);
            mFootView.setText(getText(R.string.load_more));
            mFootView.setGravity(Gravity.CENTER);
            mFootView.setTextColor(Color.WHITE);
            mFootView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
            mFootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSwipeRefreshLayout.isRefreshing()) return;
                    mPage++;
                    requestSystemNewsList();
                }
            });
            mListView.addFooterView(mFootView);
        }

        if (systemNewsModels.size() < mSize) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mSystemNewsAdapter != null) {
                mSystemNewsAdapter.clear();
                mSystemNewsAdapter.notifyDataSetChanged();
            }
            stopRefreshAnimation();
        }
        mSystemNewsAdapter.addAll(systemNewsModels);
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HistoryNewsModel systemNewsModel = (HistoryNewsModel) parent.getAdapter().getItem(position);
        if (systemNewsModel != null) {
            ToastUtil.curt("点击了 " + "斯大林欢迎你");
        }
    }

    static class SystemNewsAdapter extends ArrayAdapter<HistoryNewsModel> {

        private Context mContext;

        public SystemNewsAdapter(@NonNull Context context) {
            super(context, 0);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_system_news, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindViewWithData(getItem(position), mContext, position);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.title)
            AppCompatTextView mTitle;
            @BindView(R.id.time)
            AppCompatTextView mTime;
            @BindView(R.id.content)
            TextView mContent;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindViewWithData(HistoryNewsModel item, Context context, int position) {
                if (position % 2 == 0) {
                    mTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_news_succeed, 0, 0, 0);
                }
                mTitle.setText("系统消息");
                mTime.setText(DateUtil.getFormatTime(1493024100000L));
                mContent.setText("斯大林欢迎你");
            }
        }
    }
}
