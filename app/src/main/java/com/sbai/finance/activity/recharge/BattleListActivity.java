package com.sbai.finance.activity.recharge;

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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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
import com.sbai.finance.activity.future.CreateFightActivity;
import com.sbai.finance.activity.future.FutureBattleActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.activity.mine.wallet.RechargeActivity;
import com.sbai.finance.fragment.dialog.BindBankHintDialogFragment;
import com.sbai.finance.fragment.dialog.StartMatchDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.payment.UserFundInfoModel;
import com.sbai.finance.model.versus.FutureVersus;
import com.sbai.finance.model.versus.VersusGaming;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.websocket.WSClient;
import com.sbai.finance.websocket.WSMessage;
import com.sbai.finance.websocket.WSPush;
import com.sbai.finance.websocket.callback.OnPushReceiveListener;
import com.sbai.finance.websocket.callback.WSCallback;
import com.sbai.finance.websocket.cmd.QuickMatch;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BattleListActivity extends BaseActivity implements
        CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
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

    private ImageView mAvatar;
    private TextView mIntegral;
    private TextView mWining;
    private TextView mRecharge;
    private VersusListAdapter mVersusListAdapter;
    private Long mLocation;
    private VersusBroadcastReceiver mVersusBroadcastReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private HashSet<Integer> mSet;
    private VersusGaming mMyCurrentGame;
    private StartMatchDialogFragment mStartMatchDialogFragment;
    private StringBuilder mRefusedIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_list);
        ButterKnife.bind(this);

        initListHeader();
        initCustomView();
        initView();
        updateAvatar();
        requestVersusData();

        WSClient.get().setOnPushReceiveListener(new OnPushReceiveListener<WSPush<VersusGaming>>() {
            @Override
            public void onPushReceive(WSPush<VersusGaming> versusGamingWSPush) {
                Log.d(TAG, "onPushReceive: " + versusGamingWSPush);
                // TODO: 26/06/2017 sample
            }
        });
    }

    private void initCustomView() {
        View customView = mTitleBar.getCustomView();
        mAvatar = (ImageView) customView.findViewById(R.id.avatar);
        mIntegral = (TextView) customView.findViewById(R.id.integral);
        mWining = (TextView) customView.findViewById(R.id.wining);
        mRecharge = (TextView) customView.findViewById(R.id.recharge);
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), UserDataActivity.class)
                            .putExtra(Launcher.USER_ID, LocalUser.getUser().getUserInfo().getId())
                            .execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
        mRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), RechargeActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });

    }

    private void initListHeader() {
        FrameLayout header = (FrameLayout) getLayoutInflater().inflate(R.layout.layout_future_versus_header, null);
        ImageView versusBanner = (ImageView) header.findViewById(R.id.versusBanner);
        TextView seeVersusRecord = (TextView) header.findViewById(R.id.seeVersusRecord);
        TextView versusRule = (TextView) header.findViewById(R.id.versusRule);
        Glide.with(getActivity())
                .load(R.drawable.versus_banner)
                .asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(versusBanner);
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
        mListView.addHeaderView(header);
    }

    private void initView() {
        mSet = new HashSet<>();
        mRefusedIds = new StringBuilder();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mVersusBroadcastReceiver = new VersusBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginActivity.LOGIN_SUCCESS_ACTION);
        mLocalBroadcastManager.registerReceiver(mVersusBroadcastReceiver, intentFilter);
        mVersusListAdapter = new VersusListAdapter(getActivity());
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mVersusListAdapter);
        mVersusListAdapter.setCallback(new VersusListAdapter.Callback() {
            @Override
            public void onClick(VersusGaming item, boolean isCreate) {
                if (LocalUser.getUser().isLogin()) {
                    if (isCreate) {
                        Launcher.with(getActivity(), UserDataActivity.class).putExtra(Launcher.USER_ID, item.getLaunchUser()).execute();
                    } else {
                        if (item.getGameStatus() == VersusGaming.GAME_STATUS_CREATED) {
                            if (item.getLaunchUser() == LocalUser.getUser().getUserInfo().getId()) {
                                //如果是自己创建的房间 进入到详情页
                                item.setPageType(VersusGaming.PAGE_VERSUS);
                                Launcher.with(getActivity(), FutureBattleActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, item)
                                        .execute();
                            } else {
                                showJoinVersusDialog(item);
                            }
                        } else {
                            Launcher.with(getActivity(), UserDataActivity.class).putExtra(Launcher.USER_ID, item.getAgainstUser()).execute();
                        }
                    }
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
        mListView.setAdapter(mVersusListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mVersusListAdapter.isEmpty() && (position > 0)) {
                    if (LocalUser.getUser().isLogin()) {
                        VersusGaming item = mVersusListAdapter.getItem(position - 1);
                        if (item != null) {
                            if (item.getGameStatus() == VersusGaming.GAME_STATUS_END) {
                                item.setPageType(VersusGaming.PAGE_RECORD);
                            } else {
                                item.setPageType(VersusGaming.PAGE_VERSUS);
                            }
                            Launcher.with(getActivity(), FutureBattleActivity.class).putExtra(Launcher.EX_PAYLOAD, item).execute();
                        }
                    } else {
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        scrollToTop(mTitleBar, mListView);
        if (LocalUser.getUser().isLogin()) {
            requestUserFindInfo();
            requestCurrentBattle();
        } else {
            mCurrentVersus.setVisibility(View.GONE);
            mCreateAndMatchArea.setVisibility(View.VISIBLE);
            mIntegral.setText("0.00");
            mWining.setText("0");
        }
        startScheduleJob(5 * 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        requestVisibleVersusData();
    }

    private void requestVersusData() {
        Client.getVersusGaming(mLocation).setTag(TAG)
                .setCallback(new Callback2D<Resp<FutureVersus>, FutureVersus>() {
                    @Override
                    protected void onRespSuccessData(FutureVersus data) {
                        updateVersusData(data);
                    }
                }).fireFree();
    }

    private void requestUserFindInfo() {
        Client.requestUserFundInfo()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserFundInfoModel>, UserFundInfoModel>() {
                    @Override
                    protected void onRespSuccessData(UserFundInfoModel data) {
                        updateUserFund(data);
                    }
                })
                .fireFree();
    }

    private void requestCurrentBattle() {
        Client.getCurrentBattle().setTag(TAG)
                .setCallback(new Callback<Resp<VersusGaming>>() {
                    @Override
                    protected void onRespSuccess(Resp<VersusGaming> resp) {
                        mMyCurrentGame = resp.getData();
                        updateMyVersus(resp.getData());
                    }
                }).fire();
    }

    private void requestJoinVersus(final VersusGaming data) {
        Client.joinVersus(data.getId(), VersusGaming.SOURCE_HALL).setTag(TAG)
                .setCallback(new Callback<Resp<VersusGaming>>() {
                    @Override
                    protected void onRespSuccess(Resp<VersusGaming> resp) {
                        if (resp.isSuccess()) {
                            data.setPageType(VersusGaming.PAGE_VERSUS);
                            Launcher.with(getActivity(), FutureBattleActivity.class).putExtra(Launcher.EX_PAYLOAD, data).execute();
                            requestCurrentBattle();
                        } else {
                            showJoinVersusFailureDialog();
                        }
                    }
                }).fireFree();
    }

    private void requestMatchVersus(final int type, String refuseIds) {
        Client.quickMatchForAgainst(type, refuseIds).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            updateMatchVersus(type);
                        } else {
                            ToastUtil.curt(resp.getMsg());
                        }
                    }
                }).fireFree();
    }

    private void requestMatchResult() {
        Client.getQuickMatchResult(VersusGaming.AGAGINST_FAST_MATCH, null).setTag(TAG)
                .setCallback(new Callback<Resp<VersusGaming>>() {
                    @Override
                    protected void onRespSuccess(Resp<VersusGaming> resp) {
                        if (resp.isSuccess()) {
                            updateMatchResult(resp.getData());
                        } else {
                            ToastUtil.curt(resp.getMsg());
                        }
                    }
                }).fireFree();
    }

    private void requestVisibleVersusData() {
        if (mListView != null && mVersusListAdapter != null) {
            StringBuilder stringBuilder = new StringBuilder();
            int first = mListView.getFirstVisiblePosition() - 1;
            int last = mListView.getLastVisiblePosition();
            for (int i = first; i < last; i++) {
                if (i >= 0) {
                    VersusGaming item = mVersusListAdapter.getItem(i);
                    if (item != null) {
                        stringBuilder.append(item.getId()).append(",");
                    }
                }
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                requestVisibleVersusData(stringBuilder.toString());
            }
        }
    }

    private void requestVisibleVersusData(String battleIds) {
        Client.getBattleGamingData(battleIds).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<VersusGaming>>, List<VersusGaming>>() {
                    @Override
                    protected void onRespSuccessData(List<VersusGaming> data) {
                        updateVisibleVersusData(data);
                    }
                }).fire();
    }

    private void updateVisibleVersusData(List<VersusGaming> data) {
        if (data.isEmpty() || mVersusListAdapter == null) return;
        for (int i = 0; i < mVersusListAdapter.getCount(); i++) {
            VersusGaming item = mVersusListAdapter.getItem(i);
            for (VersusGaming versusGaming : data) {
                if (item.getId() == versusGaming.getId()) {
                    item.setGameStatus(versusGaming.getGameStatus());
                    item.setLaunchPraise(versusGaming.getLaunchPraise());
                    item.setLaunchScore(versusGaming.getLaunchScore());
                    item.setAgainstPraise(versusGaming.getAgainstPraise());
                    item.setAgainstScore(versusGaming.getAgainstScore());
                    data.remove(versusGaming);
                    break;
                }
            }
        }
        mVersusListAdapter.notifyDataSetChanged();
    }

    private void updateMatchResult(VersusGaming data) {
        if (data != null) {
            showMatchSuccessDialog(data);
        }
    }

    private void updateMatchVersus(int type) {
        switch (type) {
            case VersusGaming.MATCH_CANCEL:

                break;
            case VersusGaming.MATCH_CONTINUE:
                break;
            case VersusGaming.MATCH_START:
                break;
        }
    }

    private void updateMyVersus(VersusGaming data) {
        if (null == data) {
            mCreateAndMatchArea.setVisibility(View.VISIBLE);
            mCurrentVersus.setVisibility(View.GONE);
        } else {
            mCreateAndMatchArea.setVisibility(View.GONE);
            mCurrentVersus.setVisibility(View.VISIBLE);
        }
    }

    private void updateUserFund(UserFundInfoModel data) {
        mIntegral.setText(String.valueOf(data.getCredit()));
        mWining.setText(data.getYuanbao() + "个");
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

    }

    private void updateAvatar() {
        if (LocalUser.getUser().isLogin()) {
            Glide.with(getActivity())
                    .load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .placeholder(R.drawable.ic_default_avatar)
                    .transform(new GlideCircleTransform(getActivity()))
                    .into(mAvatar);
        } else {
            mAvatar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_default_avatar));
        }
    }

    private void updateVersusData(FutureVersus futureVersus) {
        stopRefreshAnimation();
        if (mSet.isEmpty()) {
            mVersusListAdapter.clear();
        }
        for (VersusGaming versusGaming : futureVersus.getList()) {
            if (mSet.add(versusGaming.getId())) {
                mVersusListAdapter.add(versusGaming);
            }
        }
        if (!futureVersus.hasMore()) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else if (futureVersus.getList().size() > 0) {
            mLocation = futureVersus.getList().get(futureVersus.getList().size() - 1).getCreateTime();
        }
        mVersusListAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.createVersus, R.id.matchVersus, R.id.currentVersus, R.id.titleBar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createVersus:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), CreateFightActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.matchVersus:
                if (LocalUser.getUser().isLogin()) {
                    showAskMatchDialog();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.currentVersus:
                if (mMyCurrentGame != null) {
                    mMyCurrentGame.setPageType(VersusGaming.PAGE_VERSUS);
                    Launcher.with(getActivity(), FutureBattleActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, mMyCurrentGame)
                            .execute();
                }
                break;
            default:
                break;
        }
    }

    private void showJoinVersusDialog(final VersusGaming item) {
        SmartDialog.with(getActivity(), getString(R.string.join_versus_tip), getString(R.string.join_versus_title))

                .setMessageTextSize(15)
                .setPositive(R.string.confirm, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestJoinVersus(item);
                        // TODO: 2017-06-21  进行余额查询，余额充足进入对战，余额不足弹窗提示充值

//                        Launcher.with(getActivity(), FutureBattleActivity.class).execute();
                        // showJoinVersusFailureDialog();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setNegative(R.string.cancel)
                .show();

    }

    private void showJoinVersusFailureDialog() {
        SmartDialog.with(getActivity(), getString(R.string.join_versus_failure_tip), getString(R.string.join_versus_failure_title))
                .setMessageTextSize(15)
                .setPositive(R.string.go_recharge, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Launcher.with(getActivity(), RechargeActivity.class).execute();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setNegative(R.string.cancel)
                .show();

    }

    private void showAskMatchDialog() {
        SmartDialog.with(getActivity(), getString(R.string.match_versus_tip), getString(R.string.match_versus_title))
                .setMessageTextSize(15)
                .setPositive(R.string.confirm, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();

                        // TODO: 26/06/2017 sample
                        WSClient.get().send(new QuickMatch(QuickMatch.TYPE_QUICK_MATCH, ""), new WSCallback<WSMessage<Resp>>() {
                            @Override
                            public void onResponse(WSMessage<Resp> respWSMessage) {
                                showMatchingDialog();
                            }

                            @Override
                            public void onError(int code) {

                            }

                        });
                        //requestMatchVersus(VersusGaming.MATCH_START, "");
                        //showMatchingDialog();
                        //showMatchDialog();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setNegative(R.string.cancel)
                .show();

    }

    private void showMatchingDialog() {
        SmartDialog.with(getActivity(), getString(R.string.matching_tip), getString(R.string.matching))
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
                .setCancelableOnTouchOutside(false)
                .setNegativeHide()
                .show();
    }

    //开始匹配弹窗
    private void showMatchDialog() {
        if (mStartMatchDialogFragment == null) {
            mStartMatchDialogFragment = StartMatchDialogFragment
                    .newInstance()
                    .setOnCancelListener(new StartMatchDialogFragment.OnCancelListener() {
                        @Override
                        public void onCancel() {
                            mStartMatchDialogFragment.dismiss();
                            showCancelMatchDialog();
                        }
                    });
        }
        mStartMatchDialogFragment.show(getSupportFragmentManager());
    }

    private void showCancelMatchDialog() {
        SmartDialog.with(getActivity(), getString(R.string.cancel_tip), getString(R.string.cancel_matching))
                .setMessageTextSize(15)
                .setPositive(R.string.no_waiting, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestMatchVersus(VersusGaming.MATCH_CANCEL, "");
                    }
                })
                .setNegative(R.string.continue_versus, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                   //     showMatchingDialog();
                        showMatchDialog();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setCancelableOnTouchOutside(false)
                .show();

    }

    private void showMatchSuccessDialog(final VersusGaming data) {
        String reward = "";
        switch (data.getCoinType()) {
            case VersusGaming.COIN_TYPE_BAO:
                reward = data.getReward() + getActivity().getString(R.string.integral);
                break;
            case VersusGaming.COIN_TYPE_CASH:
                reward = data.getReward() + getActivity().getString(R.string.cash);
                break;
            case VersusGaming.COIN_TYPE_INTEGRAL:
                reward = data.getReward() + getActivity().getString(R.string.ingot);
                break;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.versus_variety_name)).append(data.getVarietyName()).append("\n")
                .append(getString(R.string.versus_time)).append(DateUtil.getMinutes(data.getEndline())).append("\n")
                .append(getString(R.string.versus_reward)).append(reward).append("\n")
                .append(getString(R.string.versus_tip));
        SmartDialog.with(getActivity(), sb.toString(), getString(R.string.title_match_success))
                .setMessageTextSize(15)
                .setPositive(R.string.continue_versus, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestMatchVersus(VersusGaming.MATCH_CANCEL, String.valueOf(data.getId()));
                    }
                })
                .setNegative(R.string.join_versus, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestJoinVersus(data);
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setCancelableOnTouchOutside(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mVersusBroadcastReceiver);
    }

    @Override
    public void onLoadMore() {
        requestVersusData();
    }

    @Override
    public void onRefresh() {
        reset();
        requestVersusData();
    }

    private void reset() {
        mSet.clear();
        mLocation = null;
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

    class VersusBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == LoginActivity.LOGIN_SUCCESS_ACTION) {
                updateAvatar();
                requestUserFindInfo();
                requestCurrentBattle();
            }
        }
    }

    static class VersusListAdapter extends ArrayAdapter<VersusGaming> {
        interface Callback {
            void onClick(VersusGaming item, boolean isCreate);
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
            @BindView(R.id.createName)
            TextView mCreateName;
            @BindView(R.id.varietyName)
            TextView mVarietyName;
            @BindView(R.id.progressBar)
            ProgressBar mProgressBar;
            @BindView(R.id.createProfit)
            TextView mCreateProfit;
            @BindView(R.id.againstProfit)
            TextView mAgainstProfit;
            @BindView(R.id.fighterDataArea)
            RelativeLayout mFighterDataArea;
            @BindView(R.id.depositAndTime)
            TextView mDepositAndTime;
            @BindView(R.id.againstAvatar)
            ImageView mAgainstAvatar;
            @BindView(R.id.againstKo)
            ImageView mAgainstKo;
            @BindView(R.id.againstName)
            TextView mAgainstName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(final VersusGaming item, Context context, final Callback callback) {
                mVarietyName.setText(item.getVarietyName());
                Glide.with(context).load(item.getLaunchUserPortrait())
                        .load(item.getLaunchUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar_big)
                        .transform(new GlideCircleTransform(context))
                        .into(mCreateAvatar);
                mCreateName.setText(item.getLaunchUserName());
                mCreateProfit.setText(String.valueOf(item.getLaunchScore()));
                mCreateAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onClick(item, true);
                    }
                });

                mAgainstName.setText(item.getAgainstUserName());
                mAgainstAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onClick(item, false);
                    }
                });
                mAgainstProfit.setText(String.valueOf(item.getAgainstScore()));
                String reward = "";
                switch (item.getCoinType()) {
                    case VersusGaming.COIN_TYPE_BAO:
                        reward = item.getReward() + context.getString(R.string.integral);
                        break;
                    case VersusGaming.COIN_TYPE_CASH:
                        reward = item.getReward() + context.getString(R.string.cash);
                        break;
                    case VersusGaming.COIN_TYPE_INTEGRAL:
                        reward = item.getReward() + context.getString(R.string.ingot);
                        break;
                }
                switch (item.getGameStatus()) {
                    case VersusGaming.GAME_STATUS_CREATED:
                        mDepositAndTime.setText(reward + " " + DateUtil.getMinutes(item.getEndline()));
                        mCreateKo.setVisibility(View.GONE);
                        mAgainstKo.setVisibility(View.GONE);
                        mAgainstAvatar.setImageResource(R.drawable.btn_join_versus);
                        showScoreProgress(0, 0, true);
                        break;
                    case VersusGaming.GAME_STATUS_STARTED:
                        mDepositAndTime.setText(reward + " " + context.getString(R.string.versusing));
                        mCreateKo.setVisibility(View.GONE);
                        mAgainstKo.setVisibility(View.GONE);
                        Glide.with(context).load(item.getLaunchUserPortrait())
                                .load(item.getAgainstUserPortrait())
                                .placeholder(R.drawable.ic_default_avatar_big)
                                .transform(new GlideCircleTransform(context))
                                .into(mAgainstAvatar);
                        showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
                        break;
                    case VersusGaming.GAME_STATUS_END:
                        mDepositAndTime.setText(reward + " " + context.getString(R.string.versus_end));
                        Glide.with(context).load(item.getLaunchUserPortrait())
                                .load(item.getAgainstUserPortrait())
                                .placeholder(R.drawable.ic_default_avatar_big)
                                .transform(new GlideCircleTransform(context))
                                .into(mAgainstAvatar);
                        if (item.getWinResult() == VersusGaming.RESULT_AGAINST_WIN) {
                            mCreateKo.setVisibility(View.VISIBLE);
                            mAgainstKo.setVisibility(View.GONE);
                            mDepositAndTime.setText(reward + " " + context.getString(R.string.versus_end));
                        } else if (item.getWinResult() == VersusGaming.RESULT_CREATE_WIN) {
                            mCreateKo.setVisibility(View.GONE);
                            mAgainstKo.setVisibility(View.VISIBLE);
                            mDepositAndTime.setText(reward + " " + context.getString(R.string.versus_end));
                        } else {
                            mCreateKo.setVisibility(View.GONE);
                            mAgainstKo.setVisibility(View.GONE);
                            mDepositAndTime.setText(reward + " " + context.getString(R.string.tie));
                        }
                        showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
                        break;

                }
            }

            /**
             * 显示对抗状态条
             *
             * @param createProfit  我的盈利状况
             * @param fighterProfit 对抗者盈利状况
             * @param isInviting    是否正在邀请中
             * @return
             */
            private void showScoreProgress(double createProfit, double fighterProfit, boolean isInviting) {
                String myFlag = "";
                String fighterFlag = "";
                if (isInviting) {
                    mProgressBar.setProgress(0);
                    mProgressBar.setSecondaryProgress(0);
                    mCreateProfit.setText(null);
                    mAgainstProfit.setText(null);
                } else {
                    //正正
                    if ((createProfit > 0 && fighterProfit >= 0) || (createProfit >= 0 && fighterProfit > 0)) {
                        int progress = (int) (createProfit * 100 / (createProfit + fighterProfit));
                        mProgressBar.setProgress(progress);
                    }
                    //正负
                    if (createProfit >= 0 && fighterProfit < 0) {
                        mProgressBar.setProgress(100);
                    }
                    //负正
                    if (createProfit < 0 && fighterProfit >= 0) {
                        mProgressBar.setProgress(0);
                    }
                    //负负
                    if (createProfit < 0 && fighterProfit < 0) {
                        int progress = (int) (Math.abs(createProfit) * 100 / (Math.abs(createProfit) + Math.abs(fighterProfit)));
                        mProgressBar.setProgress(100 - progress);
                    }
                    //都为0
                    if (createProfit == 0 && fighterProfit == 0) {
                        mProgressBar.setProgress(50);
                    }

                    if (createProfit > 0) {
                        myFlag = "+";
                    }

                    if (fighterProfit > 0) {
                        fighterFlag = "+";
                    }
                    mCreateProfit.setText(myFlag + FinanceUtil.formatWithScale(createProfit));
                    mAgainstProfit.setText(fighterFlag + FinanceUtil.formatWithScale(fighterProfit));
                }
            }
        }
    }
}
