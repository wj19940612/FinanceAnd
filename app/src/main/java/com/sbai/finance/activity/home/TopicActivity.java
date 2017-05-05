package com.sbai.finance.activity.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.future.FutureTradeActivity;
import com.sbai.finance.activity.stock.StockTradeActivity;
import com.sbai.finance.fragment.future.FutureListFragment;
import com.sbai.finance.model.FutureData;
import com.sbai.finance.model.TopicDetailModel;
import com.sbai.finance.model.Topic;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.netty.Netty;
import com.sbai.finance.netty.NettyHandler;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

	private FutureListFragment.FutureListAdapter mTopicListAdapter;
	private Integer id;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic);
		ButterKnife.bind(this);
		translucentStatusBar();

		Bundle bundle = this.getIntent().getExtras();
		Topic topic = (Topic) bundle.getSerializable(Launcher.KEY_TOPIC);
		if (topic !=null){
		   id = topic.getId();
			mTitle.setTitle(topic.getTitle());
			mTopicTitle.setText(topic.getIntroduction());
		}
		initView();
	}

	private void initView() {
		mTopicListAdapter = new FutureListFragment.FutureListAdapter(this);
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
					Launcher.with(getActivity(), StockTradeActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, variety).execute();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		requestTopicDetailInfo();
		Netty.get().subscribe(Netty.REQ_SUB_ALL);
		Netty.get().addHandler(mNettyHandler);
	}

	private void requestTopicDetailInfo() {
		Client.getTopicDetailData(id).setTag(TAG)
				.setCallback(new Callback2D<Resp<TopicDetailModel>,TopicDetailModel>() {
					@Override
					protected void onRespSuccessData(TopicDetailModel data) {
						updateTopicInfo(data.getSubjectDetailModelList());
					}
				}).fire();
	}

	private void updateTopicInfo(List<TopicDetailModel.SubjectDetailModelListBean> subjectLists) {
		StringBuilder codes = new StringBuilder();
		mTopicListAdapter.clear();
		for (TopicDetailModel.SubjectDetailModelListBean subject:subjectLists){
			Variety variety = new Variety();
			variety.setBigVarietyTypeCode(subject.getBigVarietyTypeCode());
			variety.setSmallVarietyTypeCode(subject.getSmallVarietyTypeCode());
			variety.setVarietyName(subject.getVarietyName());
			variety.setVarietyId(subject.getVarietyId());
			variety.setVarietyType(subject.getVarietyType());
			variety.setContractsCode(subject.getContractsCode());
			variety.setExchangeStatus(subject.getExchangeStatus());
			variety.setExchangeId(subject.getExchangeId());
			variety.setBaseline(subject.getBaseline());
			variety.setDisplayMarketTimes(subject.getDisplayMarketTimes());
			variety.setOpenMarketTime(subject.getOpenMarketTime());
			variety.setFlashChartPriceInterval(subject.getFlashChartPriceInterval());
			mTopicListAdapter.add(variety);
           if (variety.getContractsCode()!=null){
			 codes.append(variety.getContractsCode()).append(",");
           }
		}
		mTopicListAdapter.notifyDataSetChanged();

		String codesWithSplit = codes.toString();
		if (codesWithSplit.endsWith(",")){
			String code = codes.toString().substring(0,codesWithSplit.length()-1);
			requestQuotaList(code);
		}

	}
	private void requestQuotaList(String codes){
          Client.getQuotaList(codes).setTag(TAG)
				  .setCallback(new Callback2D<Resp<List<FutureData>>,List<FutureData>>() {
					  @Override
					  protected void onRespSuccessData(List<FutureData> data) {
						  updateQuota(data);
					  }
				  }).fire();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Netty.get().removeHandler(mNettyHandler);
		Netty.get().subscribe(Netty.REQ_UNSUB_ALL);
	}
	private NettyHandler mNettyHandler = new NettyHandler<Resp<FutureData>>() {
		@Override
		public void onReceiveData(Resp<FutureData> data) {
			if (data.getCode() == Netty.REQ_QUOTA) {
				updateListViewVisibleItem(data.getData());
				mTopicListAdapter.addFutureData(data.getData());
			}
		}
	};
    private void updateQuota(List<FutureData> futureDatas){
		for (FutureData data:futureDatas){
			mTopicListAdapter.addFutureData(data);
		}
		mTopicListAdapter.notifyDataSetChanged();
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
