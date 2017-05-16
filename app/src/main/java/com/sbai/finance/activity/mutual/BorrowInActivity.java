package com.sbai.finance.activity.mutual;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
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
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.economiccircle.ContentImgActivity;
import com.sbai.finance.activity.economiccircle.WantHelpHimOrYouActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mutual.BorrowHelper;
import com.sbai.finance.model.mutual.BorrowIn;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.MyGridView;
import com.sbai.finance.view.SmartDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BorrowInActivity extends BaseActivity implements AbsListView.OnScrollListener {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    private BorrowInAdapter mBorrowInAdapter;
    private int mMax;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_in_mine);
        ButterKnife.bind(this);
        initView();
        requestBorrowInData();
    }

    private void initView() {
        calculateAvatarNum(this);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestBorrowInData();
            }
        });
        mBorrowInAdapter = new BorrowInAdapter(this);
        mBorrowInAdapter.setCallback(new BorrowInAdapter.Callback() {
            @Override
            public void OnItemCancelBorrowClick(final int id) {
                SmartDialog.with(getActivity())
                        .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                requestCancelBorrow(id);
                                dialog.dismiss();
                            }
                        })
                        .setTitle(getString(R.string.cancel_confirm))
                        .setTitleMaxLines(1)
                        .setNegative(R.string.cancel)
                        .show();
            }

            @Override
            public void OnItemImageClick(final int index, String headImage) {
                Launcher.with(getActivity(), ContentImgActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, headImage.split(","))
                        .putExtra(Launcher.EX_PAYLOAD_1, index)
                        .execute();
            }

            @Override
            public void OnItemMoreClick(int id) {
                launcherWantHelpMe(id);
            }
        });
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mBorrowInAdapter);
        mListView.setOnScrollListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startScheduleJob(1000 * 60);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    private void requestBorrowInData() {
        Client.getBorrowInList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<BorrowIn>>, List<BorrowIn>>() {
                    @Override
                    protected void onRespSuccessData(List<BorrowIn> data) {
                        updateBorrowInData(data);
                        requestHelper(data);
                    }
                })
                .fire();
    }

    private void requestHelper(List<BorrowIn> data) {
        for ( final BorrowIn borrowIn:data){
            Client.getHelper(borrowIn.getId()).setTag(TAG)
                    .setCallback(new Callback2D<Resp<List<BorrowHelper>>, List<BorrowHelper>>() {
                        @Override
                        protected void onRespSuccessData(List<BorrowHelper> data) {
                            updateHelperData(borrowIn.getId(),data);
                        }
                    }).fireSync();
        }
    }

    private void requestCancelBorrow(Integer id) {
        Client.cancelBorrowIn(id).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            requestBorrowInData();
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fire();
    }

    private void updateBorrowInData(List<BorrowIn> data) {
        stopRefreshAnimation();
        mBorrowInAdapter.clear();
        mBorrowInAdapter.addAll(data);
        mBorrowInAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        updateEndLineData();
    }

    private void updateEndLineData() {
        if (mListView != null && mBorrowInAdapter != null&&mBorrowInAdapter.getCount()>0) {
            int first = mListView.getFirstVisiblePosition();
            int last = mListView.getLastVisiblePosition();
            for (int i = first; i <= last; i++) {
                BorrowIn borrowIn = mBorrowInAdapter.getItem(i);
                View childView = mListView.getChildAt(i);
                if (borrowIn != null && borrowIn.getStatus() == BorrowIn.STATUS_CHECKED && childView != null) {
                    TextView mEndLineTime = (TextView) childView.findViewById(R.id.endLineTime);
                    SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(getActivity().getString(R.string.call_helper),
                            "\n" + getActivity().getString(R.string.end_line), "  " + DateUtil.compareTime(borrowIn.getEndlineTime()), 1.455f, 1.455f,
                            ContextCompat.getColor(getActivity(), R.color.opinionText), ContextCompat.getColor(getActivity(), R.color.redPrimary));
                    mEndLineTime.setText(attentionSpannableString);
                }
            }
        }
    }

    private void updateHelperData(final int loadId, List<BorrowHelper> data) {
        if (mListView != null && mBorrowInAdapter != null) {
            for (int i = 0; i < mBorrowInAdapter.getCount(); i++) {
                BorrowIn borrowIn = mBorrowInAdapter.getItem(i);
                if (borrowIn!=null&&borrowIn.getId() == loadId){
                    View childView = mListView.getChildAt(i);
                    if (childView!=null){
                        MyGridView mGridView = (MyGridView) childView.findViewById(R.id.gridView);
                        ImageGridAdapter imageGridAdapter = (ImageGridAdapter) mGridView.getAdapter();
                        if (imageGridAdapter == null) {
                            imageGridAdapter = new ImageGridAdapter(getActivity());
                            mGridView.setAdapter(imageGridAdapter);
                        }
                        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                launcherWantHelpMe(loadId);
                            }
                        });
                        ImageView mMore = (ImageView) childView.findViewById(R.id.more);
                        imageGridAdapter.clear();
                        Log.d(TAG,String.valueOf(data.size()));
                        if (data.size() > mMax) {
                            imageGridAdapter.addAll(data.subList(0, mMax));
                            mMore.setVisibility(View.VISIBLE);
                        } else {
                            imageGridAdapter.addAll(data);
                            mMore.setVisibility(View.GONE);
                        }
                        imageGridAdapter.notifyDataSetChanged();
                    }
                    break;
                }
            }
        }
    }

    private void launcherWantHelpMe(int loadId) {
        Launcher.with(getActivity(), WantHelpHimOrYouActivity.class)
                .putExtra(Launcher.EX_PAYLOAD, loadId)
                .putExtra(Launcher.USER_ID, LocalUser.getUser().getUserInfo().getId())
                .execute();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @OnClick(R.id.borrowInHis)
    public void onClick(View view) {
        Launcher.with(getActivity(), BorrowInHisActivity.class).execute();
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

    private void calculateAvatarNum(Context context) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        float margin = Display.dp2Px(26, getResources());
        float horizontalSpacing = Display.dp2Px(5, getResources());
        float avatarWidth = Display.dp2Px(32, getResources());
        float more = Display.dp2Px(18, getResources());
        mMax = (int) ((screenWidth - margin - more + horizontalSpacing) / (horizontalSpacing + avatarWidth));
    }

    static class ImageGridAdapter extends ArrayAdapter<BorrowHelper> {

        public ImageGridAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_helper_image, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.userImg)
            ImageView mUserImg;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(BorrowHelper item, Context context) {
                Glide.with(context).load(item.getPortrait())
                        .bitmapTransform(new GlideCircleTransform(context))
                        .placeholder(R.drawable.ic_default_avatar)
                        .into(mUserImg);
            }
        }
    }

    static class BorrowInAdapter extends ArrayAdapter<BorrowIn> {
        Context mContext;
        private Callback mCallback;

        interface Callback {
            void OnItemCancelBorrowClick(int id);

            void OnItemImageClick(int index, String headImage);

            void OnItemMoreClick(int id);
        }

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        public BorrowInAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_borrow_in_mine, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), position, getContext(), mCallback);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.needAmount)
            TextView mNeedAmount;
            @BindView(R.id.borrowTime)
            TextView mBorrowTime;
            @BindView(R.id.borrowInterest)
            TextView mBorrowInterest;
            @BindView(R.id.endLineTime)
            TextView mEndLineTime;
            @BindView(R.id.opinion)
            TextView mOption;
            @BindView(R.id.cancelBorrowIn)
            TextView mCancelBorrowIn;
            @BindView(R.id.helperAmount)
            TextView mHelperAmount;
            @BindView(R.id.image1)
            ImageView mImage1;
            @BindView(R.id.image2)
            ImageView mImage2;
            @BindView(R.id.image3)
            ImageView mImage3;
            @BindView(R.id.image4)
            ImageView mImage4;
            @BindView(R.id.more)
            ImageView mMore;
            @BindView(R.id.gridView)
            MyGridView mGridView;
            @BindView(R.id.content)
            LinearLayout mContent;
            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(final BorrowIn item, int position, final Context context, final Callback callback) {
                if (item == null) return;
                ImageGridAdapter imageGridAdapter = new ImageGridAdapter(context);
                mGridView.setAdapter(imageGridAdapter);
                mGridView.setVisibility(View.VISIBLE);
                mPublishTime.setText(context.getString(R.string.publish_time, DateUtil.formatSlash(item.getCreateDate())));
                mNeedAmount.setText(context.getString(R.string.RMB, String.valueOf(item.getMoney())));
                mBorrowTime.setText(context.getString(R.string.day, String.valueOf(item.getDays())));
                mBorrowInterest.setText(context.getString(R.string.RMB, String.valueOf(item.getInterest())));
                mHelperAmount.setText(context.getString(R.string.helper, item.getIntentionCount()));
                mOption.setText(item.getContent());
                SpannableString attentionSpannableString;
                switch (item.getStatus()) {
                    case BorrowIn.STATUS_CHECKED:
                        attentionSpannableString = StrUtil.mergeTextWithRatioColor(context.getString(R.string.call_helper),
                                "\n" + context.getString(R.string.end_line), "  " + DateUtil.compareTime(item.getEndlineTime()), 1.455f, 1.455f,
                                ContextCompat.getColor(context, R.color.opinionText), ContextCompat.getColor(context, R.color.redPrimary));
                        mEndLineTime.setText(attentionSpannableString);
                        mCancelBorrowIn.setVisibility(View.VISIBLE);
                        break;
                    case BorrowIn.STATUS_NO_CHECK:
                        attentionSpannableString = StrUtil.mergeTextWithColor(context.getString(R.string.on_checking), 1.455f,
                                ContextCompat.getColor(context, R.color.opinionText));
                        mEndLineTime.setText(attentionSpannableString);
                        mCancelBorrowIn.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
                if (item.getContentImg() == null) {
                    item.setContentImg("");
                }
                mContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Launcher.with(context, BorrowInDetailsActivity.class)
                                .putExtra(BorrowInDetailsActivity.BORROW_IN, item)
                                .execute();
                    }
                });
                mOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Launcher.with(context, BorrowInDetailsActivity.class)
                                .putExtra(BorrowInDetailsActivity.BORROW_IN, item)
                                .execute();
                    }
                });
                mCancelBorrowIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.OnItemCancelBorrowClick(item.getId());
                        }
                    }
                });
                mMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.OnItemMoreClick(item.getId());
                    }
                });

                mImage1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.OnItemImageClick(0,item.getContentImg());
                    }
                });
                mImage2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.OnItemImageClick(1,item.getContentImg());
                    }
                });
                mImage3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.OnItemImageClick(2,item.getContentImg());
                    }
                });
                mImage4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.OnItemImageClick(3,item.getContentImg());
                    }
                });


                String[] images = item.getContentImg().split(",");
                switch (images.length) {
                    case 1:
                        if (TextUtils.isEmpty(images[0])) {
                            mImage1.setVisibility(View.GONE);
                            mImage2.setVisibility(View.GONE);
                            mImage3.setVisibility(View.GONE);
                            mImage4.setVisibility(View.GONE);
                        } else {
                            mImage1.setVisibility(View.VISIBLE);
                            loadImage(context, images[0], mImage1);
                            mImage2.setVisibility(View.INVISIBLE);
                            mImage3.setVisibility(View.INVISIBLE);
                            mImage4.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 2:
                        mImage1.setVisibility(View.VISIBLE);
                        loadImage(context, images[0], mImage1);
                        mImage2.setVisibility(View.VISIBLE);
                        loadImage(context, images[1], mImage2);
                        mImage3.setVisibility(View.INVISIBLE);
                        mImage4.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        mImage1.setVisibility(View.VISIBLE);
                        loadImage(context, images[0], mImage1);
                        mImage2.setVisibility(View.VISIBLE);
                        loadImage(context, images[1], mImage2);
                        mImage3.setVisibility(View.VISIBLE);
                        loadImage(context, images[2], mImage3);
                        mImage4.setVisibility(View.INVISIBLE);
                        break;
                    case 4:
                        mImage1.setVisibility(View.VISIBLE);
                        loadImage(context, images[0], mImage1);
                        mImage2.setVisibility(View.VISIBLE);
                        loadImage(context, images[1], mImage2);
                        mImage3.setVisibility(View.VISIBLE);
                        loadImage(context, images[2], mImage3);
                        mImage4.setVisibility(View.VISIBLE);
                        loadImage(context, images[3], mImage4);
                        break;
                    default:
                        break;

                }
            }
            private void loadImage(Context context, String src, ImageView image) {
                Glide.with(context).load(src).placeholder(R.drawable.ic_default_avatar).into(image);
            }
        }
    }
}
