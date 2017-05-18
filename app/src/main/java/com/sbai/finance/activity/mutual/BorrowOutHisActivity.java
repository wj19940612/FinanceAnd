package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.mutual.CallPhone;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.mutual.BorrowOutHistory;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-04-27.
 */

public class BorrowOutHisActivity extends BaseActivity implements AbsListView.OnScrollListener{
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;

    private BorrowOutHisAdapter mBorrowOutHisAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_out_mine_his);
        ButterKnife.bind(this);
        initView();
        requestBorrowOutHisData();
    }

    private void initView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestBorrowOutHisData();
            }
        });
        mBorrowOutHisAdapter = new BorrowOutHisAdapter(this);
        mBorrowOutHisAdapter.setCallback(new BorrowOutHisAdapter.Callback() {
            @Override
            public void OnItemCallClick(int id) {
                requestPhone(id);
            }
            @Override
            public void OnItemRepayClick(int id) {
                requestRepay(id);
            }

            @Override
            public void OnItemUserClick(int userId) {
                Launcher.with(getActivity(),UserDataActivity.class).putExtra(Launcher.USER_ID,userId).execute();
            }
        });
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mBorrowOutHisAdapter);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Launcher.with(getActivity(),BorrowOutHisDetailActivity.class)
                                    .putExtra(BorrowOutHisDetailActivity.ID,mBorrowOutHisAdapter.getItem(position).getId())
                                    .putExtra(BorrowOutHisDetailActivity.CONFIRM_TIME,mBorrowOutHisAdapter.getItem(position).getConfirmTime())
                                    .execute();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void requestRepay(Integer id) {
        Client.repayed(id).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()){
                            requestBorrowOutHisData();
                        }else {
                            ToastUtil.curt(resp.getMsg());
                        }
                    }
                }).fire();
    }

    private void requestBorrowOutHisData() {
        Client.getBorrowOutHisList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<BorrowOutHistory>>,List<BorrowOutHistory>>() {
                    @Override
                    protected void onRespSuccessData(List<BorrowOutHistory> data) {
                        updateBorrowOutHis(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
    }
    private void requestPhone(Integer id){
        Client.getPhone(id).setTag(TAG)
                .setCallback(new Callback<Resp<CallPhone>>() {
                    @Override
                    protected void onRespSuccess(Resp<CallPhone> resp) {
                        if (resp.isSuccess()){
                            callPhone(resp.getData());
                        }else {
                            ToastUtil.curt(resp.getMsg());
                        }
                    }
                }).fire();
    }
    private void callPhone(CallPhone phone){
        if (phone.getLoanUserPhone()!=null){
            Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone.getLoanUserPhone()));
            startActivity(intent);
        }else {
            ToastUtil.curt(getString(R.string.no_phone));
        }
    }

    private void updateBorrowOutHis(List<BorrowOutHistory> data) {
        stopRefreshAnimation();
        mBorrowOutHisAdapter.clear();
        mBorrowOutHisAdapter.addAll(data);
        mBorrowOutHisAdapter.notifyDataSetChanged();
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

    static class BorrowOutHisAdapter  extends ArrayAdapter<BorrowOutHistory>{
        Context mContext;
        private Callback mCallback;
        interface Callback{
            void OnItemCallClick(int id);
            void OnItemRepayClick(int id);
            void OnItemUserClick(int userId);
        }
        public void setCallback(Callback callback){
            mCallback = callback;
        }
        public BorrowOutHisAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_borrow_out_mine_his, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position),mContext,mCallback);
            return convertView;
         }
        static class ViewHolder{
            @BindView(R.id.userPortrait)
            ImageView mUserPortrait;
            @BindView(R.id.userNameLand)
            TextView mUserNameLand;
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.needAmount)
            TextView mNeedAmount;
            @BindView(R.id.borrowTime)
            TextView mBorrowTime;
            @BindView(R.id.borrowInterest)
            TextView mBorrowInterest;
            @BindView(R.id.alreadyRepayment)
            TextView mAlreadyRepayment;
            @BindView(R.id.borrowStatus)
            LinearLayout mBorrowStatus;
            @BindView(R.id.success)
            LinearLayout mSuccess;
            @BindView(R.id.alreadyRepay)
            TextView mAlreadyRepay;
            @BindView(R.id.call)
            TextView mCall;
            ViewHolder(View view){
                ButterKnife.bind(this, view);
            }
            private void bindDataWithView(final BorrowOutHistory item, Context context, final Callback callback){
                Glide.with(context).load(item.getPortrait())
                        .bitmapTransform(new GlideCircleTransform(context))
                        .placeholder(R.drawable.ic_default_avatar)
                        .into(mUserPortrait);
                String location = item.getLocation();
                if(TextUtils.isEmpty(location)){
                    location = context.getString(R.string.no_location);
                }
                SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(item.getUserName(), "\n"+location,0.73f,
                        ContextCompat.getColor(context,R.color.redPrimary),ContextCompat.getColor(context,R.color.assistText));
                mUserNameLand.setText(attentionSpannableString);
                mNeedAmount.setText(context.getString(R.string.RMB,String.valueOf(item.getMoney())));
                mBorrowTime.setText(context.getString(R.string.day,String.valueOf(item.getDays())));
                mBorrowInterest.setText(context.getString(R.string.RMB,String.valueOf(item.getInterest())));
                mPublishTime.setText(context.getString(R.string.borrow_out_time,DateUtil.formatSlash(item.getConfirmTime())));
                switch (item.getStatus()){
                    case BorrowOutHistory.STATUS_PAY_INTENTION:
                    case BorrowOutHistory.STATUS_SUCCESS:
                    case BorrowOutHistory.STATUS_PAY_FIVE:
                        mSuccess.setVisibility(View.VISIBLE);
                        mAlreadyRepayment.setVisibility(View.GONE);
                        break;
                    case BorrowOutHistory.STATUS_ALREADY_REPAY:
                        mSuccess.setVisibility(View.GONE);
                        mAlreadyRepayment.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
                mUserPortrait.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.OnItemUserClick(item.getUserId());
                    }
                });
                mAlreadyRepay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.OnItemRepayClick(item.getId());
                    }
                });
                mCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.OnItemCallClick(item.getId());
                    }
                });
            }
        }
    }
}
