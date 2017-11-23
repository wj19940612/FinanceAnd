package com.sbai.finance.view.radio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.utils.DateUtil;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/11/22.
 * 电台详情页面播放信息
 */

public class RadioInfoPlayLayout extends LinearLayout {

    @BindView(R.id.voiceCover)
    ImageView mVoiceCover;
    @BindView(R.id.progressLength)
    TextView mProgressLength;
    @BindView(R.id.radioSeekBar)
    AppCompatSeekBar mRadioSeekBar;
    @BindView(R.id.radioTotalLength)
    TextView mRadioTotalLength;
    @BindView(R.id.seekBarLL)
    LinearLayout mSeekBarLL;
    @BindView(R.id.play)
    ImageView mPlay;
    @BindView(R.id.listenNumber)
    TextView mListenNumber;
    private Unbinder mBind;
    private Radio mRadio;

    public RadioInfoPlayLayout(Context context) {
        this(context, null);
    }

    public RadioInfoPlayLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioInfoPlayLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_radio_play, this, true);
        mBind = ButterKnife.bind(this, view);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBind.unbind();
        mOnSeekBarChangeListener = null;
    }

    public void setRadio(Radio radio) {
        mRadio = radio;
        GlideApp.with(getContext())
                .load(radio.getAudioCover())
                .circleCrop()
                .into(mVoiceCover);
        mRadioTotalLength.setText(DateUtil.format(radio.getAudioTime(), DateUtil.FORMAT_HOUR_MINUTE));
        mListenNumber.setText(String.valueOf(radio.getViewNumber()));
        mRadioSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

}
