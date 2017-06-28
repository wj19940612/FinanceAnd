package com.sbai.finance.activity.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopicActivity extends BaseActivity {

    @BindView(R.id.top)
    LinearLayout mTop;
    @BindView(R.id.title)
    TitleBar mTitleBar;
    @BindView(R.id.topicTitle)
    TextView mTopicTitle;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;

    private ListAdapter mTopicListAdapter;
    private Topic mTopic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        ButterKnife.bind(this);
        translucentStatusBar();
        initData();
        initView();
        requestTopicDetailInfo();
    }

    private void initData() {
        mTopic = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
    }

    private void initView() {
        mTitleBar.setTitle(mTopic.getTitle());
        mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.smoothScrollToPosition(0);
            }
        });
        mTopicTitle.setText(mTopic.getIntroduction());
        ViewTreeObserver viewTreeObserver = mTopicTitle.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                TextPaint textPaint = mTopicTitle.getPaint();
                float width = textPaint.measureText(mTopicTitle.getText().toString());
                /* 计算行数 */
                //获取显示宽度
                int showWidth = mTopicTitle.getMeasuredWidth() - mTopicTitle.getPaddingRight() - mTopicTitle.getPaddingLeft();
                int lines = (int) (width / showWidth);
                if (width % showWidth != 0) {
                    lines++;
                }
                if (lines <= 3) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTop.getLayoutParams();
                    params.height = (int) Display.dp2Px(180, getResources());
                    mTop.setLayoutParams(params);
                    mTop.setBackgroundResource(R.drawable.bg_subject_small);
                }
            }
        });

        mTopicListAdapter = new ListAdapter(this);
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mTopicListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Variety variety = (Variety) parent.getItemAtPosition(position);
                if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
                    Launcher.with(getActivity(), FutureTradeActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                }
                if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                    if (variety.getSmallVarietyTypeCode().equalsIgnoreCase(Variety.STOCK_EXPONENT)) {
                        Launcher.with(getActivity(), StockIndexActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                    } else {
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
    }

    private void requestTopicDetailInfo() {
        Client.getTopicDetailData(mTopic.getId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<TopicDetailModel>, TopicDetailModel>() {
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
        for (Variety variety : subjectLists) {
            if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
                futures.add(variety);
            } else if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                stocks.add(variety);
            }
        }
        requestFutureMarketData(futures);
        requestStockMarketData(stocks);
    }

//	private void requestStockMarketData(List<Variety> data) {
//		if (data == null || data.isEmpty()) return;
//		StringBuilder stringBuilder = new StringBuilder();
//		for (Variety variety : data) {
//			stringBuilder.append(variety.getVarietyType()).append(",");
//		}
//		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
//		Client.getStockMarketData(stringBuilder.toString())
//				.setCallback(new StockCallback<StockResp, List<StockData>>() {
//					@Override
//					public void onDataMsg(List<StockData> result, StockResp.Msg msg) {
//						if (result!=null){
//							mTopicListAdapter.addStockData(result);
//						}
//					}
//				}).fireSync();
//	}

    /**
     * 新批量请求股票行情接口
     *
     * @param data
     */
    private void requestStockMarketData(List<Variety> data) {
        if (data == null || data.isEmpty()) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (Variety variety : data) {
            stringBuilder.append(variety.getVarietyType()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        Client.getStockMarketData(stringBuilder.toString())
                .setCallback(new Callback2D<Resp<List<StockData>>, List<StockData>>() {

                    @Override
                    protected void onRespSuccessData(List<StockData> result) {
                        if (result != null) {
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
                .setCallback(new Callback2D<Resp<List<FutureData>>, List<FutureData>>() {
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

    public static class ListAdapter extends ArrayAdapter<Variety> {
        Context mContext;
        private HashMap<String, FutureData> mFutureDataList;
        private HashMap<String, StockData> mStockDataList;

        public void addFutureData(List<FutureData> futureDataList) {
            for (FutureData futureData : futureDataList) {
                mFutureDataList.put(futureData.getInstrumentId(), futureData);
            }
            notifyDataSetChanged();
        }

        public void addStockData(List<StockData> stockDataList) {
            for (StockData stockData : stockDataList) {
                mStockDataList.put(stockData.getInstrumentId(), stockData);
            }
            notifyDataSetChanged();
        }

        public ListAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
            mFutureDataList = new HashMap<>();
            mStockDataList = new HashMap<>();
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_variey, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mFutureDataList, mStockDataList, mContext);
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

            ViewHolder(View content) {
                ButterKnife.bind(this, content);
            }

            private void bindDataWithView(Variety item, HashMap<String, FutureData> futureMap, HashMap<String, StockData> stockMap, Context context) {
                if (item.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                    mFutureName.setText(item.getVarietyName());
                    mFutureCode.setText(item.getVarietyType());
                    StockData stockData = stockMap.get(item.getVarietyType());
                    if (stockData != null) {
                        mLastPrice.setText(stockData.getLastPrice());
                        if (stockData.isDelist()) {
                            mRate.setEnabled(false);
                            mRate.setText(R.string.delist);
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.unluckyText));
                        } else {
                            mRate.setEnabled(true);
                            String priceChange = FinanceUtil.formatToPercentage(stockData.getUpDropSpeed());
                            if (priceChange.startsWith("-")) {
                                mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.greenAssist));
                                mRate.setSelected(false);
                                mRate.setText(priceChange);
                            } else {
                                mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                                mRate.setSelected(true);
                                mRate.setText("+" + priceChange);
                            }
                        }
                    } else {
                        mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                        mLastPrice.setText("--");
                        mRate.setSelected(true);
                        mRate.setText("--");
                    }
                } else if (item.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
                    mFutureName.setText(item.getVarietyName());
                    mFutureCode.setText(item.getContractsCode());
                    FutureData futureData = futureMap.get(item.getContractsCode());
                    if (futureData != null) {
                        double priceChange = FinanceUtil.subtraction(futureData.getLastPrice(), futureData.getPreSetPrice())
                                .divide(new BigDecimal(futureData.getPreSetPrice()), 4, RoundingMode.HALF_EVEN)
                                .multiply(new BigDecimal(100)).doubleValue();
                        mLastPrice.setText(FinanceUtil.formatWithScale(futureData.getLastPrice(), item.getPriceScale()));
                        if (priceChange >= 0) {
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                            mRate.setSelected(true);
                            mRate.setText("+" + FinanceUtil.formatWithScale(priceChange) + "%");
                        } else {
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.greenAssist));
                            mRate.setSelected(false);
                            mRate.setText(FinanceUtil.formatWithScale(priceChange) + "%");
                        }
                    } else {
                        mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                        mLastPrice.setText("--");
                        mRate.setSelected(true);
                        mRate.setText("--");
                    }
                }
            }
        }
    }
}
