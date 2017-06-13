package com.sbai.finance.activity.mine;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FansActivity extends BaseActivity {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

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
        scrollToTop(mTitleBar, mListView);
        mListView.setEmptyView(mEmpty);
        mUserFansAdapter = new UserFansAdapter(this);
        mListView.setAdapter(mUserFansAdapter);
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
                            getActivity().getString(R.string.cancel_attention_dialog_title, userFansModel.getUserName()))
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
                mSwipeRefreshLayout.setLoadMoreEnable(true);
                requestUserAttentionList();
            }
        });

        mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
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
                        setResult(RESULT_OK);
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

        if (userFansModelList.size() < mPageSize) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mUserFansAdapter != null) {
                mUserFansAdapter.clear();
            }
        }
        stopRefreshAnimation();
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
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
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

            public void bindViewWithData(final UserFansModel item, final Context context, final OnUserFansClickListener onUserFansClickListener, final int position) {
                if (item == null) return;
                Glide.with(context).load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .bitmapTransform(new GlideCircleTransform(context))
                        .into(mUserHeadImage);

                mUserHeadImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Launcher.with(context, UserDataActivity.class).putExtra(Launcher.USER_ID, item.getUserId()).execute();
                    }
                });

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
