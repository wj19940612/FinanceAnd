package com.sbai.finance.activity.battle;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.cornucopia.CornucopiaActivity;
import com.sbai.finance.fragment.dialog.BattleRuleDialogFragment;
import com.sbai.finance.fragment.dialog.StartMatchDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.FutureVersus;
import com.sbai.finance.model.mutual.ArticleProtocol;
import com.sbai.finance.model.payment.UserFundInfoModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.view.BattleProgress;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.websocket.PushCode;
import com.sbai.finance.websocket.WSMessage;
import com.sbai.finance.websocket.WSPush;
import com.sbai.finance.websocket.WsClient;
import com.sbai.finance.websocket.callback.WSCallback;
import com.sbai.finance.websocket.cmd.QuickMatch;
import com.sbai.httplib.BuildConfig;

import java.util.ArrayList;
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

    @BindView(R.id.matchBattle)
    TextView mMatchBattle;
    @BindView(R.id.createBattle)
    TextView mCreateBattle;
    @BindView(R.id.createAndMatchArea)
    LinearLayout mCreateAndMatchArea;
    @BindView(R.id.currentBattle)
    TextView mCurrentBattleBtn;

    private ImageView mAvatar;
    private TextView mIntegral;
    private TextView mIngot;
    private TextView mRecharge;
    private VersusListAdapter mVersusListAdapter;
    private Long mLocation;
    private LoginBroadcastReceiver mLoginBroadcastReceiver;
    private ScreenOnBroadcastReceiver mScreenOnBroadcastReceiver;

    private HashSet<Integer> mSet;
    private Battle mCurrentBattle;
    private StartMatchDialogFragment mQuickMatchDialogFragment;
    private StringBuilder mRefusedIds;

    @Override
    protected void onBattlePushReceived(WSPush<Battle> battleWSPush) {
        super.onBattlePushReceived(battleWSPush);
        switch (battleWSPush.getContent().getType()) {
            case PushCode.QUICK_MATCH_TIMEOUT:
            case PushCode.QUICK_MATCH_FAILURE:
                showMatchTimeoutDialog();
                break;
            case PushCode.QUICK_MATCH_SUCCESS:
                showMatchSuccessDialog((Battle) battleWSPush.getContent().getData());
                break;
            case PushCode.BATTLE_OVER:
                requestCurrentBattle();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_list);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            mQuickMatchDialogFragment = StartMatchDialogFragment.newInstance()
                    .setOnCancelListener(new StartMatchDialogFragment.OnCancelListener() {
                        @Override
                        public void onCancel() {
                            mQuickMatchDialogFragment.dismiss();
                            showCancelMatchDialog();
                        }
                    });
        } else {
            mQuickMatchDialogFragment = (StartMatchDialogFragment) getSupportFragmentManager()
                    .findFragmentByTag(StartMatchDialogFragment.TAG);
        }

        initTitleBar();
        initListHeaderAndFooter();
        initListView();

        initLoginReceiver();
        initScreenOnReceiver();
        updateAvatar();
        requestBattleList();

        scrollToTop(mTitleBar, mListView);
    }

    private void initScreenOnReceiver() {
        mScreenOnBroadcastReceiver = new ScreenOnBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        getActivity().registerReceiver(mScreenOnBroadcastReceiver,intentFilter);
    }

    private void initLoginReceiver() {
        mLoginBroadcastReceiver = new LoginBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginActivity.LOGIN_SUCCESS_ACTION);
        intentFilter.addAction(CreateBattleActivity.CREATE_SUCCESS_ACTION);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mLoginBroadcastReceiver, intentFilter);
    }

    private void initTitleBar() {
        View view = mTitleBar.getCustomView();
        mAvatar = (ImageView) view.findViewById(R.id.avatar);
        mIntegral = (TextView) view.findViewById(R.id.integral);
        mIngot = (TextView) view.findViewById(R.id.ingot);
        mRecharge = (TextView) view.findViewById(R.id.recharge);
        mRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umengEventCount(UmengCountEventIdUtils.BATTLE_HALL_RECHARGE);
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), CornucopiaActivity.class).execute();
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
                umengEventCount(UmengCountEventIdUtils.BATTLE_HALL_CHECK_RECODE);
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), BattleRecordListActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
        battleRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umengEventCount(UmengCountEventIdUtils.BATTLE_HALL_DUEL_RULES);
                Client.getArticleProtocol(ArticleProtocol.PROTOCOL_BATTLE).setTag(TAG)
                        .setCallback(new Callback2D<Resp<ArticleProtocol>, ArticleProtocol>() {
                            @Override
                            protected void onRespSuccessData(ArticleProtocol data) {
                                BattleRuleDialogFragment
                                        .newInstance(data.getTitle(), data.getContent())
                                        .show(getSupportFragmentManager());
                            }
                        }).fire();
            }
        });
        mListView.addHeaderView(header);
        //add footer
        View view = getLayoutInflater().inflate(R.layout.footer_battle_list, null);
        view.findViewById(R.id.checkHistoryBattle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), BattleHisRecordActivity.class).execute();
            }
        });
        mListView.addFooterView(view);
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

                Battle item = (Battle) parent.getItemAtPosition(position);
                if (item != null) {
                    if (item.getGameStatus() == Battle.GAME_STATUS_END) {
                        Launcher.with(getActivity(), BattleActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, item)
                                .putExtra(BattleActivity.PAGE_TYPE, BattleActivity.PAGE_TYPE_RECORD)
                                .executeForResult(CANCEL_BATTLE);
                    } else if (LocalUser.getUser().isLogin()) {
                        if (item.getGameStatus() == Battle.GAME_STATUS_CREATED
                                && LocalUser.getUser().getUserInfo().getId() != item.getLaunchUser()) {
                            showJoinBattleDialog(item);
                        } else {
                            //请求最新的数据传入到详情页
                            requestLastBattleInfo(item);
                        }
                    } else {
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                }
            }
        });
    }

    private void requestLastBattleInfo(final Battle item) {
        Client.getBattleInfo(item.getId(), item.getBatchCode()).setTag(TAG)
                .setCallback(new Callback2D<Resp<Battle>, Battle>() {
                    @Override
                    protected void onRespSuccessData(Battle data) {
                        if (data.getGameStatus() != item.getGameStatus()) {
                            item.setWinResult(data.getWinResult());
                            item.setGameStatus(data.getGameStatus());
                            item.setEndTime(data.getEndTime());
                            mVersusListAdapter.notifyDataSetChanged();
                        }
                        int pageType;
                        if (data.getGameStatus() == Battle.GAME_STATUS_END) {
                            pageType = BattleActivity.PAGE_TYPE_RECORD;
                        } else {
                            pageType = BattleActivity.PAGE_TYPE_VERSUS;
                        }
                        Launcher.with(getActivity(), BattleActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, data)
                                .putExtra(BattleActivity.PAGE_TYPE, pageType)
                                .executeForResult(CANCEL_BATTLE);

                    }
                }).fire();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LocalUser.getUser().isLogin()) {
            requestUserFindInfo();
            requestCurrentBattle();
        } else {
            mCurrentBattleBtn.setVisibility(View.GONE);
            mCreateAndMatchArea.setVisibility(View.VISIBLE);
            mIntegral.setText(FinanceUtil.formatWithScale(0));
            mIngot.setText(FinanceUtil.formatWithScaleNoZero(0));
        }
        startScheduleJob(5 * 1000);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mQuickMatchDialogFragment != null
                && mQuickMatchDialogFragment.getDialog() != null
                && mQuickMatchDialogFragment.getDialog().isShowing()) {
            requestFastMatchResult();
        }
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
                .setCallback(new Callback<Resp<Battle>>() {
                    @Override
                    protected void onRespSuccess(Resp<Battle> resp) {
                        mCurrentBattle = resp.getData();

                        if (mCurrentBattle == null) {
                            mCreateAndMatchArea.setVisibility(View.VISIBLE);
                            mCurrentBattleBtn.setVisibility(View.GONE);
                        } else {
                            mCreateAndMatchArea.setVisibility(View.GONE);
                            mCurrentBattleBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }).fire();
    }

    private void requestJoinBattle(final Battle data) {
        Client.joinBattle(data.getId(), Battle.SOURCE_HALL).setTag(TAG)
                .setCallback(new Callback<Resp<Battle>>() {
                    @Override
                    protected void onReceive(Resp<Battle> battleResp) {
                        if (battleResp.isSuccess()) {
                            Battle battle = battleResp.getData();
                            if (battle != null) {
                                //更新列表对战信息
                                data.setGameStatus(battle.getGameStatus());
                                data.setAgainstUser(battle.getAgainstUser());
                                data.setAgainstUserPortrait(battle.getAgainstUserPortrait());
                                data.setAgainstUserName(battle.getAgainstUserName());
                                mVersusListAdapter.notifyDataSetChanged();

                                Launcher.with(getActivity(), BattleActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, battle)
                                        .putExtra(BattleActivity.PAGE_TYPE, BattleActivity.PAGE_TYPE_VERSUS)
                                        .executeForResult(CANCEL_BATTLE);
                            }
                        } else {
                            showJoinBattleFailureDialog(battleResp);
                        }
                    }
                    @Override
                    protected void onRespSuccess(Resp<Battle> resp) {

                    }
                }).fireFree();
    }

    //快速匹配结果查询
    private void requestFastMatchResult() {
        Client.getQuickMatchResult(Battle.AGAINST_FAST_MATCH, null).setTag(TAG)
                .setCallback(new Callback<Resp<Battle>>() {
                    @Override
                    protected void onReceive(Resp<Battle> battleResp) {
                        if (battleResp.isSuccess() && battleResp.getData() != null) {
                            showMatchSuccessDialog(battleResp.getData());
                        } else if (battleResp.getCode() == Battle.CODE_AGAINST_FAST_MATCH_TIMEOUT) {
                            showMatchTimeoutDialog();
                        }
                    }
                    @Override
                    protected void onRespSuccess(Resp<Battle> resp) {

                    }
                }).fireFree();
    }

    private void requestQuickMatch() {
        WsClient.get().send(new QuickMatch(QuickMatch.TYPE_QUICK_MATCH), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {
                showMatchDialog();
            }
        });
    }


    private void requestCancelMatch() {
        WsClient.get().send(new QuickMatch(QuickMatch.TYPE_CANCEL), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {
                if (BuildConfig.DEBUG) {
                    ToastUtil.show("Cancel success");
                }
            }
        });
    }

    private void requestVisibleBattleData() {
        if (mListView != null && mVersusListAdapter != null) {
            StringBuilder stringBuilder = new StringBuilder();
            int first = mListView.getFirstVisiblePosition();
            int last = mListView.getLastVisiblePosition();
            for (int i = first; i < last; i++) {
                if (i >= 0) {
                    Battle item = (Battle) mListView.getItemAtPosition(i);
                    if (item != null && item.getGameStatus() != Battle.GAME_STATUS_END) {
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
                .setCallback(new Callback2D<Resp<List<Battle>>, List<Battle>>() {
                    @Override
                    protected void onRespSuccessData(List<Battle> data) {
                        updateVisibleVersusData(data);
                    }
                }).fire();
    }

    private void updateVisibleVersusData(List<Battle> data) {
        if (data.isEmpty() || mVersusListAdapter == null) return;
        List<Battle> removeBattleList = new ArrayList<>();
        for (int i = 0; i < mVersusListAdapter.getCount(); i++) {
            Battle item = mVersusListAdapter.getItem(i);
            for (Battle battle : data) {
                if (item.getId() == battle.getId() && item.getGameStatus() != Battle.GAME_STATUS_END) {

                    if (battle.getGameStatus() == Battle.GAME_STATUS_CANCELED) {
                        removeBattleList.add(item);
                    } else {
                        item.setGameStatus(battle.getGameStatus());
                        item.setLaunchPraise(battle.getLaunchPraise());
                        item.setLaunchScore(battle.getLaunchScore());
                        item.setAgainstPraise(battle.getAgainstPraise());
                        item.setAgainstScore(battle.getAgainstScore());
                        item.setWinResult(battle.getWinResult());
                        if (battle.getGameStatus() == Battle.GAME_STATUS_STARTED
                                || battle.getGameStatus() == Battle.GAME_STATUS_END) {
                            item.setAgainstUser(battle.getAgainstUser());
                            item.setAgainstUserName(battle.getAgainstUserName());
                            item.setAgainstUserPortrait(battle.getAgainstUserPortrait());
                        }
                        if (battle.getGameStatus() == Battle.GAME_STATUS_END) {
                            item.setWinResult(battle.getWinResult());
                        }
                    }
                    data.remove(battle);
                    break;
                }
            }
        }
        for (Battle battle : removeBattleList) {
            mVersusListAdapter.remove(battle);
        }
        mVersusListAdapter.notifyDataSetChanged();
    }

    private void updateUserFund(UserFundInfoModel data) {
        if (data == null) return;
        mIntegral.setText(StrFormatter.getFormIntegrate(data.getCredit()));
        mIngot.setText(getString(R.string.number_ge, StrFormatter.getFormIngot(data.getYuanbao())));
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
        for (Battle battle : futureVersus.getList()) {
            if (mSet.add(battle.getId())) {
                mVersusListAdapter.add(battle);
            }
        }
        if (!futureVersus.hasMore()) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else if (futureVersus.getList().size() > 0) {
            mLocation = futureVersus.getList().get(futureVersus.getList().size() - 1).getCreateTime();
        }
        mVersusListAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.createBattle, R.id.matchBattle, R.id.currentBattle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createBattle:
                umengEventCount(UmengCountEventIdUtils.BATTLE_HALL_CREATE_BATTLE);
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), CreateBattleActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.matchBattle:
                umengEventCount(UmengCountEventIdUtils.BATTLE_HALL_MATCH_BATTLE);
                if (LocalUser.getUser().isLogin()) {
                    showAskMatchDialog();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.currentBattle:
                umengEventCount(UmengCountEventIdUtils.BATTLE_HALL_CURRENT_BATTLE);
                if (mCurrentBattle != null) {
                    requestLastBattleInfo(mCurrentBattle);
                }
                break;
            default:
                break;
        }
    }

    private void showJoinBattleDialog(final Battle item) {
        String reward = "";
        switch (item.getCoinType()) {
            case Battle.COIN_TYPE_BAO:
                reward = item.getReward() + getActivity().getString(R.string.ingot);
                break;
            case Battle.COIN_TYPE_CASH:
                reward = item.getReward() + getActivity().getString(R.string.cash);
                break;
            case Battle.COIN_TYPE_INTEGRAL:
                reward = item.getReward() + getActivity().getString(R.string.integral);
                break;
        }
        SmartDialog.single(getActivity(), getString(R.string.join_versus_tip, reward))
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestJoinBattle(item);
                    }
                })
                .setTitle(getString(R.string.join_versus_title))
                .setNegative(R.string.cancel)
                .show();

    }

    private void showJoinBattleFailureDialog(final Resp<Battle> resp) {
        final int code = resp.getCode();
        int positiveMsg;
        String msg = null;
        SmartDialog smartDialog = SmartDialog.single(getActivity(), msg);
        if (code == Battle.CODE_BATTLE_JOINED_OR_CREATED) {
            msg = getString(R.string.battle_joined_or_created);
            positiveMsg = R.string.go_battle;
        } else if (code == Battle.CODE_NO_ENOUGH_MONEY) {
            msg = getString(R.string.join_battle_balance_not_enough);
            positiveMsg = R.string.go_recharge;
        } else {
            msg = getString(R.string.invite_invalid);
            positiveMsg = R.string.ok;
            smartDialog.setNegativeVisible(View.GONE);
        }
        smartDialog.setMessage(msg)
                .setPositive(positiveMsg, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        if (code == Battle.CODE_BATTLE_JOINED_OR_CREATED) {
                            if (mCurrentBattle != null) {
                                Launcher.with(getActivity(), BattleActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, mCurrentBattle)
                                        .putExtra(BattleActivity.PAGE_TYPE, BattleActivity.PAGE_TYPE_VERSUS)
                                        .execute();
                            }
                        } else if (code == Battle.CODE_NO_ENOUGH_MONEY) {
                            Launcher.with(getActivity(), CornucopiaActivity.class).execute();
                        }
                    }
                })
                .setTitle(getString(R.string.join_versus_failure))
                .setNegative(R.string.cancel)
                .show();


    }

    private void showAskMatchDialog() {
        SmartDialog.single(getActivity(), getString(R.string.match_battle_tip))
                .setTitle(getString(R.string.match_battle_confirm))
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestQuickMatch();
                    }
                }).setNegative(R.string.cancel)
                .show();
    }

    private void showMatchDialog() {
        if (mQuickMatchDialogFragment != null) {
            mQuickMatchDialogFragment.show(getSupportFragmentManager());
        }
    }

    private void showCancelMatchDialog() {
        SmartDialog.single(getActivity(), getString(R.string.cancel_tip))
                .setTitle(getString(R.string.cancel_matching))
                .setCancelableOnTouchOutside(false)
                .setPositive(R.string.no_waiting, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestCancelMatch();
                    }
                })
                .setNegative(R.string.continue_match, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        showMatchDialog();
                    }
                })
                .show();
    }

    private void showMatchTimeoutDialog() {
        dismissQuickMatchDialog();

        SmartDialog.single(getActivity(), getString(R.string.match_overtime))
                .setTitle(getString(R.string.match_failed))
                .setCancelableOnTouchOutside(false)
                .setPositive(R.string.rematch, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestQuickMatch();
                    }
                })
                .setNegative(R.string.later_try_again, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showMatchSuccessDialog(final Battle data) {
        dismissQuickMatchDialog();

        if (data == null) return;

        String reward = "";
        switch (data.getCoinType()) {
            case Battle.COIN_TYPE_BAO:
                reward = data.getReward() + getActivity().getString(R.string.ingot);
                break;
            case Battle.COIN_TYPE_CASH:
                reward = data.getReward() + getActivity().getString(R.string.cash);
                break;
            case Battle.COIN_TYPE_INTEGRAL:
                reward = data.getReward() + getActivity().getString(R.string.integral);
                break;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.battle_variety_name)).append(" ").append(data.getVarietyName()).append("\n")
                .append(getString(R.string.battle_time)).append(" ").append(DateUtil.getMinutes(data.getEndline())).append("\n")
                .append(getString(R.string.battle_reward)).append(" ").append(reward).append("\n")
                .append(getString(R.string.versus_tip));

        SmartDialog.single(getActivity(), sb.toString())
                .setTitle(getString(R.string.title_match_success))
                .setMessageMaxLines(10)
                .setPositive(R.string.join_versus, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestJoinBattle(data);
                    }
                })
                .setNegative(R.string.continue_match, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestContinueMatch(data.getId());
                    }
                })
                .setCancelableOnTouchOutside(false)
                .show();
    }

    private void requestContinueMatch(int filteredId) {
        if (mRefusedIds.length() == 0) {
            mRefusedIds.append(filteredId);
        } else {
            mRefusedIds.append(",").append(filteredId);
        }
        WsClient.get().send(new QuickMatch(QuickMatch.TYPE_CONTINUE, mRefusedIds.toString()), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {
                showMatchDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mScreenOnBroadcastReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mLoginBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CANCEL_BATTLE && resultCode == RESULT_OK) {
            int gameStatus = data.getIntExtra(Launcher.EX_PAYLOAD, -1);
            int id = data.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
            if (id != -1 && gameStatus == Battle.GAME_STATUS_CANCELED) {
                if (mVersusListAdapter != null) {
                    for (int i = 0; i < mVersusListAdapter.getCount(); i++) {
                        Battle item = mVersusListAdapter.getItem(i);
                        if (item != null && item.getId() == id) {
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
        if (LocalUser.getUser().isLogin()) {
            requestCurrentBattle();
            requestUserFindInfo();
        }
    }

    private void reset() {
        mSet.clear();
        mLocation = null;
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }

    private void dismissQuickMatchDialog() {
        if (mQuickMatchDialogFragment != null
                && mQuickMatchDialogFragment.getDialog() != null
                && mQuickMatchDialogFragment.getDialog().isShowing()) {
            mQuickMatchDialogFragment.dismiss();
        }
    }

    class LoginBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(LoginActivity.LOGIN_SUCCESS_ACTION) ) {
                updateAvatar();
                requestUserFindInfo();
                requestCurrentBattle();
            }
            if (intent.getAction().equalsIgnoreCase(CreateBattleActivity.CREATE_SUCCESS_ACTION)
                    ||intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_ON)) {
                reset();
                requestBattleList();
                if (LocalUser.getUser().isLogin()) {
                    requestCurrentBattle();
                    requestUserFindInfo();
                }
            }
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

    class ScreenOnBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null&&intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_ON)) {
                reset();
                requestBattleList();
                if (LocalUser.getUser().isLogin()) {
                    requestCurrentBattle();
                    requestUserFindInfo();
                }
            }
        }
    }


    static class VersusListAdapter extends ArrayAdapter<Battle> {
        interface Callback {
            void onClick(Battle item);
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
            @BindView(R.id.progress)
            BattleProgress mProgress;
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

            private void bindDataWithView(final Battle item, Context context, final Callback callback) {
                mVarietyName.setText(item.getVarietyName());
                Glide.with(context).load(item.getLaunchUserPortrait())
                        .load(item.getLaunchUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar_big)
                        .transform(new GlideCircleTransform(context))
                        .into(mCreateAvatar);
                mCreateName.setText(item.getLaunchUserName());
                mAgainstName.setText(item.getAgainstUserName());
                String reward = "";
                switch (item.getCoinType()) {
                    case Battle.COIN_TYPE_BAO:
                        reward = item.getReward() + context.getString(R.string.ingot);
                        break;
                    case Battle.COIN_TYPE_CASH:
                        reward = item.getReward() + context.getString(R.string.cash);
                        break;
                    case Battle.COIN_TYPE_INTEGRAL:
                        reward = item.getReward() + context.getString(R.string.integral);
                        break;
                }
                switch (item.getGameStatus()) {
                    case Battle.GAME_STATUS_CREATED:
                        mDepositAndTime.setText(reward + " " + DateUtil.getMinutes(item.getEndline()));
                        mCreateKo.setVisibility(View.GONE);
                        mAgainstKo.setVisibility(View.GONE);
                        mAgainstAvatar.setImageDrawable(null);
                        mAgainstAvatar.setImageResource(R.drawable.btn_join_versus);
                        mAgainstAvatar.setClickable(false);
                        mAgainstName.setText(context.getString(R.string.join_versus));
                        mProgress.showScoreProgress(0, 0, true);
                        break;
                    case Battle.GAME_STATUS_STARTED:
                        mDepositAndTime.setText(reward + " " + context.getString(R.string.versusing));
                        mCreateKo.setVisibility(View.GONE);
                        mAgainstKo.setVisibility(View.GONE);
                        Glide.with(context).load(item.getLaunchUserPortrait())
                                .load(item.getAgainstUserPortrait())
                                .placeholder(R.drawable.ic_default_avatar_big)
                                .transform(new GlideCircleTransform(context))
                                .into(mAgainstAvatar);
                        mAgainstAvatar.setClickable(false);
                        mProgress.showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
                        break;
                    case Battle.GAME_STATUS_END:
                        mDepositAndTime.setText(reward + " " + context.getString(R.string.versus_end));
                        Glide.with(context).load(item.getLaunchUserPortrait())
                                .load(item.getAgainstUserPortrait())
                                .placeholder(R.drawable.ic_default_avatar_big)
                                .transform(new GlideCircleTransform(context))
                                .into(mAgainstAvatar);
                        mAgainstAvatar.setClickable(false);
                        if (item.getWinResult() == Battle.WIN_RESULT_CHALLENGER_WIN) {
                            mCreateKo.setVisibility(View.VISIBLE);
                            mAgainstKo.setVisibility(View.GONE);
                        } else if (item.getWinResult() == Battle.WIN_RESULT_CREATOR_WIN) {
                            mCreateKo.setVisibility(View.GONE);
                            mAgainstKo.setVisibility(View.VISIBLE);
                        } else {
                            mCreateKo.setVisibility(View.GONE);
                            mAgainstKo.setVisibility(View.GONE);
                        }
                        mProgress.showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
                        break;

                }
            }
        }

    }
}