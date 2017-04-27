package com.sbai.finance.activity.trade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.fragment.PredictionFragment.PREDICT_DIRECTION;


public class PublishOpinionActivity extends BaseActivity {

    public static final String REFRESH_POINT = "refresh_point";

    @BindView(R.id.opinionType)
    TextView mOpinionType;
    @BindView(R.id.opinionContent)
    EditText mOpinionContent;
    @BindView(R.id.submitButton)
    Button mSubmitButton;

    Variety mVariety;
    int mDirection = -1 ;

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
        mDirection = getIntent().getIntExtra(PREDICT_DIRECTION, -1);
    }

    private void initViews() {
        // TODO: 2017/4/27 根据涨和跌初始化view
        if (mDirection == 1){

        }else {

        }
    }

    @OnClick(R.id.submitButton)
    public void onClick(View view) {
        saveViewPoint();
    }

    private void saveViewPoint() {
        String content = mOpinionContent.getText().toString().trim();
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
