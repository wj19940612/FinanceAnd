package com.sbai.finance.fragment.anchor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.miss.MissSwitcherModel;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.view.MissRadioViewSwitcher;
import com.sbai.finance.view.radio.MissRadioLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/12/28.
 * 米圈推荐页面
 */

public class RecommendFragment extends BaseFragment {

    @BindView(R.id.missRadioLayout)
    MissRadioLayout mMissRadioLayout;
    @BindView(R.id.missRadioViewSwitcher)
    MissRadioViewSwitcher mMissRadioViewSwitcher;
    @BindView(R.id.point)
    FrameLayout mPoint;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recomend, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestMissSwitcherList();
        requestRadioList();
    }

    private void requestMissSwitcherList() {
        Client.requestMissSwitcherList()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<MissSwitcherModel>>, List<MissSwitcherModel>>() {
                    @Override
                    protected void onRespSuccessData(List<MissSwitcherModel> data) {
                        mMissRadioViewSwitcher.setSwitcherData(data);
                    }
                })
                .fireFree();
    }

    public void requestRadioList() {
        Client.requestRadioList()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Radio>>, List<Radio>>() {
                    @Override
                    protected void onRespSuccessData(List<Radio> data) {
                        mMissRadioLayout.setMissRadioList(data);
                    }
                })
                .fireFree();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }
}
