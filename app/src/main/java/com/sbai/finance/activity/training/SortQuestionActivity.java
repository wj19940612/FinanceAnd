package com.sbai.finance.activity.training;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.BuildConfig;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingQuestion;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.TypefaceUtil;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.SortTrainResultDialog;
import com.sbai.finance.view.training.TrainProgressBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 年报出炉界面  排序题
 */
public class SortQuestionActivity extends BaseActivity {

    private static final int DEFAULT_BIG_QUESTION_NUMBER = 8;


    @BindView(R.id.sortQuestionRecyclerView)
    RecyclerView mSortQuestionRecyclerView;
    @BindView(R.id.sortResultRecycleView)
    RecyclerView mSortResultRecycleView;
    @BindView(R.id.confirmAnnals)
    ImageView mConfirmAnnals;
    @BindView(R.id.sortResultLL)
    LinearLayout mSortResultLL;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.progressBar)
    TrainProgressBar mProgressBar;
    //用來记录底部界面选择答案的索引
    private HashSet<Integer> mResultSet;
    private TrainingQuestion mTrainingQuestion;

    private int mAnnalsMaterialsBgDrawables[] = new int[]{R.drawable.bg_annals_materials_1, R.drawable.bg_annals_materials_2,
            R.drawable.bg_annals_materials_3, R.drawable.bg_annals_materials_4,
            R.drawable.bg_annals_materials_5, R.drawable.bg_annals_materials_6,
            R.drawable.bg_annals_materials_7, R.drawable.bg_annals_materials_8};


    private ArrayList<Integer> mSortQuestionBgDrawables;
    private ArrayList<int[]> mResultBgColors;

    private SortResultAdapter mSortResultAdapter;
    private SortQuestionAdapter mSortQuestionAdapter;
    private List<TrainingQuestion.ContentBean> mRandRomQuestionResultList;
    private Training mTraining;
    //服务端返回的数据
    private List<TrainingQuestion.ContentBean> mWebTrainResult;
    private CountDownTimer mCountDownTimer;

    //游戏进行的时间
    private long mTrainingCountTime;

    private int mTrainTargetTime;

    private boolean isConfirmResult;

    public interface OnItemClickListener {
        void onItemClick(TrainingQuestion.ContentBean data, int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_question);
        ButterKnife.bind(this);
        translucentStatusBar();
        Intent intent = getIntent();
        mTrainingQuestion = intent.getParcelableExtra(ExtraKeys.TRAIN_QUESTIONS);
        mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);

        initHeaderView();

        mResultSet = new HashSet<>();
        createResultBgColors();
        createSortQuestionBgDrawables();
        initQuestionData();

    }

    private void initQuestionData() {
        int size = mTrainingQuestion.getContent().size();
        saveWebResultData(size);

        mRandRomQuestionResultList = mTrainingQuestion.getRandRomResultList();

        initSortQuestionAdapter(mRandRomQuestionResultList);

        //底部答案区域创建数据源
        ArrayList<TrainingQuestion.ContentBean> contentBeenList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TrainingQuestion.ContentBean contentBean = new TrainingQuestion.ContentBean();
            contentBeenList.add(contentBean);
        }
        initSortResultAdapter(contentBeenList);
    }

    private void saveWebResultData(int size) {
        mWebTrainResult = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TrainingQuestion.ContentBean oldData = mTrainingQuestion.getContent().get(i);
            TrainingQuestion.ContentBean contentBean = new TrainingQuestion.ContentBean();
            contentBean.setSeq(oldData.getSeq());
            contentBean.setContent(oldData.getContent());
            contentBean.setId(oldData.getId());
            contentBean.setRight(oldData.isRight());
            mWebTrainResult.add(contentBean);
        }
    }

    private void createSortQuestionBgDrawables() {
        mSortQuestionBgDrawables = new ArrayList<>();
        for (int i = 0; i < mAnnalsMaterialsBgDrawables.length; i++) {
            mSortQuestionBgDrawables.add(i, mAnnalsMaterialsBgDrawables[i]);
        }
    }

    private void createResultBgColors() {
        mResultBgColors = new ArrayList<>();
        int[] firstColors = {Color.parseColor("#13c336"), Color.parseColor("#13c336")};
        int[] secondColors = {Color.parseColor("#FE9722"), Color.parseColor("#FE9722")};
        int[] thirdColors = {Color.parseColor("#2561F6"), Color.parseColor("#2561F6")};
        int[] forthColors = {Color.parseColor("#55adFF"), Color.parseColor("#55adFF")};
        int[] fifthColors = {Color.parseColor("#F25B57"), Color.parseColor("#F25B57")};
        int[] sixthColors = {Color.parseColor("#af56ff"), Color.parseColor("#af56ff")};
        int[] seventhColors = {Color.parseColor("#FEC022"), Color.parseColor("#FEC022")};
        int[] eighthColors = {Color.parseColor("#EF6D6A"), Color.parseColor("#EF6D6A")};

        mResultBgColors.add(firstColors);
        mResultBgColors.add(secondColors);
        mResultBgColors.add(thirdColors);
        mResultBgColors.add(forthColors);
        mResultBgColors.add(fifthColors);
        mResultBgColors.add(sixthColors);
        mResultBgColors.add(seventhColors);
        mResultBgColors.add(eighthColors);
    }

    private void initHeaderView() {
        if (mTraining == null) return;
        mTrainTargetTime = mTraining.getTime() * 1000;
        mProgressBar.setTotalSecondTime(mTraining.getTime());
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), HowPlayActivity.class)
                        .putExtra(ExtraKeys.TRAINING, mTraining)
                        .execute();
            }
        });

        View customView = mTitleBar.getCustomView();
        if (customView != null) {
            final TextView countDownTimeTextView = (TextView) customView.findViewById(R.id.countdownTime);
            mCountDownTimer = new CountDownTimer(mTrainTargetTime, 1) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTrainingCountTime = mTrainTargetTime - millisUntilFinished;
                    countDownTimeTextView.setText(DateUtil.format(mTrainingCountTime, "mm: ss. SS"));
                    mProgressBar.setTrainChangeTime(mTrainingCountTime);
                }

                @Override
                public void onFinish() {
                    mCountDownTimer.cancel();
                    countDownTimeTextView.setText(DateUtil.format(mTrainTargetTime, "mm: ss. SS"));
                    if (!isConfirmResult) {
                        showResultDialog(false);
                    }
                }
            }.start();
        }


    }

    @Override
    public void onBackPressed() {
        SmartDialog.with(this, R.string.is_sure_exit_train, R.string.exit_train_will_not_save_train_record)
                .setNegative(R.string.exit_train, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        SortQuestionActivity.this.finish();
                    }
                })
                .setPositive(R.string.continue_train, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showResultDialog(final boolean isRight) {
        if (isFinishing()) {
            return;
        }
        SortTrainResultDialog sortTrainResultDialog = new SortTrainResultDialog(this);
        sortTrainResultDialog.show();
        sortTrainResultDialog.setResult(isRight);
        sortTrainResultDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
                openTrainingResultPage(isRight);
            }
        });
    }

    private void openTrainingResultPage(boolean isRight) {
        mCountDownTimer.cancel();
        TrainingResultActivity.show(this, mTraining, mTrainTargetTime / 1000, isRight);
        finish();
    }

    private void initSortResultAdapter(List<TrainingQuestion.ContentBean> content) {
        mSortResultAdapter = new SortResultAdapter(new ArrayList<TrainingQuestion.ContentBean>(), this);
        mSortResultRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mSortResultRecycleView.setAdapter(mSortResultAdapter);
        mSortResultRecycleView.setItemAnimator(null);
        mSortResultAdapter.setItemBgColors(mResultBgColors);
        mSortResultAdapter.addData(content);
        mSortResultAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(TrainingQuestion.ContentBean data, int position) {
                if (data.isSelect()) {
                    updateResult(data, position);
                }
            }
        });

    }

    private void updateResult(TrainingQuestion.ContentBean data, int position) {
        data.setSelect(false);
        mResultSet.remove(position);
        mSortResultAdapter.notifyItemChanged(position, data);

        TrainingQuestion.ContentBean contentBean = new TrainingQuestion.ContentBean();
        contentBean.setId(data.getId());
        contentBean.setContent(data.getContent());
        contentBean.setBgPosition(data.getBgPosition());
        contentBean.setSelect(false);
        contentBean.setSeq(data.getSeq());

        mSortQuestionAdapter.insert(0, contentBean);
        mSortQuestionRecyclerView.scrollToPosition(0);
    }

    private void initSortQuestionAdapter(List<TrainingQuestion.ContentBean> content) {
        mSortQuestionAdapter = new SortQuestionAdapter(new ArrayList<TrainingQuestion.ContentBean>(), this);
        mSortQuestionAdapter.setItemBgDrawables(mSortQuestionBgDrawables);
        mSortQuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSortQuestionRecyclerView.setAdapter(mSortQuestionAdapter);
        mSortQuestionRecyclerView.setItemAnimator(null);
        mSortQuestionAdapter.addData(content);

        mSortQuestionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(TrainingQuestion.ContentBean data, int position) {
                chooseResult(position, data);
            }
        });

    }

    private void chooseResult(int position, TrainingQuestion.ContentBean data) {
        List<TrainingQuestion.ContentBean> resultData = mSortResultAdapter.getResultData();
        for (int i = 0; i < resultData.size(); i++) {
            if (mResultSet.add(i)) {
                TrainingQuestion.ContentBean contentBean = resultData.get(i);
                contentBean.setSelect(true);
                contentBean.setBgPosition(data.getBgPosition());
                contentBean.setContent(data.getContent());
                contentBean.setId(data.getId());
                contentBean.setSeq(data.getSeq());
                mSortResultAdapter.changeItemData(i, contentBean);
                break;
            }
        }
        mSortQuestionAdapter.notifyItemRemovedData(position, data);
    }

    @OnClick(R.id.confirmAnnals)
    public void onViewClicked() {
        boolean isFinish = true;
        ArrayList<TrainingQuestion.ContentBean> resultData = mSortResultAdapter.getResultData();
        for (TrainingQuestion.ContentBean result : resultData) {
            if (!result.isSelect()) {
                isFinish = false;
                break;
            }
        }

        if (!isFinish) {
            if (BuildConfig.DEBUG) {
                ToastUtil.show("未完成");
            }
            return;
        }
        boolean isRight = true;
        if (mTrainingCountTime > mTrainTargetTime) {
            isRight = false;
        } else {
            for (int i = 0; i < mWebTrainResult.size(); i++) {
                TrainingQuestion.ContentBean chooseResult = mWebTrainResult.get(i);
                TrainingQuestion.ContentBean randRomResult = resultData.get(i);
                if (chooseResult == null ||
                        randRomResult == null ||
                        chooseResult.getId() != randRomResult.getId()) {
                    isRight = false;
                    break;
                }
            }
        }
        isConfirmResult = true;
        Log.d(TAG, "onViewClicked: " + isRight);
        startResultListScaleAnimation(isRight);
    }

    private void startResultListScaleAnimation(final boolean isRight) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0f, 1, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
        mConfirmAnnals.startAnimation(scaleAnimation);
        mSortResultLL.startAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showResultDialog(isRight);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    static class SortQuestionAdapter extends RecyclerView.Adapter<SortQuestionAdapter.AnnalsMaterialsViewHolder> {
        private List<TrainingQuestion.ContentBean> mSortQuestionList;
        private Context mContext;
        private OnItemClickListener mOnItemClickListener;
        private ArrayList<Integer> mItemBgDrawables;

        public SortQuestionAdapter(List<TrainingQuestion.ContentBean> annalsMaterialsList, Context context) {
            this.mSortQuestionList = annalsMaterialsList;
            this.mContext = context;
        }

        public void addData(List<TrainingQuestion.ContentBean> materialsList) {
            mSortQuestionList.clear();
            notifyItemRangeRemoved(0, mSortQuestionList.size());
            mSortQuestionList.addAll(materialsList);
            notifyItemRangeChanged(0, mSortQuestionList.size());
        }

        public void notifyItemRemovedData(int position, TrainingQuestion.ContentBean result) {

            if (position < mSortQuestionList.size()) {
                mSortQuestionList.remove(position);
                this.notifyItemRemoved(position);
                this.notifyItemRangeChanged(position, getItemCount());
            }

        }

        public List<TrainingQuestion.ContentBean> getQuestionData() {
            return mSortQuestionList;
        }


        public void insert(int position, TrainingQuestion.ContentBean data) {
            mSortQuestionList.add(0, data);
            notifyItemInserted(position);
            notifyItemRangeChanged(0, mSortQuestionList.size());
        }


        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        @Override
        public AnnalsMaterialsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_annals_materials, parent, false);
            return new AnnalsMaterialsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AnnalsMaterialsViewHolder holder, int position) {
            if (mSortQuestionList != null && !mSortQuestionList.isEmpty()) {
                holder.bindDataWithView(mSortQuestionList.get(position), mContext, position, mOnItemClickListener, mSortQuestionList.size());
            }
        }

        @Override
        public int getItemCount() {
            if (mSortQuestionList != null) {
                if (mSortQuestionList.size() > DEFAULT_BIG_QUESTION_NUMBER) {
                    return DEFAULT_BIG_QUESTION_NUMBER;
                }
                return mSortQuestionList.size();
            }
            return 0;
        }

        public void setItemBgDrawables(ArrayList<Integer> sortQuestionBgDrawables) {
            this.mItemBgDrawables = sortQuestionBgDrawables;
        }

        class AnnalsMaterialsViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.materialsText)
            TextView mMaterialsText;

            public AnnalsMaterialsViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }


            public void bindDataWithView(final TrainingQuestion.ContentBean contentBean, Context context,
                                         final int position, final OnItemClickListener onItemClickListener,
                                         int size) {
                if (contentBean == null) return;
                mMaterialsText.setText(contentBean.getContent());
                mMaterialsText.setBackgroundResource(mItemBgDrawables.get(contentBean.getBgPosition()));
                mMaterialsText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(contentBean, position);
                        }
                    }
                });

            }
        }
    }

    class SortResultAdapter extends RecyclerView.Adapter<SortResultAdapter.ViewHolder> {

        private ArrayList<TrainingQuestion.ContentBean> mSortResultList;
        private Context mContext;
        private OnItemClickListener mOnItemClickListener;
        private ArrayList<int[]> mItemBgColors;

        public SortResultAdapter(ArrayList<TrainingQuestion.ContentBean> sortResultList, Context context) {
            mSortResultList = sortResultList;
            mContext = context;
            mItemBgColors = new ArrayList<int[]>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sort_result, parent, false);
            return new ViewHolder(view);
        }

        public void addData(List<TrainingQuestion.ContentBean> materialsList) {
            mSortResultList.clear();
            notifyItemRangeRemoved(0, mSortResultList.size());
            mSortResultList.addAll(materialsList);
            notifyItemRangeChanged(0, mSortResultList.size());
        }

        public void changeItemData(int position, TrainingQuestion.ContentBean data) {
            if (position <= mSortResultList.size()) {
                notifyItemChanged(position, data);
            }
        }

        public void setItemBgColors(ArrayList<int[]> itemColors) {
            this.mItemBgColors = itemColors;
        }

        public ArrayList<TrainingQuestion.ContentBean> getResultData() {
            return mSortResultList;
        }


        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (!mSortResultList.isEmpty()) {
                holder.bindDataWithView(mSortResultList.get(position), position, mContext, mOnItemClickListener, mItemBgColors);
            }
        }

        @Override
        public int getItemCount() {
            if (mSortResultList != null) {
                if (mSortResultList.size() > DEFAULT_BIG_QUESTION_NUMBER) {
                    return DEFAULT_BIG_QUESTION_NUMBER;
                }
                return mSortResultList.size();
            }
            return 0;
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.lineNumber)
            TextView mLineNumber;
            @BindView(R.id.materials)
            TextView mMaterials;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final TrainingQuestion.ContentBean contentBean, final int position, Context context, final OnItemClickListener onItemClickListener, ArrayList<int[]> itemBgColors) {
                if (contentBean == null) return;
                TypefaceUtil.setHelveticaLTCompressedFont(mLineNumber);
                mLineNumber.setText(String.valueOf(position + 1));
                if (contentBean.isSelect()) {
                    mMaterials.setText(contentBean.getContent());
                    if (itemBgColors != null && !itemBgColors.isEmpty()) {
                        mMaterials.setBackground(createDrawable(itemBgColors.get(contentBean.getBgPosition()), context));
                    }
                    mMaterials.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(contentBean, position);
                            }
                        }
                    });
                } else {
                    mMaterials.setBackgroundResource(R.drawable.bg_broken_line_oval);
                    mMaterials.setText("");
                }

            }

            private Drawable createDrawable(int[] colors, Context context) {
                GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
                gradient.setCornerRadius(Display.dp2Px(200, context.getResources()));
                return gradient;
            }
        }
    }


}
