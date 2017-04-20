package com.sbai.finance.fragment.future;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.FutureHq;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017-04-17.
 */

public class ChinaFutureFragment extends BaseFragment{
	@BindView(R.id.rate)
	TextView mRate;
	@BindView(R.id.listView)
	ListView mListView;
	@BindView(R.id.empty)
	TextView mEmpty;
	private Unbinder unbinder;
	private ForeignFutureFragment.FutureListAdapter mFutureListAdapter;
	private List<FutureHq> mListHq;
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_future_china, container, false);
		unbinder = ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mFutureListAdapter = new ForeignFutureFragment.FutureListAdapter(getActivity());
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mFutureListAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		updateFutureChinaHqList();
	}

	private void updateFutureChinaHqList() {
		if (mListHq==null){
			mListHq = new ArrayList<>();
		}
		for (int i =0;i<10;i++){
			FutureHq futureHq = new FutureHq();
			futureHq.setCodeName("美原油");
			futureHq.setInstrumentId("CL1709");
			futureHq.setLastPrice(66.66);
			futureHq.setUpDropSpeed(25.00);
			mListHq.add(futureHq);
		}
		mFutureListAdapter.clear();
		mFutureListAdapter.addAll(mListHq);
		mFutureListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}
}
