package com.sbai.finance.fragment.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.home.InformationAndFocusNewsActivity;
import com.sbai.finance.activity.web.DailyReportDetailActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mine.MyCollect;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.ListEmptyView;
import com.sbai.httplib.CookieManger;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ArticleCollectionFragment extends BaseFragment {

    private static final int REQ_CODE_ARTICLE_DETAIL_PAGE = 491;

    @BindView(android.R.id.list)
    ListView mList;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    Unbinder unbinder;
    @BindView(R.id.layout)
    LinearLayout mLayout;
    private ArticleCollectionAdapter mArticleCollectionAdapter;
    private ListEmptyView mListEmptyView;
    private int mPage;
    private HashSet<Integer> mSet;

    public ArticleCollectionFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_refresh_listview, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void requestMyArticleCollect() {
        Client.requestMyCollection(MyCollect.COLLECTI_TYPE_ARTICLE, mPage)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<MyCollect>>, List<MyCollect>>() {
                    @Override
                    protected void onRespSuccessData(List<MyCollect> data) {
                        updateArticleCollectionList(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void updateArticleCollectionList(List<MyCollect> data) {
        if (data == null) return;

        if (mSet.isEmpty()) {
            mArticleCollectionAdapter.clear();
        }

        if (data.size() < Client.DEFAULT_PAGE_SIZE) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }

        for (MyCollect result : data) {
            if (mSet.add(result.getId())) {
                mArticleCollectionAdapter.add(result);
            }
        }
    }

    private void initView() {
        mSet = new HashSet<>();
        initListEmptyView();

        mList.setDivider(null);
        mList.setVerticalScrollBarEnabled(false);
        mList.setHorizontalScrollBarEnabled(false);
        View view = new View(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Display.dp2Px(1, getResources()));
        view.setLayoutParams(params);
        mList.addFooterView(view);
        mList.setEmptyView(mListEmptyView);
        mArticleCollectionAdapter = new ArticleCollectionAdapter(getActivity());
        mList.setAdapter(mArticleCollectionAdapter);
        initSwipeRefreshLayout();
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCollect myCollect = (MyCollect) parent.getAdapter().getItem(position);
                if (myCollect != null) {
                    Intent intent = new Intent(getActivity(), DailyReportDetailActivity.class);
                    // 乐米日报列表返回的数据没有format  这里的format是1 暂时不传
//                    intent.putExtra(DailyReportDetailActivity.EX_FORMAT, myCollect.getFormat());
                    intent.putExtra(DailyReportDetailActivity.EX_ID, myCollect.getMongoId());
                    intent.putExtra(DailyReportDetailActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie());
                    startActivityForResult(intent, REQ_CODE_ARTICLE_DETAIL_PAGE);
                }
            }
        });
    }


    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestMyArticleCollect();
            }
        });
    }

    private void refreshData() {
        mSet.clear();
        mPage = 0;
        mSwipeRefreshLayout.setLoadMoreEnable(true);
        requestMyArticleCollect();
    }

    private void initListEmptyView() {
        mListEmptyView = new ListEmptyView(getActivity());
        mListEmptyView.setTitleImage(R.drawable.common_ic_no_article);
        mListEmptyView.setGoingBg(R.drawable.bg_annals_materials_4);
        mListEmptyView.setContentText(R.string.you_not_has_article_collection);
        mListEmptyView.setHintText(R.string.collect_favorite_articles_here);
        mListEmptyView.setGoingText(R.string.look_report);
        mListEmptyView.setOnGoingViewClickListener(new ListEmptyView.OnGoingViewClickListener() {
            @Override
            public void onGoingViewClick() {
                Launcher.with(getActivity(), InformationAndFocusNewsActivity.class)
                        .putExtra(ExtraKeys.PAGE_INDEX, 1)
                        .execute();
            }
        });
        mLayout.addView(mListEmptyView);
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    static class ArticleCollectionAdapter extends ArrayAdapter<MyCollect> {

        public ArticleCollectionAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_focus_news, null, true);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {

            @BindView(R.id.title)
            TextView mTitle;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.image)
            ImageView mImage;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(MyCollect item, Context context) {
                Glide.with(context).load(item.getCoverUrl()).into(mImage);
                mTime.setText(DateUtil.formatDefaultStyleTime(item.getCreateTime()));
                mTitle.setText(item.getTitle());
            }
        }
    }
}
