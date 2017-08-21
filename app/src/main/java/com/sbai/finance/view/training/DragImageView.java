package com.sbai.finance.view.training;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.sbai.finance.R;
import com.sbai.finance.utils.Display;


public class DragImageView extends android.support.v7.widget.AppCompatImageView {

	private float mDelayTime;
	private String mText = "";
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
				ObjectAnimator.ofFloat(this, "scaleX", 0f, 1.2f, 1f, 1.2f, 1f),
				ObjectAnimator.ofFloat(this, "scaleY", 0f, 1.2f, 1f, 1.2f, 1f)
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

	public void setText(String text) {
		mText = text;
	}
}
