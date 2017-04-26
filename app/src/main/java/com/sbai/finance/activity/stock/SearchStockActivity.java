package com.sbai.finance.activity.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.FutureHq;

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
	private FutureListAdapter mListAdapter;
	private List<FutureHq> mListData;
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
		mListAdapter = new FutureListAdapter(this);
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
		for (int i = 0;i<5;i++){
			FutureHq futureHq = new FutureHq();
			futureHq.setCodeName("恒生电子");
			futureHq.setInstrumentId("600570");
			futureHq.setLastPrice(66.66);
			futureHq.setUpDropSpeed(66.66);
			mListData.add(futureHq);
		}
		mListAdapter.clear();
		mListAdapter.addAll(mListData);
		mListAdapter.notifyDataSetChanged();
	}

	public static class FutureListAdapter extends ArrayAdapter<FutureHq> {
		Context mContext;

		public FutureListAdapter(@NonNull Context context) {
			super(context, 0);
			mContext = context;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_variey, parent, false);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindDataWithView(getItem(position), position, mContext);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.futureName)
			TextView mFutureName;
			@BindView(R.id.futureCode)
			TextView mFutureCode;
			@BindView(R.id.lastPrice)
			TextView mLastPrice;
			@BindView(R.id.rate)
			TextView mRate;
			@BindView(R.id.stopTrade)
			TextView mStopTrade;
			@BindView(R.id.trade)
			LinearLayout mTrade;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private void bindDataWithView(FutureHq item, int position, Context context) {
				mFutureName.setText(item.getCodeName());
				mFutureCode.setText(item.getInstrumentId());
				mLastPrice.setText(item.getLastPrice().toString());
				mRate.setText("+" + item.getUpDropSpeed().toString() + "%");
				if (position == 2) {
					mTrade.setVisibility(View.GONE);
					mStopTrade.setVisibility(View.VISIBLE);
				} else {
					mTrade.setVisibility(View.VISIBLE);
					mStopTrade.setVisibility(View.GONE);
				}
			}
		}
	}
}
