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
 * Created by linrongfang on 2017/7/10.
 */

public class StartGameDialog extends BaseDialog {

    public StartGameDialog(Activity activity) {
        super(activity);
    }

    public static void get(Activity activity, String url) {

        setCurrentDialog(DIALOG_START_GAME);

        View customView = LayoutInflater.from(activity).inflate(R.layout.dialog_fragment_start_match, null);

        TextView title = (TextView) customView.findViewById(R.id.title);
        title.setText(activity.getString(R.string.title_quick_join_battle));

        ImageView mMatchLoading = (ImageView) customView.findViewById(R.id.matchLoading);
        mMatchLoading.setVisibility(View.GONE);

        ImageView matchHead = (ImageView) customView.findViewById(R.id.matchHead);

        GlideApp.with(activity)
                .load(url)
                .placeholder(R.drawable.ic_default_avatar_big)
                .circleCrop()
                .into(matchHead);
        TextView cancel = (TextView) customView.findViewById(R.id.cancel);
        cancel.setText("");
        cancel.setBackgroundResource(android.R.color.white);

        TextView message = (TextView) customView.findViewById(R.id.message);

        StartMatchDialog.single(activity)
                .setCancelableOnBackPress(false)
                .setCancelableOnTouchOutside(false)
                .setCustomView(customView)
                .show();

        startCountDown(message, activity);
    }

    private static void startCountDown(final TextView message, final Activity activity) {
        CountDownTimer timer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int count = (int) (millisUntilFinished / 1000 - 1);
                message.setText(activity.getString(R.string.desc_match_success, count));
            }

            @Override
            public void onFinish() {
                dismiss(activity);
            }
        };
        timer.start();
    }


}
