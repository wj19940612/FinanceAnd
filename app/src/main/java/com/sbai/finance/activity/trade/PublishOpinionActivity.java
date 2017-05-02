package com.sbai.finance.activity.trade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.PredictModel;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PublishOpinionActivity extends BaseActivity {

    public static final String REFRESH_POINT = "refresh_point";

    @BindView(R.id.opinionType)
    ImageView mOpinionType;
    @BindView(R.id.opinionContent)
    EditText mOpinionContent;
    @BindView(R.id.submitButton)
    Button mSubmitButton;

    PredictModel mPredict;
    Variety mVariety;
    int mDirection = -1;
    int mCalcuId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_opinion);
        ButterKnife.bind(this);

        initData();
        initViews();
    }

    private void initData() {
        mVariety = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
        mPredict = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD_1);
        mDirection = mPredict.getDirection();
        mCalcuId = mPredict.getCalcuId();
    }

    private void initViews() {
        mOpinionContent.addTextChangedListener(mOptionContentWatcher);
        if (mDirection == 1) {
            mOpinionType.setImageResource(R.drawable.ic_opinion_up);
        } else {
            mOpinionType.setImageResource(R.drawable.ic_opinion_down);
        }
    }

    private ValidationWatcher mOptionContentWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0) {
                mSubmitButton.setEnabled(true);
            } else {
                mSubmitButton.setEnabled(false);
            }
        }
    };

    @OnClick(R.id.submitButton)
    public void onClick(View view) {
        saveViewPoint();
    }

    private void saveViewPoint() {
        String content = mOpinionContent.getText().toString().trim();
        if (mCalcuId != -1) {
            Client.saveViewPoint(mVariety.getBigVarietyTypeCode(), mCalcuId,
                    content, mDirection, mVariety.getVarietyId(), mVariety.getVarietyType())
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback<Resp<JsonObject>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonObject> resp) {
                            if (resp.isSuccess()) {
                                Intent intent = new Intent(REFRESH_POINT);
                                LocalBroadcastManager.getInstance(PublishOpinionActivity.this)
                                        .sendBroadcast(intent);
                                finish();
                            }
                        }
                    })
                    .fire();
        } else {
            Client.saveViewPoint(mVariety.getBigVarietyTypeCode(),
                    content, mDirection, mVariety.getVarietyId(), mVariety.getVarietyType())
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback<Resp<JsonObject>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonObject> resp) {
                            if (resp.isSuccess()) {
                                Intent intent = new Intent(REFRESH_POINT);
                                LocalBroadcastManager.getInstance(PublishOpinionActivity.this)
                                        .sendBroadcast(intent);
                                finish();
                            }
                        }
                    })
                    .fire();
        }
    }


}
