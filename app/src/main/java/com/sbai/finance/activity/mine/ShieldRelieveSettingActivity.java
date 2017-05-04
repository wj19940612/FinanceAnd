package com.sbai.finance.activity.mine;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.mine.ShieldedUserModel;
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

public class ShieldRelieveSettingActivity extends BaseActivity implements AbsListView.OnScrollListener {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView mFootView;
    private ShieldRelieveAdapter mShieldRelieveAdapter;

    private int mPage = 0;
    private int mPageSize = 15;
    private HashSet<Integer> mSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shield_relieve);
        ButterKnife.bind(this);
        mSet = new HashSet<>();
        mListView.setEmptyView(mEmpty);
        mShieldRelieveAdapter = new ShieldRelieveAdapter(getActivity());
        mListView.setAdapter(mShieldRelieveAdapter);
        mListView.setOnScrollListener(this);
        mShieldRelieveAdapter.setOnRelieveShieldClickListener(new ShieldRelieveAdapter.OnRelieveShieldClickListener() {
            @Override
            public void onRelieveShield(final ShieldedUserModel.DataBean shieldedUserModel) {
                SmartDialog.with(getActivity(),
                        getString(R.string.relieve_shield_dialog_content, shieldedUserModel.getShielduserName())
                        , getString(R.string.relieve_shield_dialog_title, shieldedUserModel.getShielduserName()))
                        .setPositive(android.R.string.ok, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                                relieveShield(shieldedUserModel);
                            }
                        })
                        .setTitleMaxLines(2)
                        .setNegative(android.R.string.cancel)
                        .show();
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mPage = 0;
                requestShieldUserList();
            }
        });
        requestShieldUserList();
    }

    private void relieveShield(final ShieldedUserModel.DataBean shieldedUserModel) {
        Client.shieldOrRelieveShieldUser(100, 1)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            mShieldRelieveAdapter.remove(shieldedUserModel);
                        }
                    }
                })
                .fire();
    }

    private void requestShieldUserList() {
        Client.getShieldList(mPage, mPageSize)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ShieldedUserModel>, ShieldedUserModel>() {
                    @Override
                    protected void onRespSuccessData(ShieldedUserModel data) {
                        updateShieldUserData(data.getData());
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }


    private void updateShieldUserData(List<ShieldedUserModel.DataBean> shieldUserList) {
        if (shieldUserList == null) {
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
                    requestShieldUserList();
                }
            });
            mListView.addFooterView(mFootView);
        }

        if (shieldUserList.size() < mPageSize) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mShieldRelieveAdapter != null) {
                mShieldRelieveAdapter.clear();
                mShieldRelieveAdapter.notifyDataSetChanged();
            }
            stopRefreshAnimation();
        }

        for (ShieldedUserModel.DataBean data : shieldUserList) {
            if (mSet.add(data.getId())) {
                mShieldRelieveAdapter.add(data);
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

    static class ShieldRelieveAdapter extends ArrayAdapter<ShieldedUserModel.DataBean> {

        OnRelieveShieldClickListener mOnRelieveShieldClickListener;
        Context mContext;

        interface OnRelieveShieldClickListener {
            void onRelieveShield(ShieldedUserModel.DataBean shieldedUserModel);
        }

        public ShieldRelieveAdapter(@NonNull Context context) {
            super(context, 0);
            this.mContext = context;
        }

        public void setOnRelieveShieldClickListener(OnRelieveShieldClickListener listener) {
            this.mOnRelieveShieldClickListener = listener;
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
            viewHolder.bindViewWithData(getItem(position), mContext, mOnRelieveShieldClickListener);
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

            public void bindViewWithData(final ShieldedUserModel.DataBean item, Context context, final OnRelieveShieldClickListener onRelieveShieldClickListener) {
                if (item == null) return;
                Glide.with(context).load(item.getShielduserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .bitmapTransform(new GlideCircleTransform(context))
                        .into(mUserHeadImage);

                mUserName.setText(item.getShielduserName());
                mRelive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onRelieveShieldClickListener != null) {
                            onRelieveShieldClickListener.onRelieveShield(item);
                        }
                    }
                });
            }
        }
    }
}
