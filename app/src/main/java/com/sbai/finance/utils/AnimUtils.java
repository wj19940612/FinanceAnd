package com.sbai.finance.utils;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * 动画工具类，用于创建简单动画
 */
public class AnimUtils {

    public abstract static class AnimEndListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public static Animation createTransYFromParent(long duration) {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0);
        animation.setDuration(duration);
        return animation;
    }

    public static Animation createTransYFromParent(long duration, Animation.AnimationListener listener) {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0);
        animation.setDuration(duration);
        animation.setAnimationListener(listener);
        return animation;
    }

    public static Animation createTransYFromParent(long duration, long startOffset, Animation.AnimationListener listener) {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0);
        animation.setDuration(duration);
        animation.setStartOffset(startOffset);
        animation.setAnimationListener(listener);
        return animation;
    }
}
