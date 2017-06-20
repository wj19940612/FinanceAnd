package com.sbai.finance.fragment.mine;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.fragment.dialog.InputSafetyPassDialogFragment;
import com.sbai.finance.model.mine.ExchangeProductModel;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/6/20.
 * 兑换元宝或兑换积分
 */

public class ExChangeProductFragment extends BaseFragment {

    private static final String KEY_TYPE = "TYPE";
    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private Unbinder mBind;
    private int mType;
    private ExchangeProductAdapter mExchangeProductAdapter;


    public static ExChangeProductFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);
        ExChangeProductFragment fragment = new ExChangeProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(KEY_TYPE);
        }
        mListView.setEmptyView(mEmpty);
        mExchangeProductAdapter = new ExchangeProductAdapter(getActivity());
        mListView.setAdapter(mExchangeProductAdapter);
        requestVcoinOrIntegrateList();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mExchangeProductAdapter.clear();
                requestVcoinOrIntegrateList();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExchangeProductModel item = (ExchangeProductModel) parent.getAdapter().getItem(position);
                if (item != null) {
                    if (item.getType() == 0) {
                        InputSafetyPassDialogFragment.newInstance(getString(R.string.coin_number, item.getProduct()), getString(R.string.buy)).setOnPasswordListener(new InputSafetyPassDialogFragment.OnPasswordListener() {
                            @Override
                            public void onPassWord(String passWord) {
                                ToastUtil.curt(passWord);
                                // TODO: 2017/6/20 提交请求购买元宝
                            }
                        }).show(getChildFragmentManager());
                    } else {
                        SmartDialog.with(getActivity(), getString(R.string.confirm_use_coin_buy_integrate, item.getPrice(), String.valueOf(item.getProduct())), getString(R.string.buy_confirm))
                                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        dialog.dismiss();
                                        // TODO: 2017/6/20 购买积分
                                    }
                                }).show();
                    }
                }

            }
        });
    }

    //请求元宝 积分 数据
    private void requestVcoinOrIntegrateList() {
        for (int i = 10; i < 100; i++) {
            i += 10;
            ExchangeProductModel exchangeProductModel = new ExchangeProductModel(mType, i, i);
            mExchangeProductAdapter.add(exchangeProductModel);
        }

        stopRefreshAnimation();
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_refresh_listview, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    static class ExchangeProductAdapter extends ArrayAdapter<ExchangeProductModel> {

        private Context mContext;

        public ExchangeProductAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_exchange_product, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(mContext, getItem(position), position);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.product)
            TextView mProduct;
            @BindView(R.id.oldPrice)
            TextView mOldPrice;
            @BindView(R.id.price)
            TextView mPrice;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(Context context, ExchangeProductModel item, int position) {
                if (item.getType() == 0) {
                    mProduct.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cell_vcoin_big, 0, 0, 0);
                    mProduct.setText(String.valueOf(item.getPrice()));
                    mPrice.setText(context.getString(R.string.yuan_number, item.getPrice()));
                    if (position > 3) {
                        mOldPrice.setVisibility(View.VISIBLE);
                        mOldPrice.setText(context.getString(R.string.old_price, item.getPrice()));
                    } else {
                        mOldPrice.setVisibility(View.GONE);
                    }
                } else {
                    mProduct.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cell_integration, 0, 0, 0);
                    mProduct.setText(FinanceUtil.formatWithScale(item.getPrice()));
                    mPrice.setText(context.getString(R.string.coin_number, item.getPrice()));
                }
            }
        }
    }
}
