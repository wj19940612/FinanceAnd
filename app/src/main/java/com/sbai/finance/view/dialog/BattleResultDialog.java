package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;

/**
 * Created by linrongfang on 2017/7/10.
 */

public class BattleResultDialog extends BaseDialog {

    public static final int GAME_RESULT_DRAW = 0;
    public static final int GAME_RESULT_WIN = 1;
    public static final int GAME_RESULT_LOSE = 2;

    public interface OnCloseListener {
        void onClose();
    }

    public BattleResultDialog(Activity activity) {
        super(activity);
    }

    public static void get(final Activity activity, final OnCloseListener listener, int result, String content) {

        setCurrentDialog(DIALOG_BATTLE_RESULT);

        View customView = LayoutInflater.from(activity).inflate(R.layout.dialog_fragment_win_result, null);

        customView.findViewById(R.id.dialogDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(activity);
                listener.onClose();
            }
        });

        TextView winResult = (TextView) customView.findViewById(R.id.winResult);
        TextView winDescribe = (TextView) customView.findViewById(R.id.winDescribe);
        RelativeLayout layout = (RelativeLayout) customView.findViewById(R.id.background);

        if (result == GAME_RESULT_DRAW) {
            winDescribe.setText(activity.getString(R.string.game_result_draw));
            layout.setBackgroundResource(R.drawable.bg_future_battle_alerts_adraw);
        } else if (result == GAME_RESULT_WIN) {
            winDescribe.setText(activity.getString(R.string.game_result_win));
            layout.setBackgroundResource(R.drawable.bg_future_battle_alerts_win);
        } else if (result == GAME_RESULT_LOSE) {
            winDescribe.setText(activity.getString(R.string.game_result_lose));
            layout.setBackgroundResource(R.drawable.bg_futuresvs_battle_lose);
        }
        winResult.setText(content);

        StartMatchDialog.single(activity)
                .setCancelableOnBackPress(false)
                .setCancelableOnTouchOutside(false)
                .setCustomView(customView)
                .show();
    }
}
