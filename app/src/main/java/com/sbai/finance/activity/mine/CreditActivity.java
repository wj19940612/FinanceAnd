package com.sbai.finance.activity.mine;

import android.content.Intent;
import android.os.Bundle;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserIdentityCardInfo;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
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

        updateUserCreditStatus();
        requestUserCreditApproveStatus();

    }

    private void updateUserCreditStatus() {
        switch (LocalUser.getUser().getUserInfo().getCertificationStatus()) {
            case UserInfo.CREDIT_IS_ALREADY_APPROVE:
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
                .setCallback(new Callback2D<Resp<UserIdentityCardInfo>, UserIdentityCardInfo>(false) {
                    @Override
                    protected void onRespSuccessData(UserIdentityCardInfo data) {
                        UserInfo userInfo = LocalUser.getUser().getUserInfo();
                        userInfo.setCertificationStatus(data.getStatus());
                        LocalUser.getUser().setUserInfo(userInfo);
                        updateUserCreditStatus();
                    }
                })
                .fireSync();
    }

    @OnClick(R.id.credit)
    public void onViewClicked() {
        Launcher.with(getActivity(), CreditApproveActivity.class).executeForResult(REQ_CODE_CREDIT_APPROVE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_CREDIT_APPROVE && resultCode == RESULT_OK) {
            updateUserCreditStatus();
        }
    }
}
