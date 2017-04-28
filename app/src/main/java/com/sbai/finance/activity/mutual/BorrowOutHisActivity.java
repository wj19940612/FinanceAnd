package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.BorrowInHis;
import com.sbai.finance.model.BorrowOut;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.BorrowOutHis;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-04-27.
 */

public class BorrowOutHisActivity extends BaseActivity {
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
            public void OnItemCallClick(Integer id) {
            }
            @Override
            public void OnItemRepayClick(Integer id) {
                requestRepay(id);
            }
        });
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mBorrowOutHisAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestBorrowOutHisData();
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
                .setCallback(new Callback2D<Resp<List<BorrowOutHis>>,List<BorrowOutHis>>() {
                    @Override
                    protected void onRespSuccessData(List<BorrowOutHis> data) {
                        updateBorrowOutHis(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void updateBorrowOutHis(List<BorrowOutHis> data) {
        if (data.isEmpty()){
            stopRefreshAnimation();
        }
        mBorrowOutHisAdapter.clear();
        mBorrowOutHisAdapter.addAll(data);
        mBorrowOutHisAdapter.notifyDataSetChanged();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    static class BorrowOutHisAdapter  extends ArrayAdapter<BorrowOutHis>{
        Context mContext;
        private Callback mCallback;
        interface Callback{
            void OnItemCallClick(Integer id);
            void OnItemRepayClick(Integer id);
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
            viewHolder.bindDataWithView(getItem(position),position,mContext);
            TextView mCall = (TextView) convertView.findViewById(R.id.call);
            TextView mAlreadyRepay = (TextView) convertView.findViewById(R.id.alreadyRepay);
            mCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.OnItemCallClick(getItem(position).getUserId());
                }
            });
            mAlreadyRepay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.OnItemRepayClick(getItem(position).getUserId());
                }
            });
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
            ViewHolder(View view){
                ButterKnife.bind(this, view);
            }
            private void bindDataWithView(BorrowOutHis item, int position, Context context){
                if (LocalUser.getUser().isLogin()){
                    Glide.with(context).load(LocalUser.getUser().getUserInfo().getUserPortrait()).into(mUserPortrait);
                }
                mNeedAmount.setText(context.getString(R.string.RMB,String.valueOf(item.getMoney())));
                mBorrowTime.setText(context.getString(R.string.day,String.valueOf(item.getDays())));
                mBorrowInterest.setText(context.getString(R.string.RMB,String.valueOf(item.getInterest())));
                SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor("猪猪猪",
                        "\n" +"吴彦祖", 0.733f, ContextCompat.getColor(context,R.color.assistText));
                mUserNameLand.setText(attentionSpannableString);
                mPublishTime.setText(context.getString(R.string.borrow_in_time,
                        context.getString(R.string.borrow_in_time_failure), DateUtil.formatSlash(item.getModifyDate())));
                switch (item.getStatus()){
                    case BorrowOutHis.STATUST_6:
                        break;
                    case BorrowOutHis.STATUST_7:
                    case BorrowOutHis.STATUST_8:
                        mBorrowStatus.setVisibility(View.VISIBLE);
                        mAlreadyRepayment.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
