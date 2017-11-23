package com.sbai.finance.activity.arena;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.DialogBaseActivity;
import com.sbai.finance.model.arena.ArenaActivityAndUserStatus;
import com.sbai.finance.model.arena.UserGameInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ArenaEntityExchangeInfoInputActivity extends DialogBaseActivity {

    @BindView(R.id.dialogDelete)
    ImageView mDialogDelete;
    @BindView(R.id.userName)
    AppCompatEditText mUserName;
    @BindView(R.id.userAddress)
    AppCompatEditText mUserAddress;
    @BindView(R.id.phone)
    AppCompatEditText mPhone;
    @BindView(R.id.commit)
    TextView mCommit;
    private int mAwardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_game_info);
        ButterKnife.bind(this);

        mAwardId = getIntent().getIntExtra(ExtraKeys.ARENA_EXCHANGE_AWARD_ID, -1);
        mUserName.addTextChangedListener(mValidationWatcher);
        mUserAddress.addTextChangedListener(mValidationWatcher);
        mPhone.addTextChangedListener(mValidationWatcher);
    }


    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean commitEnable = checkCommitEnable();
            if (mCommit.isEnabled() != commitEnable) {
                mCommit.setEnabled(commitEnable);
            }
        }
    };

    private boolean checkCommitEnable() {
        String userName = mUserName.getText().toString();
        String address = mUserAddress.getText().toString();
        String phone = mPhone.getText().toString();
        return !TextUtils.isEmpty(userName)
                && !TextUtils.isEmpty(address)
                && !TextUtils.isEmpty(phone);
    }


    @OnClick({R.id.dialogDelete, R.id.commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialogDelete:
                finish();
                break;
            case R.id.commit:
                commitEntityAwardInfo();
                break;
        }
    }

    private void commitEntityAwardInfo() {
        String userName = mUserName.getText().toString();
        String address = mUserAddress.getText().toString();
        String phone = mPhone.getText().toString();
        UserGameInfo userGameInfo = new UserGameInfo();
        userGameInfo.setReceiver(userName);
        userGameInfo.setAddress(address);
        userGameInfo.setPhone(phone);
        String toJson = new Gson().toJson(userGameInfo);
        Log.d(TAG, "commitEntityAwardInfo: " + toJson);
        Client.commitUserExchangeInfo(mAwardId, ArenaActivityAndUserStatus.DEFAULT_ACTIVITY_CODE, toJson)
                .setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {

                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        setResult(RESULT_OK);
                        ToastUtil.show(resp.getMsg());
                        finish();
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                })
                .fireFree();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserName.removeTextChangedListener(mValidationWatcher);
        mUserAddress.removeTextChangedListener(mValidationWatcher);
        mPhone.removeTextChangedListener(mValidationWatcher);
    }
}
