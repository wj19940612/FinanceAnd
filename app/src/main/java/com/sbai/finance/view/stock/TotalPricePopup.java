package com.sbai.finance.view.stock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sbai.finance.R;

/**
 * Modified by john on 28/11/2017
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class TotalPricePopup {

    private PopupWindow mPopupWindow;
    private TextView mTotalPrice;
    private Context mContext;

    public TotalPricePopup(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_total_price, null);
        mPopupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        mPopupWindow.setClippingEnabled(true);
        mTotalPrice = view.findViewById(R.id.totalPrice);
        mContext = context;
    }

    public void setTotalPrice(String totalPrice) {
        mTotalPrice.setText(mContext.getString(R.string.about_x_without_fee, totalPrice));
    }

    public void showAbove(View v) {
        if (!mPopupWindow.isShowing()) {
            View popupView = mPopupWindow.getContentView();
            popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            int offsetX = v.getWidth() / 2 - popupView.getMeasuredWidth() / 2;
            int offsetY = v.getHeight() + popupView.getMeasuredHeight();
            mPopupWindow.showAsDropDown(v, offsetX, -offsetY);
        }
    }

    public void dismiss() {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }
}
