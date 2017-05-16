package com.sbai.finance.fragment.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.stock.CompanyAnnualReportModel;
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
    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmpty;

    private String mStockCode;
    private int mPage;
    TextView mFootView;
    private HashSet<String> mSet;

    private Unbinder mBind;
    private StockNewsAdapter mStockNewsAdapter;


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
        mStockNewsAdapter = new StockNewsAdapter(getActivity());
        mListView.setAdapter(mStockNewsAdapter);
        mListView.setEmptyView(mEmpty);
        requestCompanyAnnualReport(0);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StockNewsModel item = (StockNewsModel) parent.getAdapter().getItem(position);
                if (item != null) {
                    Log.d(TAG, "onItemClick: " + item.getUrl());
                    Launcher.with(getActivity(), WebActivity.class)
                            .putExtra(WebActivity.EX_TITLE,item.getTitle())
                            .putExtra(WebActivity.EX_URL,item.getUrl())
                            .execute();
                }
            }
        });
    }

    public void requestCompanyAnnualReport(final int page) {
        this.mPage = page;
        Client.getCompanyAnnualReport(mStockCode, mPage, Client.DEFAULT_PAGE_SIZE, CompanyAnnualReportModel.TYPE_STOCK_NEWS)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ArrayList<StockNewsModel>>, ArrayList<StockNewsModel>>() {
                    @Override
                    protected void onRespSuccessData(ArrayList<StockNewsModel> data) {
                        updateStockNewsList(data, page);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                    }
                })
                .fire();
    }

    private void updateStockNewsList(ArrayList<StockNewsModel> StockNewsDataList, int page) {
        if (StockNewsDataList == null) return;
        if (mFootView == null) {
            mFootView = new TextView(getActivity());
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            mFootView.setPadding(padding, padding, padding, padding);
            mFootView.setText(getText(R.string.load_more));
            mFootView.setGravity(Gravity.CENTER);
            mFootView.setTextColor(ContextCompat.getColor(getActivity(), R.color.greyAssist));
            mFootView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.greyLightAssist));
            mFootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPage++;
                    requestCompanyAnnualReport(mPage);
                }
            });
            mListView.addFooterView(mFootView);
        }


        if (StockNewsDataList.size() < Client.DEFAULT_PAGE_SIZE) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }


        for (StockNewsModel data : StockNewsDataList) {
            if (mSet.add(data.getId())) {
                mStockNewsAdapter.add(data);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    class StockNewsAdapter extends ArrayAdapter<StockNewsModel> {
        private Context mContext;

        public StockNewsAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_event, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext);
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.eventSource)
            TextView mEventSource;
            @BindView(R.id.eventTime)
            TextView mEventTime;
            @BindView(R.id.eventTitle)
            TextView mEventTitle;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(StockNewsModel item, Context context) {
                if (TextUtils.isEmpty(item.getFrom())) {
                    mEventSource.setVisibility(View.GONE);
                } else {
                    mEventSource.setVisibility(View.VISIBLE);
                }
                mEventSource.setText(item.getFrom());
                mEventTime.setText(DateUtil.getFormatTime(item.getTime()));
                mEventTitle.setText(item.getTitle());
            }

        }
    }
}
