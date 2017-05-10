package com.sbai.finance.fragment.mine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.CreditApproveActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.HistoryNewsModel;
import com.sbai.finance.model.mine.NotReadMessageNumberModel;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.OnNoReadNewsListener;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SystemNewsFragment extends BaseFragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Unbinder mBind;
    private TextView mFootView;
    private SystemNewsAdapter mSystemNewsAdapter;
    private OnNoReadNewsListener mOnNoReadNewsListener;

    private int mPage;
    private HashSet<Integer> mSet;
    private int mNotReadNewsNumber;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNoReadNewsListener) {
            mOnNoReadNewsListener = (OnNoReadNewsListener) context;
        } else {
            throw new RuntimeException(context.toString() + "" +
                    "must implements OnNoReadNewsListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_refresh_listview, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSet = new HashSet<>();
        mEmpty.setText(R.string.now_not_has_data);
        mEmpty.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.img_no_message, 0, 0);
        mListView.setEmptyView(mEmpty);
        mSystemNewsAdapter = new SystemNewsAdapter(getActivity());
        mListView.setAdapter(mSystemNewsAdapter);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mPage = 0;
                requestSystemNewsList();
            }
        });

        requestSystemNewsList();
        mOnNoReadNewsListener.onNoReadNewsNumber(15, 0);
    }

    private void requestSystemNewsList() {
        Client.requestHistoryNews(false, HistoryNewsModel.NEW_TYPE_SYSTEM_NEWS, mPage, Client.PAGE_SIZE)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<HistoryNewsModel>>, List<HistoryNewsModel>>() {
                    @Override
                    protected void onRespSuccessData(List<HistoryNewsModel> data) {
                        for (HistoryNewsModel his : data) {
                            Log.d(TAG, " 系统消息" + his.toString());
                        }
                        updateSystemNewsListData(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void updateSystemNewsListData(List<HistoryNewsModel> systemNewsModels) {
        if (systemNewsModels == null) {
            stopRefreshAnimation();
            return;
        }
        if (mFootView == null) {
            mFootView = new TextView(getActivity());
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            mFootView.setPadding(padding, padding, padding, padding);
            mFootView.setText(getText(R.string.load_more));
            mFootView.setGravity(Gravity.CENTER);
            mFootView.setTextColor(Color.WHITE);
            mFootView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
            mFootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSwipeRefreshLayout.isRefreshing()) return;
                    mPage++;
                    requestSystemNewsList();
                }
            });
            mListView.addFooterView(mFootView);
        }

        if (systemNewsModels.size() < Client.PAGE_SIZE) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mSystemNewsAdapter != null) {
                mSystemNewsAdapter.clear();
            }
            stopRefreshAnimation();
        }

        for (HistoryNewsModel data : systemNewsModels) {
            if (mSet.add(data.getId())) {
                mSystemNewsAdapter.add(data);
            }
        }
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HistoryNewsModel systemNewsModel = (HistoryNewsModel) parent.getAdapter().getItem(position);
        if (systemNewsModel != null) {
            updateNewsReadStatus(position, systemNewsModel);
            switch (systemNewsModel.getType()) {
                //借款单审核未通过
                case HistoryNewsModel.BORROW_MONEY_AUDIT_IS_NOT_PASS:
                    // TODO: 2017/5/2  历史借入
                    break;
                // 14.借款发布成功 *
                case HistoryNewsModel.BORROW_MONEY_PUBLISH_SUCCESS:
                    // TODO: 2017/5/2 我的借入
                    break;
                //// 20.成为观点大神
                case HistoryNewsModel.BECOME_VIEWPOINT_MANITO:
                    // TODO: 2017/5/2 观点大神 
                    break;
                //21.实名认证已通过
                case HistoryNewsModel.REAL_NAME_APPROVE_PASSED:
                    LocalUser.getUser().getUserInfo().setStatus(UserInfo.CREDIT_IS_ALREADY_APPROVE);
                    Launcher.with(getActivity(), CreditApproveActivity.class).execute();
                    break;
                // 22.实名认证未通过
                case HistoryNewsModel.REAL_NAME_APPROVE_FAILED:
                    LocalUser.getUser().getUserInfo().setStatus(UserInfo.CREDIT_IS_NOT_APPROVE);
                    Launcher.with(getActivity(), CreditApproveActivity.class).execute();
                    break;
                //   30.意向金支付成功
                case HistoryNewsModel.THE_EARNEST_MONEY_APY_SUCCESS:
                    // TODO: 2017/5/2 跳转明细
                    break;
            }
        }
    }

    private void updateNewsReadStatus(int position, HistoryNewsModel systemNewsModel) {
        if (!systemNewsModel.isAlreadyRead()) {
            mSystemNewsAdapter.remove(systemNewsModel);
            systemNewsModel.setStatus(1);
            mSystemNewsAdapter.insert(systemNewsModel, position);
        }
        getActivity().setResult(Activity.RESULT_OK);
        mNotReadNewsNumber--;
        if (mNotReadNewsNumber == 0) {
            mOnNoReadNewsListener.onNoReadNewsNumber(HistoryNewsModel.NEW_TYPE_SYSTEM_NEWS, 0);
        }
        Client.updateMsgReadStatus(systemNewsModel.getId())
                .setTag(TAG)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                    }
                })
                .fire();
    }

    public void setNotReadNewsNumber(NotReadMessageNumberModel notReadNewsNumber) {
        mNotReadNewsNumber = notReadNewsNumber.getCount();
    }

    static class SystemNewsAdapter extends ArrayAdapter<HistoryNewsModel> {

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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_system_news, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindViewWithData(getItem(position), mContext, position);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.title)
            AppCompatTextView mTitle;
            @BindView(R.id.time)
            AppCompatTextView mTime;
            @BindView(R.id.content)
            TextView mContent;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindViewWithData(HistoryNewsModel item, Context context, int position) {
                // 13.借款单审核未通过 14.借款发布成功 *
//                public static final int BORROW_MONEY_AUDIT_IS_NOT_PASS = 13;
//                public static final int BORROW_MONEY_PUBLISH_SUCCESS = 14;
//                // 20.成为观点大神 21.实名认证已通过 22.实名认证未通过 * 25.涨跌预测成功 26.涨跌预测失败
//                public static final int BECOME_VIEWPOINT_MANITO = 20;
//                public static final int REAL_NAME_APPROVE_PASSED = 21;
//                public static final int REAL_NAME_APPROVE_FAILED = 22;
//                public static final int FORCAEST_highs_and_lows_fail = 25;
//                public static final int FORCAEST_highs_and_lows_success = 26;
//
//                //   30.意向金支付成功
//                public static final int THE_EARNEST_MONEY_APY_SUCCESS = 30;

                switch (item.getType()) {
                    //借款单审核未通过
                    case HistoryNewsModel.BORROW_MONEY_AUDIT_IS_NOT_PASS:
                        break;
//                    14.借款发布成功 *
                    case HistoryNewsModel.BORROW_MONEY_PUBLISH_SUCCESS:
                        break;
                    //// 20.成为观点大神
                    case HistoryNewsModel.BECOME_VIEWPOINT_MANITO:
                        break;
                    //21.实名认证已通过
                    case HistoryNewsModel.REAL_NAME_APPROVE_PASSED:
                        break;
                    // 22.实名认证未通过
                    case HistoryNewsModel.REAL_NAME_APPROVE_FAILED:
                        break;
                    // 25.涨跌预测成功
                    case HistoryNewsModel.FORCAEST_highs_and_lows_fail:
                        break;
                    //26.涨跌预测失败
                    case HistoryNewsModel.FORCAEST_highs_and_lows_success:
                        break;

                    //   30.意向金支付成功
                    case HistoryNewsModel.THE_EARNEST_MONEY_APY_SUCCESS:
                        break;
                }
                if (item.getType() == HistoryNewsModel.THE_EARNEST_MONEY_APY_SUCCESS) {
                    if (item.isAlreadyRead()) {
                        mTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_news_succeed, 0, 0, 0);
                    } else {
                        mTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_news_succeed_read, 0, 0, 0);
                    }
                }

                if (item.isAlreadyRead()) {
                    mContent.setSelected(true);
                    mTitle.setSelected(true);
                } else {
                    mTitle.setSelected(false);
                    mContent.setSelected(false);
                }

                mTitle.setText(item.getTitle());
                mTime.setText(DateUtil.getFormatTime(item.getCreateDate()));
                mContent.setText(item.getMsg());
            }
        }
    }
}
