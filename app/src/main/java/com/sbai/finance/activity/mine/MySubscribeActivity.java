package com.sbai.finance.activity.mine;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.miss.RadioStationListActivity;
import com.sbai.finance.model.mine.MyCollect;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.ImageTextUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.glide.GlideApp;
import com.sbai.httplib.ApiError;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017\11\20 0020.
 */

public class MySubscribeActivity extends BaseActivity {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private SubscribeAdapter mSubscribeAdapter;
    private int mPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_subscribe);
        ButterKnife.bind(this);

        mSubscribeAdapter = new SubscribeAdapter(this);
//        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mSubscribeAdapter);
        initSwipeRefreshLayout();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCollect radioInfo = (MyCollect) parent.getAdapter().getItem(position);
                if (radioInfo.getShow() == 0) {//下架的电台不让点击
                    return;
                }
                Launcher.with(MySubscribeActivity.this, RadioStationListActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, Integer.valueOf(radioInfo.getDataId()))
                        .execute();

            }
        });
        requestSubscribeList(true);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestSubscribeList(true);
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                mSwipeRefreshLayout.setLoadMoreEnable(true);
                requestSubscribeList(true);
            }
        });

        mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestSubscribeList(false);
                    }
                }, 1000);
            }
        });
    }

    private void requestSubscribeList(final boolean isRefresh) {
        Client.requestMyCollection(MyCollect.COLLECTI_TYPE_RADIO, mPage).setTag(TAG).setCallback(new Callback2D<Resp<List<MyCollect>>, List<MyCollect>>() {
            @Override
            protected void onRespSuccessData(List<MyCollect> data) {
                if (data.size() == 0 && mPage == 0) {
                    stopRefreshAnimation();
                    mListView.setVisibility(View.GONE);
                    mEmpty.setVisibility(View.VISIBLE);
                } else {
                    mEmpty.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    updateSubscribeList(data, isRefresh);
                }
            }

            @Override
            public void onFailure(ApiError apiError) {
                super.onFailure(apiError);
                stopRefreshAnimation();
                if (mPage == 0) {
                    mEmpty.setVisibility(View.VISIBLE);
                    mSubscribeAdapter.clear();
                    mSubscribeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }).fire();
    }

    private void updateSubscribeList(List<MyCollect> data, boolean isRefresh) {
        if (data == null) {
            stopRefreshAnimation();
            return;
        }

        if (data.size() < Client.DEFAULT_PAGE_SIZE) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mSwipeRefreshLayout.setLoadMoreEnable(true);
            mPage++;
        }

        if (isRefresh) {
            if (mSubscribeAdapter != null) {
                mSubscribeAdapter.clear();
            }
        }

        stopRefreshAnimation();

        for (MyCollect collect : data) {
            mSubscribeAdapter.add(collect);
        }

    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }

    public static class SubscribeAdapter extends ArrayAdapter<MyCollect> {
        public SubscribeAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_mine_subscribe, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.cover)
            ImageView mCover;
            @BindView(R.id.subtitle)
            TextView mSubtitle;
            @BindView(R.id.title)
            TextView mTitle;
            @BindView(R.id.number)
            TextView mNumber;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.iconView)
            View mIconView;
            @BindView(R.id.content)
            CardView mContent;
            @BindView(R.id.coverRL)
            RelativeLayout mCoverRL;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(MyCollect radioInfo, Context context) {
                GlideApp.with(context).load(radioInfo.getRadioCover())
                        .placeholder(R.drawable.ic_default_image)
                        .circleCrop().listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mCoverRL.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mCoverRL.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                        .into(mCover);
                mTitle.setText(radioInfo.getRadioName());
                setSpanIconText(mSubtitle, Html.fromHtml(radioInfo.getRadioIntroduction()).toString(), context);
                mNumber.setText(String.valueOf(radioInfo.getListenNumber()));
                if (radioInfo.getShow() == 1) {//显示
                    mTime.setText(DateUtil.formatDefaultStyleTime(radioInfo.getSubscribeTime()));
                } else if (radioInfo.getShow() == 0) {//下架
                    mTime.setText("该电台已下架");
                }
                if (radioInfo.getIsRead() != 0) {
                    mIconView.setVisibility(View.GONE);
                } else {
                    mIconView.setVisibility(View.VISIBLE);
                }
            }

            private void setSpanIconText(TextView textView, String str, Context context) {
                SpannableString ss = new SpannableString("  " + str);
                ImageTextUtil.CenterImageSpan span = new ImageTextUtil.CenterImageSpan(context, R.drawable.ic_miss_profile_play_small);
                ss.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                textView.setText(ss);
            }
        }
    }
}
