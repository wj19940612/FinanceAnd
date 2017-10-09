package com.sbai.finance.fragment.mine;

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
import com.sbai.finance.activity.mine.NewsActivity;
import com.sbai.finance.activity.mine.fund.WalletActivity;
import com.sbai.finance.activity.mine.userinfo.CreditApproveActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.HistoryNewsModel;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.ListEmptyView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 姐说消息
 */

public class SysNewsFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(R.id.empty)
    ListEmptyView mEmpty;
    @BindView(R.id.customSwipeRefreshLayout)
    CustomSwipeRefreshLayout mCustomSwipeRefreshLayout;
    private Unbinder mBind;
    private SystemNewsAdapter mSystemNewsAdapter;
    private List<HistoryNewsModel> mHistoryNewsModels;
    private Set<Integer> mSet;
    private int mPage = 0;
    private int mSize = 20;
    private int mNoReadCount;
    private NewsActivity.NoReadNewsCallback mNoReadNewsCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sys_news, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        mListView.setEmptyView(mEmpty);
        mListView.setDivider(null);
        mSet = new HashSet<>();
        mSystemNewsAdapter = new SystemNewsAdapter(getActivity());
        mListView.setAdapter(mSystemNewsAdapter);
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
                mNoReadCount = 0;
                mHistoryNewsModels = null;
                mCustomSwipeRefreshLayout.setLoadMoreEnable(true);
                requestSystemNewsList();
            }
        });
        requestSystemNewsList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
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
                if (data.isNotRead()) {
                    mNoReadCount++;
                }
            }
        }
        if (mNoReadNewsCallback != null) {
            mNoReadNewsCallback.noReadNews(mNoReadCount);
        }
    }

    public void scrollToTop() {
        mListView.smoothScrollToPosition(0);
    }

    public void setNoReadNewsCallback(NewsActivity.NoReadNewsCallback noReadNewsCallback) {
        mNoReadNewsCallback = noReadNewsCallback;
    }

    public void requestBatchReadMessage() {
        Client.batchRead().setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            updateAllNewsStatus();
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fireFree();
    }

    private void updateAllNewsStatus() {
        mNoReadCount = 0;
        if (mSystemNewsAdapter != null) {
            for (int i = 0; i < mSystemNewsAdapter.getCount(); i++) {
                HistoryNewsModel historyNewsModel = mSystemNewsAdapter.getItem(i);
                if (historyNewsModel != null && historyNewsModel.isNotRead()) {
                    historyNewsModel.setStatus(1);
                }
            }
            mSystemNewsAdapter.notifyDataSetChanged();
        }
        if (mNoReadNewsCallback != null) {
            mNoReadNewsCallback.noReadNews(mNoReadCount);
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
                case HistoryNewsModel.WITH_DRAW_SUCCESS:

                    break;
                case HistoryNewsModel.WITH_DRAW_FAIL:
                    if (!historyNewsModel.isLossEfficacy()) {
//                        Launcher.with(getActivity(), WithDrawActivity.class).execute();
                    }
                    break;
                case HistoryNewsModel.WORSHIP_REWARD:
                case HistoryNewsModel.REWARD:
                    if (!historyNewsModel.isLossEfficacy()) {
                        Launcher.with(getActivity(), WalletActivity.class)
                                .putExtra(Launcher.EX_PAY_END, AccountFundDetail.TYPE_INGOT)
                                .execute();
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
            if (mNoReadCount > 0) {
                mNoReadCount--;
            }
            if (mNoReadNewsCallback != null) {
                mNoReadNewsCallback.noReadNews(mNoReadCount);
            }
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

                mTime.setText(DateUtil.formatDefaultStyleTime(item.getCreateTime()));

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
