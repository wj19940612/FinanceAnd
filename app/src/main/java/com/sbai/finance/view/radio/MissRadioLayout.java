package com.sbai.finance.view.radio;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${wangJie} on 2017/11/20.
 * 姐说主页电台
 */

public class MissRadioLayout extends LinearLayout {

    private static final String TAG = "MissRadioLayout";
    private static final int DEFAULT_MISS_RADIO_LIST_SIZE = 4;
    private OnMissRadioPlayListener mOnMissRadioPlayListener;
    private ArrayList<PlayStatus> mPlayStateList;
    private ImageView mPlayImageView;
    private Radio mRadio;

    public interface OnMissRadioPlayListener {
        void onMissRadioPlay(Radio radio);

        void onMissRadioClick(Radio radio);
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

        Rect rect = new Rect();

        getGlobalVisibleRect(rect);


//        Point p=new Point();
//        getWindowManager().getDefaultDisplay().getSize(p);
//        screenWidth=p.x;
//        screenHeight=p.y;
//
//        Rect  rect=new Rect(0,0,screenWidth,screenHeight );
//
//        ImageView imageView = imageViewList.get(i);
//
//        int[] location = new int[2];
//        imageView.getLocationInWindow(location);
//        System.out.println(Arrays.toString(location));

        // Rect ivRect=new Rect(imageView.getLeft(),imageView.getTop(),imageView.getRight(),imageView.getBottom());


//        if (imageView.getLocalVisibleRect(rect)) {/*rect.contains(ivRect)*/
//            System.out.println("---------控件在屏幕可见区域-----显现-----------------");
//        } else {
//            imageView.setImageResource(R.drawable.p);
//            System.out.println("---------控件已不在屏幕可见区域（已滑出屏幕）-----隐去-----------------");
//        }
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
            TextView voiceName = view.findViewById(R.id.voiceName);
            TextView radioUpdateTime = view.findViewById(R.id.radioUpdateTime);
            TextView radioOwnerName = view.findViewById(R.id.radioOwnerName);
            TextView radioName = view.findViewById(R.id.radioName);
            final ImageView startPlay = view.findViewById(R.id.startPlay);
            TextView radioLength = view.findViewById(R.id.radioLength);

            Radio radio = radioList.get(i);
            GlideApp.with(getContext())
                    .load(radio.getAudioCover())
                    .into(radioCover);
            voiceName.setText(radio.getAudioName());
            radioUpdateTime.setText(DateUtil.formatDefaultStyleTime(radio.getModifyTime()));
            radioName.setText(radio.getRadioName());
            radioOwnerName.setText(radio.getRadioHostName());
            radioLength.setText(DateUtil.format((radio.getAudioTime() * 1000), DateUtil.FORMAT_HOUR_MINUTE_SECOND));
            radioLength.setText(DateUtil.formatMediaLength(radio.getAudioTime()));
            startPlay.setImageResource(R.drawable.bg_voice_play);
            addView(view, layoutParams);

            PlayStatus playStatus = new PlayStatus();
            playStatus.setImageView(startPlay);
            playStatus.setRadio(radio);
            playStatus.setView(view);
            mPlayStateList.add(playStatus);
        }

        for (int i = 0; i < mPlayStateList.size(); i++) {
            final PlayStatus playStatus = mPlayStateList.get(i);
            final ImageView playImageView = playStatus.getImageView();
            View view = playStatus.getView();


            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnMissRadioPlayListener != null) {
                        mOnMissRadioPlayListener.onMissRadioClick(playStatus.getRadio());
                    }
                }
            });
            playImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPlayImageView = playImageView;
                    if (mOnMissRadioPlayListener != null) {
                        mOnMissRadioPlayListener.onMissRadioPlay(playStatus.getRadio());
                    }
                    playImageView.setSelected(!playImageView.isSelected());
                    unChangePlay(playImageView);
                }
            });
        }
    }

    public void unChangePlay(ImageView playImageView) {
        if (mPlayStateList != null && !mPlayStateList.isEmpty()) {
            for (PlayStatus result : mPlayStateList) {
                if (result.getImageView() != playImageView) {
                    result.getImageView().setSelected(false);
                }
            }
        }
    }
//
//    public void setStopPlay(Radio radio) {
//        if (mPlayImageView != null) {
//            mPlayImageView.setSelected(false);
//        }
//    }
//
//    public void setPausePlay(Radio radio) {
//        if (mPlayImageView != null) {
//            mPlayImageView.setSelected(false);
//        }
//    }
//
//    public void setStartPlay(Radio radio) {
//        if (mPlayImageView != null) {
//            mPlayImageView.setSelected(true);
//        }
//        unChangePlay(null);
//    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPlayStateList.clear();
    }

    private static class PlayStatus {
        private ImageView mImageView;
        private Radio mRadio;
        private View mView;

        public View getView() {
            return mView;
        }

        public void setView(View view) {
            mView = view;
        }

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
