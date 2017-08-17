package com.sbai.finance.activity.training;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingQuestion;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.SecurityUtil;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.training.KlineTrainView;
import com.sbai.finance.view.training.TrainHeaderView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * K线概念训练页面
 */

public class KlineTrainActivity extends BaseActivity {

    @BindView(R.id.title)
    TrainHeaderView mTitle;
    @BindView(R.id.index)
    TextView mIndex;
    @BindView(R.id.trainView)
    KlineTrainView mTrainView;
    private Training mTraining;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline_train);
        ButterKnife.bind(this);
        initData(getIntent());
        initView();
        requestTrainContent();
    }

    private void initData(Intent intent) {
        mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);
    }

    private void requestTrainContent() {
        if (mTraining != null) {
            Client.getTrainingContent(mTraining.getId()).setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<String>, TrainingQuestion>() {
                        @Override
                        protected void onRespSuccessData(TrainingQuestion data) {
                            updateTrainContent(data);
                        }

                        @Override
                        protected String onInterceptData(String data) {
                            String ss = SecurityUtil.AESDecrypt(data);
                            return ss;
                        }
                    }).fireFree();
        }

    }

    private void updateTrainContent(TrainingQuestion data) {

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mTrainView.startAppearAnim();
    }

    private void initView() {
        mTitle.setCallback(new TrainHeaderView.Callback() {
            @Override
            public void onBackClick() {
                showCloseDialog();
            }

            @Override
            public void onHowPlayClick() {

            }

            @Override
            public void onEndOfTimer() {

            }
        });
    }

    private void showCloseDialog() {
        SmartDialog.single(getActivity(), getString(R.string.exit_train_will_not_save_train_record))
                .setTitle(getString(R.string.is_sure_exit_train))
                .setNegative(R.string.exit_train, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setPositive(R.string.continue_train, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @OnClick(R.id.trainView)
    public void onViewClicked() {

    }
}
