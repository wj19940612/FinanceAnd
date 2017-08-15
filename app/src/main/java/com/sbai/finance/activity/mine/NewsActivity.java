package com.sbai.finance.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.wallet.WithDrawActivity;
import com.sbai.finance.activity.opinion.OpinionActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.HistoryNewsModel;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends BaseActivity implements AdapterView.OnItemClickListener {


    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.customSwipeRefreshLayout)
    CustomSwipeRefreshLayout mCustomSwipeRefreshLayout;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    private HashSet<Integer> mSet;
    private SystemNewsAdapter mSystemNewsAdapter;
    private List<HistoryNewsModel> mHistoryNewsModels;

    private int mPage = 0;
    private int mSize = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        mEmpty.setText(R.string.now_not_has_news);
        mListView.setEmptyView(mEmpty);
        mListView.setDivider(null);
        mSet = new HashSet<>();
        scrollToTop(mTitleBar, mListView);
        mSystemNewsAdapter = new SystemNewsAdapter(this);
        mListView.setAdapter(mSystemNewsAdapter);
        requestSystemNewsList();
        mListView.setOnItemClickListener(this);
        mCustomSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestSystemNewsList();
            }
        });

        mCustomSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mPage = 0;
                mHistoryNewsModels = null;
                mCustomSwipeRefreshLayout.setLoadMoreEnable(true);
                requestSystemNewsList();
            }
        });
    }

    private void requestSystemNewsList() {
        Long createTime = null;
        if (mHistoryNewsModels != null && !mHistoryNewsModels.isEmpty()) {
            createTime = mHistoryNewsModels.get(mHistoryNewsModels.size() - 1).getCreateTime();
        }
        Client.requestHistoryNews(false, HistoryNewsModel.NEW_TYPE_SYSTEM_NEWS, mPage, mSize, null, createTime)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<HistoryNewsModel>>, List<HistoryNewsModel>>() {
                    @Override
                    protected void onRespSuccessData(List<HistoryNewsModel> data) {
                        mHistoryNewsModels = data;
                        updateSystemNewsData(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void stopRefreshAnimation() {
        if (mCustomSwipeRefreshLayout.isRefreshing()) {
            mCustomSwipeRefreshLayout.setRefreshing(false);
        }
        if (mCustomSwipeRefreshLayout.isLoading()) {
            mCustomSwipeRefreshLayout.setLoading(false);
        }
    }

    private void updateSystemNewsData(List<HistoryNewsModel> historyNewsModelList) {
        if (historyNewsModelList == null) {
            stopRefreshAnimation();
            return;
        }
        if (historyNewsModelList.size() < Client.DEFAULT_PAGE_SIZE) {
            mCustomSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }

        if (mCustomSwipeRefreshLayout.isRefreshing()) {
            if (mSystemNewsAdapter != null) {
                mSystemNewsAdapter.clear();
            }
        }
        stopRefreshAnimation();
        for (HistoryNewsModel data : historyNewsModelList) {
            if (mSet.add(data.getId())) {
                mSystemNewsAdapter.add(data);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HistoryNewsModel historyNewsModel = (HistoryNewsModel) parent.getAdapter().getItem(position);
        if (historyNewsModel != null) {
            updateNewsReadStatus(position, historyNewsModel);
            int type = historyNewsModel.getType();
            switch (type) {
                //关注
                case HistoryNewsModel.ACTION_TYPE_ATTENTION:
                    break;
                //// 20.成为观点大神
                case HistoryNewsModel.BECOME_VIEWPOINT_MANITO:
                    if (!historyNewsModel.isLossEfficacy()) {
                        Launcher.with(getActivity(), OpinionActivity.class).execute();
                    }
                    break;
                //21.实名认证已通过
                case HistoryNewsModel.REAL_NAME_APPROVE_PASSED:
                    if (!historyNewsModel.isLossEfficacy()) {
                        LocalUser.getUser().getUserInfo().setStatus(UserInfo.CREDIT_IS_ALREADY_APPROVE);
                        Launcher.with(getActivity(), CreditApproveActivity.class).putExtra(Launcher.EX_PAYLOAD, historyNewsModel.getDataId()).execute();
                    }
                    break;
                // 22.实名认证未通过
                case HistoryNewsModel.REAL_NAME_APPROVE_FAILED:
                    if (!historyNewsModel.isLossEfficacy()) {
                        LocalUser.getUser().getUserInfo().setStatus(UserInfo.CREDIT_IS_NOT_APPROVE);
                        Launcher.with(getActivity(), CreditApproveActivity.class).putExtra(Launcher.EX_PAYLOAD, historyNewsModel.getDataId()).execute();
                    }
                    break;
                // 25.涨跌预测成功
                case HistoryNewsModel.FORCAEST_highs_and_lows_fail:
                    if (!historyNewsModel.isLossEfficacy()) {

                    }
                    break;
                //26.涨跌预测失败
                case HistoryNewsModel.FORCAEST_highs_and_lows_success:
                    if (!historyNewsModel.isLossEfficacy()) {

                    }
                    break;
                //   30.意向金支付成功
                case HistoryNewsModel.THE_EARNEST_MONEY_APY_SUCCESS:
                    if (!historyNewsModel.isLossEfficacy()) {
                        Launcher.with(getActivity(), FundDetailActivity.class).execute();
                    }
                    break;
                case HistoryNewsModel.WITH_DRAW_SUCCESS:
                    if (!historyNewsModel.isLossEfficacy()) {
                        Launcher.with(getActivity(), FundDetailActivity.class).execute();
                    }
                    break;
                case HistoryNewsModel.WITH_DRAW_FAIL:
                    if (!historyNewsModel.isLossEfficacy()) {
                        Launcher.with(getActivity(), WithDrawActivity.class).execute();
                    }
                    break;

            }
        }
    }

    private void updateNewsReadStatus(int position, HistoryNewsModel historyNewsModel) {
        if (historyNewsModel.isNotRead()) {
            mSystemNewsAdapter.remove(historyNewsModel);
            historyNewsModel.setStatus(1);
            mSystemNewsAdapter.insert(historyNewsModel, position);
            getActivity().setResult(Activity.RESULT_OK);
            Client.updateMsgReadStatus(historyNewsModel.getId())
                    .setTag(TAG)
                    .setCallback(new Callback<Resp<JsonObject>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonObject> resp) {
                        }
                    })
                    .fire();
        }
    }


    class SystemNewsAdapter extends ArrayAdapter<HistoryNewsModel> {

        private Context mContext;

        public SystemNewsAdapter(@NonNull Context context) {
            super(context, 0);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_system_news, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext);
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.title)
            AppCompatTextView mTitle;
            @BindView(R.id.notReadNewsHint)
            AppCompatImageView mNotReadNewsHint;
            @BindView(R.id.time)
            AppCompatTextView mTime;
            @BindView(R.id.rootView)
            AppCompatTextView mContent;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(HistoryNewsModel item, Context context) {
                if (item == null) return;

                if (item.isNotRead()) {
                    mNotReadNewsHint.setVisibility(View.VISIBLE);
                } else {
                    mNotReadNewsHint.setVisibility(View.INVISIBLE);
                }

                if (item.titleIsUserName() && item.getSourceUser() != null) {
                    mTitle.setText(item.getSourceUser().getUserName());
                } else if (item.isForcaest() && item.getData() != null) {
                    mTitle.setText(item.getData().getContent());
                } else {
                    mTitle.setText(item.getTitle());
                }

                mTime.setText(DateUtil.getFormatTime(item.getCreateTime()));

                if (item.isTheEarnestMoneyPaySuccess() && item.getData() != null) {
                    mContent.setText(context.getString(R.string.pay_count, FinanceUtil.formatWithScale(item.getData().getMoney()) + " \n" +
                            context.getString(R.string.pay_source, item.getData().getSource())));
                } else {
                    mContent.setText(item.getMsg());
                }
            }
        }
    }

}
