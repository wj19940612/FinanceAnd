package com.sbai.finance.utils;

import android.widget.Toast;

import com.sbai.finance.App;

public class ToastUtil {

//    public static void show(int messageId, int yOffsetResId) {
//        Toast toast = Toast.makeText(App.getAppContext(), messageId, Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER, 0,
//                App.getAppContext().getResources().getDimensionPixelSize(yOffsetResId));
//        toast.show();
//    }

    private static Toast sToast;
    private static long sFirstTime;
    private static long sSecondTime;
    private static String sMessage;

    public static void show(int messageId) {
        show(App.getAppContext().getString(messageId));
    }

    public static void show(String message) {
        if (sToast == null) {
            sToast = Toast.makeText(App.getAppContext(), message, Toast.LENGTH_SHORT);
            sToast.show();
            sFirstTime = System.currentTimeMillis();
        } else {
            sSecondTime = System.currentTimeMillis();
            if (message.equals(sMessage)) {
                if (sSecondTime - sFirstTime > Toast.LENGTH_SHORT) {
                    sToast.show();
                }
            } else {
                sMessage = message;
                sToast.setText(message);
                sToast.show();
            }
        }
        sFirstTime = sSecondTime;
    }
}