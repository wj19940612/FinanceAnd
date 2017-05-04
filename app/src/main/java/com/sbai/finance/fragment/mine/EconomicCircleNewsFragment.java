package com.sbai.finance.fragment.mine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.TextUtils;
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
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.economiccircle.OpinionDetailsActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mine.HistoryNewsModel;
import com.sbai.finance.model.mine.NotReadMessageNumberModel;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.OnNoReadNewsListener;
import com.sbai.finance.utils.StrUtil;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class EconomicCircleNewsFragment extends BaseFragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Unbinder mBind;
    private EconomicCircleNewsAdapter mEconomicCircleNewsAdapter;
    private TextView mFootView;
    private OnNoReadNewsListener mOnNoReadNewsListener;

    private int mPage = 0;
    private HashSet<Integer> mSet;

    private int mNotReadNewsNumber;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNoReadNewsListener) {
            mOnNoReadNewsListener = (OnNoReadNewsListener) context;
        } else {
            throw new RuntimeException(context.toString() + "  " +
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
        mEmpty.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.img_no_message, 0, 0);
        mListView.setEmptyView(mEmpty);
        mSet = new HashSet<>();
        mEconomicCircleNewsAdapter = new EconomicCircleNewsAdapter(getActivity());
        mEconomicCircleNewsAdapter.setCallBack(new EconomicCircleNewsAdapter.CallBack() {
            @Override
            public void onUserHeadImageClick(HistoryNewsModel historyNewsModel) {
                Launcher.with(getActivity(), UserDataActivity.class).putExtra(Launcher.EX_PAYLOAD, historyNewsModel.getId()).execute();
            }
        });
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mEconomicCircleNewsAdapter);
        mListView.setOnScrollListener(this);
        requestEconomicCircleNewsList();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mPage = 0;
                requestEconomicCircleNewsList();
            }
        });

    }


    private void requestEconomicCircleNewsList() {
        Client.requestHistoryNews(false, HistoryNewsModel.NEW_TYPE_ECONOMIC_CIRCLE, mPage, Client.PAGE_SIZE)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<HistoryNewsModel>>, List<HistoryNewsModel>>() {
                    @Override
                    protected void onRespSuccessData(List<HistoryNewsModel> data) {
//                        HistoryNewsModel historyNewsModel = new HistoryNewsModel();
//                        historyNewsModel.setType(1);
//                        historyNewsModel.setTitle("哈哈哈哈哈哈");
//                        data.add(5,historyNewsModel);
                        updateEconomicCircleData(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void updateEconomicCircleData(List<HistoryNewsModel> historyNewsModelList) {
        if (historyNewsModelList == null) {
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
                    requestEconomicCircleNewsList();
                }
            });
            mListView.addFooterView(mFootView);
        }

        if (historyNewsModelList.size() < Client.PAGE_SIZE) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mEconomicCircleNewsAdapter != null) {
                mEconomicCircleNewsAdapter.clear();
            }
            stopRefreshAnimation();
        }
        for (HistoryNewsModel data : historyNewsModelList) {
            if (mSet.add(data.getId())) {
                mEconomicCircleNewsAdapter.add(data);
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

    public void setNotReadNewsNumber(NotReadMessageNumberModel notReadNews) {
        mNotReadNewsNumber = notReadNews.getCount();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HistoryNewsModel historyNewsModel = (HistoryNewsModel) parent.getAdapter().getItem(position);
        if (historyNewsModel != null) {
            updateNewsReadStatus(position, historyNewsModel);

            switch (historyNewsModel.getType()) {
                //关注
                case HistoryNewsModel.ACTION_TYPE_ATTENTION:
                    break;
                //点赞帖子
                case HistoryNewsModel.ACTION_TYPE_LIKE_POST:
                    //.点赞动态，点击可跳转至观点页面
                    Launcher.with(getActivity(), OpinionDetailsActivity.class).execute();
                    break;
                //点赞评论
                case HistoryNewsModel.ACTION_TYPE_LIKE_COMMENT:
                    Launcher.with(getActivity(), OpinionDetailsActivity.class).execute();
                    break;
                //评论
                case HistoryNewsModel.ACTION_TYPE_COMMENT:
                    //观点详情页面  将选择的这条评论置顶显示，
                    Launcher.with(getActivity(), OpinionDetailsActivity.class).putExtra(Launcher.EX_PAYLOAD, historyNewsModel.getDataId()).execute();
                    break;
            }

        }
    }

    private void updateNewsReadStatus(int position, HistoryNewsModel historyNewsModel) {
        if (!historyNewsModel.isAlreadyRead()) {
            mEconomicCircleNewsAdapter.remove(historyNewsModel);
            historyNewsModel.setStatus(1);
            mEconomicCircleNewsAdapter.insert(historyNewsModel, position);
            mNotReadNewsNumber--;
            if (mNotReadNewsNumber == 0) {
                mOnNoReadNewsListener.onNoReadNewsNumber(HistoryNewsModel.NEW_TYPE_ECONOMIC_CIRCLE, 0);
            }
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


    static class EconomicCircleNewsAdapter extends ArrayAdapter<HistoryNewsModel> {

        interface CallBack {
            void onUserHeadImageClick(HistoryNewsModel historyNewsModel);
        }

        private Context mContext;
        private CallBack mCallBack;

        public EconomicCircleNewsAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        public EconomicCircleNewsAdapter(@NonNull Context context, CallBack callBack) {
            super(context, 0);
            this.mCallBack = callBack;
        }

        public void setCallBack(CallBack callBack) {
            this.mCallBack = callBack;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_economic_circle_new, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindViewWithData(getItem(position), mContext, mCallBack);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.userHeadImage)
            AppCompatImageView mUserHeadImage;
            @BindView(R.id.userAction)
            AppCompatTextView mUserAction;
            @BindView(R.id.content)
            AppCompatTextView mContent;
            @BindView(R.id.time)
            AppCompatTextView mTime;
            @BindView(R.id.presentation)
            AppCompatTextView mPresentation;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindViewWithData(final HistoryNewsModel item, final Context context, final CallBack callBack) {
                if (item == null) return;
                UserInfo userInfo = item.getUserInfo();

                switch (item.getType()) {
                    //关注
                    case HistoryNewsModel.ACTION_TYPE_ATTENTION:
                        mContent.setVisibility(View.GONE);
                        break;
                    //点赞帖子
                    case HistoryNewsModel.ACTION_TYPE_LIKE_POST:
                        mContent.setVisibility(View.GONE);
                        break;
                    //点赞评论
                    case HistoryNewsModel.ACTION_TYPE_LIKE_COMMENT:
                        mContent.setVisibility(View.VISIBLE);
                        break;
                    //评论
                    case HistoryNewsModel.ACTION_TYPE_COMMENT:
                        mContent.setVisibility(View.VISIBLE);
                        break;
                }

                if (userInfo != null) {
                    Glide.with(context).load(userInfo.getUserPortrait())
                            .bitmapTransform(new GlideCircleTransform(context))
                            .placeholder(R.drawable.ic_default_avatar)
                            .into(mUserHeadImage);
                    if (item.isAlreadyRead()) {
                        mContent.setSelected(true);
                        SpannableString spannableString = StrUtil.mergeTextWithColor(userInfo.getUserName() + "  ",
                                !TextUtils.isEmpty(item.getTitle()) ? item.getTitle() : "",
                                ContextCompat.getColor(context, R.color.primaryText));
                        mUserAction.setText(spannableString);
                    } else {
                        SpannableString spannableString = StrUtil.mergeTextWithColor(userInfo.getUserName() + "  ",
                                !TextUtils.isEmpty(item.getTitle()) ? item.getTitle() : "",
                                ContextCompat.getColor(context, R.color.secondaryText));
                        mUserAction.setText(spannableString);
                        mContent.setSelected(false);
                    }
                }

                mContent.setText(item.getMsg());

                if (item.getData() != null) {
                    mPresentation.setText(item.getData().getContent());
                }

                mTime.setText(DateUtil.getFormatTime(item.getCreateDate()));

                mUserHeadImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callBack != null) {
                            callBack.onUserHeadImageClick(item);
                        }
                    }
                });

            }

            private String getUserAction(Context context, HistoryNewsModel item) {
                switch (item.getType()) {
                    //关注
                    case HistoryNewsModel.ACTION_TYPE_ATTENTION:
                        return context.getString(R.string.attention_you);
                    //点赞帖子
                    case HistoryNewsModel.ACTION_TYPE_LIKE_POST:
                        return context.getString(R.string.like_your_publish);
                    //点赞评论
                    case HistoryNewsModel.ACTION_TYPE_LIKE_COMMENT:
                        return context.getString(R.string.like_your_publish);
                    //评论
                    case HistoryNewsModel.ACTION_TYPE_COMMENT:
                        return context.getString(R.string.replay_you);
                }
                return "";
            }
        }

    }
}
