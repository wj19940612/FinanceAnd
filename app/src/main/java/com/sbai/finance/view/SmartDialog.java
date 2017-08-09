package com.sbai.finance.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.utils.GlideCircleTransform;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 统一普通弹框
 */
public class SmartDialog {

    private final static double DEFAULT_SCALE = 0.8f;

    private TextView mTitle;
    private TextView mMessage;
    private TextView mNegative;
    private TextView mPosition;
    private AppCompatImageView mIcon;

    private AppCompatDialog mDialog;
    private Activity mActivity;

    private String mIconUrl;
    private int mIconResId;

    private String mTitleText;
    private int mTitleMaxLines;
    private int mTitleTextColor;

    private String mMessageText;
    private int mMessageGravity;
    private int mMessageTextSize;
    private int mMessageTextColor;
    private int mMessageTextMaxLines;
    private View mCustomView;
    private View mDialogView;

    private int mPositiveId;
    private int mNegativeId;
    private OnClickListener mPositiveListener;
    private OnClickListener mNegativeListener;
    private OnCancelListener mOnCancelListener;
    private OnDismissListener mDismissListener;
    private int mPositiveTextColor;
    private int mNegativeVisible;
    private float mWidthScale;
    private int mGravity;
    private int mWindowAnim;

    private boolean mCancelableOnTouchOutside;

    public interface OnClickListener {
        void onClick(Dialog dialog);
    }

    public interface OnCancelListener {
        void onCancel(Dialog dialog);
    }

    public interface OnDismissListener {
        void onDismiss(Dialog dialog);
    }

    private static Map<String, List<SmartDialog>> mListMap = new HashMap<>();

    public static SmartDialog single(Activity activity, String msg) {
        String key = activity.getClass().getSimpleName();
        List<SmartDialog> dialogList = mListMap.get(key);
        SmartDialog dialog;
        if (dialogList != null && dialogList.size() > 0) {
            dialog = dialogList.get(0);
        } else {
            dialog = with(activity, msg);
        }
        dialog.init();
        dialog.setMessage(msg);
        return dialog;
    }

    public static SmartDialog with(Activity activity, int resId) {
        SmartDialog dialog = new SmartDialog(activity);
        addMap(activity, dialog);
        dialog.setMessage(resId);
        return dialog;
    }

    public static SmartDialog with(Activity activity, String msg) {
        SmartDialog dialog = new SmartDialog(activity);
        addMap(activity, dialog);
        dialog.setMessage(msg);
        return dialog;
    }

    public static SmartDialog with(Activity activity) {
        SmartDialog dialog = new SmartDialog(activity);
        addMap(activity, dialog);
        return dialog;
    }

    public static SmartDialog with(Activity activity, int resId, int titleId) {
        SmartDialog dialog = new SmartDialog(activity);
        addMap(activity, dialog);
        dialog.setMessage(resId);
        dialog.setTitle(titleId);
        return dialog;
    }

    public static SmartDialog with(Activity activity, String msg, String titleTxt) {
        SmartDialog dialog = new SmartDialog(activity);
        addMap(activity, dialog);
        dialog.setMessage(msg);
        dialog.setTitle(titleTxt);
        return dialog;
    }

    private static void addMap(Activity activity, SmartDialog dialog) {
        String key = activity.getClass().getSimpleName();
        List<SmartDialog> dialogList = mListMap.get(key);
        if (dialogList == null) {
            dialogList = new LinkedList<>();
        }
        dialogList.add(dialog);
        mListMap.put(key, dialogList);
    }

    public static void dismiss(Activity activity) {
        String key = activity.getClass().getSimpleName();
        List<SmartDialog> dialogList = mListMap.get(key);
        if (dialogList != null) {
            for (SmartDialog dialog : dialogList) {
                dialog.dismiss();
            }
            mListMap.remove(key);
        }
    }

    private SmartDialog(Activity activity) {
        mActivity = activity;
        init();
    }

    private void init() {
        mIconUrl = null;
        mIconResId = -1;

        mTitleText = null;
        mTitleTextColor = Color.parseColor("#222222");
        mTitleMaxLines = 2;

        mMessageText = null;
        mMessageGravity = Gravity.CENTER_VERTICAL;
        mMessageTextSize = 14;
        mMessageTextColor = Color.parseColor("#666666");
        mMessageTextMaxLines = 3;

        mPositiveId = R.string.ok;
        mNegativeId = R.string.cancel;
        mPositiveListener = null;
        mNegativeListener = null;
        mOnCancelListener = null;
        mDismissListener = null;
        mPositiveTextColor = Color.parseColor("#CD4A47");
        mNegativeVisible = View.VISIBLE;

        mCancelableOnTouchOutside = true;
        mCustomView = null;
        mGravity = -1;
        mWindowAnim = -1;
    }

    private void scaleDialogWidth(double scale) {
        if (scale == 0) {
            scale = DEFAULT_SCALE;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDialog.getWindow().setLayout((int) (displayMetrics.widthPixels * scale),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public SmartDialog setOnDismissListener(OnDismissListener onDismissListener) {
        mDismissListener = onDismissListener;
        return this;
    }

    public SmartDialog setCustomView(View customView) {
        mCustomView = customView;
        return this;
    }

    public SmartDialog setPositive(int textId, OnClickListener listener) {
        mPositiveId = textId;
        mPositiveListener = listener;
        return this;
    }

    public SmartDialog setPositive(int textId) {
        mPositiveId = textId;
        return this;
    }

    public SmartDialog setWindowAnim(int windowAnim) {
        mWindowAnim = windowAnim;
        return this;
    }

    public SmartDialog setPositiveTextColor(int resColorId) {
        mPositiveTextColor = resColorId;
        return this;
    }

    public SmartDialog setMessageGravity(int gravity) {
        mMessageGravity = gravity;
        return this;
    }

    public SmartDialog setGravity(int gravity) {
        mGravity = gravity;
        return this;
    }

    public SmartDialog setTitleMaxLines(int titleMaxLines) {
        mTitleMaxLines = titleMaxLines;
        return this;
    }

    public SmartDialog setNegative(int textId, OnClickListener listener) {
        mNegativeId = textId;
        mNegativeListener = listener;
        return this;
    }

    public SmartDialog setNegative(int textId) {
        mNegativeId = textId;
        return this;
    }


    public SmartDialog setNegativeVisible(int visable) {
        mNegativeVisible = visable;
        return this;
    }

    public SmartDialog setCancelableOnTouchOutside(boolean cancelable) {
        mCancelableOnTouchOutside = cancelable;
        return this;
    }

    public SmartDialog setCancelListener(OnCancelListener cancelListener) {
        mOnCancelListener = cancelListener;
        return this;
    }

    private SmartDialog setMessage(int messageId) {
        mMessageText = mActivity.getText(messageId).toString();
        return this;
    }

    public SmartDialog setMessage(String message) {
        mMessageText = message;
        return this;
    }

    public SmartDialog setMessageTextColor(int messageTextColor) {
        mMessageTextColor = messageTextColor;
        return this;
    }

    public SmartDialog setMessageTextSize(int messageTextSize) {
        mMessageTextSize = messageTextSize;
        return this;
    }


    public SmartDialog setTitle(int titleId) {
        mTitleText = mActivity.getText(titleId).toString();
        return this;
    }

    public SmartDialog setIconUrl(String iconUrl) {
        mIconUrl = iconUrl;
        return this;
    }

    public SmartDialog setIconRes(int iconResId) {
        mIconResId = iconResId;
        return this;
    }

    public SmartDialog setTitle(String title) {
        mTitleText = title;
        return this;
    }

    public SmartDialog setTitleTextColor(int titleTextColor) {
        mTitleTextColor = titleTextColor;
        return this;
    }

    public SmartDialog setMessageMaxLines(int maxLines) {
        mMessageTextMaxLines = maxLines;
        return this;
    }

    public SmartDialog setWidthScale(float widthScale) {
        mWidthScale = widthScale;
        return this;
    }

    public void show() {
        if (mDialog != null) { // single dialog
            setupDialog();
        } else {
            create();
        }

        if (!mActivity.isFinishing()) {
            mDialog.show();
            scaleDialogWidth(mWidthScale);
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private void create() {
        mDialog = new AppCompatDialog(mActivity, R.style.DialogTheme_NoTitle);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (mOnCancelListener != null) {
                    mOnCancelListener.onCancel(mDialog);

                } else if (!mCancelableOnTouchOutside) {
                    // finish current page when not allow user to cancel on touch outside
                    if (mActivity != null) {
                        mActivity.finish();
                    }
                }
            }
        });
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mDismissListener != null) {
                    mDismissListener.onDismiss(mDialog);
                }
            }
        });

        mDialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_smart, null);
        mTitle = (TextView) mDialogView.findViewById(R.id.title);
        mMessage = (TextView) mDialogView.findViewById(R.id.message);
        mNegative = (TextView) mDialogView.findViewById(R.id.negative);
        mPosition = (TextView) mDialogView.findViewById(R.id.position);
        mIcon = (AppCompatImageView) mDialogView.findViewById(R.id.dialogIcon);

        setupDialog();
    }

    private void setupDialog() {
        mDialog.setCanceledOnTouchOutside(mCancelableOnTouchOutside);
        mDialog.setCancelable(mCancelableOnTouchOutside);

        if (mCustomView != null)  {
            mDialog.setContentView(mCustomView);
        } else {
            mDialog.setContentView(mDialogView);

            if (TextUtils.isEmpty(mMessageText)) {
                mMessage.setVisibility(View.GONE);
            } else {
                mMessage.setVisibility(View.VISIBLE);
            }
            mMessage.setText(mMessageText);
            mMessage.setGravity(mMessageGravity);
            mMessage.setMaxLines(mMessageTextMaxLines);
            mMessage.setTextColor(mMessageTextColor);
            mMessage.setTextSize(mMessageTextSize);

            if (TextUtils.isEmpty(mIconUrl)) {
                mIcon.setVisibility(View.VISIBLE);
                Glide.with(mActivity).load(mIconUrl)
                        .bitmapTransform(new GlideCircleTransform(mActivity))
                        .into(mIcon);
            } else {
                mIcon.setVisibility(View.GONE);
            }

            if (mIconResId != -1) {
                mIcon.setVisibility(View.VISIBLE);
                Glide.with(mActivity).load(mIconResId)
                        .bitmapTransform(new GlideCircleTransform(mActivity))
                        .into(mIcon);
            } else {
                mIcon.setVisibility(View.GONE);
            }

            mTitle.setMaxLines(mTitleMaxLines);
            mTitle.setText(mTitleText);
            mTitle.setTextColor(mTitleTextColor);
            if (TextUtils.isEmpty(mTitleText)) {
                mTitle.setVisibility(View.GONE);
            } else {
                mTitle.setVisibility(View.VISIBLE);
            }

            mPosition.setText(mPositiveId);
            mPosition.setTextColor(mPositiveTextColor);
            mPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPositiveListener != null) {
                        mPositiveListener.onClick(mDialog);
                    } else {
                        mDialog.dismiss();
                    }
                }
            });
            mNegative.setVisibility(mNegativeVisible);
            mNegative.setText(mNegativeId);
            mNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mNegativeListener != null) {
                        mNegativeListener.onClick(mDialog);
                    } else {
                        mDialog.dismiss();
                    }
                }
            });
        }

        if (mGravity != -1) {
            WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
            params.gravity = mGravity;
            mDialog.getWindow().setAttributes(params);
        }

        if (mWindowAnim != -1) {
            WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
            params.windowAnimations = mWindowAnim;
            mDialog.getWindow().setAttributes(params);
        }
    }
}
