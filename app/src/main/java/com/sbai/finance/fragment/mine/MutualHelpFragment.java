package com.sbai.finance.fragment.mine;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.economiccircle.WantHelpHimOrYouActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.activity.mutual.BorrowOutHisActivity;
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
import com.sbai.finance.view.CustomSwipeRefreshLayout;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MutualHelpFragment extends BaseFragment implements AdapterView.OnItemClickListener {


    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private Unbinder mBind;
    private TextView mFootView;
    private MutualHelpAdapter mMutualHelpAdapter;
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
        mMutualHelpAdapter = new MutualHelpAdapter(getActivity());
        mListView.setAdapter(mMutualHelpAdapter);
        mListView.setOnItemClickListener(this);
        mMutualHelpAdapter.setOnUserHeadImageClickListener(new MutualHelpAdapter.OnUserHeadImageClickListener() {
            @Override
            public void onUserHeadImageClick(HistoryNewsModel historyNewsModel) {
                Launcher.with(getActivity(), UserDataActivity.class).putExtra(Launcher.USER_ID, historyNewsModel.getSourceUserId()).execute();
            }
        });

        requestMutualHelpList();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mPage = 0;
                mSwipeRefreshLayout.setLoadMoreEnable(true);
                requestMutualHelpList();
            }
        });
        mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestMutualHelpList();
            }
        });
    }

    private void requestMutualHelpList() {
        Client.requestHistoryNews(false, HistoryNewsModel.NEW_TYPE_MUTUAL_HELP, mPage)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<HistoryNewsModel>>, List<HistoryNewsModel>>() {
                    @Override
                    protected void onRespSuccessData(List<HistoryNewsModel> data) {

                        updateMutualHelpData(data);
                        for (HistoryNewsModel his : data) {
                            Log.d(TAG, " 互助消息" + his.toString());
                        }
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                })
                .fire();

    }

    private void updateMutualHelpData(List<HistoryNewsModel> historyNewsModels) {
        if (historyNewsModels == null) {
            stopRefreshAnimation();
            return;
        }

        if (historyNewsModels.size() < Client.DEFAULT_PAGE_SIZE) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mMutualHelpAdapter != null) {
                mMutualHelpAdapter.clear();
                mMutualHelpAdapter.notifyDataSetChanged();
            }
        }
        stopRefreshAnimation();
        for (HistoryNewsModel data : historyNewsModels) {
            if (mSet.add(data.getId())) {
                mMutualHelpAdapter.add(data);
            }
        }

    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HistoryNewsModel item = (HistoryNewsModel) parent.getAdapter().getItem(position);
        if (item != null) {
            updateNewsReadStatus(position, item);
            switch (item.getType()) {
                case HistoryNewsModel.ACTION_TYPE_WANT_TO_HELP_FOR_YOU:
                    if (item.isLossEfficacy()) {
                        return;
                    }
                    Launcher.with(getActivity(), WantHelpHimOrYouActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, item.getDataId())
                            .putExtra(Launcher.USER_ID, item.getUserId())
                            .execute();
                    break;
                case HistoryNewsModel.ACTION_TYPE_REFUSE_YOU_PEOPLE:
                    break;
                case HistoryNewsModel.ACTION_TYPE_ACCEPT_YOUR_HELP_PEOPLE:
                    if (item.isLossEfficacy()) {
                        return;
                    }
                    Launcher.with(getActivity(), BorrowOutHisActivity.class).execute();
                    break;

            }

        }
    }

    private void updateNewsReadStatus(int position, HistoryNewsModel item) {
        if (!item.isAlreadyRead()) {
            mMutualHelpAdapter.remove(item);
            item.setStatus(1);
            mMutualHelpAdapter.insert(item, position);
            mNotReadNewsNumber--;
            if (mNotReadNewsNumber == 0) {
                mOnNoReadNewsListener.onNoReadNewsNumber(HistoryNewsModel.NEW_TYPE_MUTUAL_HELP, 0);
            }
            getActivity().setResult(Activity.RESULT_OK);
            Client.updateMsgReadStatus(item.getId())
                    .setTag(TAG)
                    .setCallback(new Callback<Resp<JsonObject>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonObject> resp) {
                        }
                    })
                    .fire();
        }
    }

    public void setNotReadNewsNumber(NotReadMessageNumberModel notReadNews) {
        mNotReadNewsNumber = notReadNews.getCount();
    }

    static class MutualHelpAdapter extends ArrayAdapter<HistoryNewsModel> {

        public interface OnUserHeadImageClickListener {
            void onUserHeadImageClick(HistoryNewsModel historyNewsModel);
        }

        public void setOnUserHeadImageClickListener(OnUserHeadImageClickListener userHeadImageClickListener) {
            this.mOnUserHeadImageClickListener = userHeadImageClickListener;
        }

        private OnUserHeadImageClickListener mOnUserHeadImageClickListener;
        private Context mContext;

        public MutualHelpAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_mutual_help, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext, mOnUserHeadImageClickListener);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.userHeadImage)
            AppCompatImageView mUserHeadImage;
            @BindView(R.id.userAction)
            AppCompatTextView mUserAction;
            @BindView(R.id.time)
            AppCompatTextView mTime;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final HistoryNewsModel item, Context context, final OnUserHeadImageClickListener onUserHeadImageClickListener) {
                if (item == null) return;
                UserInfo userInfo = item.getUserInfo();
                if (userInfo != null) {
                    Glide.with(context).load(userInfo.getUserPortrait())
                            .placeholder(R.drawable.ic_default_avatar)
                            .bitmapTransform(new GlideCircleTransform(context))
                            .into(mUserHeadImage);
                    if (item.isAlreadyRead()) {
                        SpannableString spannableString = StrUtil.mergeTextWithColor(userInfo.getUserName() + "  ", item.getTitle(),
                                ContextCompat.getColor(context, R.color.secondaryText));
                        mUserAction.setText(spannableString);
                    } else {
                        SpannableString spannableString = StrUtil.mergeTextWithColor(userInfo.getUserName() + "  ", item.getTitle(),
                                ContextCompat.getColor(context, R.color.primaryText));
                        mUserAction.setText(spannableString);
                    }
                }
                mTime.setText(DateUtil.getFormatTime(item.getCreateDate()));
                mUserHeadImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onUserHeadImageClickListener != null) {
                            onUserHeadImageClickListener.onUserHeadImageClick(item);
                        }
                    }
                });

            }
        }
    }
}
