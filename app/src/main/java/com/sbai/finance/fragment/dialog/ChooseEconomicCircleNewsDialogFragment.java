package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.R;
import com.sbai.finance.activity.mine.EconomicCircleNewsActivity;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ${wangJie} on 2017/6/8.
 */

public class ChooseEconomicCircleNewsDialogFragment extends BaseDialogFragment {

    @BindView(R.id.economic_circle_news_list)
    AppCompatTextView mEconomicCircleNewsList;
    @BindView(R.id.cancel)
    AppCompatTextView mCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_ec_circle_news, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }


    @OnClick({R.id.economic_circle_news_list, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.economic_circle_news_list:
                Launcher.with(getActivity(), EconomicCircleNewsActivity.class).execute();
                dismissAllowingStateLoss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }
}
