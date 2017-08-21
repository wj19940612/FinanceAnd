package com.sbai.finance.view.training;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;

public class TrainingAchievementView2 extends LinearLayout {
    public TrainingAchievementView2(Context context) {
        super(context);
        init();
    }

    public TrainingAchievementView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private ImageView mAchieveIcon;
    private TextView mContent;
    private TextView mAchieveFlag;

    private void init() {
        setOrientation(HORIZONTAL);

        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                getResources().getDisplayMetrics());

        mAchieveIcon = new ImageView(getContext());
        mAchieveIcon.setImageResource(R.drawable.ic_small_training_goal_gray);
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, 0, 0, 0);
        addView(mAchieveIcon, params);

        mContent = new TextView(getContext());
        mContent.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.training_result_text));
        mContent.setTextSize(14);
        mContent.setGravity(Gravity.CENTER_VERTICAL);
        params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        params.weight = 1;
        params.setMargins(margin, 0, 0, 0);
        addView(mContent, params);

        mAchieveFlag = new TextView(getContext());
        mAchieveFlag.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.training_result_text_2));
        mAchieveFlag.setTextSize(12);
        mAchieveFlag.setGravity(Gravity.CENTER_VERTICAL);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, margin, 0);
        addView(mAchieveFlag, params);

        setAchieved(false);
    }

    public void setAchieved(boolean achieved) {
        if (achieved) {
            mAchieveIcon.setImageResource(R.drawable.ic_small_training_goal_gold);
            mAchieveFlag.setText(R.string.achieved);
            mAchieveFlag.setEnabled(true);
        } else {
            mAchieveIcon.setImageResource(R.drawable.ic_small_training_goal_gray);
            mAchieveFlag.setText(R.string.not_achieved);
            mAchieveFlag.setEnabled(false);
        }
    }

    public void setContent(String content) {
        mContent.setText(content);
    }

    public void setContent(int contentRes) {
        mContent.setText(contentRes);
    }
}


