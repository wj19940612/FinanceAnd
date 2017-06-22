package com.sbai.finance.activity.future;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.StartMatchDialogFragment;
import com.sbai.finance.fragment.future.FutureBattleDetailFragment;
import com.sbai.finance.fragment.future.FutureBattleFragment;
import com.sbai.finance.model.versus.VersusGaming;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.BattleButtons;
import com.sbai.finance.view.BattleFloatView;
import com.sbai.finance.view.BattleTradeView;
import com.sbai.finance.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.model.versus.VersusGaming.PAGE_RECORD;

/**
 * Created by linrongfang on 2017/6/19.
 */

public class FutureBattleActivity extends BaseActivity implements BattleButtons.OnViewClickListener,
        BattleTradeView.OnViewClickListener {

    @BindView(R.id.futureArea)
    LinearLayout mFutureArea;
    @BindView(R.id.battleView)
    BattleFloatView mBattleView;

    private FutureBattleFragment mFutureBattleFragment;
    private FutureBattleDetailFragment mFutureBattleDetailFragment;
    private StartMatchDialogFragment mStartMatchDialogFragment;

    private VersusGaming mVersusGaming;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_battle);
        ButterKnife.bind(this);

        initData();

        initViews();
    }

    private void initData() {
        mVersusGaming = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
    }


    private void initViews() {
        if (mVersusGaming.getPageType() == PAGE_RECORD) {
            showFutureBattleDetail();
        } else {
            showFutureBattle();
        }
    }

    public void showFutureBattle() {
        if (mFutureBattleFragment == null) {
            mFutureBattleFragment = FutureBattleFragment.newInstance(null);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.futureArea, mFutureBattleFragment)
                .commit();
    }

    public void showFutureBattleDetail() {
        if (mFutureBattleDetailFragment == null) {
            mFutureBattleDetailFragment = FutureBattleDetailFragment.newInstance(mVersusGaming.getId());
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.futureArea, mFutureBattleFragment)
                .commit();

        mBattleView.setMode(BattleFloatView.Mode.MINE)
                .setCreateAvatar(mVersusGaming.getLaunchUserPortrait())
                .setCreateName(mVersusGaming.getLaunchUserName())
                .setAgainstAvatar(mVersusGaming.getAgainstUserPortrait())
                .setAgainstName(mVersusGaming.getAgainstUserName())
                .setDeposit(mVersusGaming.getReward(), mVersusGaming.getCoinType())
                .setDeadline(mVersusGaming.getGameStatus(), 0)
                .setProgress(mVersusGaming.getLaunchScore(), mVersusGaming.getAgainstScore(), false)
                .setPraise(mVersusGaming.getLaunchPraise(), mVersusGaming.getAgainstPraise())
                .setWinResult(mVersusGaming.getWinResult());
    }

    @Override
    public void onInviteButtonClick() {
         showInviteDialog();
    }

    private void showInviteDialog() {
        // TODO: 2017/6/22 分享 
    }

    @Override
    public void onMatchButtonClick() {
        showMatchDialog();
        // TODO: 2017/6/22 房主开始匹配
    }

    //开始匹配弹窗
    private void showMatchDialog() {
        if (mStartMatchDialogFragment == null) {
            mStartMatchDialogFragment = StartMatchDialogFragment
                    .newInstance()
                    .setOnCancelListener(new StartMatchDialogFragment.OnCancelListener() {
                        @Override
                        public void onCancel() {
                            showCancelMatchDialog();
                        }
                    });
        }
        mStartMatchDialogFragment.show(getSupportFragmentManager());
    }

    //取消匹配弹窗
    private void showCancelMatchDialog() {
        SmartDialog.with(getActivity(), getString(R.string.cancel_tip), getString(R.string.cancel_matching))
                .setMessageTextSize(15)
                .setPositive(R.string.no_waiting, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        mStartMatchDialogFragment.dismiss();
                    }
                })
                .setNegative(R.string.continue_versus, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .show();

    }

    //超时弹窗
    private void showOvertimeMatchDialog() {
        SmartDialog.with(getActivity(), getString(R.string.match_overtime), getString(R.string.match_failed))
                .setMessageTextSize(15)
                .setPositive(R.string.later_try_again, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setNegative(R.string.rematch, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .show();

    }

    @Override
    public void onCancelButtonClick() {
        showCancelBattleDialog();
    }

    private void showCancelBattleDialog() {
        SmartDialog.with(getActivity(), getString(R.string.cancel_battle_tip), getString(R.string.cancel_battle))
                .setMessageTextSize(15)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setNegative(R.string.continue_to_battle, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        // TODO: 2017/6/22 退出房间 退出失败弹提示
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .show();

    }

    @Override
    public void onLongPurchaseButtonClick() {

    }

    @Override
    public void onShortPurchaseButtonClick() {

    }

    @Override
    public void onClosePositionButtonClick() {

    }
}
