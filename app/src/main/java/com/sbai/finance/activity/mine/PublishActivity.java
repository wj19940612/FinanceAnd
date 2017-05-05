package com.sbai.finance.activity.mine;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.model.mine.UserPublishModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PublishActivity extends BaseActivity implements AbsListView.OnScrollListener {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView mFootView;
    private PublishAdapter mPublishAdapter;
    private int mPage;
    private int mUserId;
    private HashSet<Integer> mSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        mListView.setEmptyView(mEmpty);
        mSet = new HashSet<Integer>();
        mPublishAdapter = new PublishAdapter(getActivity());
        mListView.setAdapter(mPublishAdapter);
        mListView.setOnScrollListener(this);

        mUserId = getIntent().getIntExtra(Launcher.EX_PAYLOAD, -1);
        int userSex = getIntent().getIntExtra(Launcher.EX_PAYLOAD_1, 0);
        if (mUserId == -1) {
            mPublishAdapter.setIsHimSelf(true);
            mTitleBar.setTitle(R.string.mine_publish);
        } else {
            mTitleBar.setTitle(UserInfo.isGril(userSex) ? R.string.her_publish : R.string.his_publish);
            mPublishAdapter.setIsHimSelf(false);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                mSet.clear();
                requestUserPublishList();
            }
        });
        requestUserPublishList();
    }

    private void requestUserPublishList() {
        Client.getUserPublishList(mPage, Client.PAGE_SIZE, mUserId != -1 ? mUserId : null)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<UserPublishModel>>, List<UserPublishModel>>() {
                    @Override
                    protected void onRespSuccessData(List<UserPublishModel> data) {
                        updateUserPublishData(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void updateUserPublishData(List<UserPublishModel> userPublishModelList) {
        if (userPublishModelList == null) {
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
                    requestUserPublishList();
                }
            });
            mListView.addFooterView(mFootView);
        }

        if (userPublishModelList.size() < Client.PAGE_SIZE) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mPublishAdapter != null) {
                mPublishAdapter.clear();
            }
            stopRefreshAnimation();
        }
        for (UserPublishModel data : userPublishModelList) {
            if (mSet.add(data.getId())) {
                mPublishAdapter.add(data);
            }
        }
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
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

    static class PublishAdapter extends ArrayAdapter<UserPublishModel> {

        private Context mContext;
        private boolean isHimSelf;

        public PublishAdapter(@NonNull Context context) {
            super(context, 0);
            this.mContext = context;
        }

        public void setIsHimSelf(boolean isUserSelf) {
            this.isHimSelf = isUserSelf;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_publish, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext, isHimSelf);
            return convertView;

        }


        static class ViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.isAttention)
            TextView mIsAttention;
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.opinionContent)
            TextView mOpinionContent;
            @BindView(R.id.bigVarietyName)
            TextView mBigVarietyName;
            @BindView(R.id.varietyName)
            TextView mVarietyName;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.upDownPrice)
            TextView mUpDownPrice;
            @BindView(R.id.upDownPercent)
            TextView mUpDownPercent;
            @BindView(R.id.upDownArea)
            LinearLayout mUpDownArea;
            @BindView(R.id.replyCount)
            AppCompatTextView mReplyCount;
            @BindView(R.id.praiseCount)
            AppCompatTextView mPraiseCount;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(UserPublishModel item, Context context, boolean isHimSelf) {
                mUserName.setText(item.getUserName());
                Glide.with(context).load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .bitmapTransform(new GlideCircleTransform(context))
                        .into(mAvatar);
                mReplyCount.setText(context.getString(R.string.number, item.getReplyCount()));
                mPraiseCount.setText(context.getString(R.string.number, item.getPraiseCount()));
                mOpinionContent.setText(item.getContent());
                mVarietyName.setText(item.getVarietyName());
                mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
                mBigVarietyName.setText(item.getBigVarietyTypeName());
                if (!isHimSelf) {
                    mIsAttention.setText(item.isAttention() ? context.getString(R.string.is_attention) : "");
                }
                mLastPrice.setText(item.getLastPrice());
                mUpDownPrice.setText(item.getRisePrice());
                mUpDownPercent.setText(item.getRisePre());
            }
        }
    }
}
