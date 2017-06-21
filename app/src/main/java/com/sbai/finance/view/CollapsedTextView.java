package com.sbai.finance.view;

/**
 * Created by lixiaokuan0819 on 2017/6/14.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewTreeObserver;

import com.sbai.finance.R;

import java.util.ArrayList;

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
                TextPaint textPaint = getPaint();
                float width = textPaint.measureText(getReplaceString(text));
                /* 计算行数 */
                //获取显示宽度
                int showWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
                int lines = (int) (width / showWidth);
                if (width % showWidth != 0) {
                    lines++;
                }
                allLines = (int) (textPaint.measureText(getReplaceString(text) + collapsedText) / showWidth);
                if (lines > maxLine) {
                    int expect = getReplaceString(text).length() / lines;
                    int end = 0;
                    int lastLineEnd = 0;
                    //...+expandedText的宽度，需要在最后一行加入计算
                    int expandedTextWidth = (int) Math.ceil(textPaint.measureText(ELLIPSE + StaticLayout.getDesiredWidth(expandedText, getPaint())));
                    //计算每行显示文本数
                    for (int i = 1; i <= maxLine; i++) {
                        int tempWidth = 0;
                        if (i == maxLine) {

                            tempWidth = expandedTextWidth;
                        }
                        end += expect;
                        if (end > getReplaceString(text).length()) {
                            end = getReplaceString(text).length();
                        }
                        if (textPaint.measureText(getReplaceString(text), lastLineEnd, end) > showWidth - tempWidth) {
                            //预期的第一行超过了实际显示的宽度
                            end--;
                            while (textPaint.measureText(getReplaceString(text), lastLineEnd, end) > showWidth - tempWidth) {
                                end--;
                            }
                        } else {
                            end++;
                            while (textPaint.measureText(getReplaceString(text), lastLineEnd, end) < showWidth - tempWidth) {
                                end++;
                            }
                            end--;
                        }
                        lastLineEnd = end;
                    }
                    SpannableStringBuilder s = new SpannableStringBuilder(getReplaceString(text), 0, end)
                            .append(ELLIPSE)
                            .append(expandedText);

                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#999999"));
                    s.setSpan(colorSpan, s.length() - expandedText.length(), s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    setText(s);
                } else {
                    setText(getReplaceString(text));
                }
            }
        });
    }

    private String getReplaceString(String text) {
        //获取显示宽度
        int showWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        String temp = " ";
        TextPaint tp = new TextPaint();
        int width = (int) tp.measureText(temp);
        int rate = showWidth / width;

        StringBuilder tempString = new StringBuilder();
        for (int i = 0; i < rate; i++) {
            tempString.append(temp);
        }

        StringBuilder replaceString = new StringBuilder();
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                replaceString.append(tempString);
            } else {
                replaceString.append(c);
            }
        }

        return replaceString.toString();
    }

    /**
     * 返回每行能显示字符串的集合
     *
     * @param content 所有字符
     * @param p       用于测量每个字符的宽度
     * @param width   文本框有效的宽度 去掉左右边距
     * @return
     */

    private ArrayList<String> autoSplit(String content, Paint p, float width) {
        ArrayList<String> as = new ArrayList<String>();
        //字符口中 的个数
        int length = content.length();
        float textWidth = p.measureText(content);
        if (textWidth < width) {
            as.add(content);
            return as;
        }

        int start = 0, end = 1;
        while (start < length) {
            // 一定个数的文本宽度小于控件宽度
            // 且 个数加1个(防止越界)文本宽度大于控件宽度时
            if (p.measureText(content, start, (end + 1) > length ? length
                    : (end + 1)) >= width
                    && p.measureText(content, start, end) <= width) {

                as.add((String) content.subSequence(start, end));
                start = end;
            }
            // 不足一行的文本 最后一行处理
            if (end == length) {
                as.add((String) content.subSequence(start, end));
                break;
            }
            // 对换行符的识别
            if (content.charAt(end) == '\n') {
                as.add((String) content.subSequence(start, end));
                // end +1 去掉'\n'
                start = end + 1;
            }
            end += 1;
        }
        return as;
    }

    protected void onDraw(Canvas canvas) {
        TextPaint paint1 = getPaint();
        String txt = super.getText().toString();
        Paint.FontMetrics fm = paint1.getFontMetrics();
        float baseline = fm.descent - fm.ascent;
        float x = getPaddingLeft();
        float y = baseline; // 由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。
        // 一行的内容长度
        float lineLenth = getWidth() - getPaddingLeft() - getPaddingRight();
        ArrayList<String> texts = autoSplit(txt, paint1, lineLenth);

        for (String string : texts) {
            System.out.println("string-->" + string);
        }

        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            if (text == null) {
                continue;
            }
            // 左对齐
            if (getGravity() == getFormatGravity(Gravity.CENTER)) {
                float textLength = paint1.measureText(text, 0, text.length());
                canvas.drawText(text, x + (lineLenth - textLength) / 2, y, paint1);

            }
            // 右对齐
            else if (getGravity() == getFormatGravity(Gravity.RIGHT)) {
                float textLength = paint1.measureText(text, 0, text.length());
                canvas.drawText(text, x + lineLenth - textLength, y, paint1);
            }
            //居中对齐
            else {
                canvas.drawText(text, x, y, paint1); // 坐标以控件左上角为原点
            }
            //最后一行不添加间距
            if (i != texts.size() - 1) {
                y += baseline + fm.leading;
            }  // 添加字体行间距
        }
    }

    private int getFormatGravity(int gravity) {
//      if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
//            gravity |= Gravity.START;
//        }
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
            gravity |= Gravity.TOP;
        }
        return gravity;
    }
}