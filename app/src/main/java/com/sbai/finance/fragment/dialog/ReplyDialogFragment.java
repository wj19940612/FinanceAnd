package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.mine.EconomicCircleNewsActivity;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by lixiaokuan0819 on 2017/8/3.
 */

public class ReplyDialogFragment extends BaseDialogFragment{
	@BindView(R.id.economic_circle_news_list)
	TextView mEconomicCircleNewsList;
	@BindView(R.id.cancel)
	TextView mCancel;

	Unbinder unbinder;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_fragment_ec_circle_news, container, false);
		unbinder = ButterKnife.bind(this, view);
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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}
}
