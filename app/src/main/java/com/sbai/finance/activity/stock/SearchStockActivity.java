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
import com.sbai.finance.model.market.StockSearchData;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ValidationWatcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
	private StockListAdapter mListAdapter;
	private View mClearRecord;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_search);
		mClearRecord = LayoutInflater.from(getActivity()).inflate(R.layout.layout_stock_listview_footer,null);
		ButterKnife.bind(this);
		initView();
	}
	private ValidationWatcher mStockValidationWatcher = new ValidationWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			requestSearchStock(mStock.getText().toString());
		}
	};

	private void initView() {
		mStock.addTextChangedListener(mStockValidationWatcher);
		mClearRecord.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListAdapter.clear();
				mListAdapter.notifyDataSetChanged();
			}
		});
		mListAdapter = new StockListAdapter(this);
		mListView.addFooterView(mClearRecord);
		mListView.setAdapter(mListAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mStock.removeTextChangedListener(mStockValidationWatcher);
	}

	private void requestSearchStock(String key){
		Client.stockSearch(key).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<StockSearchData>>,List<StockSearchData>>() {
					@Override
					protected void onRespSuccessData(List<StockSearchData> data) {
						updateRecordData(data);
					}
				}).fire();
	}
	@OnClick(R.id.search)
	public void onClick(View view){
		requestSearchStock(mStock.getText().toString());
	}
	private void updateRecordData(List<StockSearchData> data) {
		mListAdapter.clear();
		mListAdapter.addAll(data);
		mListAdapter.notifyDataSetChanged();
	}

	public static class StockListAdapter extends ArrayAdapter<StockSearchData> {
		Context mContext;

		public StockListAdapter(@NonNull Context context) {
			super(context, 0);
			mContext = context;
		}
		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_stock_search, parent, false);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindDataWithView(getItem(position));
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.stockName)
			TextView mStockName;
			@BindView(R.id.stockCode)
			TextView mStockCode;
			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private void bindDataWithView(StockSearchData item) {
				mStockCode.setText(item.getInstrumentId());
				mStockName.setText(item.getName());
			}
		}
	}
}
