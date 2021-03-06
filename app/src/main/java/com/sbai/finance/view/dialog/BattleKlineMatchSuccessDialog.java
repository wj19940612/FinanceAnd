package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.model.klinebattle.BattleKlineUserInfo;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

/**
 * 猜K线开始对战的倒数两秒弹窗
 */
public class BattleKlineMatchSuccessDialog extends BaseDialog {

    public interface OnDismissListener {
        void onDismiss();
    }

    public BattleKlineMatchSuccessDialog(Activity activity) {
        super(activity);
    }

    public static void get(Activity activity, @NonNull List<BattleKlineUserInfo> battleKlineUserInfos, OnDismissListener onDismissListener) {

        setCurrentDialog(DIALOG_START_GAME);

        View customView = LayoutInflater.from(activity).inflate(R.layout.dialog_battle_kline_match_success, null);

        TextView countDown = customView.findViewById(R.id.countdown);
        LinearLayout onePkArea = customView.findViewById(R.id.onePkArea);
        TextView userName = customView.findViewById(R.id.userName);
        ImageView myAvatar = customView.findViewById(R.id.myAvatar);
        TextView againstName = customView.findViewById(R.id.againstName);
        ImageView againstAvatar = customView.findViewById(R.id.againstAvatar);

        LinearLayout fourPkArea = customView.findViewById(R.id.fourPkArea);
        TextView userName1 = customView.findViewById(R.id.userName1);
        ImageView avatar1 = customView.findViewById(R.id.avatar1);
        TextView userName2 = customView.findViewById(R.id.userName2);
        ImageView avatar2 = customView.findViewById(R.id.avatar2);
        TextView userName3 = customView.findViewById(R.id.userName3);
        ImageView avatar3 = customView.findViewById(R.id.avatar3);
        List<BattleKlineUserInfo> userInfos = new ArrayList();
        for (BattleKlineUserInfo userInfo : battleKlineUserInfos) {
            if (userInfo.getUserId() != LocalUser.getUser().getUserInfo().getId()) {
                userInfos.add(userInfo);
            }
        }
        if (userInfos.size() >= 3) {
            onePkArea.setVisibility(View.GONE);
            fourPkArea.setVisibility(View.VISIBLE);
            if (!activity.isFinishing()) {
                loadAvatar(activity, userInfos.get(0).getUserPortrait(), avatar1);
                loadAvatar(activity, userInfos.get(1).getUserPortrait(), avatar2);
                loadAvatar(activity, userInfos.get(2).getUserPortrait(), avatar3);
                userName1.setText(userInfos.get(0).getUserName());
                userName2.setText(userInfos.get(1).getUserName());
                userName3.setText(userInfos.get(2).getUserName());
            }
        } else if (userInfos.size() >= 1) {
            onePkArea.setVisibility(View.VISIBLE);
            fourPkArea.setVisibility(View.GONE);
            if (!activity.isFinishing()) {
                loadAvatar(activity, LocalUser.getUser().getUserInfo().getUserPortrait(), myAvatar);
                loadAvatar(activity, userInfos.get(0).getUserPortrait(), againstAvatar);
                userName.setText(LocalUser.getUser().getUserInfo().getUserName());
                againstName.setText(userInfos.get(0).getUserName());
            }
        }


        BattleKlineMatchSuccessDialog.single(activity)
                .setCancelableOnBackPress(false)
                .setCancelableOnTouchOutside(false)
                .setCustomView(customView)
                .show();

        startCountDown(countDown, activity, onDismissListener);
    }

    private static void loadAvatar(Activity activity, String url, ImageView avatar) {
        GlideApp.with(activity)
                .load(url)
                .placeholder(R.drawable.ic_default_avatar_big)
                .circleCrop()
                .into(avatar);
    }

    private static void startCountDown(final TextView message, final Activity activity, final OnDismissListener onDismissListener) {
        CountDownTimer timer = new CountDownTimer(2000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                int count = (int) (Math.ceil(millisUntilFinished / 1000f));
                if (count == 0) {
                    return;
                }
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
