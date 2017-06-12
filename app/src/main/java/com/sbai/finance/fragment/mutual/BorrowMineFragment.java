package com.sbai.finance.fragment.mutual;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.activity.mutual.BorrowMineDetailsActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
import com.sbai.finance.model.mutual.BorrowMine;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;


public class BorrowMineFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, CustomSwipeRefreshLayout.OnLoadMoreListener {
    public static final int REQ_CODE_STATUS_CHANGE = 250;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private Unbinder unbinder;
    private Set<Integer> mSet;
    private int mPage = 0;
    private int mPageSize = 15;
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
        mSet = new HashSet<>();
        mBorrowMoneyAdapter = new BorrowMoneyAdapter(getActivity());
        mBorrowMoneyAdapter.setCallback(new BorrowMoneyAdapter.Callback() {
            @Override
            public void onAvatarBorrowMoneyClick(int userId) {
                if (LocalUser.getUser().isLogin()) {
                    Intent intent = new Intent(getActivity(), UserDataActivity.class);
                    intent.putExtra(Launcher.USER_ID, userId);
                    startActivityForResult(intent, REQ_CODE_USERDATA);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mBorrowMoneyAdapter);
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mBorrowMoneyAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BorrowMine item = (BorrowMine) parent.getItemAtPosition(position);
                if (item != null) {
                    Intent intent = new Intent(getActivity(), BorrowMineDetailsActivity.class);
                    intent.putExtra(Launcher.EX_PAYLOAD, item);
                    startActivityForResult(intent, REQ_CODE_STATUS_CHANGE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requestBorrowData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void requestBorrowData() {
        Client.getMyLoad(mPage, mPageSize).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<BorrowMine>>, List<BorrowMine>>() {
                    @Override
                    protected void onRespSuccessData(List<BorrowMine> data) {
                        updateBorrowData(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void updateBorrowData(List<BorrowMine> data) {
        stopRefreshAnimation();
        if (mSet.isEmpty()) {
            mBorrowMoneyAdapter.clear();
        }
        for (BorrowMine borrowMine : data) {
            if (mSet.add(borrowMine.getId())) {
                mBorrowMoneyAdapter.add(borrowMine);
            }
        }
        if (data.size() < 15) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }
        mBorrowMoneyAdapter.notifyDataSetChanged();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_STATUS_CHANGE && resultCode == RESULT_OK) {
            updateBorrowStatus((BorrowMine) data.getParcelableExtra(Launcher.EX_PAYLOAD));
        }
        if (requestCode == REQ_CODE_USERDATA && resultCode == RESULT_OK) {
            if (data != null) {
                WhetherAttentionShieldOrNot whetherAttentionShieldOrNot =
                        (WhetherAttentionShieldOrNot) data.getSerializableExtra(Launcher.EX_PAYLOAD_1);

                AttentionAndFansNumberModel attentionAndFansNumberModel =
                        (AttentionAndFansNumberModel) data.getSerializableExtra(Launcher.EX_PAYLOAD_2);

                if (attentionAndFansNumberModel != null && whetherAttentionShieldOrNot != null) {
                    for (int i = 0; i < mBorrowMoneyAdapter.getCount(); i++) {
                        BorrowMine borrowMine = mBorrowMoneyAdapter.getItem(i);
                        if (borrowMine.getUserId() == attentionAndFansNumberModel.getUserId()) {
                            if (whetherAttentionShieldOrNot.isFollow()) {
                                borrowMine.setIsAttention(2);
                                mBorrowMoneyAdapter.notifyDataSetChanged();
                            } else {
                                borrowMine.setIsAttention(1);
                                mBorrowMoneyAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    if (whetherAttentionShieldOrNot.isShield()) {
                        for (int i = 0; i < mBorrowMoneyAdapter.getCount(); i++) {
                            BorrowMine borrowMine = mBorrowMoneyAdapter.getItem(i);
                            if (borrowMine.getUserId() == attentionAndFansNumberModel.getUserId()) {
                                mBorrowMoneyAdapter.remove(borrowMine);
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateBorrowStatus(BorrowMine data) {
        if (data != null && mListView != null && mBorrowMoneyAdapter != null) {
            int first = mListView.getFirstVisiblePosition();
            int last = mListView.getLastVisiblePosition();
            for (int i = first; i <= last; i++) {
                BorrowMine borrowMine = mBorrowMoneyAdapter.getItem(i);
                if (borrowMine != null && borrowMine.getId() == data.getId()) {
                    borrowMine.setStatus(data.getStatus());
                    View childView = mListView.getChildAt(i - mListView.getFirstVisiblePosition());
                    if (childView != null) {
                        TextView status = (TextView) childView.findViewById(R.id.status);
                        status.setText(getActivity().getString(R.string.end));
                        status.setTextColor(ContextCompat.getColor(getActivity(), R.color.luckyText));
                    }
                    break;
                }
            }
        }
    }

    public static class BorrowMoneyAdapter extends ArrayAdapter<BorrowMine> {
        interface Callback {
            void onAvatarBorrowMoneyClick(int userId);
        }

        private Callback mCallback;

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        public BorrowMoneyAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_borrow_mine, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(getItem(position), getContext(), mCallback);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.isAttention)
            TextView mIsAttention;
            @BindView(R.id.status)
            TextView mStatus;
            @BindView(R.id.borrowMoneyContent)
            TextView mBorrowMoneyContent;
            @BindView(R.id.borrowingIcon)
            ImageView mBorrowingIcon;
            @BindView(R.id.needAmount)
            TextView mNeedAmount;
            @BindView(R.id.borrowDeadline)
            TextView mBorrowDeadline;
            @BindView(R.id.borrowInterest)
            TextView mBorrowInterest;
            @BindView(R.id.borrowingImg)
            ImageView mBorrowingImg;
            @BindView(R.id.circleMoreIcon)
            ImageView mCircleMoreIcon;
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.location)
            TextView mLocation;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(final BorrowMine item, Context context, final Callback callback) {
                if (item == null) return;

                Glide.with(context).load(item.getPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .transform(new GlideCircleTransform(context))
                        .into(mAvatar);
                boolean isSelf = item.getUserId() == LocalUser.getUser().getUserInfo().getId();
                mUserName.setText(item.getUserName());
                switch (item.getStatus()) {
                    case BorrowMine.STASTU_END_NO_HELP:
                    case BorrowMine.STATUS_END_CANCEL:
                    case BorrowMine.STATUS_END_NO_ALLOW:
                    case BorrowMine.STATUS_END_NO_CHOICE_HELP:
                    case BorrowMine.STATUS_END_REPAY:
                    case BorrowMine.STATUS_END_FIIL:
                        mStatus.setText(context.getString(R.string.end));
                        mStatus.setTextColor(ContextCompat.getColor(context, R.color.luckyText));
                        break;
                    case BorrowMine.STATUS_GIVE_HELP:
                    case BorrowMine.STATUS_NO_CHECKED:
                        mStatus.setTextColor(ContextCompat.getColor(context, R.color.redAssist));
                        if (isSelf) {
                            mStatus.setText(context.getString(R.string.wait_help));
                        } else {
                            mStatus.setText(context.getString(R.string.commit));
                        }
                        break;
                     case BorrowMine.STATUS_INTENTION:
                         mStatus.setTextColor(ContextCompat.getColor(context,R.color.redAssist));
                         if (isSelf){
                             mStatus.setText(context.getString(R.string.borrow_in_days,item.getConfirmDays()));
                         }else {
                             mStatus.setText(context.getString(R.string.borrow_out_days,item.getConfirmDays()));
                         }
                         break;
                    case BorrowMine.STATUS_INTENTION_OVER_TIME:
                        mStatus.setTextColor(ContextCompat.getColor(context, R.color.redAssist));
                        mStatus.setText(context.getString(R.string.over_time));
                        break;

                }
                if (item.getIsAttention() == 2) {
                    mIsAttention.setText(R.string.is_attention);
                } else {
                    mIsAttention.setText("");
                }
                mBorrowMoneyContent.setText(item.getContent());
                mNeedAmount.setText(context.getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(item.getMoney())));
                mBorrowDeadline.setText(context.getString(R.string.day, FinanceUtil.formatWithScaleNoZero(item.getDays())));
                mBorrowInterest.setText(context.getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(item.getInterest())));

                mPublishTime.setText(DateUtil.getFormatTime(item.getCreateDate()));
                if (TextUtils.isEmpty(item.getLocation())) {
                    mLocation.setText(R.string.no_location_information);
                } else {
                    mLocation.setText(item.getLocation());
                }
                mAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onAvatarBorrowMoneyClick(item.getUserId());
                        }
                    }
                });

                if (!TextUtils.isEmpty(item.getContentImg())) {
                    String[] images = item.getContentImg().split(",");
                    Glide.with(context).load(images[0])
                            .placeholder(R.drawable.ic_loading_pic)
                            .into(mBorrowingImg);
                    if (images.length >= 2) {
                        mCircleMoreIcon.setVisibility(View.VISIBLE);
                    } else {
                        mCircleMoreIcon.setVisibility(View.GONE);
                    }
                } else {
                    mBorrowingImg.setVisibility(View.GONE);
                    mCircleMoreIcon.setVisibility(View.GONE);
                }
            }
        }
    }

}