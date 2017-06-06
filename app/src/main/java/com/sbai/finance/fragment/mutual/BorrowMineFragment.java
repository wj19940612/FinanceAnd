package com.sbai.finance.fragment.mutual;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mutual.BorrowDetails;
import com.sbai.finance.view.CustomSwipeRefreshLayout;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class BorrowMineFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, CustomSwipeRefreshLayout.OnLoadMoreListener {
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private Unbinder unbinder;
    private Set<String> mSet;
    private int mPage;
    private BorrowMoneyAdapter mBorrowMoneyAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow_mine, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSet = new HashSet();
        mBorrowMoneyAdapter = new BorrowMoneyAdapter(getActivity());
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mBorrowMoneyAdapter);
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mBorrowMoneyAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void requestBorrowData() {
    }

    @Override
    public void onRefresh() {
        reset();
        requestBorrowData();
    }

    @Override
    public void onLoadMore() {
        requestBorrowData();
    }

    private void reset() {
        mPage = 0;
        mSet.clear();
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }

    public static class BorrowMoneyAdapter extends ArrayAdapter<BorrowDetails> {

        public BorrowMoneyAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_borrow_money, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.divider)
            View mDivider;
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.isAttention)
            TextView mIsAttention;
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.location)
            TextView mLocation;
            @BindView(R.id.needAmount)
            TextView mNeedAmount;
            @BindView(R.id.borrowTime)
            TextView mBorrowTime;
            @BindView(R.id.borrowInterest)
            TextView mBorrowInterest;
            @BindView(R.id.borrowMoneyContent)
            TextView mBorrowMoneyContent;
            @BindView(R.id.image1)
            ImageView mImage1;
            @BindView(R.id.image2)
            ImageView mImage2;
            @BindView(R.id.image3)
            ImageView mImage3;
            @BindView(R.id.image4)
            ImageView mImage4;
            @BindView(R.id.contentImg)
            LinearLayout mContentImg;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
            private void bindingData(BorrowDetails item,Context context){

            }
        }
    }
}
