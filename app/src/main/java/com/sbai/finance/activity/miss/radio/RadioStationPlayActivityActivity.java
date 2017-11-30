package com.sbai.finance.activity.miss.radio;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.miss.CommentActivity;
import com.sbai.finance.activity.miss.MediaPlayActivity;
import com.sbai.finance.activity.training.LookBigPictureActivity;
import com.sbai.finance.fragment.dialog.ReplyDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.Praise;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.miss.QuestionReply;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.model.radio.RadioDetails;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.service.MediaPlayService;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MissAudioManager;
import com.sbai.finance.utils.OnItemClickListener;
import com.sbai.finance.utils.RenderScriptGaussianBlur;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.HasLabelImageLayout;
import com.sbai.finance.view.MissFloatWindow;
import com.sbai.finance.view.dialog.ShareDialog;
import com.sbai.finance.view.radio.RadioInfoLayout;
import com.sbai.finance.view.radio.RadioInfoPlayLayout;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RadioStationPlayActivityActivity extends MediaPlayActivity {

    @BindView(R.id.radioPlayLL)
    RadioInfoPlayLayout mRadioPlayLL;
    @BindView(R.id.radioInfoLayout)
    RadioInfoLayout mRadioInfoLayout;
    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.share)
    ImageView mShare;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.missFloatWindow)
    MissFloatWindow mMissFloatWindow;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.bg)
    ImageView mBg;
    @BindView(R.id.split)
    View mSplit;
    @BindView(R.id.review)
    TextView mReview;
    @BindView(R.id.radioCollect)
    TextView mRadioCollect;

    private Radio mRadio;

    private boolean mLoadMore;
    private ReplyDialogFragment mReplyDialogFragment;

    private HashSet<String> mSet;
    private ArrayList<QuestionReply.DataBean> mQuestionReplyList;
    private RadioReviewAdapter mRadioReviewAdapter;
    private int mPage;
    private boolean mPlayThisVoice;

    protected MediaPlayService mMediaPlayService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMediaPlayService = ((MediaPlayService.MediaBinder) iBinder).getMediaPlayService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    private int mVoiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_station_play_activity);
        setSupportActionBar(mToolbar);
        ButterKnife.bind(this);
        translucentStatusBar();

        mSet = new HashSet<>();
        initData();
        initView();

        Intent intent = new Intent(getActivity(), MediaPlayService.class);
        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestAudioDetails();
        requestRadioReplyList(false);
        requestRadioDetails();
    }

    private void requestRadioReplyList(final boolean showToast) {
        Client.requestRadioReplyList(mPage, mRadio.getRadioId(), mRadio.getAudioId())
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<QuestionReply>, QuestionReply>() {
                    @Override
                    protected void onRespSuccessData(QuestionReply questionReply) {
                        if (questionReply.getData() != null) {
                            updateRadioReply(questionReply.getData(), questionReply.getResultCount());
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (mSwipeRefreshLayout.isRefreshing())
                            mSwipeRefreshLayout.setRefreshing(false);
                    }
                })
                .fireFree();
    }

    private void updateRadioReply(List<QuestionReply.DataBean> questionReplyList, int resultCount) {
        mRadioInfoLayout.setReviewNumber(questionReplyList.size());

        if (questionReplyList.size() < Client.DEFAULT_PAGE_SIZE) {
            mLoadMore = false;
        } else {
            mLoadMore = true;
            mPage++;
        }

        if (mSet.isEmpty()) mRadioReviewAdapter.clear();
        for (QuestionReply.DataBean questionReply : questionReplyList) {
            if (questionReply != null) {
                if (mSet.add(questionReply.getId())) {
                    mRadioReviewAdapter.add(questionReply);
                }
            }
        }
    }

    private void requestRadioDetails() {
        if (mRadio != null) {
            Client.requestRadioDetails(mRadio.getRadioId())
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<RadioDetails>, RadioDetails>() {

                        @Override
                        protected void onRespSuccessData(RadioDetails radioDetails) {
                            mRadioInfoLayout.setRadioDetails(radioDetails);
                        }
                    })
                    .fireFree();
        }
    }

    private void initData() {
        mRadio = getIntent().getParcelableExtra(ExtraKeys.RADIO);
        mVoiceId = getIntent().getIntExtra(ExtraKeys.IAudio, -1);
        if (mVoiceId == -1 && mRadio != null) {
            mVoiceId = mRadio.getId();
        }
        updateAudio();
    }

    private void updateAudio() {
        if (mRadio != null) {
            mRadioInfoLayout.setRadio(mRadio);
            mRadioPlayLL.setRadio(mRadio);
            mRadioPlayLL.setPlayStatus(mRadio);
            initMissFloatWindow();
        }
    }

    private void initMissFloatWindow() {
        MissAudioManager missAudioManager = MissAudioManager.get();
        MissAudioManager.IAudio audio = missAudioManager.getAudio();
        if (missAudioManager.isPlaying()) {
            if (audio instanceof Question) {
                mMissFloatWindow.startAnim();
                mMissFloatWindow.setVisibility(View.VISIBLE);
                Question question = (Question) audio;
                mMissFloatWindow.setMissAvatar(question.getCustomPortrait());
                mPlayThisVoice = true;
            } else if (audio instanceof Radio) {
                Radio playRadio = (Radio) audio;
                mMissFloatWindow.setMissAvatar(playRadio.getUserPortrait());
                mMissFloatWindow.startAnim();
                if (mRadio != null && mRadio.getId() == ((Radio) audio).getId()) {
                    mMissFloatWindow.setVisibility(View.GONE);
                    mPlayThisVoice = true;
                    mRadioPlayLL.startAnimation();
                } else {
                    mMissFloatWindow.setVisibility(View.VISIBLE);
                    mPlayThisVoice = false;
                }
            }
        } else {
            if (mRadio == null) return;
            if (mMediaPlayService != null
                    && !MissAudioManager.get().isStarted(mRadio)
                    && !MissAudioManager.get().isPaused(mRadio)) {
                mMediaPlayService.startPlay(mRadio, MediaPlayService.MEDIA_SOURCE_RECOMMEND_RADIO);
                mRadioPlayLL.setPlayStatus(mRadio);
                updateListenNumber();
            }
        }
        if (mRadio != null && MissAudioManager.get().isPaused(mRadio)) {
            mRadioPlayLL.setMediaPlayProgress(MissAudioManager.get().getCurrentPosition(), mRadio.getAudioTime() * 1000);
        }
    }

    private void updateListenNumber() {
        if (mRadio == null) return;
        Client.listenRadioAudio(mRadio.getAudioId())
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {

                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        mRadioPlayLL.updateListenNumber();
                    }
                })
                .fireFree();
    }

    private void requestAudioDetails() {
        Client.requestVoiceDetails(mVoiceId)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Radio>, Radio>() {

                    @Override
                    protected void onRespSuccessData(Radio radio) {
                        if (MissAudioManager.get().isPlaying() && MissAudioManager.get().getAudio() instanceof Radio) {
                            mMissFloatWindow.setMissAvatar(radio.getUserPortrait());
                        }
                        updateAudioDetail(radio);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (mSwipeRefreshLayout.isRefreshing())
                            mSwipeRefreshLayout.setRefreshing(false);
                    }
                })
                .fireFree();
    }

    private void updateAudioDetail(Radio radio) {
        mRadio = radio;
        mRadioCollect.setSelected(radio.getCollect() == RadioDetails.COLLECT);
        updateAudio();
    }

    private void initView() {
        mAppBarLayout.addOnOffsetChangedListener(sOnOffsetChangedListener);
        if (mRadio != null) {
            GlideApp.with(getActivity())
                    .asBitmap()
                    .load(mRadio.getAudioCover())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            //后台有可能传入gif图片 会抛出android.renderscript.RSIllegalArgumentException: USAGE_SHARED cannot be used with a Bitmap that has a null config.
                            if (resource != null) {
                                try {
                                    Bitmap gaussianBlur = new RenderScriptGaussianBlur(getActivity()).gaussianBlur(25, resource);
                                    mBg.setImageBitmap(gaussianBlur);
                                } catch (Exception e) {
                                    GlideApp.with(getActivity()).load(mRadio.getAudioCover()).into(mBg);
                                }
                            } else {
                                GlideApp.with(getActivity()).load(mRadio.getAudioCover()).into(mBg);
                            }
                        }
                    });
        }

        mQuestionReplyList = new ArrayList<>();
        mRadioReviewAdapter = new RadioReviewAdapter(mQuestionReplyList, getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRadioReviewAdapter);
        mRadioReviewAdapter.setOnItemClickListener(new OnItemClickListener<QuestionReply.DataBean>() {
            @Override
            public void onItemClick(QuestionReply.DataBean dataBean, int position) {
                if (dataBean != null) {
                    if (mReplyDialogFragment == null) {
                        mReplyDialogFragment = ReplyDialogFragment.newInstance(dataBean, mRadio);
                    }
                    if (!mReplyDialogFragment.isAdded()) {
                        mReplyDialogFragment.show(getSupportFragmentManager());
                    }

                    mReplyDialogFragment.setCallback(new ReplyDialogFragment.Callback() {
                                                         @Override
                                                         public void onLoginSuccess() {
                                                             MissAudioManager.get().stop();
                                                         }
                                                     }
                    );
                }
            }
        });


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRadioReviewData();
            }
        });


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLoadMore && recycleIsScrollBottom(recyclerView)) {
                    requestRadioReplyList(false);
                }
            }

            private boolean recycleIsScrollBottom(RecyclerView recyclerView) {
                if (recyclerView == null) return false;
                return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange();
            }
        });

        mRadioPlayLL.setOnRadioPlayListener(new RadioInfoPlayLayout.OnRadioPlayListener() {
            @Override
            public void onRadioPlay() {
                if (mRadio == null) return;
                if (MissAudioManager.get().isStarted(mRadio)) {
                    if (mMediaPlayService != null) {
                        mMediaPlayService.onPausePlay(mRadio);
                    }
                } else if (MissAudioManager.get().isPaused(mRadio)) {
                    if (mMediaPlayService != null) {
                        mMediaPlayService.onResume();
                    }
                } else {
                    if (mMediaPlayService != null) {
                        mMediaPlayService.startPlay(mRadio, MediaPlayService.MEDIA_SOURCE_RECOMMEND_RADIO);
                    }
                    updateListenNumber();
                }
            }

            @Override
            public void onSeekChange(int progress) {
                if (mMediaPlayService != null) {
                    mMediaPlayService.seekTo(progress);
                }
            }
        });
    }

    private void refreshRadioReviewData() {
        mSet.clear();
        mPage = 0;
        requestRadioReplyList(true);
        requestAudioDetails();
    }

    private AppBarLayout.OnOffsetChangedListener sOnOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            boolean b = verticalOffset > -1;
            if (b != mSwipeRefreshLayout.isEnabled()) {
                mSwipeRefreshLayout.setEnabled(b);
            }

            if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (mRadio != null)
                    mTitle.setText(mRadio.getAudioName());
            } else {
                if (!TextUtils.isEmpty(mTitle.getText())) {
                    mTitle.setText("");
                }
            }

            if (verticalOffset < -400) {
                if (MissAudioManager.get().isStarted(mRadio)) {
                    if (mMissFloatWindow.getVisibility() == View.GONE) {
                        mMissFloatWindow.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (MissAudioManager.get().isStarted(mRadio)) {
                    if (mMissFloatWindow.getVisibility() == View.VISIBLE) {
                        mMissFloatWindow.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAppBarLayout.removeOnOffsetChangedListener(sOnOffsetChangedListener);
        sOnOffsetChangedListener = null;
        unbindService(mServiceConnection);
        mBg.clearAnimation();
    }

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();
        intentFilter.addAction(CommentActivity.BROADCAST_ACTION_REPLY_SUCCESS);
        intentFilter.addAction(LoginActivity.ACTION_LOGIN_SUCCESS);
        intentFilter.addAction(LoginActivity.ACTION_LOGOUT_SUCCESS);
        return intentFilter;
    }

    @Override
    public void onMediaPlayStart(int IAudioId, int source) {
        changeFloatWindowView();
    }

    @Override
    public void onMediaPlay(int IAudioId, int source) {
        mMissFloatWindow.startAnim();
        mRadioPlayLL.setPlayStatus(mRadio);
        changeFloatWindowView();
    }

    @Override
    public void onMediaPlayResume(int IAudioId, int source) {
        mMissFloatWindow.startAnim();
        mRadioPlayLL.setPlayStatus(mRadio);
        mRadioPlayLL.startAnimation();
        changeFloatWindowView();
    }

    @Override
    public void onMediaPlayPause(int IAudioId, int source) {
        mRadioPlayLL.setPlayStatus(mRadio);
        mMissFloatWindow.stopAnim();
        mRadioPlayLL.stopAnimation();
        mMissFloatWindow.setVisibility(View.GONE);
    }

    @Override
    protected void onMediaPlayStop(int IAudioId, int source) {
        mMissFloatWindow.stopAnim();
        mMissFloatWindow.setVisibility(View.GONE);
        if (source == MediaPlayService.MEDIA_SOURCE_RECOMMEND_RADIO) {
            mRadioPlayLL.onPlayStop();
        }
    }

    @Override
    protected void onMediaPlayError(int IAudioId, int source) {
        mMissFloatWindow.clearAnimation();
        mMissFloatWindow.setVisibility(View.GONE);
        if (source == MediaPlayService.MEDIA_SOURCE_RECOMMEND_RADIO) {
            mRadioPlayLL.stopAnimation();
        }
    }

    @Override
    public void onOtherReceive(Context context, Intent intent) {
        if (CommentActivity.BROADCAST_ACTION_REPLY_SUCCESS.equalsIgnoreCase(intent.getAction())
                || LoginActivity.ACTION_LOGIN_SUCCESS.equalsIgnoreCase(intent.getAction())
                || LoginActivity.ACTION_LOGOUT_SUCCESS.equalsIgnoreCase(intent.getAction())) {
            refreshRadioReviewData();
        }
    }

    @Override
    protected void onMediaPlayCurrentPosition(int IAudioId, int source, int mediaPlayCurrentPosition, int totalDuration) {
        if (source == MediaPlayService.MEDIA_SOURCE_RECOMMEND_RADIO && mPlayThisVoice) {
            mRadioPlayLL.setMediaPlayProgress(mediaPlayCurrentPosition, totalDuration);
        }
    }

    private void changeFloatWindowView() {
        MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
        if (audio instanceof Question) {
            mMissFloatWindow.setMissAvatar(((Question) audio).getCustomPortrait());
        } else if (audio instanceof Radio) {
            mMissFloatWindow.setMissAvatar(((Radio) audio).getUserPortrait());
            if (mRadio.getId() == audio.getAudioId()) {
                if (mRadio != null) {
                    mMissFloatWindow.setMissAvatar(mRadio.getUserPortrait());
                }
                mPlayThisVoice = true;
                mRadioPlayLL.startAnimation();
            } else {
                mRadioPlayLL.stopAnimation();
            }
        }
    }


    @OnClick({R.id.back, R.id.share, R.id.review, R.id.radioCollect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.share:
                radioShare();
                break;
            case R.id.review:
                commentRadio();
                break;
            case R.id.radioCollect:
                if (LocalUser.getUser().isLogin()) {
                    radioCollect();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
        }
    }

    private void radioShare() {
        if (mRadio != null) {
            ShareDialog.with(getActivity())
                    .setShareUrl(String.format(Client.SHARE_URL_RADIO, mRadio.getRadioId(), mRadio.getId()))
                    .setShareTitle(mRadio.getAudioName())
                    .setShareDescription(mRadio.getAudioIntroduction())
                    .setShareThumbUrl(mRadio.getAudioCover())
                    .setListener(new ShareDialog.OnShareDialogCallback() {
                        @Override
                        public void onSharePlatformClick(ShareDialog.SHARE_PLATFORM platform) {
                            if (LocalUser.getUser().isLogin()) {
                                Client.share().fireFree();
                            }
                        }

                        @Override
                        public void onFeedbackClick(View view) {

                        }
                    })
                    .show();
        }
    }

    private void radioCollect() {
        if (mRadio != null) {
            Client.collect(String.valueOf(mRadio.getId()), Radio.USER_COLLECT_TYPE_VOICE)
                    .setCallback(new Callback<Resp<Object>>() {
                        @Override
                        protected void onRespSuccess(Resp<Object> resp) {
                            ToastUtil.show(resp.getMsg());
                            mRadio.setCollect(mRadio.getCollect() == RadioDetails.COLLECT ? 0 : RadioDetails.COLLECT);
                            mRadioCollect.setSelected(mRadio.getCollect() == RadioDetails.COLLECT);
                        }
                    })
                    .fireFree();
        }
    }

    private void commentRadio() {
        if (mRadio != null) {
            if (LocalUser.getUser().isLogin()) {
                openCommentPage();
            } else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, CommentActivity.REQ_CODE_COMMENT_LOGIN);
            }
        }
    }

    private void openCommentPage() {
        Launcher.with(getActivity(), CommentActivity.class)
                .putExtra(ExtraKeys.RADIO, mRadio.getRadioId())
                .putExtra(ExtraKeys.IAudio, mRadio.getAudioId())
                .putExtra(ExtraKeys.COMMENT_SOURCE, CommentActivity.COMMENT_TYPE_RADIO)
                .executeForResult(CommentActivity.REQ_CODE_COMMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == BaseActivity.RESULT_OK) {
            switch (requestCode) {
                case CommentActivity.REQ_CODE_COMMENT_LOGIN:
                    openCommentPage();
                    break;
                case CommentActivity.REQ_CODE_COMMENT:
                    refreshRadioReviewData();
                    break;
            }
        }
    }

    public static class RadioReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<QuestionReply.DataBean> mDataBeanArrayList;
        private Context mContext;
        private OnItemClickListener mOnItemClickListener;

        public RadioReviewAdapter(ArrayList<QuestionReply.DataBean> questionReplyList, Context context) {
            mDataBeanArrayList = questionReplyList;
            mContext = context;
        }

        public void clear() {
            mDataBeanArrayList.clear();
            notifyDataSetChanged();
        }

        public void add(QuestionReply.DataBean dataBean) {
            mDataBeanArrayList.add(dataBean);
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(OnItemClickListener<QuestionReply.DataBean> onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_question_reply, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).bindDataWithView(mDataBeanArrayList.get(position), mContext, mOnItemClickListener, position);
        }

        @Override
        public int getItemCount() {
            return mDataBeanArrayList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.avatar)
            HasLabelImageLayout mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.opinionContent)
            TextView mOpinionContent;
            @BindView(R.id.replyName)
            TextView mReplyName;
            @BindView(R.id.replyContent)
            TextView mReplyContent;
            @BindView(R.id.replyArea)
            LinearLayout mReplyArea;
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.reviewPriceCount)
            TextView mReviewPriceCount;
            @BindView(R.id.rootView)
            LinearLayout mRootView;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final QuestionReply.DataBean item, final Context context, final OnItemClickListener onItemClickListener, final int position) {
                if (item == null) return;

                QuestionReply.DataBean.UserModelBean userModelBean = item.getUserModel();
                if (userModelBean != null) {
                    int userIdentity = userModelBean.getCustomId() != 0 ? Question.USER_IDENTITY_HOST : Question.USER_IDENTITY_ORDINARY;
                    mAvatar.setAvatar(userModelBean.getUserPortrait(), userIdentity);
                    mUserName.setText(item.getUserModel().getUserName());
                    mAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Launcher.with(context, LookBigPictureActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, item.getUserModel().getUserPortrait())
                                    .putExtra(Launcher.EX_PAYLOAD_2, 0)
                                    .execute();
                        }
                    });
                } else {
                    mAvatar.setAvatar("", 0);
                    mUserName.setText("");

                    mAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Launcher.with(context, LookBigPictureActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, "")
                                    .putExtra(Launcher.EX_PAYLOAD_2, 0)
                                    .execute();
                        }
                    });
                }

                setReviewPrice(item.getPriseCount(), item.getIsPrise());

                mReviewPriceCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (LocalUser.getUser().isLogin()) {
                            Client.praiseMissReply(item.getId())
                                    .setCallback(new Callback2D<Resp<Praise>, Praise>() {
                                        @Override
                                        protected void onRespSuccessData(Praise data) {
                                            setReviewPrice(data.getPriseCount(), data.getIsPrise());
                                        }
                                    })
                                    .fireFree();
                        } else {
                            Launcher.with(context, LoginActivity.class).execute();
                        }
                    }
                });
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(item, position);
                        }
                    }
                });

                mPublishTime.setText(DateUtil.formatDefaultStyleTime(item.getCreateDate()));
                mOpinionContent.setText(item.getContent());

                if (item.getReplys() != null) {
                    mReplyArea.setVisibility(View.VISIBLE);
                    if (item.getReplys().size() == 0) {
                        mReplyArea.setVisibility(View.GONE);
                    } else {
                        mReplyArea.setVisibility(View.VISIBLE);
                        if (item.getReplys().get(0) != null) {
                            if (item.getReplys().get(0).getUserModel() != null) {
                                mReplyName.setText(context.getString(R.string.reply_name, item.getReplys().get(0).getUserModel().getUserName()));
                            } else {
                                mReplyName.setText("");
                            }
                            mReplyContent.setText(item.getReplys().get(0).getContent());
                        } else {
                            mReplyName.setText("");
                            mReplyContent.setText("");
                        }
                    }
                } else {
                    mReplyArea.setVisibility(View.GONE);
                }
            }

            private void setReviewPrice(int priseCount, int prise) {
                mReviewPriceCount.setText(String.valueOf(priseCount));
                if (prise == Praise.IS_PRAISE) {
                    mReviewPriceCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_praise, 0, 0, 0);
                } else {
                    mReviewPriceCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_unpraise, 0, 0, 0);
                }
            }
        }
    }

}
