package com.sbai.finance.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sbai.finance.R;
import com.sbai.glide.GlideApp;

public class MissFloatWindow extends LinearLayout {
    private ImageView mMissAvatar;
    private ImageView mAudioAnim;

    public MissFloatWindow(Context context) {
        super(context);
        init();
    }

    public MissFloatWindow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setBackgroundResource(R.drawable.bg_float_window);
        setGravity(Gravity.CENTER_VERTICAL);
        setMinimumHeight((int) dp2Px(40f, getResources()));

        mMissAvatar = new ImageView(getContext());
        mMissAvatar.setImageResource(R.drawable.ic_default_avatar);

        mAudioAnim = new ImageView(getContext());
        mAudioAnim.setBackgroundResource(R.drawable.bg_miss_voice_float);

        int side = (int) dp2Px(32f, getResources());
        int margin = (int) dp2Px(4f, getResources());
        LayoutParams params = new LayoutParams(side, side);
        params.setMargins(margin, margin, margin, margin);
        addView(mMissAvatar, params);

        side = (int) dp2Px(18f, getResources());
        margin = (int) dp2Px(14f, getResources());
        params = new LayoutParams(side, side);
        params.setMargins(margin, 0, margin, 0);
        addView(mAudioAnim, params);
    }

    public void setMissAvatar(String avatarUrl) {
        GlideApp.with(getContext()).load(avatarUrl)
                .placeholder(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(mMissAvatar);
    }

    public void startAnim() {
        AnimationDrawable animation = (AnimationDrawable) mAudioAnim.getBackground();
        animation.start();
    }

    public void stopAnim() {
        AnimationDrawable animation = (AnimationDrawable) mAudioAnim.getBackground();
        animation.stop();
    }

    public float dp2Px(float value, Resources res) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, res.getDisplayMetrics());
    }
    
}