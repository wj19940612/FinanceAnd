package com.sbai.finance.activity.stock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.future.FutureFragment;
import com.sbai.finance.model.FutureHq;
import com.sbai.finance.model.VarietyModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-04-20.
 */

public class SearchStockActivity extends BaseActivity {
	@BindView(R.id.listView)
	ListView mListView;
	@BindView(R.id.stock)
	EditText mStock;
	@BindView(R.id.search)
	ImageView mSearch;
	private FutureFragment.FutureListAdapter mListAdapter;
	private List<VarietyModel> mListData;
	private View mClearRecord;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_search);
		mClearRecord = LayoutInflater.from(getActivity()).inflate(R.layout.layout_stock_listview_footer,null);
		ButterKnife.bind(this);
		initView();
	}

	private void initView() {
		mStock.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		mClearRecord.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListAdapter.clear();
				mListAdapter.notifyDataSetChanged();
			}
		});
		mListAdapter = new FutureFragment.FutureListAdapter(this);
		mListView.addFooterView(mClearRecord);
		mListView.setAdapter(mListAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateRecordData();
	}

	private void updateRecordData() {
		if (mListData==null){
			mListData = new ArrayList<>();
		}
//		for (int i = 0;i<5;i++){
//			VarietyModel futureHq = new VarietyModel();
//			futureHq.setCodeName("恒生电子");
//			futureHq.setInstrumentId("600570");
//			futureHq.setLastPrice(66.66);
//			futureHq.setUpDropSpeed(66.66);
//			mListData.add(futureHq);
//		}
		mListAdapter.clear();
		mListAdapter.addAll(mListData);
		mListAdapter.notifyDataSetChanged();
	}
}
