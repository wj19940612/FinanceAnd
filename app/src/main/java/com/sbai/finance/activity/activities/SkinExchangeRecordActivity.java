package com.sbai.finance.activity.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.SkinExchangeRecord;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 皮肤兑换记录
 */

public class SkinExchangeRecordActivity extends BaseActivity implements CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String TYPE_ACTIVITIES = "TYPE_ACTIVITIES";
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
    private int mActivityId = 1;
    private HashSet<Integer> mSet;
    private SkinAdapter mSkinAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_skin_record_list);
        ButterKnife.bind(this);
        initData(getIntent());
        initListHeader();
        initListFooter();
        initListView();
        requestSkinRecordList();
    }

    private void initData(Intent intent) {
        mActivityId = intent.getIntExtra(TYPE_ACTIVITIES, -1);
    }

    private void initListHeader() {
        View view = new View(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Display.dp2Px(1, getResources()));
        view.setLayoutParams(params);
        mListView.addHeaderView(view);
    }

    private void initListFooter() {
        View view = new View(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Display.dp2Px(1, getResources()));
        view.setLayoutParams(params);
        mListView.addFooterView(view);
    }

    private void initListView() {
        mSet = new HashSet<>();
        scrollToTop(mTitleBar, mListView);
        mSkinAdapter = new SkinAdapter(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mSkinAdapter);
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mSkinAdapter);
    }


    @Override
    public void onLoadMore() {
        requestSkinRecordList();
    }

    @Override
    public void onRefresh() {
        reset();
        requestSkinRecordList();
    }

    private void requestSkinRecordList() {
        Client.getSkinList(mActivityId, mPageNo).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<SkinExchangeRecord>>, List<SkinExchangeRecord>>() {
                    @Override
                    protected void onRespSuccessData(List<SkinExchangeRecord> data) {
                        updateSkinRecordList(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                }).fireFree();
    }

    private void updateSkinRecordList(List<SkinExchangeRecord> data) {
        if (mSet.isEmpty()) {
            mSkinAdapter.clear();
        }
        for (SkinExchangeRecord skinExchangeRecord : data) {
            if (mSet.add(skinExchangeRecord.getId())) {
                mSkinAdapter.add(skinExchangeRecord);
            }
        }
        if (data.size() < mPageSize) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPageNo++;
        }
        mSkinAdapter.notifyDataSetChanged();
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

    static class SkinAdapter extends ArrayAdapter<SkinExchangeRecord> {

        public SkinAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_exchange_skin, null, true);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.skin)
            ImageView mSkin;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.ingot)
            TextView mIngot;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(SkinExchangeRecord item, Context context) {
                Glide.with(context).load(item.getProductUrl()).into(mSkin);
                mName.setText(item.getProductName());
                mTime.setText(DateUtil.format(item.getCreateTime()));
                mIngot.setText(context.getString(R.string.ingot_number_no_blank, item.getPrice()));
            }
        }
    }
}
