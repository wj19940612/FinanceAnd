package com.sbai.finance.activity;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.battle.BattleActivity;
import com.sbai.finance.activity.training.JudgeTrainingActivity;
import com.sbai.finance.activity.training.KlineTrainActivity;
import com.sbai.finance.activity.training.NounExplanationActivity;
import com.sbai.finance.activity.training.SortQuestionActivity;
import com.sbai.finance.activity.training.TrainingCountDownActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MissAudioManager;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.websocket.PushCode;
import com.sbai.finance.websocket.WSPush;
import com.sbai.finance.websocket.WsClient;
import com.sbai.finance.websocket.callback.OnPushReceiveListener;

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        WsClient.get().removePushReceiveListener(mPushReceiveListener);
    }

    private OnPushReceiveListener<WSPush<Battle>> mPushReceiveListener = new OnPushReceiveListener<WSPush<Battle>>() {
        @Override
        public void onPushReceive(WSPush<Battle> battleWSPush) {
            switch (battleWSPush.getContent().getType()) {
                case PushCode.BATTLE_JOINED:
                    if (isShowQuickJoinBattleDialog() && battleWSPush.getContent() != null) {
                        if (battleWSPush.getContent().getData() != null) {
                            Battle battle = (Battle) battleWSPush.getContent().getData();
                            boolean isCreator = battle.getLaunchUser() == LocalUser.getUser().getUserInfo().getId();
                            if (isCreator) { // 只有创建对战的人才显示弹窗通知
                                showQuickJoinBattleDialog(battle);
                            }
                        }
                    }
                    break;
            }
            onBattlePushReceived(battleWSPush);
        }
    };

    protected void onBattlePushReceived(WSPush<Battle> battleWSPush) {
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

    private boolean isShowQuickJoinBattleDialog() {
        if (getActivity() instanceof SortQuestionActivity
                || getActivity() instanceof KlineTrainActivity
                || getActivity() instanceof NounExplanationActivity
                || getActivity() instanceof JudgeTrainingActivity
                || getActivity() instanceof TrainingCountDownActivity
                || getActivity() instanceof BattleActivity) {
            return false;
        }
        return LocalUser.getUser().isLogin();
    }

    protected FragmentActivity getActivity() {
        return this;
    }
}
