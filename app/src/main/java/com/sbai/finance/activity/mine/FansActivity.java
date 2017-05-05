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
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.mine.UserFansModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.view.SmartDialog;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FansActivity extends BaseActivity implements AbsListView.OnScrollListener {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView mFootView;
    private UserFansAdapter mUserFansAdapter;

    private HashSet<Integer> mSet;
    private int mPage;
    private int mPageSize = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans);
        ButterKnife.bind(this);
        mSet = new HashSet<>();
        mListView.setEmptyView(mEmpty);
        mUserFansAdapter = new UserFansAdapter(this);
        mListView.setAdapter(mUserFansAdapter);
        mListView.setOnScrollListener(this);
        mUserFansAdapter.setOnUserFansClickListener(new UserFansAdapter.OnUserFansClickListener() {
            @Override
            public void onFansClick(final UserFansModel userFansModel, final int position) {
                if (userFansModel.isNotAttention()) {
                    SmartDialog.with(getActivity(),
                            getActivity().getString(R.string.if_attention, userFansModel.getUserName()))
                            .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                @Override
                                public void onClick(Dialog dialog) {
                                    dialog.dismiss();
                                    relieveAttentionUser(userFansModel, position);
                                }
                            })
                            .setMessageTextSize(16)
                            .setNegative(R.string.cancel)
                            .show();
                } else {
                    SmartDialog.with(getActivity(),
                            getActivity().getString(R.string.if_not_attention, userFansModel.getUserName()))
                            .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                @Override
                                public void onClick(Dialog dialog) {
                                    dialog.dismiss();
                                    relieveAttentionUser(userFansModel, position);
                                }
                            })
                            .setMessageTextSize(16)
                            .setNegative(R.string.cancel)
                            .show();
                }
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

    private void relieveAttentionUser(final UserFansModel userFansModel, final int position) {
        Client.attentionOrRelieveAttentionUser(userFansModel.getUserId(),
                userFansModel.isNotAttention() ? 0 : 1)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        mUserFansAdapter.remove(userFansModel);
                        userFansModel.setOther(userFansModel.isNotAttention() ? 0 : 1);
                        mUserFansAdapter.insert(userFansModel, position);
                    }
                })
                .fire();
    }


    private void requestUserAttentionList() {
        Client.getUserFansList(mPage, mPageSize)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ArrayList<UserFansModel>>, ArrayList<UserFansModel>>(false) {
                    @Override
                    protected void onRespSuccessData(ArrayList<UserFansModel> data) {
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


    private void updateShieldUserData(ArrayList<UserFansModel> userFansModelList) {
        if (userFansModelList == null) {
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

        if (userFansModelList.size() < mPageSize) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mUserFansAdapter != null) {
                mUserFansAdapter.clear();
                mUserFansAdapter.notifyDataSetChanged();
            }
            stopRefreshAnimation();
        }
        for (UserFansModel data : userFansModelList) {
            if (mSet.add(data.getId())) {
                mUserFansAdapter.add(data);
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

    static class UserFansAdapter extends ArrayAdapter<UserFansModel> {

        interface OnUserFansClickListener {
            void onFansClick(UserFansModel userFansModel, int position);
        }

        public void setOnUserFansClickListener(OnUserFansClickListener onUserFansClickListener) {
            this.mOnUserFansClickListener = onUserFansClickListener;
        }

        private Context mContext;
        private OnUserFansClickListener mOnUserFansClickListener;

        public UserFansAdapter(@NonNull Context context) {
            super(context, 0);
            this.mContext = context;
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
            viewHolder.bindViewWithData(getItem(position), mContext, mOnUserFansClickListener, position);
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

            public void bindViewWithData(final UserFansModel item, Context context, final OnUserFansClickListener onUserFansClickListener, final int position) {
                if (item == null) return;
                Glide.with(context).load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .bitmapTransform(new GlideCircleTransform(context))
                        .into(mUserHeadImage);

                if (item.isNotAttention()) {
                    mRelive.setText(R.string.attention);
                    mRelive.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fans_follow, 0, 0);
                } else {
                    mRelive.setText(R.string.is_attention);
                    mRelive.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fans_followed, 0, 0);
                }

                mUserName.setText(item.getUserName());
                mRelive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onUserFansClickListener != null) {
                            onUserFansClickListener.onFansClick(item, position);
                        }
                    }
                });
            }
        }
    }
}
