package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.view.SmartDialog;

/**
 * Created by Administrator on 2018\1\4 0004.
 */

public class ReceiveIntegrationDialog {

    private static Animation getFlashAnim(){
        Animation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2500);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        animation.setFillAfter(true);//设置为true，动画转化结束后被应用
        return animation;
    }
    public interface OnCallback {
        public void onDialogClick();
    }
    public static void get(final Activity activity, final OnCallback onCallback, int integration) {
        View customView = LayoutInflater.from(activity).inflate(R.layout.dialog_receive_integration_result, null);
        ImageView flashIcon = customView.findViewById(R.id.successFlash);
        flashIcon.startAnimation(getFlashAnim());
        TextView integrationView = customView.findViewById(R.id.integration);
        integrationView.setText(String.format(activity.getString(R.string.get_integration), integration));
        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmartDialog.dismiss(activity);
                if (onCallback != null) {
                    onCallback.onDialogClick();
                }
            }
        });

        SmartDialog.single(activity)
                .setCancelableOnTouchOutside(true)
                .setCustomView(customView).setStyle(R.style.DialogTheme_NoTitleAndBottomEnter)
                .show();
    }
}
