package com.sbai.finance.activity.mine;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.mine.UserAttentionModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.view.SmartDialog;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AttentionActivity extends BaseActivity implements AbsListView.OnScrollListener {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView mFootView;
    private RelieveAttentionAdapter mRelieveAttentionAdapter;
    private HashSet<Integer> mSet;

    private int mPage = 0;
    private int mPageSize = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
        ButterKnife.bind(this);
        mSet = new HashSet<>();
        mListView.setEmptyView(mEmpty);
        mRelieveAttentionAdapter = new RelieveAttentionAdapter(this);
        mListView.setAdapter(mRelieveAttentionAdapter);
        mListView.setOnScrollListener(this);
        mRelieveAttentionAdapter.setOnRelieveAttentionClickListener(new RelieveAttentionAdapter.OnRelieveAttentionClickListener() {
            @Override
            public void onRelieveAttention(final UserAttentionModel userAttentionModel) {
                SmartDialog.with(getActivity(),
                        getString(R.string.if_not_attention, userAttentionModel.getFollowuserName()))
                        .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                                relieveAttentionUser(userAttentionModel);
                            }
                        })
                        .setMessageTextSize(16)
                        .setNegative(R.string.cancel)
                        .show();
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mPage = 0;
                requestUserAttentionList();
            }
        });
        requestUserAttentionList();
    }

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        return super.getDelegate();
    }

    private void relieveAttentionUser(final UserAttentionModel userAttentionModel) {
        Client.attentionOrRelieveAttentionUser(userAttentionModel.getFollowUserId(), 1)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            mRelieveAttentionAdapter.remove(userAttentionModel);
                            setResult(RESULT_OK);
                        }
                    }
                })
                .fire();
    }


    private void requestUserAttentionList() {
        Client.getUserAttentionList(mPage, mPageSize)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<UserAttentionModel>>, List<UserAttentionModel>>(false) {
                    @Override
                    protected void onRespSuccessData(List<UserAttentionModel> data) {
                        updateShieldUserData(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                })
                .fire();

    }


    private void updateShieldUserData(List<UserAttentionModel> userAttentionModelList) {
        if (userAttentionModelList == null) {
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
                    requestUserAttentionList();
                }
            });
            mListView.addFooterView(mFootView);
        }

        if (userAttentionModelList.size() < mPageSize) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mRelieveAttentionAdapter != null) {
                mRelieveAttentionAdapter.clear();
            }
            stopRefreshAnimation();
        }

        for (UserAttentionModel data : userAttentionModelList) {
            if (mSet.add(data.getId())) {
                mRelieveAttentionAdapter.add(data);
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

    static class RelieveAttentionAdapter extends ArrayAdapter<UserAttentionModel> {

        OnRelieveAttentionClickListener mOnRelieveAttentionClickListener;
        Context mContext;

        interface OnRelieveAttentionClickListener {
            void onRelieveAttention(UserAttentionModel userAttentionModel);
        }

        public RelieveAttentionAdapter(@NonNull Context context) {
            super(context, 0);
            this.mContext = context;
        }

        public void setOnRelieveAttentionClickListener(OnRelieveAttentionClickListener listener) {
            this.mOnRelieveAttentionClickListener = listener;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_shield_relieve, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindViewWithData(getItem(position), mContext, mOnRelieveAttentionClickListener);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.userHeadImage)
            AppCompatImageView mUserHeadImage;
            @BindView(R.id.userName)
            AppCompatTextView mUserName;
            @BindView(R.id.relive)
            AppCompatTextView mRelive;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindViewWithData(final UserAttentionModel item, Context context, final OnRelieveAttentionClickListener onRelieveAttentionClickListener) {
                if (item == null) return;
                Glide.with(context).load(item.getFollowUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .bitmapTransform(new GlideCircleTransform(context))
                        .into(mUserHeadImage);
                mRelive.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_follow_relieve, 0, 0);
                mUserName.setText(item.getFollowuserName());
                mRelive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onRelieveAttentionClickListener != null) {
                            onRelieveAttentionClickListener.onRelieveAttention(item);
                        }
                    }
                });
            }
        }
    }
}
