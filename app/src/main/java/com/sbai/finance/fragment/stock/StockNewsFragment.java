package com.sbai.finance.fragment.stock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.web.EventDetailActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.stock.CompanyAnnualReportModel;
import com.sbai.finance.model.stock.StockNewsInfoModel;
import com.sbai.finance.model.stock.StockNewsModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class StockNewsFragment extends BaseFragment {

    private static final String KEY_STOCK_CODE = "stock_code";
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    TextView mFootView;

    private String mStockCode;
    private int mPage;
    private int mPageSize = 15;
    private HashSet<String> mSet;

    private Unbinder mBind;
    private StockNewsAdapter mStockNewsAdapter;
    private ArrayList<StockNewsModel> mStockNewsModels;
    private boolean mLoadMore = true;


    public StockNewsFragment() {
    }

    public static StockNewsFragment newInstance(String stock_code) {
        StockNewsFragment fragment = new StockNewsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_STOCK_CODE, stock_code);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStockCode = getArguments().getString(KEY_STOCK_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_news, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSet = new HashSet<>();
        initViewWithAdapter();
        mStockNewsModels = new ArrayList<>();
        mStockNewsAdapter = new StockNewsAdapter(mStockNewsModels);
        mRecyclerView.setAdapter(mStockNewsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        requestStockNewsList(0);
    }

    private void initViewWithAdapter() {
//        mFootView = new TextView(getActivity());
//        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
//        mFootView.setPadding(padding, padding, padding, padding);
//        mFootView.setText(getText(R.string.load_all));
//        mFootView.setGravity(Gravity.CENTER);
//        mFootView.setTextColor(ContextCompat.getColor(getActivity(), R.color.secondaryText));
//        mFootView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.greyLightAssist));
//
//        mEmpty = new TextView(getActivity());
//        mEmpty.setText(getText(R.string.quick_publish));
//        mEmpty.setPadding(0, 10 * padding, 0, 0);
//        mEmpty.setGravity(Gravity.CENTER_HORIZONTAL);
//        mEmpty.setTextColor(ContextCompat.getColor(getActivity(), R.color.assistText));
//        mEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//        mEmpty.setCompoundDrawablePadding(padding);
//        mEmpty.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.img_no_message, 0, 0);
    }

    RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (isSlideToBottom(recyclerView) && mLoadMore) {
                requestStockNewsList(mPage);
            }
        }
    };

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }


    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

    public void requestStockNewsList(final int page) {
        this.mPage = page;
        Client.getCompanyAnnualReport(mStockCode, mPage, mPageSize, CompanyAnnualReportModel.TYPE_STOCK_NEWS)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ArrayList<StockNewsModel>>, ArrayList<StockNewsModel>>() {
                    @Override
                    protected void onRespSuccessData(ArrayList<StockNewsModel> data) {
                        updateStockNewsList(data, page);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        updateStockNewsList(null, -1);
                    }
                })
                .fire();
    }

    private void updateStockNewsList(ArrayList<StockNewsModel> data, int page) {
        if (data == null || data.isEmpty() && mStockNewsModels.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
            if (page == 0) {
                mStockNewsAdapter.clear();
            }
            mStockNewsModels.addAll(data);
            if (data.size() < mPageSize) {
                mLoadMore = false;
            } else {
                mLoadMore = true;
                mPage++;
            }
            mStockNewsAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    class StockNewsAdapter extends RecyclerView.Adapter<StockNewsAdapter.ViewHolder> {

        private ArrayList<StockNewsModel> mStockNewsModels;

        public StockNewsAdapter(ArrayList<StockNewsModel> stockNewsModels) {
            this.mStockNewsModels = stockNewsModels;
        }

        public void addAll(ArrayList<StockNewsModel> stockNewsModels) {
            this.mStockNewsModels.addAll(stockNewsModels);
            notifyDataSetChanged();
        }

        public void clear() {
            mStockNewsModels.clear();
            notifyItemRangeRemoved(0, mStockNewsModels.size());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_stock_news, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mStockNewsModels.get(position));
        }

        @Override
        public int getItemCount() {
            return mStockNewsModels != null ? mStockNewsModels.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.eventTitle)
            TextView mEventTitle;
            @BindView(R.id.eventSource)
            TextView mEventSource;
            @BindView(R.id.eventTime)
            TextView mEventTime;
            @BindView(R.id.event)
            LinearLayout mEvent;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindDataWithView(final StockNewsModel item) {
                if (item == null) return;
                mEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Client.getStockNewsInfo(item.getId())
                                .setTag(TAG)
                                .setCallback(new Callback2D<Resp<StockNewsInfoModel>, StockNewsInfoModel>() {
                                    @Override
                                    protected void onRespSuccessData(StockNewsInfoModel data) {
                                        if (data != null) {
                                            Launcher.with(getActivity(), EventDetailActivity.class)
                                                    .putExtra(EventDetailActivity.EX_STOCK_NEWS, data)
                                                    .execute();
                                        }
                                    }
                                })
                                .fire();
                    }
                });
//                if (TextUtils.isEmpty(item.getFrom())) {
//                    mEventSource.setVisibility(View.GONE);
//                } else {
//                    mEventSource.setVisibility(View.VISIBLE);
//                }
//                mEventSource.setText(item.getFrom());
                mEventTime.setText(DateUtil.getFormatTime(item.getTime()));
                mEventTitle.setText(item.getTitle());
            }
        }

    }
}
