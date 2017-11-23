package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.glide.GlideApp;

/**
 * Modified by john on 31/10/2017
 * <p>
 * 开始对战的倒数两秒弹窗
 */
public class StartBattleDialog extends BaseDialog {

    public interface OnDismissListener {
        void onDismiss();
    }

    public StartBattleDialog(Activity activity) {
        super(activity);
    }

    public static void get(Activity activity,
                           String ownerAvatarUrl, String ownerNameText,
                           String challengerAvatarUrl, String challengerNameText,
                           OnDismissListener onDismissListener) {

        setCurrentDialog(DIALOG_START_GAME);

        View customView = LayoutInflater.from(activity).inflate(R.layout.dialog_start_battle, null);

        TextView challengerName = (TextView) customView.findViewById(R.id.challengerName);
        ImageView challengerAvatar = (ImageView) customView.findViewById(R.id.challengerAvatar);
        TextView ownerName = (TextView) customView.findViewById(R.id.ownerName);
        ImageView ownerAvatar = (ImageView) customView.findViewById(R.id.ownerAvatar);

        if (!activity.isFinishing()) {
            GlideApp.with(activity)
                    .load(ownerAvatarUrl)
                    .placeholder(R.drawable.ic_default_avatar_big)
                    .circleCrop()
                    .into(ownerAvatar);
            GlideApp.with(activity)
                    .load(challengerAvatarUrl)
                    .placeholder(R.drawable.ic_default_avatar_big)
                    .circleCrop()
                    .into(challengerAvatar);
        }

        ownerName.setText(ownerNameText);
        challengerName.setText(challengerNameText);

        TextView message = (TextView) customView.findViewById(R.id.message);

        StartBattleDialog.single(activity)
                .setCancelableOnBackPress(false)
                .setCancelableOnTouchOutside(false)
                .setCustomView(customView)
                .show();

        startCountDown(message, activity, onDismissListener);
    }

    private static void startCountDown(final TextView message, final Activity activity, final OnDismissListener onDismissListener) {
        CountDownTimer timer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int count = (int) (millisUntilFinished / 1000);
                if (count == 0) return;
                message.setText(activity.getString(R.string.match_success_x_seconds_start_battle, count));
            }

            @Override
            public void onFinish() {
                dismiss(activity);
                if (onDismissListener != null) {
                    onDismissListener.onDismiss();
                }
            }
        };
        timer.start();
    }


}
