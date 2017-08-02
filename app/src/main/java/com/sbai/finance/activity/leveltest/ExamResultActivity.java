package com.sbai.finance.activity.leveltest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.model.leveltest.TestResultModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.leveltest.CreditScoreView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExamResultActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.accuracy)
    TextView mAccuracy;
    @BindView(R.id.accuracyHint)
    TextView mAccuracyHint;
    @BindView(R.id.accidence)
    TextView mAccidence;
    @BindView(R.id.primary)
    TextView mPrimary;
    @BindView(R.id.intermediate)
    TextView mIntermediate;
    @BindView(R.id.expert)
    TextView mExpert;
    @BindView(R.id.master)
    TextView mMaster;
    @BindView(R.id.result)
    CardView mResult;
    @BindView(R.id.resultImage)
    CreditScoreView mResultImage;
    @BindView(R.id.going_train)
    TextView mGoingTrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result);
        ButterKnife.bind(this);
        translucentStatusBar();

        requestUserTestResult();
        updateUserResult(null);
    }

    private void requestUserTestResult() {
        Client.requestUserTestResult()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<TestResultModel>, TestResultModel>() {
                    @Override
                    protected void onRespSuccessData(TestResultModel data) {
                        Log.d(TAG, "onRespSuccessData: " + data.toString());
                        updateUserResult(data);
                    }
                })
                .fire();

    }

    private void updateUserResult(TestResultModel data) {

    }

    public void setResultImage(int result) {
        switch (result) {
            case 0:
                mAccidence.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_level_accidence_pass, 0, 0);
                break;
            case 1:
                // TODO: 2017/8/1 缺少初级通过图片
                mPrimary.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_level_primary_not_pass, 0, 0);
                break;
            case 2:
                mIntermediate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_level_intermediate_pass, 0, 0);
                break;
            case 3:
                mExpert.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_level_expert_pass, 0, 0);
                break;
            case 4:
                mMaster.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_level_master_pass, 0, 0);
                break;
        }
    }

    @OnClick(R.id.going_train)
    public void onViewClicked() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
