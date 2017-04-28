package com.sbai.finance.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.trade.PredictionRuleActivity;
import com.sbai.finance.activity.trade.PublishOpinionActivity;
import com.sbai.finance.fragment.dialog.UploadUserImageDialogFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by lixiaokuan0819 on 2017/4/24.
 */

public class PredictionFragment extends DialogFragment {

    public static final int TREND_UP = 1;
    public static final int TREND_DOWN = 0;

    public static final int REQ_CODE_PREDICT = 300;

    public static final String PREDICT_DIRECTION = "predict_direction";

    @BindView(R.id.bullishButton)
    TextView mBullishButton;
    @BindView(R.id.bearishButton)
    TextView mBearishButton;
    @BindView(R.id.predictionRule)
    TextView mPredictionRule;
    Unbinder unbinder;

    Variety mVariety;

    public static PredictionFragment newInstance(Bundle args) {
        PredictionFragment fragment = new PredictionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_prediction, container, false);
        unbinder = ButterKnife.bind(this, view);
        mVariety = getArguments().getParcelable(Launcher.EX_PAYLOAD);
        return view;
    }

    @OnClick({R.id.bullishButton, R.id.bearishButton, R.id.predictionRule})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bullishButton:
                Launcher.with(getActivity(), PublishOpinionActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mVariety)
                        .putExtra(PREDICT_DIRECTION, TREND_UP)
                        .execute();
                this.dismiss();
                break;
            case R.id.bearishButton:
                Launcher.with(getActivity(), PublishOpinionActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mVariety)
                        .putExtra(PREDICT_DIRECTION, TREND_DOWN)
                        .execute();
                this.dismiss();
                break;
            case R.id.predictionRule:
                Launcher.with(getActivity(), PredictionRuleActivity.class).execute();
                this.dismiss();
                break;
        }
    }

    public void show(FragmentManager manager) {
        this.show(manager, UploadUserImageDialogFragment.class.getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
