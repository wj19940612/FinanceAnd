package com.sbai.finance.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.economiccircle.BorrowMoneyDetailsActivity;
import com.sbai.finance.activity.economiccircle.OpinionDetailsActivity;
import com.sbai.finance.model.EconomicCircle;
import com.sbai.finance.utils.Launcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EconomicCircleFragment extends BaseFragment {
    private static final int TYPE_PRODUCT = 0;
    private static final int TYPE_HELP = 1;

    @BindView(android.R.id.list)
    ListView mList;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    Unbinder unbinder;

    private List<EconomicCircle> mEconomicCircleList;
    private EconomicCircleAdapter mEconomicCircleAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_economic_circle, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEconomicCircleList = new ArrayList<>();
        initData();
        mEconomicCircleAdapter = new EconomicCircleAdapter(getContext(), mEconomicCircleList);
        //mList.setEmptyView(mEmpty);
        mList.setAdapter(mEconomicCircleAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position % 2 == 0) {
                    Launcher.with(getContext(), OpinionDetailsActivity.class)
                            .execute();
                } else {
                    Launcher.with(getContext(), BorrowMoneyDetailsActivity.class)
                            .execute();
                }
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            mEconomicCircleList.add(new EconomicCircle(5));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestEconomicCircleList();
    }

    private void requestEconomicCircleList() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    static class EconomicCircleAdapter extends BaseAdapter {
        private Context mContext;
        private List<EconomicCircle> mEconomicCircleList;

        private EconomicCircleAdapter(Context context, List<EconomicCircle> economicCircleList) {
            this.mContext = context;
            this.mEconomicCircleList = economicCircleList;
        }

        @Override
        public int getCount() {
            return mEconomicCircleList.size();
        }

        @Override
        public Object getItem(int position) {
            return mEconomicCircleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position % 2 == 0) {
                return TYPE_PRODUCT;
            }
            return TYPE_HELP;
            //return mEconomicCircleList.get(position).getType();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ProductViewHolder productViewHolder;
            HelpViewHolder helpViewHolder;
                switch (getItemViewType(position)) {
                    case TYPE_PRODUCT:
                        if (convertView == null) {
                            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_economic_circle_product, null);
                            productViewHolder = new ProductViewHolder(convertView);
                            convertView.setTag(productViewHolder);
                        } else {
                            productViewHolder = (ProductViewHolder) convertView.getTag();
                        }

                        productViewHolder.bindingData(mContext, (EconomicCircle) getItem(position));
                        break;

                    case TYPE_HELP:
                        if (convertView == null) {
                            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_economic_circle_help, null);
                            helpViewHolder = new HelpViewHolder(convertView);
                            convertView.setTag(helpViewHolder);
                        } else {
                            helpViewHolder = (HelpViewHolder) convertView.getTag();
                        }
                        helpViewHolder.bindingData(mContext, (EconomicCircle) getItem(position));
                        break;
                }

            return convertView;
        }

        static class ProductViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.followed)
            TextView mFollowed;
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.opinion)
            TextView mOpinion;
            @BindView(R.id.product)
            TextView mProduct;
            @BindView(R.id.productName)
            TextView mProductName;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.upDownPrice)
            TextView mUpDownPrice;
            @BindView(R.id.upDownPercent)
            TextView mUpDownPercent;
            @BindView(R.id.upDownArea)
            LinearLayout mUpDownArea;

            ProductViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(Context context, EconomicCircle item) {
                mUserName.setText("刘亦菲");
                mFollowed.setText("已关注");
                mPublishTime.setText("战国时期");
                mOpinion.setText("话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。");
                mProduct.setText("股票");
                mProductName.setText("曹操股份");
                mLastPrice.setText("88.88");
                mUpDownPrice.setText("+8.8");
                mUpDownPercent.setText("+10%");
            }
        }

        static class HelpViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.address)
            TextView mAddress;
            @BindView(R.id.needAmount)

            TextView mNeedAmount;
            @BindView(R.id.borrowTime)
            TextView mBorrowTime;
            @BindView(R.id.borrowInterest)
            TextView mBorrowInterest;
            @BindView(R.id.opinion)
            TextView mOpinion;

            HelpViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(Context context, EconomicCircle item) {
                mUserName.setText("吴彦祖");
                mPublishTime.setText("战国时期");
                mAddress.setText("山东");
                mNeedAmount.setText(context.getString(R.string.RMB, "8888"));
                mBorrowTime.setText(context.getString(R.string.day, "8888"));
                mBorrowInterest.setText(context.getString(R.string.RMB, "8888"));
                mOpinion.setText("话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。");
            }
        }
    }
}
