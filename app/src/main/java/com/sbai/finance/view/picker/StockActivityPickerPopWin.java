package com.sbai.finance.view.picker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.PopupWindow;

import com.sbai.finance.R;

import java.util.ArrayList;
import java.util.List;

/**
 * PopWindow for  Pick
 */
public class StockActivityPickerPopWin extends PopupWindow implements OnClickListener {

    private static final int DEFAULT_MIN_YEAR = 1900;
    public Button cancelBtn;
    public Button confirmBtn;
    public LoopView loopView;
    public View pickerContainerV;
    public View contentView;//root view

    private int loopPos = 0;
    private Context mContext;
    private String textCancel;
    private String textConfirm;
    private int colorCancel;
    private int colorConfirm;
    private int btnTextSize;//text btnTextSize of cancel and confirm button
    List<String> activityList;


    public static class Builder {

        //Required
        private Context context;
        private OnPickedListener listener;

        public Builder(Context context, OnPickedListener listener) {
            this.context = context;
            this.listener = listener;
        }

        //Option
        private String textCancel = "取消";
        private String textConfirm = "确定";
        private int dataChoseIndex = 0;
        private int colorCancel = Color.parseColor("#999999");
        private int colorConfirm = Color.parseColor("#303F9F");
        private int btnTextSize = 16;//text btnTextSize of cancel and confirm button
        private int viewTextSize = 10;
        private List<String> dataList;

        public Builder textCancel(String textCancel) {
            this.textCancel = textCancel;
            return this;
        }

        public Builder textConfirm(String textConfirm) {
            this.textConfirm = textConfirm;
            return this;
        }

        public Builder dataChose(int dataChose) {
            this.dataChoseIndex = dataChose;
            return this;
        }

        public Builder colorCancel(int colorCancel) {
            this.colorCancel = colorCancel;
            return this;
        }

        public Builder colorConfirm(int colorConfirm) {
            this.colorConfirm = colorConfirm;
            return this;
        }

        /**
         * set btn text btnTextSize
         *
         * @param textSize dp
         */
        public Builder btnTextSize(int textSize) {
            this.btnTextSize = textSize;
            return this;
        }

        public Builder viewTextSize(int textSize) {
            this.viewTextSize = textSize;
            return this;
        }

        public Builder dataList(List<String> dataList) {
            this.dataList = dataList;
            return this;
        }

        public StockActivityPickerPopWin build() {
            if (dataChoseIndex < 0) {
                throw new IllegalArgumentException();
            }
            return new StockActivityPickerPopWin(this);
        }
    }

    public StockActivityPickerPopWin(Builder builder) {
        this.textCancel = builder.textCancel;
        this.textConfirm = builder.textConfirm;
        this.mContext = builder.context;
        this.mListener = builder.listener;
        this.colorCancel = builder.colorCancel;
        this.colorConfirm = builder.colorConfirm;
        this.btnTextSize = builder.btnTextSize;
        this.activityList = builder.dataList;
        setSelectedData(builder.dataChoseIndex);
        initView();
    }

    private OnPickedListener mListener;

    private void initView() {

        contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_activity_picker, null);
        cancelBtn = (Button) contentView.findViewById(R.id.cancel);
        cancelBtn.setTextColor(colorCancel);
        cancelBtn.setTextSize(btnTextSize);
        confirmBtn = (Button) contentView.findViewById(R.id.confirm);
        confirmBtn.setTextColor(colorConfirm);
        confirmBtn.setTextSize(btnTextSize);
        loopView = (LoopView) contentView.findViewById(R.id.picker);
        pickerContainerV = contentView.findViewById(R.id.container_picker);
        //set checked listen
        loopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                loopPos = item;
//                initDayPickerView();
            }
        });
        initPickerViews();
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        contentView.setOnClickListener(this);

        if (!TextUtils.isEmpty(textConfirm)) {
            confirmBtn.setText(textConfirm);
        }

        if (!TextUtils.isEmpty(textCancel)) {
            cancelBtn.setText(textCancel);
        }

        setTouchable(true);
        setFocusable(true);
        // setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(R.style.FadeInPopWin);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * Init loop view,
     */
    private void initPickerViews() {
        loopView.setDataList((ArrayList) activityList);
        loopView.setInitPosition(loopPos);

    }

    /**
     * set selected date position value when initView.
     *
     * @param dataIndex
     */
    public void setSelectedData(int dataIndex) {
        loopPos = dataIndex;
    }

    /**
     * Show date picker popWindow
     *
     * @param activity
     */
    public void showPopWin(Activity activity) {

        if (null != activity) {

            TranslateAnimation trans = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    0, Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0);

            showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM,
                    0, 0);
            trans.setDuration(400);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());

            pickerContainerV.startAnimation(trans);
        }
    }

    /**
     * Dismiss date picker popWindow
     */
    public void dismissPopWin() {

        TranslateAnimation trans = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);

        trans.setDuration(400);
        trans.setInterpolator(new AccelerateInterpolator());
        trans.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                dismiss();
            }
        });

        pickerContainerV.startAnimation(trans);
    }

    @Override
    public void onClick(View v) {

        if (v == contentView || v == cancelBtn) {
            dismissPopWin();
        } else if (v == confirmBtn) {
            if (null != mListener) {
                mListener.onPickCompleted(loopPos);
            }
            dismissPopWin();
        }
    }

    public interface OnPickedListener {

        /**
         * Listener when date has been checked
         */
        void onPickCompleted(int position);
    }
}
