package com.sbai.finance.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.mine.UserIdentityCardInfo;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreditActivity extends BaseActivity {

    private static final int REQ_CODE_CREDIT_APPROVE = 298;

    @BindView(R.id.credit)
    IconTextRow mCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
        ButterKnife.bind(this);

        mCredit.setSubText(R.string.unauthorized);
        requestUserCreditApproveStatus();

    }

    private void updateUserCreditStatus(Integer status) {
        switch (status) {
            case UserInfo.CREDIT_IS_ALREADY_APPROVE:
                mCredit.setSubTextColor(ContextCompat.getColor(this, R.color.blueAssist));
                mCredit.setSubText(R.string.authenticated);
                break;
            case UserInfo.CREDIT_IS_APPROVE_ING:
                mCredit.setSubText(R.string.is_auditing);
                break;
            default:
                mCredit.setSubText(R.string.unauthorized);
                break;
        }
    }

    private void requestUserCreditApproveStatus() {
        Client.getUserCreditApproveStatus()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<UserIdentityCardInfo>, UserIdentityCardInfo>() {
                    @Override
                    protected void onRespSuccessData(UserIdentityCardInfo data) {
                        if (data.getStatus() != null) {
                            updateUserCreditStatus(data.getStatus());
                        }
                    }

                    @Override
                    protected boolean onErrorToast() {
                        return false;
                    }
                })
                .fireFree();
    }

    @OnClick(R.id.credit)
    public void onViewClicked() {
        umengEventCount(UmengCountEventIdUtils.ME_CERTIFICATION);
        Launcher.with(getActivity(), CreditApproveActivity.class).executeForResult(REQ_CODE_CREDIT_APPROVE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_CREDIT_APPROVE && resultCode == RESULT_OK) {
            updateUserCreditStatus(0);
        }
    }
}
