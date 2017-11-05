package com.sbai.finance.activity.arena;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.DialogBaseActivity;
import com.sbai.finance.model.arena.ArenaActivityAndUserStatus;
import com.sbai.finance.model.arena.ArenaVirtualAwardName;
import com.sbai.finance.model.arena.UserGameInfo;
import com.sbai.finance.net.API;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.utils.ValidityDecideUtil;
import com.sbai.finance.view.RequestProgress;
import com.sbai.httplib.ApiIndeterminate;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ArenaVirtualAwardExchangeActivity extends DialogBaseActivity {

    private static final int REQ_CODE_CHOOSE_AWARD = 26455;
    @BindView(R.id.dialogDelete)
    ImageView mDialogDelete;
    @BindView(R.id.gameNumber)
    EditText mGameNumber;
    @BindView(R.id.skin)
    TextView mSkin;
    @BindView(R.id.gameNickName)
    EditText mGameNickName;
    @BindView(R.id.gameArea)
    EditText mGameArea;
    @BindView(R.id.commit)
    TextView mCommit;
    private RequestProgress mRequestProgress;


    private int mAwardId;
    private ArenaVirtualAwardName.VirtualAwardName mSelectVirtualAwardName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_virtual_award_exchange);
        ButterKnife.bind(this);
        mAwardId = getIntent().getIntExtra(ExtraKeys.ARENA_EXCHANGE_AWARD_ID, -1);

        mRequestProgress = new RequestProgress(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                API.cancel(TAG);
            }
        });

        mGameNumber.addTextChangedListener(mValidationWatcher);
        mGameNickName.addTextChangedListener(mValidationWatcher);
        mGameArea.addTextChangedListener(mValidationWatcher);
        mSkin.addTextChangedListener(mValidationWatcher);
        mGameArea.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (ValidityDecideUtil.isGameAreaLegal(source)) {
                    return source;
                }
                return "";
            }
        }, new InputFilter.LengthFilter(10)});
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean commitBtnEnable = checkoutCommitBtnEnable();
            if (mCommit.isEnabled() != commitBtnEnable) {
                mCommit.setEnabled(commitBtnEnable);
            }
        }
    };

    private boolean checkoutCommitBtnEnable() {
        String gameArena = mGameArea.getText().toString();
        String skin = mSkin.getText().toString();
        String gameNickName = mGameNickName.getText().toString();
        String gameNumber = mGameNumber.getText().toString();
        return !TextUtils.isEmpty(gameArena)
                && !TextUtils.isEmpty(skin)
                && !TextUtils.isEmpty(gameNickName)
                && !TextUtils.isEmpty(gameNumber);
    }

    @OnClick({R.id.dialogDelete, R.id.skin, R.id.commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialogDelete:
                finish();
                break;
            case R.id.skin:
                requestArenaVirtualAward();

                break;
            case R.id.commit:
                commitUserVirtualAwardInfo();
                break;
        }
    }

    private void commitUserVirtualAwardInfo() {
        String gameArena = mGameArea.getText().toString();
        String skin = mSkin.getText().toString();
        String gameNickName = mGameNickName.getText().toString();
        String gameNumber = mGameNumber.getText().toString();
        UserGameInfo userGameInfo = new UserGameInfo();
        userGameInfo.setGameZone(gameArena);
        userGameInfo.setSkin(skin);
        userGameInfo.setWxqq(gameNumber);
        userGameInfo.setGameNickName(gameNickName);
        String json = new Gson().toJson(userGameInfo);

        Client.commitUserExchangeInfo(mAwardId, ArenaActivityAndUserStatus.DEFAULT_ACTIVITY_CODE
                , json)
                .setTag(TAG)
                .setIndeterminate(new ApiIndeterminate() {
                    @Override
                    public void onHttpUiShow(String tag) {
                        if (mRequestProgress != null) {
                            mRequestProgress.show(ArenaVirtualAwardExchangeActivity.this);
                        }
                    }

                    @Override
                    public void onHttpUiDismiss(String tag) {
                        if (mRequestProgress != null) {
                            mRequestProgress.dismiss();
                        }
                    }
                })
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        ToastUtil.show(resp.getMsg());
                        setResult(RESULT_OK);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                })
                .fireFree();
    }

    private void requestArenaVirtualAward() {
        Client.requestArenaVirtualAward()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ArenaVirtualAwardName>, ArenaVirtualAwardName>() {
                    @Override
                    protected void onRespSuccessData(ArenaVirtualAwardName data) {
                        if (!TextUtils.isEmpty(data.getPvp())) {
                            Launcher.with(getActivity(), ArenaVirtualAwardNameActivity.class)
                                    .putExtra(ExtraKeys.ARENA_VIRTUAL_AWARD_NAME, data)
                                    .putExtra(ArenaVirtualAwardNameActivity.CHOOSE_AWARD, mSelectVirtualAwardName)
                                    .executeForResult(REQ_CODE_CHOOSE_AWARD);
                        }
                    }
                })
                .fireFree();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_CHOOSE_AWARD:
                    ArenaVirtualAwardName.VirtualAwardName result = data.getParcelableExtra(ArenaVirtualAwardNameActivity.CHOOSE_AWARD);
                    if (result != null) {
                        mSelectVirtualAwardName = result;
                        mSkin.setText(mSelectVirtualAwardName.getAwardName());
                    }
                    break;
            }
        }
    }
}
