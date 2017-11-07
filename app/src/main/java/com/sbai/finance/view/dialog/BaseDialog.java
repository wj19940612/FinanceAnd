package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by linrongfang on 2017/7/10.
 */

public class BaseDialog {

    public static final int DIALOG_DISMISS = 111;
    public static final int DIALOG_START_GAME = 222;
    public static final int DIALOG_START_MATCH = 333;
    public static final int DIALOG_BATTLE_RESULT = 444;

    private Activity mActivity;

    private View mCustomView;

    private AppCompatDialog mDialog;

    private DialogInterface.OnDismissListener mOnDismissListener;

    private boolean mCancelableOnTouchOutside;
    private boolean mCancelableOnBackPress;

    private static int sCurrentDialog;

    private static Map<String, List<BaseDialog>> mListMap = new HashMap<>();

    public static void setCurrentDialog(int dialogType) {
        sCurrentDialog = dialogType;
    }

    public static int getCurrentDialog() {
        return sCurrentDialog;
    }

    public static BaseDialog single(Activity activity) {
        String key = activity.getClass().getSimpleName();
        List<BaseDialog> dialogList = mListMap.get(key);
        BaseDialog dialog;
        if (dialogList != null && dialogList.size() > 0) {
            dialog = dialogList.get(0);
        } else {
            dialog = with(activity);
        }
        dialog.init();
        return dialog;
    }

    public static BaseDialog with(Activity activity) {
        BaseDialog dialog = new BaseDialog(activity);
        addMap(activity, dialog);
        return dialog;
    }

    public BaseDialog(Activity activity) {
        mActivity = activity;
        init();
    }

    private static void addMap(Activity activity, BaseDialog dialog) {
        String key = activity.getClass().getSimpleName();
        List<BaseDialog> dialogList = mListMap.get(key);
        if (dialogList == null) {
            dialogList = new LinkedList<>();
        }
        dialogList.add(dialog);
        mListMap.put(key, dialogList);
    }

    private void init() {
        mCancelableOnTouchOutside = true;
        mCancelableOnBackPress = true;
        mCustomView = null;
    }

    public BaseDialog setCustomView(View customView) {
        mCustomView = customView;
        return this;
    }

    public BaseDialog setCancelableOnTouchOutside(boolean cancelable) {
        mCancelableOnTouchOutside = cancelable;
        return this;
    }

    public BaseDialog setCancelableOnBackPress(boolean cancelableOnBackPress) {
        this.mCancelableOnBackPress = cancelableOnBackPress;
        return this;
    }

    public BaseDialog setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.mOnDismissListener = dismissListener;
        return this;
    }

    public void show() {
        if (mDialog != null) {
            setupDialog();
        } else {
            create();
        }

        if (mActivity != null && !mActivity.isFinishing()) {
            scaleDialogWidth(0.8);
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing() && !mActivity.isFinishing()) {
            mDialog.dismiss();
        }
        sCurrentDialog = DIALOG_DISMISS;
    }

    public static void dismiss(Activity activity) {
        String key = activity.getClass().getSimpleName();
        List<BaseDialog> dialogList = mListMap.get(key);
        if (dialogList != null) {
            for (BaseDialog dialog : dialogList) {
                dialog.dismiss();
            }
            mListMap.remove(key);
        }
        sCurrentDialog = DIALOG_DISMISS;
    }

    private void scaleDialogWidth(double scale) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDialog.getWindow().setLayout((int) (displayMetrics.widthPixels * scale),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    private void create() {
        mDialog = new AppCompatDialog(mActivity, R.style.DialogTheme_NoTitle);
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss(null);
                }
            }
        });
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (!mCancelableOnTouchOutside) {
                    // finish current page when not allow user to cancel on touch outside
                    if (mActivity != null) {
                        mActivity.finish();
                    }
                }
            }
        });

        setupDialog();
    }

    private void setupDialog() {
        mDialog.setCanceledOnTouchOutside(mCancelableOnTouchOutside);

        if (!mCancelableOnBackPress) {
            mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            });
        }

        if (mCustomView != null) {
            mDialog.setContentView(mCustomView);
        }
    }


}
