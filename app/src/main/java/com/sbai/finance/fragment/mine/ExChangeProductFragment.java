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

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.setting.ModifySafetyPassActivity;
import com.sbai.finance.activity.mine.wallet.RechargeActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.fragment.dialog.InputSafetyPassDialogFragment;
import com.sbai.finance.model.mine.cornucopia.CornucopiaProductModel;
import com.sbai.finance.model.payment.UserFundInfoModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
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
    private UserFundInfoModel mUserFundInfoModel;

    public interface OnUserFundChangeListener {
        void onUserFundChange();
    }

    private OnUserFundChangeListener mOnUserFundChangeListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserFundChangeListener) {
            mOnUserFundChangeListener = (OnUserFundChangeListener) context;
        } else {
            throw new IllegalStateException(context.toString() + "" +
                    " must implements ExChangeProductFragment.OnUserFundChangeListener");
        }
    }

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
                if (item == null) return;
                if (mUserFundInfoModel != null) {
                    if (item.isVcoin() ? mUserFundInfoModel.getMoney() >= item.getFromRealMoney() : mUserFundInfoModel.getYuanbao() >= item.getFromRealMoney()) {
                        showExchangePassDialog(item);
                    } else {
                        showExchangeFailDialog(item);
                    }
                } else {
                    showExchangePassDialog(item);
                }
            }
        });
    }

    public void scrollToTop() {
        mListView.smoothScrollToPosition(0);
    }

    private void showExchangePassDialog(final CornucopiaProductModel item) {
        String msg = item.isVcoin() ? getString(R.string.confirm_use_money_buy_coin, FinanceUtil.formatWithScale(item.getFromRealMoney()), FinanceUtil.formatWithScaleNoZero(item.getToRealMoney())) :
                getString(R.string.confirm_use_coin_buy_integrate, FinanceUtil.formatWithScaleNoZero(item.getFromRealMoney()), FinanceUtil.formatWithScale(item.getToRealMoney()));
        String title = item.isVcoin() ? getString(R.string.buy_confirm) : getString(R.string.exchange_confirm);
        SmartDialog.with(getActivity(), msg, title)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Client.getUserHasPassWord()
                                .setTag(TAG)
                                .setIndeterminate(ExChangeProductFragment.this)
                                .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                                    @Override
                                    protected void onRespSuccessData(Boolean data) {
                                        if (!data) {
                                            Launcher.with(getActivity(), ModifySafetyPassActivity.class).putExtra(Launcher.EX_PAYLOAD, data.booleanValue()).execute();
                                        } else {
                                            showInputSafetyPassDialog(item);
                                        }
                                    }
                                })
                                .fire();
                    }
                }).show();
    }

    private void showInputSafetyPassDialog(final CornucopiaProductModel item) {
        String content = item.isVcoin() ? getString(R.string.coin_number, FinanceUtil.formatWithScaleNoZero(item.getToRealMoney())) :
                getString(R.string.integrate_number, FinanceUtil.formatWithScale(item.getToRealMoney()));
        String hintText = item.isVcoin() ? getString(R.string.buy) : getString(R.string.exchange);
        InputSafetyPassDialogFragment.newInstance(content, hintText)
                .setOnPasswordListener(new InputSafetyPassDialogFragment.OnPasswordListener() {
                    @Override
                    public void onPassWord(String passWord) {
                        exchange(item, passWord);
                    }
                }).show(getChildFragmentManager());
    }

    public void setUserFundInfo(UserFundInfoModel userFundInfo) {
        mUserFundInfoModel = userFundInfo;
    }

    private void exchange(final CornucopiaProductModel item, String passWord) {
        Client.exchange(item.getId(), passWord)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>(false) {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            if (mOnUserFundChangeListener != null) {
                                mOnUserFundChangeListener.onUserFundChange();
                            }
                            ToastUtil.curt(resp.getMsg());
                        }
                    }

                    @Override
                    protected void onReceive(Resp<Object> objectResp) {
                        super.onReceive(objectResp);
                        if (objectResp.getCode() == 2201) {
                            showExchangeFailDialog(item);
                        } else {
                            ToastUtil.curt(objectResp.getMsg());
                        }
                    }
                })
                .fire();
    }

    private void showExchangeFailDialog(CornucopiaProductModel item) {
        if (item.isVcoin()) {
            SmartDialog.with(getActivity(), getString(R.string.money_is_not_enough))
                    .setPositive(R.string.go_recharge, new SmartDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            Launcher.with(getActivity(), RechargeActivity.class).execute();
                        }
                    }).show();
        } else {
            SmartDialog.with(getActivity(), getString(R.string.coin_is_not_enough), getString(R.string.exchange_fail))
                    .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
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
                    mProduct.setText(FinanceUtil.formatWithScaleNoZero(item.getToRealMoney()));
                    mPrice.setText(context.getString(R.string.yuan, FinanceUtil.formatWithScale(item.getFromRealMoney())));
                } else {
                    mProduct.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cell_integration, 0, 0, 0);
                    mProduct.setText(FinanceUtil.formatWithScale(item.getToRealMoney()));
                    mPrice.setText(context.getString(R.string.coin_number, FinanceUtil.formatWithScaleNoZero(item.getFromRealMoney())));
                }
                if (item.isDiscount()) {
                    mOldPrice.setVisibility(View.VISIBLE);
                    mOldPrice.setText(context.getString(R.string.old_price, FinanceUtil.formatWithScale(item.getFromMoney())));
                } else {
                    mOldPrice.setVisibility(View.GONE);
                }
            }
        }
    }
}
