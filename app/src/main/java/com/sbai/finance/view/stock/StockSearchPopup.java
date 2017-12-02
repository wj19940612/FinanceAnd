package com.sbai.finance.view.stock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.stock.Stock;
import com.sbai.finance.utils.Display;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Modified by john on 29/11/2017
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class StockSearchPopup {

    private PopupWindow mPopupWindow;
    private ListView mListView;
    private StockSearchAdapter mStockSearchAdapter;
    private Context mContext;
    private OnStockSelectListener mOnStockSelectListener;
    private List<Stock> mStockList;

    public interface OnStockSelectListener {
        void onStockSelect(Stock stock);
    }

    public StockSearchPopup(Context context) {
        mContext = context;
        mStockList = new ArrayList<>();
        View view = LayoutInflater.from(context).inflate(R.layout.popup_stock_search, null);
        mPopupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        mPopupWindow.setClippingEnabled(true);
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mListView = view.findViewById(R.id.list);

        mStockSearchAdapter = new StockSearchAdapter(context, mStockList);
        mListView.setAdapter(mStockSearchAdapter);
        mListView.setEmptyView(view.findViewById(R.id.empty));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Object itemAtPosition = adapterView.getItemAtPosition(pos);
                if (itemAtPosition instanceof Stock) {
                    if (mOnStockSelectListener != null) {
                        mOnStockSelectListener.onStockSelect((Stock) itemAtPosition);
                    }
                }
            }
        });
    }

    public void setStocks(List<Stock> data) {
        mStockList.clear();
        mStockList.addAll(data);
        mStockSearchAdapter.notifyDataSetChanged();
        if (data.size() > 0) { // 修复一个有数据也显示 empty view 的问题
            mListView.setVisibility(View.VISIBLE);
        }
    }

    public void showBelow(View v) {
        if (!mPopupWindow.isShowing()) {
            View popupView = mPopupWindow.getContentView();
            popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            int offsetX = v.getWidth() / 2 - popupView.getMeasuredWidth() / 2;
            int offsetY = (int) Display.dp2Px(17, mContext.getResources());
            int height = calculatePopupHeight(v, offsetY);
            mPopupWindow.setHeight(height);
            mPopupWindow.showAsDropDown(v, offsetX, offsetY);
        }
    }

    private int calculatePopupHeight(View v, int offsetY) {
        Rect rect = new Rect();
        v.getWindowVisibleDisplayFrame(rect);
        int[] location = new int[2];
        v.getLocationInWindow(location);
        return rect.height() + rect.top - (location[1] + v.getHeight() + offsetY);
    }

    public void dismiss() {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public void setOnStockSelectListener(OnStockSelectListener onStockSelectListener) {
        mOnStockSelectListener = onStockSelectListener;
    }

    static class StockSearchAdapter extends ArrayAdapter<Stock> {
        public StockSearchAdapter(@NonNull Context context, List<Stock> stockList) {
            super(context, 0, 0, stockList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_search_stock, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.stockName)
            TextView mStockName;
            @BindView(R.id.stockCode)
            TextView mStockCode;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(Stock item, Context context) {
                mStockName.setText(item.getVarietyName());
                mStockCode.setText(item.getVarietyCode());
            }
        }
    }

}
