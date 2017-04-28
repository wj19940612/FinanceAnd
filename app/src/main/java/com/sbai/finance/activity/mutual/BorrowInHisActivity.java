package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.BorrowInHis;
import com.sbai.finance.utils.DateUtil;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_in_mine_his);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mListView.setEmptyView(mEmpty);
       // mListView.setAdapter();
    }
    static class BorrowInAdapter extends ArrayAdapter<BorrowInHis>{
        Context mContext;
        public BorrowInAdapter(@NonNull Context context) {
            super(context, 0);
            mContext =context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_borrow_in_mine_his, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
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
            @BindView(R.id.call)
            TextView mCall;
            @BindView(R.id.alreadyRepay)
            TextView mAlreadyRepay;
            @BindView(R.id.alreadyRepayment)
            TextView mAlreadyRepayment;
            ViewHolder(View view){
                ButterKnife.bind(this, view);
            }
            private void bindDataWithView(BorrowInHis item, int position, Context context){
                mNeedAmount.setText(context.getString(R.string.RMB,String.valueOf(item.getMoney())));
                mBorrowTime.setText(context.getString(R.string.day,String.valueOf(item.getDays())));
                mBorrowInterest.setText(context.getString(R.string.RMB,String.valueOf(item.getInterest())));
                switch (item.getStatus()){
                    case BorrowInHis.STATUS_SUCCESS:
                        break;
                    case BorrowInHis.STATUS_FAIL:
                        mUserPortrait.setVisibility(View.GONE);
                        mPublishTime.setText(context.getString(R.string.borrow_in_time,
                                context.getString(R.string.borrow_in_time_failure), DateUtil.formatSlash(item.getModifyTime())));

                        break;
                    case BorrowInHis.STATUS_PAY_INTENTION:
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
