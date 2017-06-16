package com.sbai.finance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.sbai.finance.R;

public class RedPointTextView extends android.support.v7.widget.AppCompatTextView {

	private Paint mBgPaint;
	private Paint mTextPaint;
	private int mNum;

	/**
	 * 需要绘制的数字大小
	 * 默认大小为12sp
	 */
	private float mTextSize = sp2px(12);

	/**
	 * 默认背景颜色
	 */
	private int mBgColor = 0xffff0000;

	/**
	 * 默认字体颜色
	 */
	private int mTextColor = 0xffffffff;

	//圆心坐标
	private int mX = 0;
	private int mY = 0;
	private int mNormalRadius;

	public RedPointTextView(Context context) {
		this(context, null);
	}

	public RedPointTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RedPointTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RedPointTextView);
		mTextColor = typedArray.getColor(R.styleable.RedPointTextView_badge_txt_color, 0xffffffff);
		mBgColor = typedArray.getColor(R.styleable.RedPointTextView_badge_bg_color, 0xffff0000);
		mTextSize = typedArray.getDimensionPixelSize(R.styleable.RedPointTextView_badge_txt_size, (int) sp2px(10));
		mNum = typedArray.getInteger(R.styleable.RedPointTextView_badge_txt_num, -1);

		//画背景圆形
		mBgPaint = new Paint();
		mBgPaint.setAntiAlias(true);

		//绘制数字
		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

		typedArray.recycle();
		mNormalRadius = dp2px(8);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mX = getWidth() * 29 / 50;
		mY = getHeight() * 2 / 11;
		mTextPaint.setTextSize(mTextSize);

		if (mNum > 0) {

			mBgPaint.setColor(mBgColor);

			String str = null;
			if (mNum > 999) {
				str = "999+";
			} else {
				str = String.valueOf(mNum);
			}
			float textWidth = mTextPaint.measureText(str);
			int padding = dp2px(2);
			if (mNormalRadius * 2 - textWidth < padding) {
				RectF rectF = new RectF(mX - textWidth / 2 - padding, mY - mNormalRadius, mX + textWidth / 2 + padding, mY + mNormalRadius);
				canvas.drawRoundRect(rectF, mNormalRadius, mNormalRadius, mBgPaint);
			} else {
				canvas.drawCircle(mX, mY, mNormalRadius, mBgPaint);
			}
			//绘制的文本
			mTextPaint.setColor(mTextColor);
			canvas.drawText(str, mX - textWidth / 2, mY + mTextPaint.getFontMetrics().bottom * 1.2f, mTextPaint);
		} else {
			mBgPaint.setColor(0x00000000);
			canvas.drawCircle(mX, mY, mNormalRadius, mBgPaint);
			String str = "";
			//颜色
			mTextPaint.setColor(0x00ffffff);
			canvas.drawText(str, mX - mTextPaint.measureText(str) / 2, mY + mTextPaint.getFontMetrics().bottom * 1.2f, mTextPaint);
		}
	}

	/**
	 * 设置提醒数字
	 *
	 * @param mNum
	 */
	public void setNum(int mNum) {
		this.mNum = mNum;
		invalidate();
	}

	/**
	 * 清除提醒数字
	 */
	public void clearNum() {
		this.mNum = -1;
		invalidate();
	}

	/**
	 * 设置数字颜色
	 *
	 * @param textColor
	 */
	public void setTextColor(int textColor) {
		this.mTextColor = textColor;
	}

	/**
	 * 设置数字背景
	 *
	 * @param bgColor
	 */
	public void setBgColor(int bgColor) {
		this.mBgColor = bgColor;
	}

	/**
	 * 设置提示数字大小，单位sp
	 *
	 * @param textSize
	 */
	public void setTextSize(int textSize) {
		this.mTextSize = textSize;
	}

	/**
	 * 将dp转为px
	 *
	 * @param dp
	 * @return
	 */
	private int dp2px(int dp) {
		int ret = 0;
		ret = (int) (dp * getContext().getResources().getDisplayMetrics().density);
		return ret;
	}

	/**
	 * 将sp值转换为px值
	 *
	 * @param sp
	 * @return
	 */
	private float sp2px(int sp) {
		float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (sp * fontScale + 0.5f);
	}
}
