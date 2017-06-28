package com.sbai.finance.activity.battle;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.activity.mine.wallet.RechargeActivity;
import com.sbai.finance.fragment.dialog.BindBankHintDialogFragment;
import com.sbai.finance.fragment.dialog.StartMatchDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.payment.UserFundInfoModel;
import com.sbai.finance.model.battle.FutureVersus;
import com.sbai.finance.model.battle.VersusGaming;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.websocket.PushCode;
import com.sbai.finance.websocket.WSClient;
import com.sbai.finance.websocket.WSMessage;
import com.sbai.finance.websocket.WSPush;
import com.sbai.finance.websocket.callback.OnPushReceiveListener;
import com.sbai.finance.websocket.callback.WSCallback;
import com.sbai.finance.websocket.cmd.QuickMatch;
import com.sbai.httplib.ApiCallback;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BattleListActivity extends BaseActivity implements
        CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    public static final int CANCEL_BATTLE = 250;
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
    private TextView mIngot;
    private TextView mRecharge;
    private VersusListAdapter mVersusListAdapter;
    private Long mLocation;
    private LoginBroadcastReceiver mLoginBroadcastReceiver;

    private HashSet<Integer> mSet;
    private VersusGaming mMyCurrentGame;
    private StartMatchDialogFragment mStartMatchDialogFragment;

    private SmartDialog mJoinDialog;
    private SmartDialog mJoinFailureDialog;
    private SmartDialog mAskMatchDialog;
    private SmartDialog mCancelMatchDialog;
    private StringBuilder mRefusedIds;

    private OnPushReceiveListener mPushReceiveListener = new OnPushReceiveListener<WSPush<Object>>() {
        @Override
        public void onPushReceive(final WSPush<Object> versusGamingWSPush) {
            switch (versusGamingWSPush.getContent().getType()) {
                case PushCode.QUICK_MATCH_TIMEOUT:
                    showMatchTimeoutDialog();
                    break;
                case PushCode.QUICK_MATCH_FAILURE:
                    showMatchTimeoutDialog();
                    break;
                case PushCode.QUICK_MATCH_SUCCESS:
                    showMatchSuccessDialog((VersusGaming) versusGamingWSPush.getContent().getData());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_list);
        ButterKnife.bind(this);
        initTitleBar();
        initListHeaderAndFooter();
        initListView();

        initLoginReceiver();
        updateAvatar();
        requestBattleList();

        WSClient.get().setOnPushReceiveListener(mPushReceiveListener);
        scrollToTop(mTitleBar, mListView);

    }

    private void initLoginReceiver() {
        mLoginBroadcastReceiver = new LoginBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginActivity.LOGIN_SUCCESS_ACTION);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mLoginBroadcastReceiver, intentFilter);
    }

    private void initTitleBar() {
        View view = mTitleBar.getCustomView();
        mAvatar = (ImageView) view.findViewById(R.id.avatar);
        mIntegral = (TextView) view.findViewById(R.id.integral);
        mIngot = (TextView) view.findViewById(R.id.ingot);
        mRecharge = (TextView) view.findViewById(R.id.recharge);
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

    private void initListHeaderAndFooter() {
        FrameLayout header = (FrameLayout) getLayoutInflater().inflate(R.layout.list_header_battle, null);
        ImageView battleBanner = (ImageView) header.findViewById(R.id.battleBanner);
        TextView checkBattleRecord = (TextView) header.findViewById(R.id.checkBattleRecord);
        TextView battleRule = (TextView) header.findViewById(R.id.battleRule);
        Glide.with(getActivity())
                .load(R.drawable.battle_banner)
                .asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.HIGH)
                .into(battleBanner);
        checkBattleRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), FutureVersusRecordActivity.class).execute();
            }
        });
        battleRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BindBankHintDialogFragment
                        .newInstance(R.string.versus_rule_title, R.string.versus_rule_tip)
                        .show(getSupportFragmentManager());
            }
        });
        mListView.addHeaderView(header);
        //add footer
        LinearLayout footParent = new LinearLayout(getActivity());
        View view = new View(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) Display.dp2Px(60, getResources()));
        view.setLayoutParams(layoutParams);
        footParent.setBackgroundColor(Color.TRANSPARENT);
        footParent.addView(view);
        mListView.addFooterView(footParent);
    }

    private void initListView() {
        mSet = new HashSet<>();
        mRefusedIds = new StringBuilder();
        mVersusListAdapter = new VersusListAdapter(getActivity());
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mVersusListAdapter);
        mListView.setAdapter(mVersusListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == parent.getCount() - 1) return;

                if (LocalUser.getUser().isLogin()) {
                    VersusGaming item = (VersusGaming) parent.getItemAtPosition(position);
                    if (item != null) {
                        if (item.getGameStatus() == VersusGaming.GAME_STATUS_END) {
                            item.setPageType(VersusGaming.PAGE_RECORD);
                            Launcher.with(getActivity(), FutureBattleActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, item)
                                    .executeForResult(CANCEL_BATTLE);
                        } else if (item.getGameStatus() == VersusGaming.GAME_STATUS_CREATED
                                && LocalUser.getUser().getUserInfo().getId() != item.getLaunchUser()) {
                            showJoinBattleDialog(item);
                        } else {
                            item.setPageType(VersusGaming.PAGE_VERSUS);
                            Launcher.with(getActivity(), FutureBattleActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, item)
                                    .executeForResult(CANCEL_BATTLE);
                        }
                    } else {
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                }
        }});
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LocalUser.getUser().isLogin()) {
            requestUserFindInfo();
            requestCurrentBattle();
        } else {
            mCurrentVersus.setVisibility(View.GONE);
            mCreateAndMatchArea.setVisibility(View.VISIBLE);
            mIntegral.setText("0.00");
            mIngot.setText("0");
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
        requestVisibleBattleData();
    }

    private void requestBattleList() {
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

    private void requestJoinBattle(final VersusGaming data) {
        Client.joinVersus(data.getId(), VersusGaming.SOURCE_HALL).setTag(TAG)
                .setCallback(new ApiCallback<Resp<VersusGaming>>() {
                    @Override
                    public void onSuccess(Resp<VersusGaming> resp) {
                        if (resp.isSuccess()) {
                            VersusGaming versusGaming = resp.getData();
                            if (versusGaming != null) {
                                //更新列表对战信息
                                data.setGameStatus(versusGaming.getGameStatus());
                                data.setAgainstUser(versusGaming.getAgainstUser());
                                data.setAgainstUserPortrait(versusGaming.getAgainstUserPortrait());
                                data.setAgainstUserName(versusGaming.getAgainstUserName());
                                
                                versusGaming.setPageType(VersusGaming.PAGE_VERSUS);
                                Launcher.with(getActivity(), FutureBattleActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, versusGaming)
                                        .executeForResult(CANCEL_BATTLE);

                            } else {
                                requestBattleInfo(data);
                            }
                        } else {
                            showJoinVersusFailureDialog(resp.getMsg(), resp.getCode());
                        }
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {

                    }
                }).fireFree();
    }

    private void requestBattleInfo(VersusGaming item) {
        Client.getBattleInfo(item.getId(), item.getBatchCode()).setTag(TAG)
                .setCallback(new Callback2D<Resp<VersusGaming>, VersusGaming>() {
                    @Override
                    protected void onRespSuccessData(VersusGaming data) {
                        data.setPageType(VersusGaming.PAGE_VERSUS);
                        Launcher.with(getActivity(), FutureBattleActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, data)
                                .executeForResult(CANCEL_BATTLE);
                    }
                }).fire();
    }

    private void requestMatchVersus(final int type, String refuseId) {
        String refuseIds = "";
        if (refuseId.isEmpty()) {
            mRefusedIds.delete(0, mRefusedIds.length());
        } else {
            mRefusedIds.append(refuseId).append(",");
            refuseIds = mRefusedIds.substring(0, mRefusedIds.length() - 1);
        }
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

    private void requestMatchVersusOfSocket(String refuseId) {
        String refuseIds = "";
        if (refuseId.isEmpty()) {
            mRefusedIds.delete(0, mRefusedIds.length());
        } else {
            mRefusedIds.append(refuseId).append(",");
            refuseIds = mRefusedIds.substring(0, mRefusedIds.length() - 1);
        }
        WSClient.get().send(new QuickMatch(QuickMatch.TYPE_QUICK_MATCH, refuseIds), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {
                showMatchDialog();
            }

            @Override
            public void onError(final int code) {
                ToastUtil.curt(String.valueOf(code));
            }
        });
    }

    private void requestCancelMatchOfSocket() {

        WSClient.get().send(new QuickMatch(QuickMatch.TYPE_CANCEL, ""), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {

            }

            @Override
            public void onError(final int code) {
                ToastUtil.curt(String.valueOf(code));
            }
        });
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

    private void requestVisibleBattleData() {
        if (mListView != null && mVersusListAdapter != null) {
            StringBuilder stringBuilder = new StringBuilder();
            int first = mListView.getFirstVisiblePosition();
            int last = mListView.getLastVisiblePosition();
            for (int i = first; i < last; i++) {
                if (i >= 0) {
                    VersusGaming item = (VersusGaming) mListView.getItemAtPosition(i);
                    if (item != null && item.getGameStatus() != VersusGaming.GAME_STATUS_END) {
                        stringBuilder.append(item.getId()).append(",");
                    }
                }
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                requestVisibleBattleData(stringBuilder.toString());
            }
        }
    }

    private void requestVisibleBattleData(String battleIds) {
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
                if (item.getId() == versusGaming.getId() && item.getGameStatus() == VersusGaming.GAME_STATUS_STARTED) {
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
                showMatchDialog();
                break;
            case VersusGaming.MATCH_START:
                showMatchDialog();
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
        mIngot.setText(data.getYuanbao() + "个");
    }


    private void updateAvatar() {
        if (LocalUser.getUser().isLogin()) {
            Glide.with(getActivity())
                    .load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .placeholder(R.drawable.ic_default_avatar)
                    .transform(new GlideCircleTransform(getActivity()))
                    .into(mAvatar);
        } else {
            mAvatar.setImageResource(R.drawable.ic_default_avatar);
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
                            .executeForResult(CANCEL_BATTLE);
                }
                break;
            default:
                break;
        }
    }

    private void showJoinBattleDialog(final VersusGaming item) {
        String reward = "";
        switch (item.getCoinType()) {
            case VersusGaming.COIN_TYPE_BAO:
                reward = item.getReward() + getActivity().getString(R.string.ingot);
                break;
            case VersusGaming.COIN_TYPE_CASH:
                reward = item.getReward() + getActivity().getString(R.string.cash);
                break;
            case VersusGaming.COIN_TYPE_INTEGRAL:
                reward = item.getReward() + getActivity().getString(R.string.integral);
                break;
        }
        if (mJoinDialog == null) {
            mJoinDialog = SmartDialog.with(getActivity());
        }
        mJoinDialog.setMessage(getString(R.string.join_versus_tip, reward))
                .setMessageTextSize(15)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestJoinBattle(item);
                    }
                })
                .setTitle(getString(R.string.join_versus_title))
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setNegative(R.string.cancel)
                .show();

    }

    private void showJoinVersusFailureDialog(String msg, final int type) {
        Integer positiveMsg;
        Integer negativeMsg = R.string.cancel;
        //存在还没有结束的游戏
        if (type == VersusGaming.CODE_EXIT_VERSUS) {
            msg = getString(R.string.exit_versus);
            positiveMsg = R.string.go_versus;
        } else if (type == VersusGaming.CODE_NO_ENOUGH_MONEY) {
            msg = getString(R.string.join_versus_failure_tip);
            positiveMsg = R.string.go_recharge;
        } else {
            msg = getString(R.string.invite_invalid);
            positiveMsg = R.string.ok;
            negativeMsg = null;
        }
        if (mJoinFailureDialog == null) {
            mJoinFailureDialog = SmartDialog.with(getActivity());
        }
        mJoinFailureDialog.setMessage(msg)
                .setMessageTextSize(15)
                .setPositive(positiveMsg, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        if (type == VersusGaming.CODE_EXIT_VERSUS) {
                            if (mMyCurrentGame != null) {
                                mMyCurrentGame.setPageType(VersusGaming.PAGE_VERSUS);
                                Launcher.with(getActivity(), FutureBattleActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, mMyCurrentGame)
                                        .execute();
                            }
                        } else if (type == VersusGaming.CODE_NO_ENOUGH_MONEY) {
                            Launcher.with(getActivity(), RechargeActivity.class).execute();
                        }
                    }
                })
                .setTitle(getString(R.string.join_versus_failure_title))
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setNegative(negativeMsg)
                .show();

    }

    private void showAskMatchDialog() {
        if (mAskMatchDialog == null) {
            mAskMatchDialog = SmartDialog.with(getActivity(), getString(R.string.match_versus_tip), getString(R.string.match_versus_title));
            mAskMatchDialog
                    .setMessageTextSize(15)
                    .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            requestMatchVersusOfSocket("");
                        }
                    })
                    .setTitleMaxLines(1)
                    .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                    .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                    .setNegative(R.string.cancel);
        }
        mAskMatchDialog.show();

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
        if (mCancelMatchDialog == null) {
            mCancelMatchDialog = SmartDialog.with(getActivity(), getString(R.string.cancel_tip), getString(R.string.cancel_matching));
            mCancelMatchDialog.setMessageTextSize(15)
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
                            showMatchDialog();
                        }
                    })
                    .setTitleMaxLines(1)
                    .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                    .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                    .setCancelableOnTouchOutside(false)
                    .show();
        }
        mCancelMatchDialog.show();
    }

    private void showMatchTimeoutDialog() {
        dismissAllDialog();
        SmartDialog.with(getActivity(), getString(R.string.match_overtime), getString(R.string.match_failed))
                .setMessageTextSize(15)
                .setPositive(R.string.rematch, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestMatchVersusOfSocket("");
                    }
                })
                .setNegative(R.string.later_try_again, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setCancelableOnTouchOutside(false)
                .show();
    }

    private void showMatchSuccessDialog(final VersusGaming data) {
        dismissAllDialog();
        if (data == null) return;
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
        sb.append(getString(R.string.versus_variety_name)).append(" ").append(data.getVarietyName()).append("\n")
                .append(getString(R.string.versus_time)).append(" ").append(DateUtil.getMinutes(data.getEndline())).append("\n")
                .append(getString(R.string.versus_reward)).append(" ").append(reward).append("\n")
                .append(getString(R.string.versus_tip));
        SmartDialog.with(getActivity(), sb.toString(), getString(R.string.title_match_success))
                .setMessageTextSize(15)
                .setMessageMaxLines(10)
                .setPositive(R.string.continue_versus, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        //传入拒绝的id
                        requestMatchVersusOfSocket(String.valueOf(data.getId()));
                    }
                })
                .setNegative(R.string.join_versus, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestJoinBattle(data);
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
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(mLoginBroadcastReceiver);
        WSClient.get().removePushReceiveListener(mPushReceiveListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CANCEL_BATTLE && resultCode == RESULT_OK) {
            int gameStatus = data.getIntExtra(Launcher.EX_PAYLOAD, -1);
            int id = data.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
            if (id != -1 && gameStatus == VersusGaming.GAME_STATUS_CANCELED) {
                if (mVersusListAdapter != null) {
                    for (int i = 0; i < mVersusListAdapter.getCount(); i++) {
                        VersusGaming item = mVersusListAdapter.getItem(i);
                        if (item != null) {
                            mVersusListAdapter.remove(item);
                            mVersusListAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onLoadMore() {
        requestBattleList();
    }

    @Override
    public void onRefresh() {
        reset();
        requestBattleList();
        requestCurrentBattle();
    }

    private void reset() {
        mSet.clear();
        mLocation = null;
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }

    private void dismissAllDialog() {
        if (mJoinDialog != null) {
            mJoinDialog.dismiss();
        }
        if (mJoinFailureDialog != null) {
            mJoinFailureDialog.dismiss();
        }
        if (mAskMatchDialog != null) {
            mAskMatchDialog.dismiss();
        }
        if (mCancelMatchDialog != null) {
            mCancelMatchDialog.dismiss();
        }
        if (mStartMatchDialogFragment != null) {
            mStartMatchDialogFragment.dismiss();
        }
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }

    class LoginBroadcastReceiver extends BroadcastReceiver {
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
            void onClick(VersusGaming item);
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
                mAgainstName.setText(item.getAgainstUserName());
                mAgainstProfit.setText(String.valueOf(item.getAgainstScore()));
                String reward = "";
                switch (item.getCoinType()) {
                    case VersusGaming.COIN_TYPE_BAO:
                        reward = item.getReward() + context.getString(R.string.ingot);
                        break;
                    case VersusGaming.COIN_TYPE_CASH:
                        reward = item.getReward() + context.getString(R.string.cash);
                        break;
                    case VersusGaming.COIN_TYPE_INTEGRAL:
                        reward = item.getReward() + context.getString(R.string.integral);
                        break;
                }
                switch (item.getGameStatus()) {
                    case VersusGaming.GAME_STATUS_CREATED:
                        mDepositAndTime.setText(reward + " " + DateUtil.getMinutes(item.getEndline()));
                        mCreateKo.setVisibility(View.GONE);
                        mAgainstKo.setVisibility(View.GONE);
                        mAgainstAvatar.setImageDrawable(null);
                        mAgainstAvatar.setImageResource(R.drawable.btn_join_versus);
                        mAgainstAvatar.setClickable(false);
                        mAgainstName.setText(context.getString(R.string.join_versus));
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
                        mAgainstAvatar.setClickable(false);
                        showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
                        break;
                    case VersusGaming.GAME_STATUS_END:
                        mDepositAndTime.setText(reward + " " + context.getString(R.string.versus_end));
                        Glide.with(context).load(item.getLaunchUserPortrait())
                                .load(item.getAgainstUserPortrait())
                                .placeholder(R.drawable.ic_default_avatar_big)
                                .transform(new GlideCircleTransform(context))
                                .into(mAgainstAvatar);
                        mAgainstAvatar.setClickable(false);
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
                    mProgressBar.setSecondaryProgress(100);
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
