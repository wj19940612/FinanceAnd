package com.sbai.finance.view.training;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.sbai.finance.R;
import com.sbai.finance.utils.Display;


public class DragImageView extends android.support.v7.widget.AppCompatImageView {

	private float mDownX = -1;
	private float mDownY = -1;
	private float mDelayTime;
	private String mText = "年报";
	private float mDx;
	private float mDy;
	private Paint mTextPaint;

	public DragImageView(Context context) {
		this(context, null, 0);
	}

	public DragImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mTextPaint = new Paint();
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DragImageView);
		mDelayTime = typedArray.getFloat(R.styleable.DragImageView_delayTime, 0);
		typedArray.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawContent(canvas);
		startAnimation();
	}

	private void startAnimation() {
		setScaleX(0);
		setScaleY(0);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(
				ObjectAnimator.ofFloat(this, "scaleX",0f, 1.2f, 1f,1.2f,1f),
				ObjectAnimator.ofFloat(this, "scaleY",0f, 1.2f, 1f,1.2f,1f)
		);
		animatorSet.setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.setStartDelay((long) mDelayTime);
		animatorSet.start();
	}

	private void drawContent(Canvas canvas) {
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(Display.sp2Px(16, getResources()));
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(mText, getWidth() / 2, getHeight() / 2 + getHeight() / 5, mTextPaint);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownX = event.getRawX();
				mDownY = event.getRawY();

				break;
			case MotionEvent.ACTION_MOVE:
				mDx = event.getRawX() - mDownX;
				mDy = event.getRawY() - mDownY;
				setTranslationX(mDx);
				setTranslationY(mDy);
				break;
			case MotionEvent.ACTION_UP:
				AnimatorSet animatorSet = new AnimatorSet();
				animatorSet.playTogether(
						ObjectAnimator.ofFloat(this, "translationX", 0f),
						ObjectAnimator.ofFloat(this, "translationY", 0f));
				animatorSet.setDuration(200 * 2).setInterpolator(new OvershootInterpolator());
				animatorSet.start();
				break;
		}

		return true;
	}

	public void setText(String text){
		mText = text;
	}
}
