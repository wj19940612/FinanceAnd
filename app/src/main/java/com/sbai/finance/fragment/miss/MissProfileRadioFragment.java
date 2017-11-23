package com.sbai.finance.fragment.miss;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.miss.MissProfileDetailActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.miss.RadioInfo;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.sbai.finance.activity.miss.MissProfileDetailActivity.CUSTOM_ID;

/**
 * Created by Administrator on 2017\11\23 0023.
 */

public class MissProfileRadioFragment extends BaseFragment {

    Unbinder mBind;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.createRadio)
    Button mCreateRadio;

    private int mCustomId;
    private List<RadioInfo> mRadioInfos;
    private RadioAdapter mRadioAdapter;

    MissProfileQuestionFragment.OnFragmentRecycleViewScrollListener mOnFragmentRecycleViewScrollListener;


    public static MissProfileRadioFragment newInstance(int customId) {
        MissProfileRadioFragment missProfileRadioFragment = new MissProfileRadioFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CUSTOM_ID, customId);
        missProfileRadioFragment.setArguments(bundle);
        return missProfileRadioFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MissProfileDetailActivity) {
            mOnFragmentRecycleViewScrollListener = (MissProfileQuestionFragment.OnFragmentRecycleViewScrollListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCustomId = getArguments().getInt(CUSTOM_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_miss_profile_radio, container, false);
        mBind = ButterKnife.bind(this, view);
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
        refresh();
    }

    public void refresh() {
        Client.requestRadiosOfMiss(mCustomId).setTag(TAG).setCallback(new Callback2D<Resp<List<RadioInfo>>, List<RadioInfo>>() {
            @Override
            protected void onRespSuccessData(List<RadioInfo> data) {
                if (data != null) {
                    updateRadioData(data);
                }
            }
        }).fireFree();
    }

    private void updateRadioData(List<RadioInfo> data) {
        mRadioAdapter.clear();
        mRadioAdapter.addAll(data);
    }

    private void initView() {
        mRadioInfos = new ArrayList<RadioInfo>();
        mRadioAdapter = new RadioAdapter(mRadioInfos, getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRadioAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                boolean isTop = layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
                if (mOnFragmentRecycleViewScrollListener != null) {
                    mOnFragmentRecycleViewScrollListener.onSwipRefreshEnable(isTop, 0);
                }
            }
        });

        mRadioAdapter.setCallback(new RadioAdapter.CallBack() {
            @Override
            public void onItemClick(RadioInfo radioInfo) {
                //TODO 点击跳转电台节目详情
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick(R.id.createRadio)
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.createRadio:
                //TODO 创建电台的H5
                break;
        }
    }

    static class RadioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        interface CallBack {
            public void onItemClick(RadioInfo radioInfo);
        }

        private Context mContext;
        private List<RadioInfo> mRadioInfos;
        private CallBack mCallback;

        public void setCallback(CallBack callback) {
            mCallback = callback;
        }

        public RadioAdapter(List<RadioInfo> radioInfos, Context context) {
            mContext = context;
            mRadioInfos = radioInfos;
        }

        public void clear() {
            mRadioInfos.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<RadioInfo> radioInfos) {
            mRadioInfos.addAll(radioInfos);
            notifyDataSetChanged();
        }

        public void add(RadioInfo radioInfo) {
            mRadioInfos.add(radioInfo);
            notifyDataSetChanged();
        }

        public int getCount() {
            return mRadioInfos == null ? 0 : mRadioInfos.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_miss_profile_radio, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).bindingData(mContext, mRadioInfos.get(position), mCallback);
        }

        @Override
        public int getItemCount() {
            return mRadioInfos != null ? mRadioInfos.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.cover)
            ImageView mCover;
            @BindView(R.id.subtitle)
            TextView mSubtitle;
            @BindView(R.id.title)
            TextView mTitle;
            @BindView(R.id.number)
            TextView mNumber;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.content)
            CardView mContent;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindingData(Context context, final RadioInfo radioInfo, final CallBack callback) {
                GlideApp.with(context).load(radioInfo.getRadioCover())
                        .placeholder(R.drawable.ic_default_image)
                        .circleCrop()
                        .into(mCover);

                mTitle.setText(radioInfo.getRadioName());
                setSpanIconText(mSubtitle, radioInfo.getRadioIntroduction(), context);
//                mSubtitle.setText(radioInfo.getRadioIntroduction());
                mNumber.setText(String.valueOf(radioInfo.getListenNumber()));
                mTime.setText(DateUtil.formatDefaultStyleTime(radioInfo.getCreateTime()));
                mContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (callback != null) {
                            callback.onItemClick(radioInfo);
                        }
                    }
                });
            }

            private void setSpanIconText(TextView textView, String str, Context context) {
                SpannableString ss = new SpannableString("  " + str);
                CenterImageSpan span = new CenterImageSpan(context, R.drawable.ic_miss_profile_play_small);
                ss.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                textView.setText(ss);
            }
        }
    }

    public static class CenterImageSpan extends ImageSpan {
        public CenterImageSpan(Context arg0, int drawable) {
            super(arg0, drawable);
        }

        public int getSize(Paint paint, CharSequence text, int start, int end,
                           Paint.FontMetricsInt fm) {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            if (fm != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fm.ascent = -bottom;
                fm.top = -bottom;
                fm.bottom = top;
                fm.descent = top;
            }
            return rect.right;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end,
                         float x, int top, int y, int bottom, Paint paint) {
            Drawable b = getDrawable();
            canvas.save();
            int transY = 0;
            transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        }
    }
}
