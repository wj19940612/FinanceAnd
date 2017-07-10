package com.sbai.finance.activity.battle;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.chart.KlineView;
import com.sbai.chart.TrendView;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.fragment.battle.FutureBattleDetailFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.BattleButtons;
import com.sbai.finance.view.BattleFloatView;
import com.sbai.finance.view.BattleTradeView;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.HackTabLayout;
import com.sbai.finance.websocket.WSPush;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.view.BattleFloatView.Mode.MINE;


/**
 * Created by linrongfang on 2017/7/7.
 */

public class FutureBattleActivity extends BaseActivity {

    public static final String PAGE_TYPE = "page_type";
    //0 对战记录 1 对战中
    public static final int PAGE_TYPE_RECORD = 0;
    public static final int PAGE_TYPE_BATTLE = 1;

    @BindView(R.id.content)
    LinearLayout mContent;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.lastPrice)
    TextView mLastPrice;
    @BindView(R.id.priceChange)
    TextView mPriceChange;
    @BindView(R.id.todayOpen)
    TextView mTodayOpen;
    @BindView(R.id.preClose)
    TextView mPreClose;
    @BindView(R.id.highest)
    TextView mHighest;
    @BindView(R.id.lowest)
    TextView mLowest;

    @BindView(R.id.tabLayout)
    HackTabLayout mTabLayout;
    @BindView(R.id.trendView)
    TrendView mTrendView;
    @BindView(R.id.klineView)
    KlineView mKlineView;

    @BindView(R.id.battleButtons)
    BattleButtons mBattleButtons;
    @BindView(R.id.battleTradeView)
    BattleTradeView mBattleTradeView;

    @BindView(R.id.loading)
    ImageView mLoading;
    @BindView(R.id.loadingContent)
    LinearLayout mLoadingContent;

    @BindView(R.id.battleView)
    BattleFloatView mBattleView;


    private int mBattleId;
    private String mBatchCode;

    private Battle mBattle;
    private int mPageType;


    @Override
    protected void onBattlePushReceived(WSPush<Battle> battleWSPush) {
        super.onBattlePushReceived(battleWSPush);
        
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_future_battle);
        ButterKnife.bind(this);
        initData();


    }

    private void initData() {
        mBattleId = getIntent().getIntExtra(Launcher.EX_PAYLOAD_1, -1);
        mBatchCode = getIntent().getStringExtra(Launcher.EX_PAYLOAD_2);

        requestLastBattleInfo(mBattleId, mBatchCode);
    }

    private void requestLastBattleInfo(int battleId, String batchCode) {
        Client.getBattleInfo(battleId, batchCode).setTag(TAG)
                .setCallback(new Callback2D<Resp<Battle>, Battle>() {
                    @Override
                    protected void onRespSuccessData(Battle data) {
                        if (data != null) {
                            mBattle = data;
                            if (data.isBattleStop()) {
                                mPageType = PAGE_TYPE_RECORD;
                                initBattleRecordPage();
                            } else {
                                // TODO: 2017/7/7 区分对战和观战
                                mPageType = PAGE_TYPE_BATTLE;
                                initBattlePage();
                            }

                        }
                    }
                }).fire();
    }

    private void initBattleRecordPage() {
        //只加载一个详情的Fragment
        mContent.removeAllViews();
        FutureBattleDetailFragment fragment = FutureBattleDetailFragment.newInstance(mBattle);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, fragment)
                .commitAllowingStateLoss();

        mBattleView.setMode(MINE)
                .initWithModel(mBattle)
                .setDeadline(mBattle.getGameStatus(), 0)
                .setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), false)
                .setWinResult(mBattle.getWinResult());
    }

    private void initBattlePage() {
        //处理从通知栏点进来的
        if (!LocalUser.getUser().isLogin()) {
            Launcher.with(FutureBattleActivity.this, MainActivity.class).execute();
            finish();
        }

    }
}
