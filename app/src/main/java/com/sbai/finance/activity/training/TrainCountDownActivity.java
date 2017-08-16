package com.sbai.finance.activity.training;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 训练倒计时页面
 */

public class TrainCountDownActivity extends BaseActivity {

    public static final String TYPE = "type";
    public static final String ORIENTATION = "orientation";

    @BindView(R.id.image)
    ImageView mImage;
    @BindView(R.id.mainArea)
    RelativeLayout mMainArea;

    private int mType;
    private static final int MESSAGE_SUCCESS = 250;
    //handler发送消息所携带的参数（持续时间）
    private int duration;
    private Handler mHandler;

    public static void show(Activity activity, int type) {
        Launcher.with(activity, TrainCountDownActivity.class)
                .putExtra(TYPE, type)
                .execute();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_count_down);
        ButterKnife.bind(this);

        initData(getIntent());
        initView();
    }

    private void initData(Intent intent) {
        mType = intent.getIntExtra(TYPE, -1);
    }

    private void initView() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MESSAGE_SUCCESS) {
                    Launcher.with(getActivity(), KlineTrainActivity.class).execute();
                    finish();
                }
            }
        };
        int drawable = 0;
        int color = 0;
        switch (mType) {
            case Training.TYPE_THEORY:
                drawable = R.drawable.ic_count_down_theory;
                color = Color.parseColor("#e44b58");
                break;
            case Training.TYPE_TECHNOLOGY:
                drawable = R.drawable.ic_count_down_technology;
                color = Color.parseColor("#6843c4");
                break;
            case Training.TYPE_FUNDAMENTAL:
                drawable = R.drawable.ic_count_down_fundamentals;
                color = Color.parseColor("#f9b727");
                break;

        }
        if (color != 0) {
            mMainArea.setBackgroundColor(color);
        }
        if (drawable != 0) {
            Glide.with(getActivity())
                    .load(drawable)
                    .listener(new RequestListener<Integer, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            // 计算动画时长
                            GifDrawable drawable = (GifDrawable) resource;
                            GifDecoder decoder = drawable.getDecoder();
                            for (int i = 0; i < drawable.getFrameCount(); i++) {
                                duration += decoder.getDelay(i);
                            }
                            //发送延时消息，通知动画结束
                            mHandler.sendEmptyMessageDelayed(MESSAGE_SUCCESS,
                                    duration);
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)//添加缓存
                    .into(new GlideDrawableImageViewTarget(mImage, 1));
        }
    }
}
