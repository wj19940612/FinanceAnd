package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by linrongfang on 2017/6/20.
 */

public class BattleTradeView extends LinearLayout {

    @BindView(R.id.listview)
    ListView mListview;

    @BindView(R.id.longPurchase)
    Button mLongPurchase;
    @BindView(R.id.shortPurchase)
    Button mShortPurchase;
    @BindView(R.id.closePosition)
    Button mClosePosition;

    @BindView(R.id.direction)
    TextView mDirection;
    @BindView(R.id.buyPrice)
    TextView mBuyPrice;
    @BindView(R.id.profit)
    TextView mProfit;
    @BindView(R.id.noPosition)
    TextView mNoPosition;

    private OnViewClickListener mOnViewClickListener;

    public interface OnViewClickListener {
        void onLongPurchaseButtonClick();

        void onShortPurchaseButtonClick();

        void onClosePositionButtonClick();
    }

    public BattleTradeView(Context context) {
        super(context);
        init();
    }

    public BattleTradeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BattleTradeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setBackgroundResource(R.drawable.ic_futures_versus_bg);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_battle_trade, null, false);
        addView(view);
        ButterKnife.bind(this);

        mLongPurchase.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onLongPurchaseButtonClick();
                }
            }
        });

        mShortPurchase.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onShortPurchaseButtonClick();
                }
            }
        });

        mClosePosition.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onClosePositionButtonClick();
                }
            }
        });
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
    }


    static class BattleTradeAdapter extends BaseAdapter {
        Context mContext;

        public BattleTradeAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_battle_trade, null, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(position);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.myInfo)
            TextView mMyInfo;
            @BindView(R.id.myInfoTime)
            TextView mMyInfoTime;
            @BindView(R.id.userInfo)
            TextView mUserInfo;
            @BindView(R.id.userInfoTime)
            TextView mUserInfoTime;
            @BindView(R.id.point)
            ImageView mPoint;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(int position) {

            }
        }
    }


}
