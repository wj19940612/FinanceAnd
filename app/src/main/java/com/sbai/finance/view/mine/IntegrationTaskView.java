package com.sbai.finance.view.mine;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017\12\29 0029.
 */

public class IntegrationTaskView extends RelativeLayout {
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.integration)
    TextView mIntegration;
    @BindView(R.id.tip)
    TextView mTip;
    @BindView(R.id.goBtn)
    TextView mGoBtn;
    @BindView(R.id.bottomLine)
    View mBottomLine;

    private Context mContext;
    private boolean mHasBottomLine;
    private String mTitle;

    public IntegrationTaskView(Context context) {
        this(context, null);
    }

    public IntegrationTaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IntegrationTaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IntegrationTaskView);
        mHasBottomLine = typedArray.getBoolean(R.styleable.IntegrationTaskView_hasBottomLine, false);
        mTitle = typedArray.getString(R.styleable.IntegrationTaskView_itemTitle);
        typedArray.recycle();

        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_integration_tast, this, true);
        ButterKnife.bind(this);
        if (mTitle != null) {
            mName.setText(mTitle);
        }
    }

    private void setIntegration(int integration) {
        mIntegration.setText(String.format(mContext.getString(R.string.get_integration), integration));
    }

    private void setGotoBtnBg(){
        mGoBtn.setBackground(ContextCompat.getDrawable(mContext,R.drawable.btn_integration_to_finish));
    }

    private void setRequireBtnBg(){
        mGoBtn.setBackground(ContextCompat.getDrawable(mContext,R.drawable.btn_quire_integration));
    }
}
