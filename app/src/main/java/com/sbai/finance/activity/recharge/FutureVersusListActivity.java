package com.sbai.finance.activity.recharge;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.future.FutureBattleActivity;
import com.sbai.finance.activity.future.FutureTradeActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.fragment.dialog.BindBankHintDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.versus.FutureVersus;
import com.sbai.finance.net.Client;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.SmartDialog;

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
        ImageView versusBanner = (ImageView) header.findViewById(R.id.versusBanner);
        TextView seeVersusRecord = (TextView) header.findViewById(R.id.seeVersusRecord);
        TextView versusRule = (TextView) header.findViewById(R.id.versusRule);
        Glide.with(getActivity()).load(R.drawable.versus_banner).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(versusBanner);
        seeVersusRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), FutureVersusRecordActivity.class).execute();

            }
        });
        versusRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BindBankHintDialogFragment.newInstance(R.string.versus_rule_title, R.string.versus_rule_tip).show(getSupportFragmentManager());
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
        mVersusListAdapter.setCallback(new VersusListAdapter.Callback() {
            @Override
            public void onClick(int userId) {
                if (LocalUser.getUser().isLogin()){
                 showJoinVersusDialog();
                }else{
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
        mListView.setAdapter(mVersusListAdapter);
        for (int i = 0; i < 10; i++) {
            FutureVersus futureVersus = new FutureVersus();
            futureVersus.setUserName("测试");
            mVersusListAdapter.add(futureVersus);
        }
        mVersusListAdapter.notifyDataSetChanged();

    }

    @OnClick({R.id.back, R.id.recharge, R.id.avatar, R.id.createVersus, R.id.matchVersus, R.id.currentVersus, R.id.title})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                getActivity().onBackPressed();
                break;
            case R.id.title:
                mListView.smoothScrollToPositionFromTop(0, 0);
                break;
            case R.id.recharge:
                break;
            case R.id.avatar:
                break;
            case R.id.createVersus:
                break;
            case R.id.matchVersus:
                showAskMatchDialog();
                break;
            case R.id.currentVersus:
                break;
            default:
                break;
        }
    }
    private void showJoinVersusDialog(){
        SmartDialog.with(getActivity(), getString(R.string.join_versus_tip),getString(R.string.join_versus_title))
                .setMessageTextSize(15)
                .setPositive(R.string.confirm, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        // TODO: 2017-06-21  进行余额查询，余额充足进入对战，余额不足弹窗提示充值
                        Launcher.with(getActivity(), FutureBattleActivity.class).execute();
                       // showJoinVersusFailureDialog();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setNegative(R.string.cancel)
                .show();

    }
    private void showJoinVersusFailureDialog(){
        SmartDialog.with(getActivity(), getString(R.string.join_versus_failure_tip),getString(R.string.join_versus_failure_title))
                .setMessageTextSize(15)
                .setPositive(R.string.go_recharge, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        // TODO: 2017-06-21  进行余额查询，余额充足进入对战，余额不足弹窗提示充值
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setNegative(R.string.cancel)
                .show();

    }
    private void showAskMatchDialog(){
        SmartDialog.with(getActivity(), getString(R.string.match_versus_tip),getString(R.string.match_versus_title))
                .setMessageTextSize(15)
                .setPositive(R.string.confirm, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        showMatchingDialog();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setNegative(R.string.cancel)
                .show();

    }
    private void showMatchingDialog(){
        SmartDialog.with(getActivity(), getString(R.string.matching_tip),getString(R.string.matching))
                .setMessageTextSize(15)
                .setPositive(R.string.cancel_matching, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        showCancelMatchDialog();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setNegativeHide()
                .show();

    }
    private void showCancelMatchDialog(){
        SmartDialog.with(getActivity(), getString(R.string.cancel_tip),getString(R.string.cancel_matching))
                .setMessageTextSize(15)
                .setPositive(R.string.no_waiting, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setNegative(R.string.continue_versus, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        showMatchingDialog();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .show();

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

        static class ViewHolder {
            @BindView(R.id.createAvatar)
            ImageView mCreateAvatar;
            @BindView(R.id.createKo)
            ImageView mCreateKo;
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
            @BindView(R.id.againstAvatar)
            ImageView mAgainstAvatar;
            @BindView(R.id.againstKo)
            ImageView mAgainstKo;
            @BindView(R.id.userName)
            TextView mUserName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
            private void bindDataWithView(FutureVersus item, Context context, final Callback callback) {
                mUserName.setText(item.getUserName());
                mCreateAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onClick(11);
                    }
                });
                mAgainstAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onClick(11);
                    }
                });
            }
        }
    }
}
