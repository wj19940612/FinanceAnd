package com.sbai.finance.activity.mine;

import android.os.Bundle;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
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
        switch (LocalUser.getUser().getUserInfo().getStatus()) {
            case UserInfo.CREDIT_IS_ALREADY_APPROVE:
                mCredit.setSubText(R.string.authenticated);
                break;
            case UserInfo.CREDIT_IS_APPROVEING:
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
                .setCallback(new Callback2D<Resp<UserInfo>, UserInfo>() {
                    @Override
                    protected void onRespSuccessData(UserInfo data) {
                        LocalUser.getUser().getUserInfo().setStatus(data.getStatus());
                        updateUserCreditStatus();
                    }
                })
                .fireSync();
    }

    @OnClick(R.id.credit)
    public void onViewClicked() {
        Launcher.with(getActivity(), CreditApproveActivity.class).execute();
    }
}
