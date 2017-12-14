package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 猜K 我的战绩
 */

public class KlineBattleRecordFragment extends DialogFragment {
    @BindView(R.id.dialogDelete)
    AppCompatImageView mDialogDelete;
    @BindView(R.id.onePkCount)
    TextView mOnePkCount;
    @BindView(R.id.onePkProfit)
    TextView mOnePkProfit;
    @BindView(R.id.onePkRate)
    TextView mOnePkRate;
    @BindView(R.id.fourPkCount)
    TextView mFourPkCount;
    @BindView(R.id.fourPkProfit)
    TextView mFourPkProfit;
    @BindView(R.id.firstCount)
    TextView mFirstCount;
    @BindView(R.id.secondCount)
    TextView mSecondCount;
    @BindView(R.id.thirdCount)
    TextView mThirdCount;
    Unbinder mBinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_kline_battle_record, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.BaseDialog);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels * 0.8), WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    public void show(FragmentManager manager) {
        if (!isAdded()) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, this.getClass().getSimpleName());
            ft.commitAllowingStateLoss();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    @OnClick({R.id.dialogDelete, R.id.button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialogDelete:
                dismissAllowingStateLoss();
                break;
        }
    }
}
