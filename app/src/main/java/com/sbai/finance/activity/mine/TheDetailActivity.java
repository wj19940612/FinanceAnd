package com.sbai.finance.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.Detail;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by linrongfang on 2017/5/8.
 */

public class TheDetailActivity extends BaseActivity implements CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private DetailAdapter mDetailAdapter;
    private List<Detail> mDetailList;

    private int mPageSize = 15;
    private int mPageNo = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        mDetailList = new ArrayList<>();
        mDetailAdapter = new DetailAdapter(this, mDetailList);
        mListView.setAdapter(mDetailAdapter);
        mListView.setEmptyView(mEmpty);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mDetailAdapter);
    }

    private void updateDetail(List<Detail> detailList) {
        if (detailList == null) {
            return;
        }
        stopRefreshAnimation();
        mDetailAdapter.addAll(detailList);
        if (detailList.size() < mPageSize) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPageNo++;
        }
        mDetailAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        reset();
        //request
    }

    @Override
    public void onLoadMore() {
        //request
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
        mDetailAdapter.clear();
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }


    private static class DetailAdapter extends BaseAdapter {
        private Context mContext;
        private List<Detail> mList;

        public DetailAdapter(Context context, List<Detail> list) {
            mContext = context;
            mList = list;
        }

        public void addAll(List<Detail> detailList) {
            mList.addAll(detailList);
            notifyDataSetChanged();
        }

        public void clear() {
            mList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_detail, null, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            boolean needTitle = needTitle(position);
            holder.bindDataWithView((Detail) getItem(position), needTitle);
            return convertView;
        }

        private boolean needTitle(int position) {
            if (position == 0) {
                return true;
            }

            if (position < 0) {
                return false;
            }
            Detail pre = mList.get(position - 1);
            Detail next = mList.get(position);
            //判断两个bean 相差的时间 如果跨了一个月就要显示标题  时间格式转为yyyyMM
            return false;
        }

        static class ViewHolder {
            @BindView(R.id.head)
            TextView mHead;
            @BindView(R.id.paymentContent)
            TextView mPaymentContent;
            @BindView(R.id.paymentTime)
            TextView mPaymentTime;
            @BindView(R.id.paymentType)
            TextView mPaymentType;
            @BindView(R.id.paymentNum)
            TextView mPaymentNum;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(Detail detail, boolean needTitle) {

            }
        }
    }
}
