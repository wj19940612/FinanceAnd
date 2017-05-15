package com.sbai.finance.fragment.trade;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sbai.finance.R;
import com.sbai.finance.activity.economiccircle.OpinionDetailsActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Opinion;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.android.volley.Request.Method.HEAD;

public class ViewpointFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    Unbinder unbinder;
    TextView mFootView;
    TextView mEmpty;

    private OpinionAdapter mOpinionAdapter;
    private List<Opinion> mOpinionList;

    private int mPage = 0;
    private int mPageSize = 15;
    private int mVarietyId;
    boolean mLoadMore = true;

    public static ViewpointFragment newInstance(int varietyId) {
        ViewpointFragment fragment = new ViewpointFragment();
        Bundle args = new Bundle();
        args.putInt(Launcher.EX_PAYLOAD, varietyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVarietyId = getArguments().getInt(Launcher.EX_PAYLOAD);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opinion, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOpinionList = new ArrayList<>();
        initViewWithAdapter();
        getPointList();

    }

    private void initViewWithAdapter() {
        mFootView = new TextView(getActivity());
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        mFootView.setPadding(padding, padding, padding, padding);
        mFootView.setText(getText(R.string.load_all));
        mFootView.setGravity(Gravity.CENTER);
        mFootView.setTextColor(ContextCompat.getColor(getActivity(), R.color.secondaryText));
        mFootView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.greyLightAssist));

        mEmpty = new TextView(getActivity());
        mEmpty.setText(getText(R.string.quick_publish));
        mEmpty.setPadding(0, 10 * padding, 0, 0);
        mEmpty.setGravity(Gravity.CENTER_HORIZONTAL);
        mEmpty.setTextColor(ContextCompat.getColor(getActivity(), R.color.assistText));
        mEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        mEmpty.setCompoundDrawablePadding(padding);
        mEmpty.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.img_no_message, 0, 0);
        mOpinionAdapter = new OpinionAdapter(R.layout.row_opinion, mOpinionList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mOpinionAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
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
                getPointList();
            }
        }
    };

    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

    private void getPointList() {
        Client.findViewpoint(mPage, mPageSize, mVarietyId)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<Opinion>>, List<Opinion>>() {
                    @Override
                    protected void onRespSuccessData(List<Opinion> data) {
                        updateViewWithData(data, false);
                    }
                })
                .fire();
    }

    private void updateViewWithData(List<Opinion> data, boolean needScrollToTop) {
        //获取的数据为空 并且原来就无数据 则显示空view
        if (data == null || data.size() == 0 && mOpinionList.size() == 0) {
            mOpinionAdapter.removeAllFooterView();
            mOpinionAdapter.addFooterView(mEmpty);
            mOpinionAdapter.notifyDataSetChanged();
        }
        if (data != null && data.size() > 0) {
            mOpinionList.addAll(data);
            if (data.size() < mPageSize) {
                mOpinionAdapter.removeAllFooterView();
                mOpinionAdapter.addFooterView(mFootView);
                mLoadMore = false;
            } else {
                mOpinionAdapter.removeAllFooterView();
                mLoadMore = true;
                mPage++;
            }
            mOpinionAdapter.notifyDataSetChanged();
            if (needScrollToTop) {
                mRecyclerView.scrollToPosition(0);
            }
        }
    }

    public void refreshPointList() {
        getNewPointList();
    }

    private void getNewPointList() {
        mPage = 0;
        mOpinionList.clear();
        Client.findViewpoint(mPage, mPageSize, mVarietyId)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<Opinion>>, List<Opinion>>() {
                    @Override
                    protected void onRespSuccessData(List<Opinion> data) {
                        updateViewWithData(data, true);
                    }
                })
                .fire();
    }


    //刷新某条点赞和评论
    public void updateItemById(int id, int replyCount, int praiseCount) {
        int position = 0;
        for (Opinion opinion : mOpinionList) {
            if (opinion.getId() == id) {
                break;
            } else {
                position++;
            }
        }
        mOpinionList.get(position).setReplyCount(replyCount);
        mOpinionList.get(position).setPraiseCount(praiseCount);
        mOpinionAdapter.notifyItemChanged(position);
    }

    //根据user id刷新关注
    public void updateItemByUserId(int userId, boolean isAttention) {
        int attentionStatus = isAttention ? 2 : 1;  //2已关注 1 未关注
        for (Opinion opinion : mOpinionList) {
            if (opinion.getUserId() == userId) {
                opinion.setIsAttention(attentionStatus);
            }
        }
        mOpinionAdapter.notifyDataSetChanged();
    }

    public void shieldUserByUserId(int userId, boolean isShield) {
        if (isShield) {
            for (Iterator it = mOpinionList.iterator(); it.hasNext(); ) {
                Opinion opinion = (Opinion) it.next();
                if (opinion.getUserId() == userId) {
                    it.remove();
                }
            }
            mOpinionAdapter.notifyDataSetChanged();
            if (mOpinionList.size() == 0) {
                mOpinionAdapter.removeAllFooterView();
                mOpinionAdapter.addFooterView(mEmpty);
                mOpinionAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private class OpinionAdapter extends BaseQuickAdapter<Opinion, BaseViewHolder> {

        private OpinionAdapter(int layoutResId, List<Opinion> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Opinion item) {
            bindDataWithView(helper, item);
        }

        private void bindDataWithView(BaseViewHolder helper, final Opinion item) {
            String attend = item.getIsAttention() == 1 ? "" : getString(R.string.is_attention);
            String time = DateUtil.getFormatTime(item.getCreateTime());
            helper.setText(R.id.userName, item.getUserName())
                    .setText(R.id.followed, attend)
                    .setText(R.id.publishTime, time);

            if (item.getReplyCount() > 999) {
                ((TextView) helper.getView(R.id.commentNum))
                        .setText(R.string.number999);
            } else {
                ((TextView) helper.getView(R.id.commentNum))
                        .setText(String.valueOf(item.getReplyCount()));
            }

            if (item.getPraiseCount() > 999) {
                ((TextView) helper.getView(R.id.likeNum))
                        .setText(R.string.number999);
            } else {
                ((TextView) helper.getView(R.id.likeNum))
                        .setText(String.valueOf(item.getPraiseCount()));
            }
            Glide.with(getActivity()).load(item.getUserPortrait())
                    .bitmapTransform(new GlideCircleTransform(getActivity()))
                    .placeholder(R.drawable.ic_default_avatar_big)
                    .into((ImageView) helper.getView(R.id.avatar));
            if (item.getGuessPass() == 0) {
                if (item.getDirection() == 1) {
                    ((TextView) helper.getView(R.id.opinion))
                            .setText(StrUtil.mergeTextWithImage(getActivity(), item.getContent(), R.drawable.ic_opinion_up));
                } else {
                    ((TextView) helper.getView(R.id.opinion))
                            .setText(StrUtil.mergeTextWithImage(getActivity(), item.getContent(), R.drawable.ic_opinion_down));
                }
            } else if (item.getGuessPass() == 1) {
                if (item.getDirection() == 1) {
                    ((TextView) helper.getView(R.id.opinion))
                            .setText(StrUtil.mergeTextWithImage(getActivity(), item.getContent(), R.drawable.ic_opinion_up_succeed));
                } else {
                    ((TextView) helper.getView(R.id.opinion))
                            .setText(StrUtil.mergeTextWithImage(getActivity(), item.getContent(), R.drawable.ic_opinion_down_succeed));
                }
            } else {
                if (item.getDirection() == 1) {
                    ((TextView) helper.getView(R.id.opinion))
                            .setText(StrUtil.mergeTextWithImage(getActivity(), item.getContent(), R.drawable.ic_opinion_up_failed));
                } else {
                    ((TextView) helper.getView(R.id.opinion))
                            .setText(StrUtil.mergeTextWithImage(getActivity(), item.getContent(), R.drawable.ic_opinion_down_failed));
                }
            }
            helper.getView(R.id.avatar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LocalUser.getUser().isLogin()) {
                        Launcher.with(getActivity(), UserDataActivity.class)
                                .putExtra(Launcher.USER_ID, item.getUserId())
                                .execute();
                    } else {
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                }
            });
            helper.getView(R.id.contentRl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LocalUser.getUser().isLogin()) {
                        Launcher.with(getActivity(), OpinionDetailsActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, item.getId())
                                .execute();
                    } else {
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                }
            });
            helper.getView(R.id.commentRl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LocalUser.getUser().isLogin()) {
                        Launcher.with(getActivity(), OpinionDetailsActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, item.getId())
                                .execute();
                    } else {
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                }
            });
        }
    }
}
