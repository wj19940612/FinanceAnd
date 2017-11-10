package com.sbai.finance.view.praise;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sbai.finance.R;

import java.util.Random;

/**
 * Modified by john on 08/11/2017
 * <p>
 * Description: 用于包含点赞控件实现点赞动效的布局
 * <p>
 */
public class FlyPraiseLayout extends RelativeLayout {

    /**
     * Modified by john on 08/11/2017
     * <p>
     * Description: 用于计算贝塞尔曲线点的 Evaluator。
     * <p>
     * 三次方公式贝兹曲线的路径由给定点P0、P1、P2、P3四个点在平面或在三维空间中定义了三次方贝兹曲线：
     * B（t）= P0 * (1-t)^3  +  3 * P1 * t * (1-t)^2 + 3 * P2 * t^2 * (1-t) + P3 * t^3, t<[0,1]
     * 有：
     * X = X0 * (1-t)^3  + 3 * X1 * t * (1-t)^2 + 3 * X2 * t^2 * (1-t) + X3 * t^3, t<[0,1]
     * Y = Y0 * (1-t)^3  + 3 * Y1 * t * (1-t)^2 + 3 * Y2 * t^2 * (1-t) + Y3 * t^3, t<[0,1]
     * </p>
     */
    public static class BezierTypeEvaluator implements TypeEvaluator<PointF> {

        private PointF mP1;
        private PointF mP2;

        /**
         * p1, p2 贝兹控制坐标
         *
         * @param p1
         * @param p2
         */
        public BezierTypeEvaluator(PointF p1, PointF p2) {
            mP1 = p1;
            mP2 = p2;
        }

        @Override
        public PointF evaluate(float v, PointF startP, PointF endP) {
            float t = v;
            float tl = (1 - t);
            float x = (float) (startP.x * Math.pow(tl, 3) + mP1.x * 3 * t * Math.pow(tl, 2) + mP2.x * Math.pow(t, 2) * tl + endP.x * Math.pow(t, 3));
            float y = (float) (startP.y * Math.pow(tl, 3) + mP1.y * 3 * t * Math.pow(tl, 2) + mP2.y * Math.pow(t, 2) * tl + endP.y * Math.pow(t, 3));
            return new PointF(x, y);
        }
    }

    private PraiseView[] mPraiseViews;

    public FlyPraiseLayout(Context context) {
        super(context);
    }

    public FlyPraiseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPraiseViews = new PraiseView[getChildCount()];
        for (int i = 0; i < getChildCount(); i++) {
            mPraiseViews[i] = (PraiseView) getChildAt(i);
            mPraiseViews[i].setOnPraiseChangeListener(new PraiseView.OnPraiseChangeListener() {
                @Override
                public void onPraiseChange(View view) {
                    if (getWidth() != 0) {
                        attachFlyAnim(view);
                    }
                }
            });
        }
    }

    private void attachFlyAnim(View beAttachedView) {
        ImageView flyView = new ImageView(getContext());
        flyView.setImageResource(R.drawable.ic_praise);
        flyView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_BOTTOM, beAttachedView.getId());
        params.addRule(ALIGN_LEFT, beAttachedView.getId());
        params.addRule(ALIGN_TOP, beAttachedView.getId());
        params.addRule(ALIGN_RIGHT, beAttachedView.getId());
        addView(flyView, params);

        Animator flyAnim = attachFlyAnim(flyView, beAttachedView);
        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(flyAnim);
        finalSet.setInterpolator(new LinearInterpolator());
        finalSet.setTarget(flyView);
        finalSet.addListener(new AnimEndListener(flyView));
        finalSet.start();
    }

    private Animator attachFlyAnim(ImageView attachView, View beAttachedView) {
        PointF p0 = new PointF(beAttachedView.getLeft(), beAttachedView.getTop());
        PointF p1 = getPoint(beAttachedView, 1);
        PointF p2 = getPoint(beAttachedView, 2);
        PointF p3 = getPoint(beAttachedView, 3);

        BezierTypeEvaluator evaluator = new BezierTypeEvaluator(p1, p2);
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, p0, p3);
        animator.addUpdateListener(new BezierAnimListener(attachView));
        animator.setTarget(attachView);
        animator.setDuration(3000);

        return animator;
    }

    private PointF getPoint(View beAttachView, int i) {
        int centerOfWidth = getWidth() / 2;
        PointF pointF = new PointF();
        if (beAttachView.getLeft() < centerOfWidth) {
            pointF.x = new Random().nextInt(centerOfWidth);
        } else {
            pointF.x = centerOfWidth + new Random().nextInt(centerOfWidth);
        }
        if (i < 3) {
            pointF.y = new Random().nextInt(getHeight() - beAttachView.getHeight()) / i;
        } else {
            pointF.y = 0;
        }
        return pointF;
    }

    private class BezierAnimListener implements ValueAnimator.AnimatorUpdateListener {

        private View mView;

        public BezierAnimListener(View view) {
            mView = view;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            PointF pointF = (PointF) valueAnimator.getAnimatedValue();
            mView.setX(pointF.x);
            mView.setY(pointF.y);
            float fraction = valueAnimator.getAnimatedFraction();
            mView.setAlpha(1 - fraction);
            if (fraction <= 0.2) { // Max is 3
                mView.setScaleX(1 + fraction * 5);
                mView.setScaleY(1 + fraction * 5);
            } else {
                mView.setScaleX(2 - fraction / 5);
                mView.setScaleY(2 - fraction / 5);
            }
        }
    }

    private class AnimEndListener extends AnimatorListenerAdapter {

        private View mAttachView;

        public AnimEndListener(View attachView) {
            mAttachView = attachView;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            removeView(mAttachView);
        }
    }
}
