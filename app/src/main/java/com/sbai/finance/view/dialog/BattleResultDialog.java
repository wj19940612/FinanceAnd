package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.battle.Battle;

/**
 * Created by linrongfang on 2017/7/10.
 */

public class BattleResultDialog extends BaseDialog {

    public static final int GAME_RESULT_TIE = 0;
    public static final int GAME_RESULT_WIN = 1;
    public static final int GAME_RESULT_LOSE = 2;

    public interface OnCloseListener {
        void onClose();
    }

    public BattleResultDialog(Activity activity) {
        super(activity);
    }

    public static void get(final Activity activity, final OnCloseListener listener,
                           int winLoss, String content, int gameType) {

        setCurrentDialog(DIALOG_BATTLE_RESULT);

        View customView = LayoutInflater.from(activity).inflate(R.layout.dialog_battle_result, null);

        customView.findViewById(R.id.dialogDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(activity);
                listener.onClose();
            }
        });

        TextView winResult = (TextView) customView.findViewById(R.id.winResult);
        TextView winDescribe = (TextView) customView.findViewById(R.id.winDescribe);
        RelativeLayout background = (RelativeLayout) customView.findViewById(R.id.background);
        TextView fightAgain = (TextView) customView.findViewById(R.id.fightAgain);
        TextView goToNormalBattle = (TextView) customView.findViewById(R.id.goToNormalBattle);

        if (winLoss == GAME_RESULT_TIE) {
            winDescribe.setText(activity.getString(R.string.game_result_draw));
            background.setBackgroundResource(R.drawable.bg_future_battle_alerts_adraw);
        } else if (winLoss == GAME_RESULT_WIN) {
            winDescribe.setText(activity.getString(R.string.game_result_win));
            background.setBackgroundResource(R.drawable.bg_future_battle_alerts_win);
        } else if (winLoss == GAME_RESULT_LOSE) {
            if (gameType == Battle.GAME_TYPE_MATCH) {
                winDescribe.setText(activity.getString(R.string.game_result_lose));
            } else {
                winDescribe.setText(activity.getString(R.string.game_result_lose_match));
            }
            background.setBackgroundResource(R.drawable.bg_futuresvs_battle_lose);
        }
        winResult.setText(content);

        if (gameType == Battle.GAME_TYPE_MATCH) {
            if (winLoss == GAME_RESULT_LOSE) {
                goToNormalBattle.setVisibility(View.VISIBLE);
                fightAgain.setVisibility(View.VISIBLE);
            } else {
                goToNormalBattle.setVisibility(View.GONE);
                fightAgain.setVisibility(View.VISIBLE);
            }
        } else {
            fightAgain.setVisibility(View.GONE);
            goToNormalBattle.setVisibility(View.GONE);
        }

        StartMatchDialog.single(activity)
                .setCancelableOnBackPress(false)
                .setCancelableOnTouchOutside(false)
                .setCustomView(customView)
                .show();
    }
}
