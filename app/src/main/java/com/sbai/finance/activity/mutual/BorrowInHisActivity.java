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

public class BorrowInHisActivity extends BaseActivity {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;

    private BorrowInHisAdapter mBorrowInHisAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_in_mine_his);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestBorrowHisData();
            }
        });
        mBorrowInHisAdapter = new BorrowInHisAdapter(this);
        mBorrowInHisAdapter.setCallback(new BorrowInHisAdapter.Callback() {
            @Override
            public void OnItemCallClick(Integer id) {

            }

            @Override
            public void OnItemRepayClick(Integer id) {
                requestRepay(id);
            }
        });
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mBorrowInHisAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestBorrowHisData();
    }

    private void requestBorrowHisData(){
       Client.getBorrowInHisList().setTag(TAG)
               .setCallback(new Callback2D<Resp<List<BorrowInHis>>,List<BorrowInHis>>() {
                   @Override
                   protected void onRespSuccessData(List<BorrowInHis> data) {
                       updateBorrowHis(data);
                   }

                   @Override
                   public void onFailure(VolleyError volleyError) {
                       super.onFailure(volleyError);
                       stopRefreshAnimation();
                   }
               }).fire();
   }
   private void requestRepay(Integer id){
       Client.repayed(id).setTag(TAG)
               .setCallback(new Callback<Resp<Object>>() {
                   @Override
                   protected void onRespSuccess(Resp<Object> resp) {
                       if (resp.isSuccess()){
                           requestBorrowHisData();
                       }else {
                           ToastUtil.curt(resp.getMsg());
                       }
                   }
               }).fire();
   }
   private void updateBorrowHis(List<BorrowInHis> data){
       stopRefreshAnimation();
       mBorrowInHisAdapter.clear();
       mBorrowInHisAdapter.addAll(data);
       mBorrowInHisAdapter.notifyDataSetChanged();
   }
    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
    static class BorrowInHisAdapter extends ArrayAdapter<BorrowInHis>{
        Context mContext;
        private Callback mCallback;
        interface Callback{
            void OnItemCallClick(Integer id);
            void OnItemRepayClick(Integer id);
        }
        public void setCallback(Callback callback){
            mCallback = callback;
        }
        public BorrowInHisAdapter(@NonNull Context context) {
            super(context, 0);
            mContext =context;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_borrow_in_mine_his, parent, false);
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
            private void bindDataWithView(BorrowInHis item, int position, Context context){
                mNeedAmount.setText(context.getString(R.string.RMB,String.valueOf(item.getMoney())));
                mBorrowTime.setText(context.getString(R.string.day,String.valueOf(item.getDays())));
                mBorrowInterest.setText(context.getString(R.string.RMB,String.valueOf(item.getInterest())));
                SpannableString attentionSpannableString = null;
                switch (item.getStatus()){
                    case BorrowInHis.STATUS_FAIL:
                        mUserPortrait.setVisibility(View.GONE);
                        attentionSpannableString = StrUtil.mergeTextWithRatioColor(context.getString(R.string.borrow_failure),
                                " "+item.getFailMsg(),1.0f,ContextCompat.getColor(context,R.color.redPrimary));
                        mUserNameLand.setText(attentionSpannableString);
                        mPublishTime.setText(context.getString(R.string.borrow_in_time,
                                context.getString(R.string.borrow_in_time_failure), DateUtil.formatSlash(item.getModifyTime())));
                        mBorrowStatus.setVisibility(View.GONE);
                        break;
                    case BorrowInHis.STATUS_SUCCESS:
                    case BorrowInHis.STATUS_PAY_INTENTION:
                        mUserPortrait.setVisibility(View.VISIBLE);
                        attentionSpannableString = StrUtil.mergeTextWithRatioColor(item.getUserName(), "\n"+"佛山",0.73f,
                                ContextCompat.getColor(context,R.color.redPrimary),ContextCompat.getColor(context,R.color.assistText));
                        mUserNameLand.setText(attentionSpannableString);
                        mPublishTime.setText(context.getString(R.string.borrow_in_time,
                                context.getString(R.string.borrow_in_time_success), DateUtil.formatSlash(item.getModifyTime())));
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
