package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.view.SmartDialog;

/**
 * Created by Administrator on 2018\1\3 0003.
 */

public class BuyRadioResultDialog extends BaseDialog {
    public BuyRadioResultDialog(Activity activity) {
        super(activity);
    }

    public interface OnCallback {
        public void onDialogClick();
    }

    public static void get(final Activity activity, final OnCallback onCallback, int integration) {
        View customView = LayoutInflater.from(activity).inflate(R.layout.dialog_pay_radio_result, null);
        TextView integrationView = customView.findViewById(R.id.integrationAdd);
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
