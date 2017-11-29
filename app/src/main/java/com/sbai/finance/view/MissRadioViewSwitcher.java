package com.sbai.finance.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.sbai.finance.R;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.model.miss.MissSwitcherModel;
import com.sbai.finance.net.Client;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.TimerHandler;

import java.util.List;

/**
 * Created by ${wangJie} on 2017/11/21.
 * 姐说界面
 */

public class MissRadioViewSwitcher extends LinearLayout implements TimerHandler.TimerCallback {

    private TextSwitcher mTextSwitcher;
    private TimerHandler mTimerHandler;
    private static final int SWITCHER_CHANGE_TIME = 3000;
    private List<MissSwitcherModel> mMissSwitcherModelList;
    private int mCount;

    public MissRadioViewSwitcher(Context context) {
        this(context, null);
    }

    public MissRadioViewSwitcher(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MissRadioViewSwitcher(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        setBackgroundColor(Color.WHITE);
        int defaultPadding = (int) Display.dp2Px(14, getResources());
        setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);

        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.ic_miss_topic_switcher);
        addView(imageView);

        mTextSwitcher = new TextSwitcher(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(defaultPadding, 0, defaultPadding, 0);
        layoutParams.weight = 1;
        addView(mTextSwitcher, layoutParams);
        mTextSwitcher.setInAnimation(getContext(), R.anim.slide_in_from_bottom);
        mTextSwitcher.setOutAnimation(getContext(), R.anim.slide_out_to_top);
        mTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getContext());
                textView.setTextSize(15);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.blackPrimary));
                textView.setMaxLines(1);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                return textView;
            }
        });

        mTextSwitcher.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openRadioDetailsPage();
            }
        });

        ImageView arrowImageView = new ImageView(getContext());
        arrowImageView.setImageResource(R.drawable.ic_arrow_right_tint);
        arrowImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openRadioDetailsPage();
            }
        });
        addView(arrowImageView);

        mTimerHandler = new TimerHandler(this);
    }

    public void setSwitcherData(List<MissSwitcherModel> missSwitcherModelList) {
        if (missSwitcherModelList == null || missSwitcherModelList.isEmpty()) {
            setVisibility(GONE);
        } else {
            mMissSwitcherModelList = missSwitcherModelList;
            setVisibility(VISIBLE);
            mTimerHandler.removeCallbacksAndMessages(null);
            mTimerHandler.sendEmptyMessageDelayed(SWITCHER_CHANGE_TIME, 0);
        }
    }

    private void openRadioDetailsPage() {
        if (mMissSwitcherModelList != null && !mMissSwitcherModelList.isEmpty()) {
            int position = mCount % mMissSwitcherModelList.size();
            MissSwitcherModel missSwitcherModel = mMissSwitcherModelList.get(position);
            if (missSwitcherModel != null) {
                String format = String.format(Client.MISS_TOP_DETAILS_H5_URL, missSwitcherModel.getId());
                Launcher.with(getContext(), WebActivity.class)
                        .putExtra(WebActivity.EX_URL, String.format(Client.MISS_TOP_DETAILS_H5_URL, String.valueOf(missSwitcherModel.getId())))
                        .execute();
            }
        }
    }

    @Override
    public void onTimeUp(int count) {
        mCount = count;
        if (mMissSwitcherModelList != null && !mMissSwitcherModelList.isEmpty()) {
            int position = count % mMissSwitcherModelList.size();
            mTextSwitcher.setText(mMissSwitcherModelList.get(position).getTopicTitle());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimerHandler.removeCallbacksAndMessages(null);
    }
}
