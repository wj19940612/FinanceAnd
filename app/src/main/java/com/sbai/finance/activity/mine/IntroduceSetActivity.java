package com.sbai.finance.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.KeyBoardUtils;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017\11\20 0020.
 */

public class IntroduceSetActivity extends BaseActivity {
    @BindView(R.id.introduce)
    EditText mIntroduce;
    @BindView(R.id.wordsNumber)
    TextView mWordsNumber;
    @BindView(R.id.publish)
    TextView mPublish;

    private String localPrief;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_introduce);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData(){
        localPrief = getIntent().getStringExtra(ExtraKeys.PROFILE_INTRODUCE);
    }

    private void initView() {
        if(localPrief!=null){
            mIntroduce.setText(localPrief);
        }
        if (localPrief.length() > 40) {
            mPublish.setEnabled(false);
            mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.redAssist));
        } else {
            mPublish.setEnabled(true);
            mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
        }
        mWordsNumber.setText(getString(R.string.words_number2,localPrief.length()));
        mIntroduce.setFocusable(true);
        mIntroduce.addTextChangedListener(mValidationWatcher);
        mIntroduce.requestFocus();
        mIntroduce.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyBoardUtils.openKeyBoard(mIntroduce);
            }
        }, 200);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(mIntroduce.getText().toString().trim())) {
                mPublish.setEnabled(false);
                mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
            } else if (mIntroduce.getText().length() > 40) {
                mPublish.setEnabled(false);
                mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.redAssist));
            } else {
                mPublish.setEnabled(true);
                mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
            }
            mWordsNumber.setText(getString(R.string.words_number2, mIntroduce.getText().length()));
        }
    };

    private void publishIntroduce() {
        if (TextUtils.isEmpty(mIntroduce.getText())) return;
        Client.modifyProfileIntroduction(mIntroduce.getText().toString())
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            ToastUtil.show(R.string.publish_success);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fireFree();
    }


    @OnClick({R.id.introduce, R.id.wordsNumber, R.id.publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.publish:
                publishIntroduce();
                break;
        }
    }
}
