package com.sbai.finance.fragment.mine;

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

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mine.HistoryNewsModel;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MutualHelpFragment extends BaseFragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {


    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Unbinder mBind;
    private TextView mFootView;
    private MutualHelpAdapter mMutualHelpAdapter;

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
        mListView.setEmptyView(mEmpty);
        mMutualHelpAdapter = new MutualHelpAdapter(getActivity());
        mListView.setAdapter(mMutualHelpAdapter);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);
        mMutualHelpAdapter.setOnUserHeadImageClickListener(new MutualHelpAdapter.OnUserHeadImageClickListener() {
            @Override
            public void onUserHeadImageClick(HistoryNewsModel historyNewsModel) {
                ToastUtil.curt("用户头像");
            }
        });

        requestMutualHelpList();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMutualHelpList();
            }
        });
    }

    private void requestMutualHelpList() {

//        API.requestHistoryNews(HistoryNewsModel.NEW_TYPE_MUTUAL_HELP)
//                .setIndeterminate(this)
//                .setTag(TAG)
//                .setCallback(new Callback2D<Resp<List<HistoryNewsModel>>, List<HistoryNewsModel>>() {
//                    @Override
//                    protected void onRespSuccessData(List<HistoryNewsModel> data) {
//
//                    }
//                })
//                .fireSync();

        ArrayList<HistoryNewsModel> economicCircleNewModels = new ArrayList<>();
        for (int i = 0; i < url.length; i++) {
            HistoryNewsModel hahahha = new HistoryNewsModel();
            if (i == 0) {
                hahahha.setType(10);
            } else if (i == 1) {
                hahahha.setType(11);
            } else {
                hahahha.setType(12);
            }
            UserInfo userInfo = new UserInfo();
            userInfo.setUserPortrait(url[i]);
            hahahha.setUserInfo(userInfo);
            economicCircleNewModels.add(hahahha);
        }
        updateMutualHelpData(economicCircleNewModels);
    }

    // TODO: 2017/4/18 后期删除
    private String url[] = new String[]{"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1492510917267&di=d5b3057b37d5c83964230849e42cfead&imgtype=0&src=http%3A%2F%2Fpic1.cxtuku.com%2F00%2F15%2F11%2Fb998b8878108.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1492510860938&di=64f5b45b80c90746513b448207191e4f&imgtype=0&src=http%3A%2F%2Fpic7.nipic.com%2F20100613%2F3823726_085130049412_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1492510590388&di=034d5a13126feef4ed18beff5dfe9e50&imgtype=0&src=http%3A%2F%2Fpic38.nipic.com%2F20140228%2F8821914_204428973000_2.jpg"};

    private void updateMutualHelpData(ArrayList<HistoryNewsModel> historyNewsModels) {
        if (historyNewsModels == null) {
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
//                    mPageNo++;
                    requestMutualHelpList();
                }
            });
            mListView.addFooterView(mFootView);
        }

        if (historyNewsModels.size() < 15) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mMutualHelpAdapter != null) {
                mMutualHelpAdapter.clear();
                mMutualHelpAdapter.notifyDataSetChanged();
            }
            stopRefreshAnimation();
        }
        mMutualHelpAdapter.addAll(historyNewsModels);
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
        HistoryNewsModel item = (HistoryNewsModel) parent.getAdapter().getItem(position);
        if (item != null) {
            switch (item.getType()) {
                case HistoryNewsModel.ACTION_TYPE_WANT_TO_HELP_FOR_YOU:
                    ToastUtil.curt("跳转至想帮助我的人");
                    break;
                case HistoryNewsModel.ACTION_TYPE_REFUSE_YOU_PEOPLE:
                    ToastUtil.curt("拒绝我的帮助");
                    break;
                case HistoryNewsModel.ACTION_TYPE_ACCEPT_YOUR_HELP_PEOPLE:
                    ToastUtil.curt("跳转至我的借出");
                    break;

            }

        }
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
                UserInfo userInfo = item.getUserInfo();
                if (userInfo != null) {
                    Glide.with(context).load(userInfo.getUserPortrait())
                            .placeholder(R.drawable.default_headportrait64x64)
                            .bitmapTransform(new GlideCircleTransform(context))
                            .into(mUserHeadImage);

                    SpannableString spannableString = StrUtil.mergeTextWithColor(userInfo.getUserName(), context.getString(getUserAction(item)),
                            ContextCompat.getColor(context, R.color.primaryText));
                    mUserAction.setText(spannableString);
                }
                mTime.setText(DateUtil.getFormatTime(1492667145000L));
                mUserHeadImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onUserHeadImageClickListener != null) {
                            onUserHeadImageClickListener.onUserHeadImageClick(item);
                        }
                    }
                });

            }

            /**
             * @param historyNewsModel
             * @return 用户对应的动作的resId
             */
            private int getUserAction(HistoryNewsModel historyNewsModel) {
                switch (historyNewsModel.getType()) {
                    case HistoryNewsModel.ACTION_TYPE_WANT_TO_HELP_FOR_YOU:
                        return R.string.want_to_help_you;
                    case HistoryNewsModel.ACTION_TYPE_REFUSE_YOU_PEOPLE:
                        return R.string.refuse_your_help;
                    case HistoryNewsModel.ACTION_TYPE_ACCEPT_YOUR_HELP_PEOPLE:
                        return R.string.accept_your_help;
                }
                return 10;
            }
        }
    }
}
