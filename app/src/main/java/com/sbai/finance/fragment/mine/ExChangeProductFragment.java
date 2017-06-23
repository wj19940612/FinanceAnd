package com.sbai.finance.fragment.mine;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.fragment.dialog.InputSafetyPassDialogFragment;
import com.sbai.finance.model.mine.cornucopia.CornucopiaProductModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.SmartDialog;

import java.util.List;
import java.util.ListIterator;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(KEY_TYPE);
        }
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
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                final CornucopiaProductModel item = (CornucopiaProductModel) parent.getAdapter().getItem(position);
                if (item != null) {
                    if (item.isVcoin()) {
                        SmartDialog.with(getActivity(), getString(R.string.confirm_use_money_buy_coin, item.getFromRealMoney(), String.valueOf(item.getToRealMoney())), getString(R.string.buy_confirm))
                                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        dialog.dismiss();

                                        InputSafetyPassDialogFragment.newInstance(getString(R.string.coin_number, item.getFromRealMoney()), getString(R.string.buy)).setOnPasswordListener(new InputSafetyPassDialogFragment.OnPasswordListener() {
                                            @Override
                                            public void onPassWord(String passWord) {
                                                exchange(item, passWord);
                                            }
                                        }).show(getChildFragmentManager());

                                    }
                                }).show();


                    } else {
                        SmartDialog.with(getActivity(), getString(R.string.confirm_use_coin_buy_integrate, item.getFromRealMoney(), String.valueOf(item.getToRealMoney())), getString(R.string.buy_confirm))
                                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        dialog.dismiss();
                                        exchange(item, null);
                                    }
                                }).show();
                    }
                }
            }
        });
    }

    private void exchange(CornucopiaProductModel item, String passWord) {
        Client.exchange(item.getId(), passWord)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        Log.d(TAG, "onRespSuccess: " + resp.toString());
                    }
                })
                .fire();
    }

    //请求元宝 积分 数据
    private void requestVcoinOrIntegrateList() {
        Client.getExchangeProduct()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<CornucopiaProductModel>>, List<CornucopiaProductModel>>() {
                    @Override
                    protected void onRespSuccessData(List<CornucopiaProductModel> data) {
                        mExchangeProductAdapter.clear();
                        ListIterator<CornucopiaProductModel> cornucopiaProductModelListIterator = data.listIterator();
                        while (cornucopiaProductModelListIterator.hasNext()) {
                            CornucopiaProductModel productModel = cornucopiaProductModelListIterator.next();
                            if (mType == CornucopiaProductModel.TYPE_VCOIN) {
                                if (!productModel.isVcoin()) {
                                    cornucopiaProductModelListIterator.remove();
                                }
                            } else if (mType == CornucopiaProductModel.TYPE_INTEGRATION) {
                                if (productModel.isVcoin()) {
                                    cornucopiaProductModelListIterator.remove();
                                }
                            }
                        }
                        mExchangeProductAdapter.addAll(data);
                        stopRefreshAnimation();
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                })
                .fireSync();

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

    static class ExchangeProductAdapter extends ArrayAdapter<CornucopiaProductModel> {

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

            public void bindDataWithView(Context context, CornucopiaProductModel item, int position) {
                if (item.isVcoin()) {
                    mProduct.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cell_vcoin_big, 0, 0, 0);
                    mProduct.setText(String.valueOf(item.getToRealMoney()));
                    mPrice.setText(context.getString(R.string.yuan, FinanceUtil.formatWithScale(item.getFromRealMoney())));
                    if (!item.isNotDiscount()) {
                        mOldPrice.setVisibility(View.VISIBLE);
                        mOldPrice.setText(context.getString(R.string.old_price, FinanceUtil.formatWithScale(item.getFromMoney())));
                    } else {
                        mOldPrice.setVisibility(View.GONE);
                    }
                } else {
                    mProduct.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cell_integration, 0, 0, 0);
                    mProduct.setText(FinanceUtil.formatWithScale(item.getToRealMoney()));
                    mPrice.setText(context.getString(R.string.coin_number, item.getFromRealMoney()));
                }
            }
        }
    }
}
