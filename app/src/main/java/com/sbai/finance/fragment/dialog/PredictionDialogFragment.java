package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.trade.PredictionRuleActivity;
import com.sbai.finance.model.Prediction;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Modified by John on 2017/5/10.
 */

public class PredictionDialogFragment extends DialogFragment {


    @BindView(R.id.bullishButton)
    Button mBullishButton;
    @BindView(R.id.bearishButton)
    Button mBearishButton;
    @BindView(R.id.predictionRule)
    TextView mPredictionRule;
    Unbinder unbinder;

    OnPredictButtonListener mOnPredictButtonListener;

    public static PredictionDialogFragment newInstance() {
        return new PredictionDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_prediction, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.bullishButton, R.id.bearishButton, R.id.predictionRule})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bullishButton:
                mOnPredictButtonListener.onBullishButtonClick(Prediction.DIRECTION_LONG);
                this.dismiss();
                break;
            case R.id.bearishButton:
                mOnPredictButtonListener.onBearishButtonClick(Prediction.DIRECTION_SHORT);
                this.dismiss();
                break;
            case R.id.predictionRule:
                Launcher.with(getActivity(), PredictionRuleActivity.class).execute();
                this.dismiss();
                break;
        }
    }

    public void show(FragmentManager manager) {
        this.show(manager, PredictionDialogFragment.class.getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public PredictionDialogFragment setOnPredictButtonListener(OnPredictButtonListener listener) {
        this.mOnPredictButtonListener = listener;
        return this;
    }

    public interface OnPredictButtonListener {
        void onBullishButtonClick(int directionLong);

        void onBearishButtonClick(int directionShort);
    }
}