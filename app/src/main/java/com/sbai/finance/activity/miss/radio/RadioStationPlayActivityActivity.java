package com.sbai.finance.activity.miss.radio;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.model.radio.RadioDetails;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.view.radio.RadioInfoLayout;
import com.sbai.finance.view.radio.RadioInfoPlayLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RadioStationPlayActivityActivity extends BaseActivity {


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
    private Radio mRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_station_play_activity);
        setSupportActionBar(mToolbar);
        translucentStatusBar();

        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestRadioDetails(mRadio);
    }

    private void initData() {
        mRadio = getIntent().getParcelableExtra(ExtraKeys.RADIO);
        if (mRadio != null) {
            mRadioInfoLayout.setRadio(mRadio);
            mRadioPlayLL.setRadio(mRadio);
        }
    }

    private void requestRadioDetails(Radio radio) {
        if (radio != null) {
            Client.requestRadioDetails(radio.getRadioId())
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<RadioDetails>, RadioDetails>() {

                        @Override
                        protected void onRespSuccessData(RadioDetails data) {
                            mRadioInfoLayout.setRadioDetails(data);
                        }

                    })
                    .fireFree();
        }
    }

    private void initView() {
        mAppBarLayout.addOnOffsetChangedListener(sOnOffsetChangedListener);
    }

    private AppBarLayout.OnOffsetChangedListener sOnOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (mRadio != null)
                    mTitle.setText(mRadio.getAudioName());
            } else {
                if (!TextUtils.isEmpty(mTitle.getText())) {
                    mTitle.setText("");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAppBarLayout.removeOnOffsetChangedListener(sOnOffsetChangedListener);
        sOnOffsetChangedListener = null;
    }

    @OnClick({R.id.back, R.id.share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.share:
                // TODO: 2017/11/23 分享
                break;
        }
    }

    static class RadioReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_question_reply, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((RadioReviewAdapter.ViewHolder)holder).bindDataWithView();
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
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

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView() {
//                if (item.getUserModel() != null) {
//                    GlideApp.with(context).load(item.getUserModel().getUserPortrait())
//                            .placeholder(R.drawable.ic_default_avatar)
//                            .circleCrop()
//                            .into(mAvatar);
//
//                    mUserName.setText(item.getUserModel().getUserName());
//
//                    mAvatar.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Launcher.with(context, LookBigPictureActivity.class)
//                                    .putExtra(Launcher.EX_PAYLOAD, item.getUserModel().getUserPortrait())
//                                    .putExtra(Launcher.EX_PAYLOAD_2, 0)
//                                    .execute();
//                        }
//                    });
//                } else {
//                    GlideApp.with(context).load(R.drawable.ic_default_avatar)
//                            .circleCrop()
//                            .into(mAvatar);
//
//                    mUserName.setText("");
//
//                    mAvatar.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Launcher.with(context, LookBigPictureActivity.class)
//                                    .putExtra(Launcher.EX_PAYLOAD, "")
//                                    .putExtra(Launcher.EX_PAYLOAD_2, 0)
//                                    .execute();
//                        }
//                    });
//                }
//
//                mPublishTime.setText(DateUtil.formatDefaultStyleTime(item.getCreateDate()));
//                mOpinionContent.setText(item.getContent());
//
//                if (item.getReplys() != null) {
//                    mReplyArea.setVisibility(View.VISIBLE);
//                    if (item.getReplys().size() == 0) {
//                        mReplyArea.setVisibility(View.GONE);
//                    } else {
//                        mReplyArea.setVisibility(View.VISIBLE);
//                        if (item.getReplys().get(0) != null) {
//                            if (item.getReplys().get(0).getUserModel() != null) {
//                                mReplyName.setText(context.getString(R.string.reply_name, item.getReplys().get(0).getUserModel().getUserName()));
//                            } else {
//                                mReplyName.setText("");
//                            }
//                            mReplyContent.setText(item.getReplys().get(0).getContent());
//                        } else {
//                            mReplyName.setText("");
//                            mReplyContent.setText("");
//                        }
//                    }
//                } else {
//                    mReplyArea.setVisibility(View.GONE);
//                }
            }
        }
    }

}
