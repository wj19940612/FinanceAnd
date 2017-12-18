package com.sbai.finance.activity;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.arena.klinebattle.BattleKlineActivity;
import com.sbai.finance.activity.arena.klinebattle.BattleKlineDetailActivity;
import com.sbai.finance.activity.arena.klinebattle.BattleKlinePkActivity;
import com.sbai.finance.activity.battle.BattleActivity;
import com.sbai.finance.activity.training.JudgeTrainingActivity;
import com.sbai.finance.activity.training.KlineTrainActivity;
import com.sbai.finance.activity.training.NounExplanationActivity;
import com.sbai.finance.activity.training.SortQuestionActivity;
import com.sbai.finance.activity.training.TrainingCountDownActivity;
import com.sbai.finance.game.PushCode;
import com.sbai.finance.game.WSPush;
import com.sbai.finance.game.WsClient;
import com.sbai.finance.game.callback.OnPushReceiveListener;
import com.sbai.finance.kgame.GamePusher;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.Praise;
import com.sbai.finance.model.battle.TradeOrder;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.audio.MissAudioManager;
import com.sbai.finance.view.SmartDialog;

import java.lang.reflect.Type;

/**
 * Modified by john on 01/11/2017
 * <p>
 * Description: 统一处理 对战的 push Activity
 * <p>
 * APIs:
 */
public class BattlePushActivity extends StatusBarActivity {

    @Override
    protected void onPostResume() {
        super.onPostResume();
        WsClient.get().addOnPushReceiveListener(mPushReceiveListener);
        GamePusher.get().setOnPushReceiveListener(mKlineBattlePushReceiverListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WsClient.get().removePushReceiveListener(mPushReceiveListener);
        WsClient.get().removePushReceiveListener(mKlineBattlePushReceiverListener);
    }

    private OnPushReceiveListener<BattleKline.BattleBean> mKlineBattlePushReceiverListener = new OnPushReceiveListener<BattleKline.BattleBean>() {
        @Override
        public void onPushReceive(BattleKline.BattleBean battleBean, String originalData) {
            onBattleKlinePushReceived(battleBean);
        }
    };
    private OnPushReceiveListener<WSPush> mPushReceiveListener = new OnPushReceiveListener<WSPush>() {
        @Override
        public void onPushReceive(WSPush wsPush, String originalData) {
            int pushType = wsPush.getContent().getType();
            if (pushType == PushCode.ORDER_CREATED || pushType == PushCode.ORDER_CLOSE) {
                Type type = new TypeToken<WSPush<TradeOrder>>() {
                }.getType();
                WSPush<TradeOrder> tradeOrderWSPush = new Gson().fromJson(originalData, type);
                onBattleOrdersReceived(tradeOrderWSPush);
            } else if (pushType == PushCode.USER_PRAISE) {
                Type type = new TypeToken<WSPush<Praise>>() {
                }.getType();
                WSPush<Praise> praiseWSPush = new Gson().fromJson(originalData, type);
                onBattlePraiseReceived(praiseWSPush);
            } else {
                Type type = new TypeToken<WSPush<Battle>>() {
                }.getType();
                WSPush<Battle> battleWSPush = new Gson().fromJson(originalData, type);
                onBattlePushReceived(battleWSPush);
            }
        }
    };


    protected void onBattlePushReceived(WSPush<Battle> battleWSPush) {
        if (battleWSPush.getContent().getType() == PushCode.BATTLE_JOINED) {
            if (LocalUser.getUser().isLogin() && isValidPage()) {
                Battle battle = (Battle) battleWSPush.getContent().getData();
                boolean isCreator =
                        battle.getLaunchUser() == LocalUser.getUser().getUserInfo().getId();
                if (isCreator) { // 只有创建对战的人才显示弹窗通知
                    showQuickJoinBattleDialog(battle);
                }
            }
        }
    }

    private boolean isValidPage() {
        if (getActivity() instanceof SortQuestionActivity
                || getActivity() instanceof KlineTrainActivity
                || getActivity() instanceof NounExplanationActivity
                || getActivity() instanceof JudgeTrainingActivity
                || getActivity() instanceof TrainingCountDownActivity
                || getActivity() instanceof BattleActivity
                || getActivity() instanceof BattleKlineDetailActivity) {
            return false;
        }
        return true;
    }

    protected void onBattlePraiseReceived(WSPush<Praise> praiseWSPush) {
    }

    protected void onBattleOrdersReceived(WSPush<TradeOrder> tradeOrderWSPush) {
    }

    protected void onBattleKlinePushReceived(BattleKline.BattleBean battleBean) {
        if (!LocalUser.getUser().isLogin()) return;
        if (!(getActivity() instanceof BattleKlineActivity) && isValidPage()
                && battleBean.getCode().equalsIgnoreCase( BattleKline.PUSH_CODE_MATCH_SUCCESS)) {
            SmartDialog.single(getActivity(), getString(R.string.match_success_please_go_to_battle))
                    .setTitle(getString(R.string.join_battle))
                    .setPositive(R.string.quick_battle, new SmartDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            MissAudioManager.get().stop();

                            dialog.dismiss();
                            Launcher.with(getActivity(), BattleKlinePkActivity.class)
//                                    .putExtra(ExtraKeys.GUESS_TYPE, battle)
                                    .execute();

                        }
                    })
                    .setNegative(R.string.cancel)
                    .show();
        }
    }

    protected void showQuickJoinBattleDialog(final Battle battle) {
        SmartDialog.single(getActivity(), getString(R.string.quick_join_battle))
                .setTitle(getString(R.string.join_battle))
                .setPositive(R.string.quick_battle, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        MissAudioManager.get().stop();

                        dialog.dismiss();
                        Launcher.with(getActivity(), BattleActivity.class)
                                .putExtra(ExtraKeys.BATTLE, battle)
                                .execute();

                    }
                })
                .setNegative(R.string.cancel)
                .show();
    }

    protected FragmentActivity getActivity() {
        return this;
    }
}
