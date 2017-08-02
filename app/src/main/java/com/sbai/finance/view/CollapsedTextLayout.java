package com.sbai.finance.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sbai.finance.R;

/**
 * Created by ${wangJie} on 2017/6/26.
 */

public class CollapsedTextLayout extends RelativeLayout {
    private static final String TAG = "CollapsedTextLayout";

    private AppCompatTextView mContentTextView;
    private AppCompatTextView mHintTextView;

    public CollapsedTextLayout(Context context) {
        this(context, null);
        init(context);
    }

    public CollapsedTextLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public CollapsedTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_collapsed, null);
        mContentTextView = (AppCompatTextView) view.findViewById(R.id.rootView);
        mHintTextView = (AppCompatTextView) view.findViewById(R.id.hint);
        mHintTextView.setVisibility(GONE);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(view, layoutParams);
    }

    public void setContentText(CharSequence s) {
        mContentTextView.setText(s);
        mContentTextView.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = mContentTextView.getLineCount();
                if (lineCount > 2) {
                    mHintTextView.setVisibility(VISIBLE);
                } else {
                    mHintTextView.setVisibility(GONE);
                }
            }
        });
//        Log.d(TAG, "setContentText: "+mContentTextView.getLineCount());
    }
}
