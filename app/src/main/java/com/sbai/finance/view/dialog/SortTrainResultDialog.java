package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.SmartDialog;

import butterknife.BindView;

/**
 * Created by ${wangJie} on 2017/8/19.
 */

public class SortTrainResultDialog {

    @BindView(R.id.titleIcon)
    ImageView mTitleIcon;
    @BindView(R.id.hintTitle)
    TextView mHintTitle;
    @BindView(R.id.hintContent)
    TextView mHintContent;
    @BindView(R.id.completeTraining)
    TextView mCompleteTraining;

    private boolean mResult;

    private Activity mActivity;
    private SmartDialog mSmartDialog;
    private View mView;

    private OnDialogDismissListener mOnDialogDismissListener;

    public interface OnDialogDismissListener {
        void onDismiss();
    }


    public SortTrainResultDialog setOnDialogDismissListener(OnDialogDismissListener completeBtnClickListener) {
        mOnDialogDismissListener = completeBtnClickListener;
        return this;
    }

    public static SortTrainResultDialog with(Activity activity) {
        SortTrainResultDialog sortTrainResultDialog = new SortTrainResultDialog();
        sortTrainResultDialog.mActivity = activity;
        sortTrainResultDialog.mSmartDialog = SmartDialog.single(activity);
        sortTrainResultDialog.mView = LayoutInflater.from(activity).inflate(R.layout.dialog_sort_training_result, null);
        sortTrainResultDialog.mSmartDialog.setCustomView(sortTrainResultDialog.mView);
        sortTrainResultDialog.init();
        return sortTrainResultDialog;
    }

    private void init() {
        mTitleIcon = (ImageView) mView.findViewById(R.id.titleIcon);
        mHintTitle = (TextView) mView.findViewById(R.id.hintTitle);
        mHintContent = (TextView) mView.findViewById(R.id.hintContent);
        mCompleteTraining = (TextView) mView.findViewById(R.id.completeTraining);
        mCompleteTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmartDialog.dismiss();

            }
        });
        mSmartDialog.setOnDismissListener(new SmartDialog.OnDismissListener() {
            @Override
            public void onDismiss(Dialog dialog) {
                if (mOnDialogDismissListener != null) {
                    mOnDialogDismissListener.onDismiss();
                }
            }
        });
    }

    public void setResult(boolean result) {
        this.mResult = result;
        if (mResult) {
            setTitleIcon(R.drawable.ic_training_success);
            setHintTitle(R.string.congratulation);
            setHintContent(R.string.success_create_report);
        } else {
            setTitleIcon(R.drawable.ic_training_fail);
            setHintTitle(R.string.create_fail);
            setHintContent(R.string.your_company_is_close);
        }
        int[] colors = {Color.parseColor("#FF8930"), Color.parseColor("#F7D34C")};
        setCompleteTrainingBg(createDrawable(colors, mActivity));
        mSmartDialog.show();
    }

    public void setTitleIcon(Drawable drawable) {
        mTitleIcon.setImageDrawable(drawable);
    }

    public void setTitleIcon(int drawableResId) {
        mTitleIcon.setImageResource(drawableResId);
    }

    public void setHintTitle(CharSequence hintTitle) {
        mHintTitle.setText(hintTitle);
    }

    public void setHintTitle(int resId) {
        mHintTitle.setText(resId);
    }

    public void setHintContent(CharSequence hintContent) {
        mHintContent.setText(hintContent);
    }

    public void setHintContent(int resId) {
        mHintContent.setText(resId);
    }

    public void setCompleteTraining(CharSequence completeTraining) {
        mCompleteTraining.setText(completeTraining);
    }

    public void setCompleteTraining(int resId) {
        mCompleteTraining.setText(resId);
    }

    public void setCompleteTrainingBg(int resId) {
        mCompleteTraining.setBackgroundResource(resId);
    }

    public void setCompleteTrainingBg(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mCompleteTraining.setBackground(drawable);
        } else {
            mCompleteTraining.setBackgroundDrawable(drawable);
        }
    }


    private Drawable createDrawable(int[] colors, Context context) {
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        gradient.setCornerRadius(Display.dp2Px(200, context.getResources()));
        return gradient;
    }

}
