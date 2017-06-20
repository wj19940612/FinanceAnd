package com.sbai.finance.activity.recharge;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.versus.VersusRecord;
import com.sbai.finance.view.CustomSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FutureVersusRecordActivity extends BaseActivity {
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.customSwipeRefreshLayout)
    CustomSwipeRefreshLayout mCustomSwipeRefreshLayout;
    private VersusRecordListAdapter mVersusRecordListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_versus_record);
        ButterKnife.bind(this);
        initView();
        updateRecord();
    }

    private void updateRecord() {
        VersusRecord vr= new VersusRecord();
        vr.setUserId(100);
        vr.setUserName("抽象工作室抽象工");
        vr.setVarietyName("美黄金");
        mVersusRecordListAdapter.add(vr);
        mVersusRecordListAdapter.add(vr);
        mVersusRecordListAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mVersusRecordListAdapter = new VersusRecordListAdapter(getActivity());
        mListView.setAdapter(mVersusRecordListAdapter);
    }


    static class VersusRecordListAdapter extends ArrayAdapter<VersusRecord> {
        interface Callback {
            void onClick(int userId);
        }

        private Callback mCallback;

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        public VersusRecordListAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_future_versus_record, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext(), mCallback);
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.versusResultImg)
            ImageView mVersusResultImg;
            @BindView(R.id.versusResult)
            TextView mVersusResult;
            @BindView(R.id.versusVarietyAndProfit)
            TextView mVersusVarietyAndProfit;
            @BindView(R.id.myAvatar)
            ImageView mMyAvatar;
            @BindView(R.id.myName)
            TextView mMyName;
            @BindView(R.id.againstAvatar)
            ImageView mAgainstAvatar;
            @BindView(R.id.againstName)
            TextView mAgainstName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
            private void bindDataWithView(VersusRecord item,Context context,Callback callback){
                mVersusVarietyAndProfit.setText(item.getVarietyName()+"  200元宝");
                mMyName.setText(item.getUserName());
                mAgainstName.setText(item.getUserName());
            }
        }
    }
}
