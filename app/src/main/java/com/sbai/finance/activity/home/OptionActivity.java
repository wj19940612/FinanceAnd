package com.sbai.finance.activity.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.home.ForeignFutureFragment;
import com.sbai.finance.model.FutureHq;
import com.sbai.finance.view.MyListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-04-18.
 */

public class OptionActivity extends BaseActivity {
	@BindView(R.id.listView)
	MyListView mListView;
	@BindView(R.id.empty)
	TextView mEmpty;

	private ForeignFutureFragment.FutureListAdapter mFutureListAdapter;
	private List<FutureHq> mListHq;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		ButterKnife.bind(this);
		initView();
	}

	private void initView() {
		mFutureListAdapter = new ForeignFutureFragment.FutureListAdapter(this);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mFutureListAdapter);
		mListView.setDelButtonClickListener(new MyListView.DelButtonClickListener() {
			@Override
			public void clickHappend(int position) {
				mFutureListAdapter.remove(mFutureListAdapter.getItem(position));
			}
		});
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateOptionInfo();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	private void updateOptionInfo() {
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

}
