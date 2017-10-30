package com.sbai.finance.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.sbai.finance.utils.Display;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017\10\24 0024.
 */

public class VerticalScrollTextView extends TextSwitcher implements ViewSwitcher.ViewFactory {
    private static final int FLAG_START_AUTO_SCROLL = 0;
    private static final int FLAG_STOP_AUTO_SCROLL = 1;
    public static final int TEXT_SIZE_SP = 14;
    public static final int TEXT_ANIMATIONTIME = 300;
    public static final int TEXT_STILLTIME = 1000;

    private int textColor = Color.WHITE;

    private Context mContext;
    private List<String> textList;
    private OnItemClickListener itemClickListener;
    private Handler handler;
    private int currentId = -1;

    public VerticalScrollTextView(Context context) {
        super(context);
        mContext = context;
    }

    public VerticalScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        textList = new ArrayList<String>();
        setTextStillTime(TEXT_STILLTIME);
        setAnimTime(TEXT_ANIMATIONTIME);
    }

    @Override
    public View makeView() {
        TextView t = new TextView(mContext);
        t.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        t.setMaxLines(1);
        t.setTextColor(textColor);
        t.setTextSize(Display.dp2Px(TEXT_SIZE_SP,getResources()));
        t.setClickable(true);
        t.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (itemClickListener != null && textList.size() > 0 && currentId != -1) {
//                    itemClickListener.onItemClick(currentId % textList.size());
//                }
            }
        });
        return t;
    }

    //设置进出动画以及动画的持续时间
    public void setAnimTime(long animDuration) {
        setFactory(this);
        Animation in = new TranslateAnimation(0, 0, 30, 0);
        in.setDuration(animDuration);
        in.setInterpolator(new AccelerateInterpolator());
        Animation out = new TranslateAnimation(0, 0, 0, -30);
        out.setDuration(animDuration);
        out.setInterpolator(new AccelerateInterpolator());
        setInAnimation(in);
        setOutAnimation(out);
    }

    /**
     * 设置数据源
     * @param titles
     */
    public void setTextList(List<String> titles) {
        textList.clear();
        textList.addAll(titles);
        currentId = -1;
    }

    /**
     * 开始滚动
     */
    public void startAutoScroll() {
        handler.sendEmptyMessage(FLAG_START_AUTO_SCROLL);
    }

    /**
     * 停止滚动
     */
    public void stopAutoScroll() {
        handler.sendEmptyMessage(FLAG_STOP_AUTO_SCROLL);
    }

    /**
     * 设置点击事件监听
     * @param itemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * 轮播文本点击监听器
     */
    public interface OnItemClickListener {
        /**
         * 点击回调
         * @param position 当前点击ID
         */
        void onItemClick(int position);
    }


    /**
     * 间隔时间
     *
     * @param time
     */
    public void setTextStillTime(final long time) {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FLAG_START_AUTO_SCROLL:
                        if (textList.size() > 0) {
                            currentId++;
                            setText(textList.get(currentId % textList.size()));
                        }
                        handler.sendEmptyMessageDelayed(FLAG_START_AUTO_SCROLL, time);
                        break;
                    case FLAG_STOP_AUTO_SCROLL:
                        handler.removeMessages(FLAG_START_AUTO_SCROLL);
                        break;
                }
            }
        };
    }
}
