package com.sbai.finance.view;

/**
 * Created by lixiaokuan0819 on 2017/6/14.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import com.sbai.finance.R;

public class CollapsedTextView extends android.support.v7.widget.AppCompatTextView {
	/**
	 * 收起状态下的最大行数
	 */
	private int maxLine = 2;
	/**
	 * 截取后，文本末尾的字符串
	 */
	private static final String ELLIPSE = "...";
	/**
	 * 默认全文的Text
	 */
	private static final String EXPANDEDTEXT = "全文";
	/**
	 * 默认收起的text
	 */
	private static final String COLLAPSEDTEXT = "收起";
	/**
	 * 全文的text
	 */
	private String expandedText = EXPANDEDTEXT;
	/**
	 * 收起的text
	 */
	private String collapsedText = COLLAPSEDTEXT;
	/**
	 * 所有行数
	 */
	private int allLines;
	/**
	 * 是否是收起状态，默认收起
	 */
	private boolean collapsed = true;
	/**
	 * 真实的text
	 */
	private String text;
	/**
	 * 收起时实际显示的text
	 */
	private CharSequence collapsedCs;

	public CollapsedTextView(Context context) {
		super(context);
		init(context, null);
	}

	public CollapsedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CollapsedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	@Override
	public TextPaint getPaint() {
		return super.getPaint();
	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CollapsedTextView);
			allLines = ta.getInt(R.styleable.CollapsedTextView_trimLines, 0);
			expandedText = ta.getString(R.styleable.CollapsedTextView_expandedText);
			if (TextUtils.isEmpty(expandedText)) {
				expandedText = EXPANDEDTEXT;
			}
			collapsedText = ta.getString(R.styleable.CollapsedTextView_collapsedText);
			if (TextUtils.isEmpty(collapsedText)) {
				collapsedText = COLLAPSEDTEXT;
			}
			ta.recycle();
		}

	}

	public void setShowText(final String text) {
		this.text = text;

		getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				ViewTreeObserver obs = getViewTreeObserver();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					obs.removeOnGlobalLayoutListener(this);
				} else {
					obs.removeGlobalOnLayoutListener(this);
				}
				TextPaint tp = getPaint();
				float width = tp.measureText(text);
		        /* 计算行数 */
				//获取显示宽度
				int showWidth = getWidth() - getPaddingRight() - getPaddingLeft();
				int lines = (int) (width / showWidth);
				if (width % showWidth != 0) {
					lines++;
				}
				allLines = (int) (tp.measureText(text + collapsedText) / showWidth);
				if (lines > maxLine) {
					int expect = text.length() / lines;
					int end = 0;
					int lastLineEnd = 0;
					//...+expandedText的宽度，需要在最后一行加入计算
					int expandedTextWidth = (int) tp.measureText(ELLIPSE + expandedText);
					//计算每行显示文本数
					for (int i = 1; i <= maxLine; i++) {
						int tempWidth = 0;
						if (i == maxLine) {

							tempWidth = expandedTextWidth;
						}
						end += expect;
						if (end > text.length()) {
							end = text.length();
						}
						if (tp.measureText(text, lastLineEnd, end) > showWidth - tempWidth) {
							//预期的第一行超过了实际显示的宽度
							end--;
							while (tp.measureText(text, lastLineEnd, end) > showWidth - tempWidth) {
								end--;
							}
						} else {
							end++;
							while (tp.measureText(text, lastLineEnd, end) < showWidth - tempWidth) {
								end++;
							}
							end--;
						}
						lastLineEnd = end;
					}
					SpannableStringBuilder s = new SpannableStringBuilder(text, 0, end)
							.append(ELLIPSE)
							.append(expandedText);

					ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#999999"));
					s.setSpan(colorSpan, s.length() - expandedText.length(), s.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					setText(s);
				} else {
					setText(text);
				}
			}
		});
	}
}