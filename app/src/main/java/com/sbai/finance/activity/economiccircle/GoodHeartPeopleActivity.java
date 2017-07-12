package com.sbai.finance.activity.economiccircle;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.activity.recharge.PayIntentionActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.GoodHeartPeople;
import com.sbai.finance.model.mutual.BorrowDetail;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.android.volley.Request.Method.HEAD;
import static com.sbai.finance.activity.mine.LoginActivity.LOGIN_SUCCESS_ACTION;

public class GoodHeartPeopleActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.payIntention)
    TextView mPayIntention;

    private int mDataId;
    private static int mUserId;
    private int mStatus;
    private int mSelectedUserId;
    private static int status;
    private static int userId;
    private static int selectedUserId;
    private List<GoodHeartPeople> mGoodHeartPeopleList;
    private GoodHeartPeopleAdapter mGoodHeartPeopleAdapter;
    private RefreshReceiver mRefreshReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_heart_people);
        ButterKnife.bind(this);
        initData(getIntent());
        initViews();
        requestGoodHeartPeopleList();
        registerRefreshReceiver();
    }

    private void registerRefreshReceiver() {
        mRefreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter(LOGIN_SUCCESS_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, intentFilter);
    }

    private void initViews() {
        if (LocalUser.getUser().isLogin()) {
            if (mUserId == LocalUser.getUser().getUserInfo().getId()
                    && (mStatus == BorrowDetail.STATUS_WAIT_HELP || mStatus == BorrowDetail.STATUS_ACCEPTY
                    || mStatus == BorrowDetail.STATUS_NO_CHECKED || mStatus == BorrowDetail.STATUS_NO_ALLOW)) {
                mPayIntention.setVisibility(View.VISIBLE);
            } else {
                mPayIntention.setVisibility(View.GONE);
            }
        } else {
            mPayIntention.setVisibility(View.GONE);
        }

        scrollToTop(mTitleBar, mListView);
        mGoodHeartPeopleAdapter = new GoodHeartPeopleAdapter(this);
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mGoodHeartPeopleAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final GoodHeartPeople item = (GoodHeartPeople) parent.getItemAtPosition(position);
        if (LocalUser.getUser().isLogin()) {
            if (mUserId == LocalUser.getUser().getUserInfo().getId()
                    && (mStatus == BorrowDetail.STATUS_WAIT_HELP || mStatus == BorrowDetail.STATUS_ACCEPTY
                    || mStatus == BorrowDetail.STATUS_NO_CHECKED || mStatus == BorrowDetail.STATUS_NO_ALLOW)) {

                mPayIntention.setEnabled(true);
                mGoodHeartPeopleAdapter.setChecked(position);
                mGoodHeartPeopleAdapter.notifyDataSetInvalidated();
                mPayIntention.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SmartDialog.with(getActivity(),
                                getString(R.string.select_help, item.getUserName()))
                                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                    @Override
                                    public void onClick(final Dialog dialog) {
                                        Client.chooseGoodPeople(mDataId, mGoodHeartPeopleList.get(position).getUserId())
                                                .setTag(TAG)
                                                .setIndeterminate(GoodHeartPeopleActivity.this)
                                                .setCallback(new Callback<Resp<JsonPrimitive>>() {
                                                    @Override
                                                    protected void onRespSuccess(Resp<JsonPrimitive> resp) {
                                                        Launcher.with(getActivity(), PayIntentionActivity.class)
                                                                .putExtra(Launcher.EX_PAYLOAD, mDataId)
                                                                .execute();
                                                        dialog.dismiss();
                                                    }
                                                }).fire();
                                    }
                                })
                                .setMessageTextSize(16)
                                .setMessageTextColor(ContextCompat.getColor(GoodHeartPeopleActivity.this, R.color.blackAssist))
                                .setNegative(R.string.cancel)
                                .show();

                    }
                });
            } else {
                Launcher.with(GoodHeartPeopleActivity.this, UserDataActivity.class)
                        .putExtra(Launcher.USER_ID, item.getUserId())
                        .execute();
            }
        } else {
            Launcher.with(GoodHeartPeopleActivity.this, LoginActivity.class).execute();
        }
    }

    private void initData(Intent intent) {
        mDataId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
        mUserId = intent.getIntExtra(Launcher.USER_ID, -1);
        mStatus = intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
        mSelectedUserId = intent.getIntExtra(Launcher.EX_PAYLOAD_2, -1);
        userId = mUserId;
        status = mStatus;
        selectedUserId = mSelectedUserId;
    }

    private void requestGoodHeartPeopleList() {
        Client.getGoodHeartPeopleList(mDataId).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<GoodHeartPeople>>, List<GoodHeartPeople>>() {
                    @Override
                    protected void onRespSuccessData(List<GoodHeartPeople> goodHeartPeopleList) {
                        mGoodHeartPeopleList = goodHeartPeopleList;
                        updateGoodHeartPeopleList();
                    }
                }).fire();
    }

    private void updateGoodHeartPeopleList() {
        mGoodHeartPeopleAdapter.clear();
        mGoodHeartPeopleAdapter.addAll(mGoodHeartPeopleList);
        mGoodHeartPeopleAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshReceiver);
    }

    static class GoodHeartPeopleAdapter extends ArrayAdapter<GoodHeartPeople> {

        private Context mContext;
        private int mChecked = -1;

        private GoodHeartPeopleAdapter(Context context) {
            super(context, 0);
            mContext = context;
        }

        private void setChecked(int checked) {
            this.mChecked = checked;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_good_heart_people, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(mContext, getItem(position), mChecked, position);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.location)
            TextView mLocation;
            @BindView(R.id.hotArea)
            RelativeLayout mHotArea;
            @BindView(R.id.checkboxClick)
            ImageView mCheckboxClick;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(final Context context, final GoodHeartPeople item, int checked, int position) {
                if (item == null) return;

                Glide.with(context).load(item.getPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .transform(new GlideCircleTransform(context))
                        .into(mAvatar);

                mUserName.setText(item.getUserName());

                mHotArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (LocalUser.getUser().isLogin()) {
                            Launcher.with(context, UserDataActivity.class)
                                    .putExtra(Launcher.USER_ID, item.getUserId())
                                    .execute();
                        } else {
                            Launcher.with(context, LoginActivity.class).execute();
                        }
                    }
                });

                if (TextUtils.isEmpty(item.getLocation())) {
                    mLocation.setText(R.string.no_location_information);
                } else {
                    mLocation.setText(item.getLocation());
                }

                if (LocalUser.getUser().isLogin()) {
                    if (userId == LocalUser.getUser().getUserInfo().getId()
                            && (status == BorrowDetail.STATUS_WAIT_HELP || status == BorrowDetail.STATUS_ACCEPTY
                            || status == BorrowDetail.STATUS_NO_CHECKED || status == BorrowDetail.STATUS_NO_ALLOW)) {
                        //如果是自己
                        mCheckboxClick.setVisibility(View.VISIBLE);
                        mCheckboxClick.setImageResource(R.drawable.ic_pay_way_checkbox_checked);
                        if (checked == position) {
                            mCheckboxClick.setImageResource(R.drawable.ic_pay_way_checkbox_checked);
                        } else {
                            mCheckboxClick.setImageResource(R.drawable.ic_pay_way_checkbox_unchecked);
                        }
                    } else {
                        //不是自己
                        mCheckboxClick.setVisibility(View.INVISIBLE);
                        if (status == BorrowDetail.STATUS_INTENTION || status == BorrowDetail.STATUS_END_REPAY
                                || status == BorrowDetail.STATUS_INTENTION_OVER_TIME) {
                            if (selectedUserId == item.getUserId()) {
                                mCheckboxClick.setVisibility(View.VISIBLE);
                                mCheckboxClick.setImageResource(R.drawable.ic_pay_way_checkbox_checked);
                            } else {
                                mCheckboxClick.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                } else {
                    //没登陆过
                    mCheckboxClick.setVisibility(View.INVISIBLE);
                }
            }
        }
    }


    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mUserId == LocalUser.getUser().getUserInfo().getId()
                    && (mStatus == BorrowDetail.STATUS_WAIT_HELP || mStatus == BorrowDetail.STATUS_ACCEPTY
                    || mStatus == BorrowDetail.STATUS_NO_CHECKED || mStatus == BorrowDetail.STATUS_NO_ALLOW)) {
                mPayIntention.setVisibility(View.VISIBLE);
            } else {
                mPayIntention.setVisibility(View.GONE);
            }
        }
    }
}
