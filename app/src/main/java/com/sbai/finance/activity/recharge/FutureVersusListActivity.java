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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.BindBankHintDialogFragment;
import com.sbai.finance.fragment.dialog.BindBankHintDialogFragment$$ViewBinder;
import com.sbai.finance.model.versus.FutureVersus;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FutureVersusListActivity extends BaseActivity {
    @BindView(R.id.back)
    TextView mBack;
    @BindView(R.id.title)
    LinearLayout mTitle;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.integral)
    TextView mIntegral;
    @BindView(R.id.wining)
    TextView mWining;
    @BindView(R.id.recharge)
    TextView mRecharge;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.matchVersus)
    TextView mMatchVersus;
    @BindView(R.id.createVersus)
    TextView mCreateVersus;
    @BindView(R.id.createAndMatchArea)
    LinearLayout mCreateAndMatchArea;
    @BindView(R.id.currentVersus)
    TextView mCurrentVersus;
    private VersusListAdapter mVersusListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_versus_list);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_future_versus_header, mListView, false);
        TextView seeVersusRecord = (TextView) header.findViewById(R.id.seeVersusRecord);
        TextView versusRule = (TextView) header.findViewById(R.id.versusRule);
        seeVersusRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(),FutureVersusRecordActivity.class).execute();

            }
        });
        versusRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BindBankHintDialogFragment.newInstance(R.string.versus_rule_title,R.string.versus_rule_tip).show(getSupportFragmentManager());
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        mListView.addHeaderView(header);
        mVersusListAdapter = new VersusListAdapter(getActivity());
        mListView.setAdapter(mVersusListAdapter);
        for (int i=0;i<10;i++){
            FutureVersus futureVersus = new FutureVersus();
            futureVersus.setUserName("测试");
            mVersusListAdapter.add(futureVersus);
        }
        mVersusListAdapter.notifyDataSetChanged();

    }
    @OnClick({R.id.back,R.id.recharge,R.id.avatar,R.id.createVersus,R.id.matchVersus,R.id.currentVersus,R.id.title})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                getActivity().onBackPressed();
                break;
            case R.id.title:
                mListView.smoothScrollToPositionFromTop(0,0);
                break;
            case R.id.recharge:
                break;
            case R.id.avatar:
                break;
            case R.id.createVersus:
                break;
            case R.id.matchVersus:
                break;
            case R.id.currentVersus:
                break;
            default:
                break;
        }
    }


    static class VersusListAdapter extends ArrayAdapter<FutureVersus> {
        interface Callback {
            void onClick(int userId);
        }

        private Callback mCallback;

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        public VersusListAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_future_versus, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext(), mCallback);
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.myAvatar)
            ImageView mMyAvatar;
            @BindView(R.id.myName)
            TextView mMyName;
            @BindView(R.id.varietyName)
            TextView mVarietyName;
            @BindView(R.id.progressBar)
            ProgressBar mProgressBar;
            @BindView(R.id.myProfit)
            TextView mMyProfit;
            @BindView(R.id.userProfit)
            TextView mUserProfit;
            @BindView(R.id.fighterDataArea)
            RelativeLayout mFighterDataArea;
            @BindView(R.id.depositAndTime)
            TextView mDepositAndTime;
            @BindView(R.id.userAvatar)
            ImageView mUserAvatar;
            @BindView(R.id.userName)
            TextView mUserName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
            private void bindDataWithView(FutureVersus item, Context context, Callback callback) {
                mUserName.setText(item.getUserName());
            }
        }
    }
}
