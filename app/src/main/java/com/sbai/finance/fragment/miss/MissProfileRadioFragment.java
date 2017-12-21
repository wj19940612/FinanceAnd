package com.sbai.finance.fragment.miss;

import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sbai.finance.R;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.miss.MissProfileDetailActivity;
import com.sbai.finance.activity.miss.RadioStationListActivity;
import com.sbai.finance.fragment.MediaPlayFragment;
import com.sbai.finance.model.miss.Miss;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.miss.RadioInfo;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.ImageTextUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.audio.MissAudioManager;
import com.sbai.finance.view.MissFloatWindow;
import com.sbai.glide.GlideApp;
import com.sbai.httplib.ApiError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.sbai.finance.activity.miss.MissProfileDetailActivity.CUSTOM_ID;

/**
 * Created by Administrator on 2017\11\23 0023.
 */

public class MissProfileRadioFragment extends MediaPlayFragment {

    Unbinder mBind;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;

    Button mCreateRadio;
    MissFloatWindow mMissFloatWindow;

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

    public void setMiss(Miss miss) {

    }

    public void initScrollState() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        boolean isTop = layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
        if (mOnFragmentRecycleViewScrollListener != null) {
            mOnFragmentRecycleViewScrollListener.onSwipRefreshEnable(isTop, 0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MissProfileDetailActivity) {
            mOnFragmentRecycleViewScrollListener = (MissProfileQuestionFragment.OnFragmentRecycleViewScrollListener) context;
            mMissFloatWindow = ((MissProfileDetailActivity) context).getFloatWindow();
            mCreateRadio = ((MissProfileDetailActivity) context).getCreateRadioBtn();
            if (mCreateRadio != null) {
                mCreateRadio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Launcher.with(getActivity(), WebActivity.class)
                                .putExtra(WebActivity.EX_URL, Client.CREATE_RADIO_STATION)
                                .execute();
                    }
                });
            }
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
        mRootMissFloatWindow = mMissFloatWindow;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
        updateFloatState();
    }

    public void refresh() {
        Client.requestRadiosOfMiss(mCustomId).setTag(TAG).setCallback(new Callback2D<Resp<List<RadioInfo>>, List<RadioInfo>>() {
            @Override
            public void onFailure(ApiError apiError) {
                super.onFailure(apiError);
                mEmpty.setVisibility(View.VISIBLE);
                mRadioAdapter.clear();
                mRadioAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onRespSuccessData(List<RadioInfo> data) {
                if (data != null && data.size() != 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmpty.setVisibility(View.GONE);
                    updateRadioData(data);
                } else {
                    mEmpty.setVisibility(View.VISIBLE);
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
                Launcher.with(getActivity(), RadioStationListActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, radioInfo.getId())
                        .execute();
            }
        });

    }

    private void updateFloatState() {
        if (MissAudioManager.get().isPlaying()) {
            MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
            if (audio.getAvatar() != null) {
                mMissFloatWindow.startAnim();
                mMissFloatWindow.setVisibility(View.VISIBLE);
                mMissFloatWindow.setMissAvatar(audio.getAvatar());
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onMediaPlayStart(int IAudioId, int source) {
        MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
        if (audio instanceof Question) {
            mMissFloatWindow.setMissAvatar(((Question) audio).getCustomPortrait());
        }
    }

    @Override
    public void onMediaPlay(int IAudioId, int source) {
        mMissFloatWindow.startAnim();
    }

    @Override
    public void onMediaPlayResume(int IAudioId, int source) {
        mMissFloatWindow.setVisibility(View.VISIBLE);
        mMissFloatWindow.startAnim();
    }

    @Override
    public void onMediaPlayPause(int IAudioId, int source) {
        mMissFloatWindow.stopAnim();
        mMissFloatWindow.setVisibility(View.GONE);
    }

    @Override
    protected void onMediaPlayStop(int IAudioId, int source) {
        mMissFloatWindow.stopAnim();
        mMissFloatWindow.setVisibility(View.GONE);
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
            ((ViewHolder) holder).bindingData(mContext, mRadioInfos.get(position), mCallback, position, getCount());
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
            @BindView(R.id.coverRL)
            RelativeLayout mCoverRL;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindingData(Context context, final RadioInfo radioInfo, final CallBack callback, int position, int count) {
                if (position == count - 1) {
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) mContent.getLayoutParams();
                    layoutParams.setMargins((int) Display.dp2Px(14, context.getResources()), (int) Display.dp2Px(14, context.getResources()), (int) Display.dp2Px(14, context.getResources()), (int) Display.dp2Px(64, context.getResources()));
                } else {
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) mContent.getLayoutParams();
                    layoutParams.setMargins((int) Display.dp2Px(14, context.getResources()), (int) Display.dp2Px(14, context.getResources()), (int) Display.dp2Px(14, context.getResources()), 0);
                }
                GlideApp.with(context).load(radioInfo.getRadioCover())
                        .placeholder(R.drawable.ic_default_image)
                        .circleCrop().listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mCoverRL.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mCoverRL.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                        .into(mCover);

                mTitle.setText(radioInfo.getRadioName());
                setSpanIconText(mSubtitle, radioInfo.getRadioIntroduction(), context);
//                mSubtitle.setText(radioInfo.getRadioIntroduction());
                mNumber.setText(String.valueOf(radioInfo.getListenNumber()));
                mTime.setText(DateUtil.formatDefaultStyleTime(radioInfo.getShowTime()));
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
                ImageTextUtil.CenterImageSpan span = new ImageTextUtil.CenterImageSpan(context, R.drawable.ic_miss_profile_play_small);
                ss.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                textView.setText(ss);
            }
        }
    }
}
