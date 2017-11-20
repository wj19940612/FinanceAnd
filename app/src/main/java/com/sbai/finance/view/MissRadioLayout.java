package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.glide.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ${wangJie} on 2017/11/20.
 * 姐说主页电台
 */

public class MissRadioLayout extends LinearLayout {

    private static final int DEFAULT_MISS_RADIO_LIST_SIZE = 4;
    @BindView(R.id.view)
    View mView;
    @BindView(R.id.radioCover)
    ImageView mRadioCover;
    @BindView(R.id.radioName)
    TextView mRadioName;
    @BindView(R.id.radioUpdateTime)
    TextView mRadioUpdateTime;
    @BindView(R.id.radioOwnerName)
    TextView mRadioOwnerName;
    @BindView(R.id.radioSpecialName)
    TextView mRadioSpecialName;
    @BindView(R.id.startPlay)
    ImageView mStartPlay;
    @BindView(R.id.radioLength)
    TextView mRadioLength;

//    private View mView;
//    ImageView mRadioCover;
//    TextView mRadioName;
//    TextView mRadioUpdateTime;
//    TextView mRadioOwnerName;
//    TextView mRadioSpecialName;
//    ImageView mStartPlay;
//    TextView mRadioLength;

    public MissRadioLayout(Context context) {
        this(context, null);
    }

    public MissRadioLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MissRadioLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();

    }

    private void init() {
        setOrientation(VERTICAL);
        setVisibility(GONE);
        int defaultPadding = (int) Display.dp2Px(14, getResources());
        setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, (int) Display.dp2Px(9, getResources()), 0, (int) Display.dp2Px(12, getResources()));
        ImageView imageView = new ImageView(getContext());
        // TODO: 2017/11/20 后期切图更换
        imageView.setImageResource(R.drawable.ic_miss_radio_title);
        addView(imageView, layoutParams);


       View view = LayoutInflater.from(getContext()).inflate(R.layout.view_miss_radio, this, true);
        ButterKnife.bind(this, view);
//        mRadioCover = mView.findViewById(R.id.radioCover);
//        mRadioName = mView.findViewById(R.id.radioName);
//        mRadioUpdateTime = mView.findViewById(R.id.radioUpdateTime);
//        mRadioOwnerName = mView.findViewById(R.id.radioOwnerName);
//        mRadioSpecialName = mView.findViewById(R.id.radioSpecialName);
//        mStartPlay = mView.findViewById(R.id.startPlay);
//        mRadioLength = mView.findViewById(R.id.radioLength);
    }

    public void setMissRadioList(List<Radio> radioList) {
        if (radioList == null) return;
        int radioListShowSize = radioList.size() > DEFAULT_MISS_RADIO_LIST_SIZE ? DEFAULT_MISS_RADIO_LIST_SIZE : radioList.size();
        for (int i = 0; i < radioListShowSize; i++) {
            Radio radio = radioList.get(i);
            GlideApp.with(getContext())
                    .load(radio.getRadioCover())
                    .into(mRadioCover);
            mRadioName.setText(radio.getRadioTitle());
            mRadioUpdateTime.setText(DateUtil.format(radio.getTime()));
            addView(mView);
        }
    }

}
