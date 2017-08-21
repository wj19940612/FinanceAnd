package com.sbai.finance.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.Display;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/8/19.
 */

public class SortTrainResultDialog extends AppCompatDialog {

    private final Unbinder mBind;

    Context mContext;
    @BindView(R.id.titleIcon)
    ImageView mTitleIcon;
    @BindView(R.id.hintTitle)
    TextView mHintTitle;
    @BindView(R.id.hintContent)
    TextView mHintContent;
    @BindView(R.id.completeTraining)
    TextView mCompleteTraining;

    private boolean mResult;

    private OnCompleteBtnClickListener mOnCompleteBtnClickListener;

    public interface OnCompleteBtnClickListener {
        void onClick();
    }

    public void setOnCompleteBtnClickListener(OnCompleteBtnClickListener completeBtnClickListener) {
        mOnCompleteBtnClickListener = completeBtnClickListener;
    }

    public SortTrainResultDialog(Context context) {
        this(context, R.style.custom_dialog);
    }

    public SortTrainResultDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f;
        lp.dimAmount=0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        View view = getLayoutInflater().inflate(R.layout.dialog_fragment_sort_training_result, null);
        mBind = ButterKnife.bind(this, view);
        setContentView(view);

        mCompleteTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCompleteBtnClickListener != null) {
                    mOnCompleteBtnClickListener.onClick();
                }
                SortTrainResultDialog.this.dismiss();
            }
        });
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
        mBind.unbind();
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
        setCompleteTrainingBg(createDrawable(colors, mContext));
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
        mCompleteTraining.setBackground(drawable);
    }


    private Drawable createDrawable(int[] colors, Context context) {
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        gradient.setCornerRadius(Display.dp2Px(200, context.getResources()));
        return gradient;
    }

}
