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
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.mine.ShieldedUserModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.SmartDialog;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
        ButterKnife.bind(this);
        mListView.setEmptyView(mEmpty);
        mRelieveAttentionAdapter = new RelieveAttentionAdapter(this);
        mListView.setAdapter(mRelieveAttentionAdapter);
        mListView.setOnScrollListener(this);
        mRelieveAttentionAdapter.setOnRelieveAttentionClickListener(new RelieveAttentionAdapter.OnRelieveAttentionClickListener() {
            @Override
            public void onRelieveAttention(final ShieldedUserModel shieldedUserModel) {
                SmartDialog.with(getActivity(),
                        getString(R.string.relieve_shield_dialog_content, shieldedUserModel.getUserName())
                        , getString(R.string.relieve_shield_dialog_title, shieldedUserModel.getUserName()))
                        .setPositive(android.R.string.ok, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                                ToastUtil.curt("移除 " + shieldedUserModel.getUserName());
                                relieveAttentionUser(shieldedUserModel);
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
                requestUserAttentionList();
            }
        });
        requestUserAttentionList();
    }

    private void relieveAttentionUser(final ShieldedUserModel shieldedUserModel) {
        Client.attentionOrRelieveAttentionUser(100, 1)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            mRelieveAttentionAdapter.remove(shieldedUserModel);
                        }
                    }
                })
                .fire();
    }

    private String url[] = new String[]{"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1492510917267&di=d5b3057b37d5c83964230849e42cfead&imgtype=0&src=http%3A%2F%2Fpic1.cxtuku.com%2F00%2F15%2F11%2Fb998b8878108.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1492510860938&di=64f5b45b80c90746513b448207191e4f&imgtype=0&src=http%3A%2F%2Fpic7.nipic.com%2F20100613%2F3823726_085130049412_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1492510590388&di=034d5a13126feef4ed18beff5dfe9e50&imgtype=0&src=http%3A%2F%2Fpic38.nipic.com%2F20140228%2F8821914_204428973000_2.jpg"};

    private void requestUserAttentionList() {
//        ArrayList<ShieldedUserModel> dataList = new ArrayList<>();
//        for (int i = 0; i < url.length; i++) {
//            dataList.add(new ShieldedUserModel("用户 " + i + i, url[i]));
//        }
//        updateShieldUserData(dataList);
    }


    private void updateShieldUserData(ArrayList<ShieldedUserModel> shieldUserList) {
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
//                    mPageNo++;
                    requestUserAttentionList();
                }
            });
            mListView.addFooterView(mFootView);
        }

//        if (economicCircleNewModels.size() < mPageSize) {
        if (shieldUserList.size() < 15) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mRelieveAttentionAdapter != null) {
                mRelieveAttentionAdapter.clear();
                mRelieveAttentionAdapter.notifyDataSetChanged();
            }
            stopRefreshAnimation();
        }
        mRelieveAttentionAdapter.addAll(shieldUserList);
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

    static class RelieveAttentionAdapter extends ArrayAdapter<ShieldedUserModel> {

        OnRelieveAttentionClickListener mOnRelieveAttentionClickListener;
        Context mContext;

        interface OnRelieveAttentionClickListener {
            void onRelieveAttention(ShieldedUserModel shieldedUserModel);
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

            public void bindViewWithData(final ShieldedUserModel item, Context context, final OnRelieveAttentionClickListener onRelieveAttentionClickListener) {
                if (!TextUtils.isEmpty(item.getUserHeadImage())) {
                    Glide.with(context).load(item.getUserHeadImage())
                            .placeholder(R.mipmap.ic_launcher_round)
                            .bitmapTransform(new GlideCircleTransform(context))
                            .into(mUserHeadImage);
                }
                mRelive.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_follow_relieve, 0, 0);
                mUserName.setText(item.getUserName());
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
