package com.sbai.finance.fragment.mutual;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.sbai.finance.activity.economiccircle.BorrowMoneyDetailsActivity;
import com.sbai.finance.activity.home.BorrowMoneyActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.BorrowMoney;
import com.sbai.finance.model.economiccircle.BorrowMoneyDetails;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
import com.sbai.finance.model.mutual.BorrowDetails;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class BorrowMoneyFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, CustomSwipeRefreshLayout.OnLoadMoreListener {
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private Unbinder unbinder;
    private Set mSet;
    private int mPage = 0;
    private int mPageSize = 15;
    private BorrowMoneyAdapter mBorrowMoneyAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow_money, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSet = new HashSet();
        mBorrowMoneyAdapter = new BorrowMoneyAdapter(getActivity());
        mBorrowMoneyAdapter.setCallback(new BorrowMoneyAdapter.Callback() {
            @Override
            public void onAvatarBorrowMoneyClick(BorrowMoney borrowMoney) {
                if (LocalUser.getUser().isLogin()) {
                    Intent intent = new Intent(getActivity(),UserDataActivity.class);
                    intent.putExtra(Launcher.USER_ID,borrowMoney.getUserId());
                    startActivityForResult(intent,REQ_CODE_USERDATA);
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
                BorrowMoney item = (BorrowMoney) parent.getItemAtPosition(position);
                if (item != null) {
                    Intent intent = new Intent(getActivity(),BorrowMoneyDetailsActivity.class);
                    intent.putExtra(Launcher.EX_PAYLOAD,item.getDataId());
                    startActivityForResult(intent,REQ_CODE_USERDATA);
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
        Client.getBorrowMoneyList(mPage, mPageSize).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<BorrowMoney>>, List<BorrowMoney>>() {
                    @Override
                    protected void onRespSuccessData(List<BorrowMoney> borrowMoneyList) {
                        updateBorrowData(borrowMoneyList);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
    }
    private void updateBorrowData(List<BorrowMoney> data){
        stopRefreshAnimation();
        if (mSet.isEmpty()){
            mBorrowMoneyAdapter.clear();
        }
        for (BorrowMoney borrowMoney:data){
            if (mSet.add(borrowMoney.getId())){
                mBorrowMoneyAdapter.add(borrowMoney);
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

    public static class BorrowMoneyAdapter extends ArrayAdapter<BorrowMoney> {
        interface Callback{
           void onAvatarBorrowMoneyClick(BorrowMoney borrowMoney);
        }
        private Callback mCallback;
        public void setCallback(Callback callback){
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_borrow_money, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(getItem(position), getContext(),mCallback);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.isAttention)
            TextView mIsAttention;
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
            private void bindingData(final BorrowMoney item, Context context, final Callback callback){
                if (item == null) return;

                Glide.with(context).load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .transform(new GlideCircleTransform(context))
                        .into(mAvatar);

                mUserName.setText(item.getUserName());

                if (item.getIsAttention() == 2) {
                    mIsAttention.setText(R.string.is_attention);
                } else {
                    mIsAttention.setText("");
                }
                mBorrowMoneyContent.setText(item.getContent());
                mNeedAmount.setText(context.getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(item.getMoney())));
                mBorrowDeadline.setText(context.getString(R.string.day, FinanceUtil.formatWithScaleNoZero(item.getDays())));
                mBorrowInterest.setText(context.getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(item.getInterest())));

                mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
                if (TextUtils.isEmpty(item.getLocation())) {
                    mLocation.setText(R.string.no_location_information);
                } else {
                    mLocation.setText(item.getLocation());
                }

                mAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onAvatarBorrowMoneyClick(item);
                        }
                    }
                });

                if (!TextUtils.isEmpty(item.getContentImg())) {
                    String[] images = item.getContentImg().split(",");
                    mBorrowingImg.setVisibility(View.VISIBLE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_USERDATA && resultCode == RESULT_OK) {
            if (data != null) {
                WhetherAttentionShieldOrNot whetherAttentionShieldOrNot =
                        (WhetherAttentionShieldOrNot) data.getSerializableExtra(Launcher.EX_PAYLOAD_1);

                AttentionAndFansNumberModel attentionAndFansNumberModel =
                        (AttentionAndFansNumberModel) data.getSerializableExtra(Launcher.EX_PAYLOAD_2);

                if (attentionAndFansNumberModel != null && whetherAttentionShieldOrNot != null) {
                    for (int i=0;i<mBorrowMoneyAdapter.getCount();i++){
                        BorrowMoney borrowMoney = mBorrowMoneyAdapter.getItem(i);
                        if (borrowMoney.getUserId() == attentionAndFansNumberModel.getUserId()){
                            if (whetherAttentionShieldOrNot.isFollow()) {
                                borrowMoney.setIsAttention(2);
                                mBorrowMoneyAdapter.notifyDataSetChanged();
                            } else {
                                borrowMoney.setIsAttention(1);
                                mBorrowMoneyAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    if (whetherAttentionShieldOrNot.isShield()) {
                        for (int i=0;i<mBorrowMoneyAdapter.getCount();i++){
                            BorrowMoney borrowMoney = mBorrowMoneyAdapter.getItem(i);
                            if (borrowMoney.getUserId() == attentionAndFansNumberModel.getUserId()){
                                mBorrowMoneyAdapter.remove(borrowMoney);
                            }
                        }
                    }
                }
            }
        }
    }
}
