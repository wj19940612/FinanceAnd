package com.sbai.finance.view.radio;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.anchor.radio.AllRadioListActivity;
import com.sbai.finance.activity.anchor.radio.RadioStationPlayActivity;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.glide.GlideApp;

import java.util.List;

/**
 * Created by ${wangJie} on 2017/11/20.
 * 米圈推荐主页电台
 */

public class AnchorRecommendRadioLayout extends LinearLayout {

    private static final String TAG = "MissRadioLayout";
    private static final int DEFAULT_MISS_RADIO_LIST_SIZE = 4;

    public AnchorRecommendRadioLayout(Context context) {
        this(context, null);
    }

    public AnchorRecommendRadioLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnchorRecommendRadioLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setVisibility(GONE);
        setBackgroundColor(Color.WHITE);

    }


    public void setMissRadioList(List<Radio> radioList) {
        if (radioList == null) return;
        removeAllViews();
        int defaultPadding = (int) Display.dp2Px(14, getResources());
        setPadding(0, defaultPadding, 0, defaultPadding);

        createRecommendRadioView(radioList);
    }

    private void createRecommendRadioView(final List<Radio> radioList) {
        RelativeLayout relativeLayout = new RelativeLayout(getContext());

        RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageLayoutParams.setMargins((int) Display.dp2Px(14, getResources()), 0, 0, 0);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.ic_miss_radio_title);
        relativeLayout.addView(imageView, imageLayoutParams);

        createMoreRadioTextView(relativeLayout);

        addView(relativeLayout);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        if (!radioList.isEmpty()) setVisibility(VISIBLE);
        int radioListShowSize = radioList.size() > DEFAULT_MISS_RADIO_LIST_SIZE ? DEFAULT_MISS_RADIO_LIST_SIZE : radioList.size();
        for (int i = 0; i < radioListShowSize; i++) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_miss_radio, null);
            ImageView radioCover = view.findViewById(R.id.radioCover);
            TextView voiceName = view.findViewById(R.id.voiceName);
            TextView radioUpdateTime = view.findViewById(R.id.radioUpdateTime);
            TextView radioOwnerName = view.findViewById(R.id.radioOwnerName);
            TextView radioName = view.findViewById(R.id.radioName);
            ImageView needPay = view.findViewById(R.id.needPay);


            final Radio radio = radioList.get(i);
            GlideApp.with(getContext())
                    .load(radio.getRadioCover())
                    .into(radioCover);
            voiceName.setText(radio.getAudioName());
            radioUpdateTime.setText(getContext().getString(R.string.time_update, DateUtil.formatDefaultStyleTime(radio.getReviewTime())));
            radioName.setText(radio.getRadioName());
            radioOwnerName.setText(radio.getRadioHostName());
            addView(view, layoutParams);

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Launcher.with(getContext(), RadioStationPlayActivity.class)
                            .putExtra(ExtraKeys.RADIO, radio)
                            .execute();
                }
            });

            if (radio.getRadioPaid() == Radio.PRODUCT_RATE_CHARGE) {
                needPay.setVisibility(View.VISIBLE);
                boolean alreadyPay = radio.getUserPayment() == Radio.PRODUCT_RECHARGE_STATUS_ALREADY_PAY;
                needPay.setSelected(alreadyPay);
            } else {
                needPay.setVisibility(View.INVISIBLE);
            }
        }

        relativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getContext(), AllRadioListActivity.class).execute();
            }
        });
    }

    private void createMoreRadioTextView(RelativeLayout relativeLayout) {
        TextView moreRadioTextView = new TextView(getContext());
        moreRadioTextView.setPadding(5, 0, 0, 0);
        moreRadioTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        moreRadioTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.unluckyText));
        moreRadioTextView.setText(R.string.more);
        moreRadioTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right_tint, 0);
        moreRadioTextView.setCompoundDrawablePadding((int) Display.dp2Px(4, getResources()));

        RelativeLayout.LayoutParams rlLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rlLayoutParams.setMargins(0, 0, (int) Display.dp2Px(14, getResources()), 0);
        rlLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rlLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

        relativeLayout.addView(moreRadioTextView, rlLayoutParams);
    }
}
