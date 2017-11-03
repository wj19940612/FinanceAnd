package com.sbai.finance.activity.arena;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.DialogBaseActivity;
import com.sbai.finance.model.arena.ArenaVirtualAwardName;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.utils.ValidityDecideUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ArenaVirtualAwardExchangeActivity extends DialogBaseActivity {

    private static final int REQ_CODE_CHOOSE_AWARD = 26455;

    @BindView(R.id.dialogDelete)
    ImageView mDialogDelete;
    @BindView(R.id.gameNumber)
    AppCompatEditText mGameNumber;
    @BindView(R.id.skin)
    TextView mSkin;
    @BindView(R.id.phone)
    AppCompatEditText mPhone;
    @BindView(R.id.gameArea)
    AppCompatEditText mGameArea;
    @BindView(R.id.commit)
    TextView mCommit;

    private int mAwardId;
    private ArenaVirtualAwardName.VirtualAwardName mSelectVirtualAwardName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_virtual_award_exchange);
        ButterKnife.bind(this);
        mAwardId = getIntent().getIntExtra(ExtraKeys.ARENA_EXCHANGE_AWARD_ID, -1);

        mGameNumber.addTextChangedListener(mValidationWatcher);
        mPhone.addTextChangedListener(mValidationWatcher);
        mGameArea.addTextChangedListener(mValidationWatcher);
        mSkin.addTextChangedListener(mValidationWatcher);
        mGameArea.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (ValidityDecideUtil.isGameAreaLegal(source)) {
                    return source;
                }
                return null;
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
        String phone = mPhone.getText().toString();
        String gameNumber = mGameNumber.getText().toString();
        return !TextUtils.isEmpty(gameArena)
                && !TextUtils.isEmpty(skin)
                && !TextUtils.isEmpty(phone)
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

                break;
        }
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
}
