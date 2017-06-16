package com.sbai.finance.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.economiccircle.OpinionDetailsActivity;
import com.sbai.finance.fragment.dialog.ChooseEconomicCircleNewsDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.OpinionDetails;
import com.sbai.finance.model.mine.UserPublishModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PublishActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    public static final int REQ_CODE_OPINION_DETAILS = 1001;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private PublishAdapter mPublishAdapter;
    private int mPage;
    private int mUserId;
    private HashSet<Integer> mSet;
    private List<UserPublishModel> mUserPublishModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        initData();
        scrollToTop(mTitleBar, mListView);

        if (mUserId == -1 || mUserId == LocalUser.getUser().getUserInfo().getId()) {
            mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ChooseEconomicCircleNewsDialogFragment().show(getSupportFragmentManager());
                }
            });
        }

        initSwipeRefreshLayout();
        requestUserPublishList();
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                mSet.clear();
                mSwipeRefreshLayout.setLoadMoreEnable(true);
                requestUserPublishList();
            }
        });

        mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestUserPublishList();
            }
        });
    }

    private void initData() {
        mListView.setEmptyView(mEmpty);
        mSet = new HashSet<Integer>();
        mPublishAdapter = new PublishAdapter(getActivity());
        mListView.setAdapter(mPublishAdapter);
        mListView.setOnItemClickListener(this);
        mUserId = getIntent().getIntExtra(Launcher.EX_PAYLOAD, -1);
        int userSex = getIntent().getIntExtra(Launcher.EX_PAYLOAD_1, 0);
        if (mUserId == -1 || mUserId == LocalUser.getUser().getUserInfo().getId()) {
            mPublishAdapter.setIsHimSelf(true);
            mTitleBar.setTitle(R.string.my_publish);
        } else {
            mTitleBar.setTitle(R.string.his_publish);
            mPublishAdapter.setIsHimSelf(false);
            mTitleBar.setRightVisible(false);
        }
    }

    private void requestUserPublishList() {
        Client.getUserPublishList(mPage, (mUserId != -1 && mUserId != LocalUser.getUser().getUserInfo().getId()) ? mUserId : null)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<UserPublishModel>>, List<UserPublishModel>>() {
                    @Override
                    protected void onRespSuccessData(List<UserPublishModel> data) {
                        mUserPublishModelList = data;
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

        if (userPublishModelList.size() < Client.DEFAULT_PAGE_SIZE) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mPublishAdapter != null) {
                mPublishAdapter.clear();
            }
        }
        stopRefreshAnimation();

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

        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserPublishModel item = (UserPublishModel) parent.getAdapter().getItem(position);
        if (item != null) {
            Launcher.with(getActivity(), OpinionDetailsActivity.class)
                    .putExtra(Launcher.EX_PAYLOAD, item.getId())
                    .putExtra(Launcher.EX_PAYLOAD_2, false)
                    .executeForResult(REQ_CODE_OPINION_DETAILS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_OPINION_DETAILS && resultCode == RESULT_OK) {
            if (data != null) {
                OpinionDetails opinionDetails = (OpinionDetails) data.getSerializableExtra(Launcher.EX_PAYLOAD);
                if (opinionDetails != null) {
                    for (UserPublishModel userPublishModel : mUserPublishModelList) {
                        if (userPublishModel.getId() == opinionDetails.getId()) {
                            userPublishModel.setPraiseCount(opinionDetails.getPraiseCount());
                            userPublishModel.setReplyCount(opinionDetails.getReplyCount());
                        }
                    }
                    mPublishAdapter.notifyDataSetChanged();
                }
            }
        }
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_economic_circle_opinion, parent, false);
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
            @BindView(R.id.label)
            ImageView mLabel;


            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(UserPublishModel item, Context context, boolean isHimSelf) {
                mUserName.setText(item.getUserName());
                Glide.with(context).load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .bitmapTransform(new GlideCircleTransform(context))
                        .into(mAvatar);
                mVarietyName.setText(item.getVarietyName());
                mOpinionContent.setText(item.getContent());
                mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
                mBigVarietyName.setText(context.getString(R.string.big_variety_name, item.getBigVarietyTypeName()));
                if (!isHimSelf) {
                    mIsAttention.setText(item.isAttention() ? context.getString(R.string.is_attention) : "");
                }

                if (item.getDirection() == 1) {
                    if (item.getGuessPass() == 1) {
                        mLabel.setBackgroundResource(R.drawable.ic_opinion_up_succeed);
                    } else if (item.getGuessPass() == 2) {
                        mLabel.setBackgroundResource(R.drawable.ic_opinion_up_failed);
                    } else {
                        mLabel.setBackgroundResource(R.drawable.ic_opinion_up);
                    }
                } else {
                    if (item.getGuessPass() == 1) {
                        mLabel.setBackgroundResource(R.drawable.ic_opinion_down_succeed);
                    } else if (item.getGuessPass() == 2) {
                        mLabel.setBackgroundResource(R.drawable.ic_opinion_down_failed);
                    } else {
                        mLabel.setBackgroundResource(R.drawable.ic_opinion_down);
                    }
                }
            }
        }
    }
}
