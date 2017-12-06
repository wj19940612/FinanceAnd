package com.sbai.finance.activity.mine.fund;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.R;
import com.sbai.finance.activity.mine.setting.UpdateSecurityPassActivity;
import com.sbai.finance.fragment.MineFragment;
import com.sbai.finance.fragment.dialog.InputSafetyPassDialogFragment;
import com.sbai.finance.model.fund.AliPayOrderInfo;
import com.sbai.finance.model.fund.UsableRechargeWay;
import com.sbai.finance.model.fund.VirtualProductInfo;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AliPayHelper;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.OnItemClickListener;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.SmartDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.R.id.position;

/**
 * 元宝 积分等虚拟产品兑换界面
 */

public class VirtualProductExchangeActivity extends RechargeActivity {

    private VirtualProductAdapter mVirtualProductAdapter;

    //被选中的model
    private VirtualProductInfo mSelectVirtualProductInfo;
    private int mSelectProductPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        requestVirtualProductList();
    }

    private void initView() {
        mRecharge.setEnabled(true);
        mSplit.setBackgroundColor(Color.WHITE);
        mCrashRecharge.setVisibility(View.GONE);
        mVirtualProductRecycleView.setVisibility(View.VISIBLE);
        mVirtualProductAdapter = new VirtualProductAdapter(getActivity(), new ArrayList<VirtualProductInfo>());
        mVirtualProductRecycleView.setAdapter(mVirtualProductAdapter);
        mVirtualProductRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mVirtualProductAdapter.setOnItemClickListener(new OnItemClickListener<VirtualProductInfo>() {
            @Override
            public void onItemClick(VirtualProductInfo virtualProductInfo, int position) {
                if (mUserSelectRechargeWay == null) return;
                if (mSelectVirtualProductInfo != null) {
                    mSelectVirtualProductInfo.setSelect(false);
                    mVirtualProductAdapter.notifyItemChanged(mSelectProductPosition, mSelectVirtualProductInfo);
                }
                if (mUserSelectRechargeWay.isBalancePay()) {
                    changeUserFundEnoughStatus(virtualProductInfo);
                } else if (mUserSelectRechargeWay.isIngotPay()) {
                    changeUserFundEnoughStatus(virtualProductInfo);
                }
                changeUserSelectProduct(virtualProductInfo, position);
                changeOtherPayStatus(virtualProductInfo);
            }
        });
    }

    private void changeOtherPayStatus(VirtualProductInfo virtualProductInfo) {
        if (mOtherRechargeWay == null || virtualProductInfo == null) return;
        if (virtualProductInfo.getFromMoney() > mUserFundCount) {
            mOtherRechargeWay.setBalanceIsEnough(false);
            mRechargeWayAdapter.notifyItemChanged(mOtherRechargeWayPosition, mOtherRechargeWay);
        } else {
            mOtherRechargeWay.setBalanceIsEnough(true);
            mRechargeWayAdapter.notifyItemChanged(mOtherRechargeWayPosition, mOtherRechargeWay);
        }
        if (mUserSelectRechargeWay.isIngotOrBalancePay()) {
            mRecharge.setEnabled(mUserSelectRechargeWay.isBalanceIsEnough());
        }

    }

    private void changeConfirmBtnStatus() {
        if (mUserSelectRechargeWay != null && mUserSelectRechargeWay.isIngotOrBalancePay()) {
            mRecharge.setEnabled(mUserSelectRechargeWay.isBalanceIsEnough());
        } else {
            mRecharge.setEnabled(true);
        }

        String text = getString(R.string.confirm_money);
        if (mSelectVirtualProductInfo != null) {
            if (mRechargeType == AccountFundDetail.TYPE_INGOT) {
                text = getString(R.string.confirm_payment_money, FinanceUtil.formatWithScale(mSelectVirtualProductInfo.getFromMoney()));
            } else {
                text = getString(R.string.confirm_payment_ingot, FinanceUtil.formatWithScale(mSelectVirtualProductInfo.getFromMoney(), 0));
            }
        }

        mRecharge.setText(text);
    }

    private void changeUserFundEnoughStatus(VirtualProductInfo virtualProductInfo) {
        if (mUserSelectRechargeWay == null || virtualProductInfo == null) return;
        if (virtualProductInfo.getFromMoney() > mUserFundCount) {
            mUserSelectRechargeWay.setBalanceIsEnough(false);
            mRechargeWayAdapter.notifyItemChanged(mHistorySelectPayWayPosition, mUserSelectRechargeWay);
        } else {
            mUserSelectRechargeWay.setBalanceIsEnough(true);
            mRechargeWayAdapter.notifyItemChanged(mHistorySelectPayWayPosition, mUserSelectRechargeWay);
        }
    }

    private void changeUserSelectProduct(VirtualProductInfo virtualProductInfo, int position) {
        virtualProductInfo.setSelect(true);
        mSelectVirtualProductInfo = virtualProductInfo;
        mSelectProductPosition = position;
        mVirtualProductAdapter.notifyItemChanged(position, virtualProductInfo);
        changeConfirmBtnStatus();
        changeOtherPayStatus(mSelectVirtualProductInfo);
    }

    private void initData() {
        if (mRechargeType == AccountFundDetail.TYPE_INGOT) {
            mTitleBar.setTitle(R.string.ingot_recharge);
        } else {
            mTitleBar.setTitle(R.string.score_recharge);
        }
    }

    private void requestVirtualProductList() {
        Client.getExchangeProduct()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<VirtualProductInfo>>, List<VirtualProductInfo>>() {
                    @Override
                    protected void onRespSuccessData(List<VirtualProductInfo> data) {
                        if (!data.isEmpty()) {
                            ListIterator<VirtualProductInfo> virtualProductModelListIterator = data.listIterator();
                            while (virtualProductModelListIterator.hasNext()) {
                                VirtualProductInfo virtualProductInfo = virtualProductModelListIterator.next();
                                if (mRechargeType == AccountFundDetail.TYPE_INGOT) {
                                    if (!virtualProductInfo.isIngot()) {
                                        virtualProductModelListIterator.remove();
                                    }
                                } else if (mRechargeType == AccountFundDetail.TYPE_SCORE) {
                                    if (virtualProductInfo.isIngot()) {
                                        virtualProductModelListIterator.remove();
                                    }
                                }
                            }
                            mVirtualProductAdapter.addAll(data);
                            if (!data.isEmpty()) {
                                mSelectVirtualProductInfo = data.get(0);
                                changeUserSelectProduct(mSelectVirtualProductInfo, 0);
                            }
                        }
                    }

                })
                .fireFree();

    }

    @Override
    protected void updateVirtualProductSelect(UsableRechargeWay nowSelectRechargeWay, int nowSelectPosition,
                                              UsableRechargeWay historySelectUsableRechargeWay, int historySelectPosition) {
//        if (!nowSelectRechargeWay.isIngotOrBalancePay()) {
//            historySelectUsableRechargeWay.setBalanceIsEnough(true);
//        }
        if (!nowSelectRechargeWay.isBalanceIsEnough()) {
            return;
        }
        historySelectUsableRechargeWay.setSelectPayWay(false);
        mRechargeWayAdapter.notifyItemChanged(historySelectPosition, historySelectUsableRechargeWay);

        nowSelectRechargeWay.setSelectPayWay(true);
        mRechargeWayAdapter.notifyItemChanged(nowSelectPosition, position);
        mHistorySelectPayWayPosition = nowSelectPosition;
        mUserSelectRechargeWay = nowSelectRechargeWay;

        if (nowSelectRechargeWay.isBalancePay()) {
            changeUserFundEnoughStatus(mSelectVirtualProductInfo);
        } else if (historySelectUsableRechargeWay.isIngotPay()) {
            changeUserFundEnoughStatus(mSelectVirtualProductInfo);
        }
        changeConfirmBtnStatus();
    }

    @Override
    protected void requestAliPayProductInfo(String money) {
        if (mSelectVirtualProductInfo == null) return;
        Client.requestAliPayOrderInfo(String.valueOf(mSelectVirtualProductInfo.getFromMoney()),
                AliPayHelper.PAY_INGOT,
                mSelectVirtualProductInfo != null ? mSelectVirtualProductInfo.getId() : -1)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<AliPayOrderInfo>, AliPayOrderInfo>() {
                    @Override
                    protected void onRespSuccessData(AliPayOrderInfo data) {
                        new AliPayHelper(VirtualProductExchangeActivity.this)
                                .setToastFailMsg("支付失败")
                                .aliPay(data.getOrderString());
                    }
                })
                .setTag(TAG)
                .fire();
    }

    @Override
    protected void confirmOtherPay(UsableRechargeWay userSelectRechargeWay) {
        if (userSelectRechargeWay != null) {
            if (userSelectRechargeWay.isIngotPay()) {
                umengEventCount(UmengCountEventId.WALLET_BUY_INGOT);
            } else {
                umengEventCount(UmengCountEventId.WALLET_EXCHANGE_INTEGRAL);
            }
            requestUserHasSafetyPassword(mSelectVirtualProductInfo);
        }

    }

    private void requestUserHasSafetyPassword(final VirtualProductInfo virtualProductInfo) {

        Client.getUserHasPassWord()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                    @Override
                    protected void onRespSuccessData(Boolean data) {
                        if (!data) {
                            showAddSafetyPassDialog();
                        } else {
                            showInputSafetyPassDialog(virtualProductInfo);
                        }
                    }
                })
                .fire();

    }

    private void showAddSafetyPassDialog() {
        SmartDialog.with(getActivity(), getString(R.string.is_not_set_safety_pass))
                .setPositive(R.string.go_to_set, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Launcher.with(getActivity(), UpdateSecurityPassActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, false)
                                .executeForResult(MineFragment.REQ_CODE_OPEN_WALLET_SET_SAFETY_PASSWORD);
                    }
                }).show();
    }

    private void showInputSafetyPassDialog(final VirtualProductInfo item) {
        String content = item.isIngot() ? getString(R.string.ingot_number, FinanceUtil.formatWithScale(item.getToMoney(), 0)) :
                getString(R.string.integrate_number, FinanceUtil.formatWithScale(item.getToMoney(), 0));
        String hintText = getString(R.string.recharge);
        InputSafetyPassDialogFragment.newInstance(content, hintText)
                .setOnPasswordListener(new InputSafetyPassDialogFragment.OnPasswordListener() {
                    @Override
                    public void onPassWord(String passWord, InputSafetyPassDialogFragment dialogFragment) {
                        exchangeVirtualProduct(item, passWord, dialogFragment);
                    }


                }).show(getSupportFragmentManager());
    }

    private void exchangeVirtualProduct(VirtualProductInfo item, String passWord, final InputSafetyPassDialogFragment dialogFragment) {
        Client.exchange(item.getId(), passWord, item.getFromMoney(), item.getToMoney())
                .setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        dialogFragment.dismissAllowingStateLoss();
                        ToastUtil.show(resp.getMsg());
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Resp.CODE_EXCHANGE_FUND_IS_NOT_ENOUGH) {
                            ToastUtil.show(failedResp.getMsg());
                            dialogFragment.dismissAllowingStateLoss();
                            requestUserFund();
                            mRecharge.setEnabled(false);
                        } else {
                            ToastUtil.show(failedResp.getMsg());
                            dialogFragment.clearPassword();
                            if (failedResp.getCode() == Resp.CODE_EXCHANGE_ITEM_IS_GONE
                                    || failedResp.getCode() == Resp.CODE_EXCHANGE_ITEM_IS_MODIFIED) {
                                requestVirtualProductList();
                            }
                        }
                    }
                }).fire();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MineFragment.REQ_CODE_OPEN_WALLET_SET_SAFETY_PASSWORD:
                    //暂时先不管，回调太多了。
                    break;
            }
        }

    }

    static class VirtualProductAdapter extends RecyclerView.Adapter<VirtualProductAdapter.ViewHolder> {

        private Context mContext;
        private List<VirtualProductInfo> mVirtualProductModelList;
        private OnItemClickListener mOnItemClickListener;

        public VirtualProductAdapter(Context context, List<VirtualProductInfo> virtualProductModelList) {
            this.mContext = context;
            this.mVirtualProductModelList = virtualProductModelList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_virtual_product, parent, false);
            return new ViewHolder(view);
        }

        public void addAll(List<VirtualProductInfo> virtualProductModelList) {
            mVirtualProductModelList.clear();
            this.mVirtualProductModelList.addAll(virtualProductModelList);
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(OnItemClickListener<VirtualProductInfo> onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mVirtualProductModelList.get(position),
                    position, mOnItemClickListener, mContext);
        }

        @Override
        public int getItemCount() {
            return mVirtualProductModelList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.product)
            AppCompatTextView mProduct;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final VirtualProductInfo virtualProductModel, final int position,
                                         final OnItemClickListener onItemClickListener, Context context) {
                if (virtualProductModel == null) return;
                if (virtualProductModel.isIngot()) {
                    mProduct.setText(context.getString(R.string.number_ingot, FinanceUtil.formatWithScale(virtualProductModel.getToMoney(), 0)));
                } else {
                    mProduct.setText(FinanceUtil.formatWithScale(virtualProductModel.getToMoney(), 0));
                }
                mProduct.setSelected(virtualProductModel.isSelect());
                mProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(virtualProductModel, position);
                        }
                    }
                });
            }
        }
    }
}
