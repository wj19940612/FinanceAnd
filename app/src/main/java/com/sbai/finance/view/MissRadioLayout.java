package com.sbai.finance.view;

import android.content.Context;
import android.graphics.Color;
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
import com.sbai.finance.utils.ToastUtil;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${wangJie} on 2017/11/20.
 * 姐说主页电台
 */

public class MissRadioLayout extends LinearLayout {

    private static final int DEFAULT_MISS_RADIO_LIST_SIZE = 4;
    private OnMissRadioPlayListener mOnMissRadioPlayListener;
    private ArrayList<PlayStatus> mPlayStateList;

    public interface OnMissRadioPlayListener {
        void onMissRadioPlay(Radio radio, boolean isPlaying);
    }

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
        setBackgroundColor(Color.WHITE);
        mPlayStateList = new ArrayList<>();
    }

    public void setOnMissRadioPlayListener(OnMissRadioPlayListener onMissRadioPlayListener) {
        mOnMissRadioPlayListener = onMissRadioPlayListener;
    }

    public void setMissRadioList(List<Radio> radioList) {
        if (radioList == null) return;
        removeAllViews();
        int defaultPadding = (int) Display.dp2Px(14, getResources());
        setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(0, (int) Display.dp2Px(3, getResources()), 0, (int) Display.dp2Px(12, getResources()));
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.ic_miss_radio_title);
        addView(imageView, layoutParams);

        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, (int) Display.dp2Px(9, getResources()), 0, 0);
        mPlayStateList.clear();
        if (!radioList.isEmpty()) setVisibility(VISIBLE);
        int radioListShowSize = radioList.size() > DEFAULT_MISS_RADIO_LIST_SIZE ? DEFAULT_MISS_RADIO_LIST_SIZE : radioList.size();
        for (int i = 0; i < radioListShowSize; i++) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_miss_radio, null);
            ImageView radioCover = view.findViewById(R.id.radioCover);
            TextView radioName = view.findViewById(R.id.radioName);
            TextView radioUpdateTime = view.findViewById(R.id.radioUpdateTime);
            TextView radioOwnerName = view.findViewById(R.id.radioOwnerName);
            TextView radioSpecialName = view.findViewById(R.id.radioSpecialName);
            final ImageView startPlay = view.findViewById(R.id.startPlay);
            TextView radioLength = view.findViewById(R.id.radioLength);

            Radio radio = radioList.get(i);
            GlideApp.with(getContext())
                    .load(radio.getRadioCover())
                    .into(radioCover);
            radioName.setText(radio.getRadioTitle());
            radioUpdateTime.setText(DateUtil.formatDefaultStyleTime(radio.getTime()));
            radioSpecialName.setText(radio.getRadioName());
            radioOwnerName.setText(radio.getRadioOwner());
            radioLength.setText(DateUtil.format(radio.getRadioLength(), DateUtil.FORMAT_HOUR_MINUTE));
            startPlay.setImageResource(R.drawable.bg_voice_play);
            addView(view, layoutParams);

            PlayStatus playStatus = new PlayStatus();
            playStatus.setImageView(startPlay);
            playStatus.setRadio(radio);
            mPlayStateList.add(playStatus);
        }

        for (int i = 0; i < mPlayStateList.size(); i++) {
            final PlayStatus playStatus = mPlayStateList.get(i);
            final ImageView playImageView = playStatus.getImageView();
            playImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnMissRadioPlayListener != null) {
                        mOnMissRadioPlayListener.onMissRadioPlay(playStatus.getRadio(), playImageView.isSelected());
                    }
                    playImageView.setSelected(!playImageView.isSelected());
                    unChangePlay(playImageView);
                }
            });
        }
    }

    private void unChangePlay(ImageView playImageView) {
        if (mPlayStateList != null && !mPlayStateList.isEmpty()) {
            for (PlayStatus result : mPlayStateList) {
                if (playImageView != result.getImageView()) {
                    result.getImageView().setSelected(false);
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPlayStateList.clear();
    }

    private static class PlayStatus {
        private ImageView mImageView;
        private Radio mRadio;

        public ImageView getImageView() {
            return mImageView;
        }

        public void setImageView(ImageView imageView) {
            mImageView = imageView;
        }

        public Radio getRadio() {
            return mRadio;
        }

        public void setRadio(Radio radio) {
            mRadio = radio;
        }
    }

}
