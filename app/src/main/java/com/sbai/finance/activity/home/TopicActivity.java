package com.sbai.finance.activity.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.future.FutureTradeActivity;
import com.sbai.finance.activity.stock.StockDetailActivity;
import com.sbai.finance.activity.stock.StockIndexActivity;
import com.sbai.finance.model.Topic;
import com.sbai.finance.model.TopicDetailModel;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.net.stock.StockCallback;
import com.sbai.finance.net.stock.StockResp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopicActivity extends BaseActivity {

	@BindView(R.id.top)
	LinearLayout mTop;
	@BindView(R.id.title)
	TitleBar mTitle;
	@BindView(R.id.topicTitle)
	TextView mTopicTitle;
	@BindView(R.id.listView)
	ListView mListView;
	@BindView(R.id.empty)
	TextView mEmpty;

	private OptionalActivity.SlideListAdapter mTopicListAdapter;
	private Topic mTopic;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic);
		ButterKnife.bind(this);
		translucentStatusBar(Color.TRANSPARENT);

		initData();
		initView();
	}

	private void initData() {
		mTopic = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
	}

	private void initView() {
		mTitle.setTitle(mTopic.getTitle());
		mTopicTitle.setText(mTopic.getIntroduction());

		mTopicListAdapter = new OptionalActivity.SlideListAdapter(this);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mTopicListAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Variety variety = (Variety) parent.getItemAtPosition(position);
				if (variety != null&&variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
					Launcher.with(getActivity(), FutureTradeActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, variety).execute();
				}
				if (variety != null&&variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
					if (variety.getSmallVarietyTypeCode().equalsIgnoreCase(Variety.STOCK_EXPONENT)){
						Launcher.with(getActivity(), StockIndexActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, variety).execute();
					}else{
						Launcher.with(getActivity(), StockDetailActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, variety).execute();
					}
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		requestTopicDetailInfo();
	}

	private void requestTopicDetailInfo() {
		Client.getTopicDetailData(mTopic.getId()).setTag(TAG)
				.setCallback(new Callback2D<Resp<TopicDetailModel>,TopicDetailModel>() {
					@Override
					protected void onRespSuccessData(TopicDetailModel data) {
						updateTopicInfo(data.getSubjectDetailModelList());
					}
				}).fire();
	}

	private void updateTopicInfo(List<Variety> subjectLists) {
		mTopicListAdapter.clear();
		mTopicListAdapter.addAll(subjectLists);
		mTopicListAdapter.notifyDataSetChanged();

		List<Variety> futures = new ArrayList<>();
		List<Variety> stocks = new ArrayList<>();
		for (Variety variety:subjectLists) {
			if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
				futures.add(variety);
			} else if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
				stocks.add(variety);
			}
		}
		requestFutureMarketData(futures);
		requestStockMarketData(stocks);
	}
	private void requestStockMarketData(List<Variety> data) {
		if (data == null || data.isEmpty()) return;
		StringBuilder stringBuilder = new StringBuilder();
		for (Variety variety : data) {
			stringBuilder.append(variety.getVarietyType()).append(",");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		Client.getStockMarketData(stringBuilder.toString())
				.setCallback(new StockCallback<StockResp, List<StockData>>() {
					@Override
					public void onDataMsg(List<StockData> result, StockResp.Msg msg) {
						if (result!=null){
							mTopicListAdapter.addStockData(result);
						}
					}
				}).fireSync();
	}
	private void requestFutureMarketData(List<Variety> data) {
		if (data == null || data.isEmpty()) return;
		StringBuilder stringBuilder = new StringBuilder();
		for (Variety variety : data) {
			stringBuilder.append(variety.getContractsCode()).append(",");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		Client.getFutureMarketData(stringBuilder.toString()).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<FutureData>>,List<FutureData>>() {
					@Override
					protected void onRespSuccessData(List<FutureData> data) {
						mTopicListAdapter.addFutureData(data);
					}
				})
				.fireSync();
	}
	private void updateListViewVisibleItem(FutureData data) {
		if (mListView != null && mTopicListAdapter != null) {
			int first = mListView.getFirstVisiblePosition();
			int last = mListView.getLastVisiblePosition();
			for (int i = first; i <= last; i++) {
				Variety variety = mTopicListAdapter.getItem(i);
				if (variety != null
						&& data.getInstrumentId().equalsIgnoreCase(variety.getContractsCode())) {
					View childView = mListView.getChildAt(i - mListView.getFirstVisiblePosition());
					if (childView != null) {
						TextView lastPrice = ButterKnife.findById(childView, R.id.lastPrice);
						TextView rate = ButterKnife.findById(childView, R.id.rate);
						double priceChange = FinanceUtil.subtraction(data.getLastPrice(), data.getPreSetPrice())
								.divide(new BigDecimal(data.getPreSetPrice()), 4, RoundingMode.HALF_EVEN)
								.multiply(new BigDecimal(100)).doubleValue();
						lastPrice.setText(FinanceUtil.formatWithScale(data.getLastPrice(), variety.getPriceScale()));
						if (priceChange >= 0) {
							lastPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
							rate.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
							rate.setText("+" + FinanceUtil.formatWithScale(priceChange) + "%");
						} else {
							lastPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.greenAssist));
							rate.setTextColor(ContextCompat.getColor(getActivity(), R.color.greenAssist));
							rate.setText("-" + FinanceUtil.formatWithScale(priceChange) + "%");
						}
					}
				}
			}
		}
	}
}
