package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.klinebattle.BattleKlineMyRecord;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.StrUtil;

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
    private BattleKlineMyRecord mBattleKlineMyRecord;

    public static KlineBattleRecordFragment newInstance(BattleKlineMyRecord battleKlineMyRecord) {
        KlineBattleRecordFragment startDialogFragment = new KlineBattleRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("record", battleKlineMyRecord);
        startDialogFragment.setArguments(bundle);
        return startDialogFragment;
    }

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
        if (getArguments() != null) {
            mBattleKlineMyRecord = getArguments().getParcelable("record");
        }
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
        if (mBattleKlineMyRecord != null) {
            updateMyRecordData(mBattleKlineMyRecord);
        }
    }

    private void updateMyRecordData(BattleKlineMyRecord data) {
        if (data.getUserRank1v1() != null) {
            mOnePkCount.setText(getString(R.string._battle_count, data.getUserRank1v1().getBattle1v1Count()));
            SpannableString str = StrUtil.mergeTextWithRatioColor(data.getUserRank1v1().getWinIngots1v1() + "\n",
                    getString(R.string.win_ingot), 0.7f, ContextCompat.getColor(getActivity(), R.color.unluckyText));
            mOnePkProfit.setText(str);
            SpannableString str1 = StrUtil.mergeTextWithRatioColor(formatWinRate(data.getUserRank1v1().getRankingRate()) + "\n",
                    getString(R.string.win_pk_rate), 0.7f, ContextCompat.getColor(getActivity(), R.color.unluckyText));
            mOnePkRate.setText(str1);
        }
        if (data.getUserRank4v4() != null) {
            mFourPkCount.setText(getString(R.string._battle_count, data.getUserRank4v4().getBattle4v4Count()));
            SpannableString str = StrUtil.mergeTextWithRatioColor(data.getUserRank4v4().getWinIngots4v4() + "\n",
                    getString(R.string.win_ingot), 0.7f, ContextCompat.getColor(getActivity(), R.color.unluckyText));
            mFourPkProfit.setText(str);
            mFirstCount.setText(String.valueOf(data.getUserRank4v4().getOne()));
            mSecondCount.setText(String.valueOf(data.getUserRank4v4().getTwo()));
            mThirdCount.setText(String.valueOf(data.getUserRank4v4().getThree()));
        }
    }

    private String formatWinRate(double winRate) {
        if (winRate == 0) {
            return "0.00%";
        } else {
            return FinanceUtil.formatToPercentage(winRate);
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

    @OnClick({R.id.dialogDelete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialogDelete:
                dismissAllowingStateLoss();
                break;
        }
    }
}
