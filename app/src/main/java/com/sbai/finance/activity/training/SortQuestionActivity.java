package com.sbai.finance.activity.training;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.Question;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingDetail;
import com.sbai.finance.model.training.TrainingSubmit;
import com.sbai.finance.model.training.question.SortData;
import com.sbai.finance.utils.AnimUtils;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.RenderScriptGaussianBlur;
import com.sbai.finance.utils.TypefaceUtil;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.SortTrainResultDialog;
import com.sbai.finance.view.dialog.TrainingRuleDialog;
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

    //默认显示最多的题目
    private static final int DEFAULT_BIG_QUESTION_NUMBER = 8;
    //动画进行的时常
    private static final int DEFAULT_ANIMATION_RUNNING_TIME = 500;

    private static final int CANCEL_RESULT_HANDLER_WHAT = 275;

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
    @BindView(R.id.bg)
    ImageView mBg;
    @BindView(R.id.content)
    RelativeLayout mContent;
    @BindView(R.id.copyView)
    TextView mCopyView;
    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;
    //用來记录底部界面选择答案的索引
    private HashSet<Integer> mResultSet;
    private Question<SortData> mTrainingQuestion;

    private int mAnnalsMaterialsBgDrawables[] = new int[]{R.drawable.bg_annals_materials_1, R.drawable.bg_annals_materials_2,
            R.drawable.bg_annals_materials_3, R.drawable.bg_annals_materials_4,
            R.drawable.bg_annals_materials_5, R.drawable.bg_annals_materials_6,
            R.drawable.bg_annals_materials_7, R.drawable.bg_annals_materials_8};


    private ArrayList<Integer> mSortQuestionBgDrawables;
    private ArrayList<int[]> mResultBgColors;

    private SortResultAdapter mSortResultAdapter;
    private SortQuestionAdapter mSortQuestionAdapter;
    private List<SortData> mRandRomQuestionResultList;
    private TrainingDetail mTrainingDetail;
    //服务端返回的数据
    private List<SortData> mWebTrainResult;

    //游戏进行的时间
    private long mTrainingCountTime;

    private int mTrainTargetTime;

    //是否提交过答案，如果提交了，就不要出来弹窗
    private boolean isConfirmResult;

    private RenderScriptGaussianBlur mRenderScriptGaussianBlur;
    private LinearLayoutManager mSortResultLinearLayoutManager;
    private LinearLayoutManager mSortQuestionLinearLayoutManager;

    private int mChooseResultX;


    //记录答案选择动画是否进行中 如果进行 不要再次点击
    private boolean isChooseResultAnimationRunning;
    //取消答案动画进行表示
    private boolean isCancelResultAnimationRunning;


    private int mQuestionFirstItemY;

    public interface OnItemClickListener {
        /**
         * @param data     数据
         * @param position 索引
         * @param textView 点击的view
         */
        void onItemClick(SortData data, int position, TextView textView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_question);
        ButterKnife.bind(this);
        translucentStatusBar();
        Intent intent = getIntent();
        try {
            mTrainingQuestion = intent.getParcelableExtra(ExtraKeys.QUESTION);
        } catch (ClassCastException e) {
            Log.d(TAG, "onCreate: " + e.toString());
            return;
        }
        mTrainingDetail = intent.getParcelableExtra(ExtraKeys.TRAINING_DETAIL);
        mRenderScriptGaussianBlur = new RenderScriptGaussianBlur(this);
        initHeaderView();

        mResultSet = new HashSet<>();
        createResultBgColors();
        createSortQuestionBgDrawables();
        initQuestionData();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        } else {
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        mChooseResultX = (int) (displayMetrics.widthPixels * 0.33 - 90);
    }

    private void initQuestionData() {
        int size = mTrainingQuestion.getContent().size();
        saveWebResultData(size);

        mRandRomQuestionResultList = SortData.getRandRomResultList(mTrainingQuestion.getContent());
        initSortQuestionAdapter(mRandRomQuestionResultList);

        //底部答案区域创建数据源
        ArrayList<SortData> contentBeenList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            SortData contentBean = new SortData();
            contentBeenList.add(contentBean);
        }
        initSortResultAdapter(contentBeenList);
    }

    private void saveWebResultData(int size) {
        mWebTrainResult = new ArrayList<>();
        try {
            SortData data = mTrainingQuestion.getContent().get(0);
        } catch (ClassCastException e) {
            Log.d(TAG, "saveWebResultData: " + e.toString());
            return;
        }

        for (int i = 0; i < size; i++) {
            SortData oldData = mTrainingQuestion.getContent().get(i);
            SortData contentBean = new SortData();
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
        if (mTrainingDetail == null) return;
        Training training = mTrainingDetail.getTrain();
        if (training == null) return;
        mTrainTargetTime = training.getTime() * 1000;
        mProgressBar.setTotalSecondTime(training.getTime());
        mProgressBar.setOnTimeUpListener(new TrainProgressBar.OnTimeUpListener() {
            @Override
            public void onTick(long millisUntilUp) {
                mTrainingCountTime = millisUntilUp;
                formatTime(mTrainingCountTime);
            }

            @Override
            public void onFinish() {
                formatTime(mTrainTargetTime);
                if (!isConfirmResult) {
                    showResultDialog(false);
                }
            }

            private void formatTime(long time) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    String format = DateUtil.format(time, "mm:ss.SS");
                    mTitleBar.setTitle(format);
                } else {
                    String format = DateUtil.format(time, "mm:ss.SSS");
                    String substring = format.substring(0, format.length() - 1);
                    mTitleBar.setTitle(substring);
                }
            }
        });
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainingRuleDialog.with(getActivity(), mTrainingDetail.getTrain())
                        .show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressBar.cancelCountDownTimer();
    }

    @Override
    public void onBackPressed() {
        SmartDialog.single(getActivity(), getString(R.string.exit_train_will_not_save_train_record))
                .setTitle(getString(R.string.is_sure_exit_train))
                .setNegative(R.string.exit_train, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        finish();
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

        mBg.setVisibility(View.VISIBLE);
        mContent.setDrawingCacheEnabled(true);
        mContent.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        Bitmap bitmap = mContent.getDrawingCache();
        mBg.setImageBitmap(mRenderScriptGaussianBlur.gaussianBlur(25, bitmap));
        mContent.setVisibility(View.INVISIBLE);


        SortTrainResultDialog.with(this)
                .setOnDialogDismissListener(new SortTrainResultDialog.OnDialogDismissListener() {

                    @Override
                    public void onDismiss() {
                        openTrainingResultPage(isRight);
                    }
                })
                .setResult(isRight);
    }

    private void openTrainingResultPage(boolean isRight) {
        Training training = mTrainingDetail.getTrain();
        if (training != null) {
            TrainingSubmit trainingSubmit = new TrainingSubmit(training.getId());
            trainingSubmit.setFinish(isRight);
            trainingSubmit.setTime((int) (mTrainingCountTime / 1000));

            Launcher.with(getActivity(), TrainingResultActivity.class)
                    .putExtra(ExtraKeys.TRAINING_DETAIL, mTrainingDetail)
                    .putExtra(ExtraKeys.TRAINING_SUBMIT, trainingSubmit)
                    .execute();
            finish();
        }
    }

    private void initSortResultAdapter(List<SortData> content) {
        mSortResultAdapter = new SortResultAdapter(new ArrayList<SortData>(), this);
        mSortResultLinearLayoutManager = new LinearLayoutManager(this);
        mSortResultRecycleView.setLayoutManager(mSortResultLinearLayoutManager);
        mSortResultRecycleView.setAdapter(mSortResultAdapter);
        mSortResultRecycleView.setItemAnimator(new DefaultItemAnimator());
        mSortResultAdapter.setItemBgColors(mResultBgColors);
        mSortResultAdapter.addData(content);
        mSortResultAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(SortData data, int position, TextView textView) {
                if (data.isSelect()) {
                    updateResult(data, position, textView);
                }
            }
        });

    }

    private void updateResult(final SortData data, int position, TextView textView) {

        if (isCancelResultAnimationRunning || isChooseResultAnimationRunning) return;
        data.setSelect(false);
        mResultSet.remove(position);
        mSortResultAdapter.notifyItemChanged(position, data);
        if (mResultSet.size() >= mWebTrainResult.size()) {
            mConfirmAnnals.setVisibility(View.VISIBLE);
        } else {
            mConfirmAnnals.setVisibility(View.GONE);
        }

        View view = mSortQuestionLinearLayoutManager.findViewByPosition(0);
        int targetY = 0;
        if (view != null) {
            int[] questionFirstItemCoordinates = new int[2];
            view.getLocationInWindow(questionFirstItemCoordinates);
            targetY = questionFirstItemCoordinates[1];
        } else {
            targetY = mQuestionFirstItemY;
        }
        int[] clickItemCoordinates = new int[2];
        textView.getLocationInWindow(clickItemCoordinates);
        int clickItemCoordinateX = clickItemCoordinates[0];
        int clickItemCoordinateY = clickItemCoordinates[1];

        mCopyView.setText(data.getContent());
        mCopyView.setBackgroundResource(mAnnalsMaterialsBgDrawables[data.getBgPosition()]);
        mCopyView.setVisibility(View.VISIBLE);
        mCopyView.setX(clickItemCoordinateX);
        mCopyView.setY(clickItemCoordinateY);

        TranslateAnimation translateAnimation = new TranslateAnimation(0, -(mChooseResultX), 0, targetY - clickItemCoordinateY + 100);
        translateAnimation.setFillAfter(false);
        translateAnimation.setDuration(DEFAULT_ANIMATION_RUNNING_TIME);

        isCancelResultAnimationRunning = true;

        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0.0f);
        alphaAnimation.setDuration(DEFAULT_ANIMATION_RUNNING_TIME);
        alphaAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);

        mCopyView.startAnimation(animationSet);


        Message message = mHandler.obtainMessage();
        message.obj = data;
        message.what = CANCEL_RESULT_HANDLER_WHAT;
        mHandler.sendMessageAtTime(message, DEFAULT_ANIMATION_RUNNING_TIME + 80);

        translateAnimation.setAnimationListener(new AnimUtils.AnimEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mCopyView.setVisibility(View.GONE);
                isCancelResultAnimationRunning = false;
            }
        });

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CANCEL_RESULT_HANDLER_WHAT:
                    SortData data = (SortData) msg.obj;
                    updateSortQuestion(data);
                    break;
            }
        }
    };

    private void updateSortQuestion(SortData data) {
        SortData contentBean = new SortData();
        contentBean.setId(data.getId());
        contentBean.setContent(data.getContent());
        contentBean.setBgPosition(data.getBgPosition());
        contentBean.setSelect(false);
        contentBean.setSeq(data.getSeq());

        mSortQuestionAdapter.insert(0, contentBean);
        mSortQuestionRecyclerView.scrollToPosition(0);
    }

    private void initSortQuestionAdapter(List<SortData> content) {
        mSortQuestionAdapter = new SortQuestionAdapter(new ArrayList<SortData>());
        mSortQuestionAdapter.setItemBgDrawables(mSortQuestionBgDrawables);
        mSortQuestionLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mSortQuestionRecyclerView.setLayoutManager(mSortQuestionLinearLayoutManager);
        mSortQuestionRecyclerView.setAdapter(mSortQuestionAdapter);
        mSortQuestionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSortQuestionAdapter.addData(content);

        mSortQuestionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(SortData data, int position, TextView textView) {
                if (isCancelResultAnimationRunning || isChooseResultAnimationRunning) {
                    return;
                }
                chooseResult(position, data, textView);
            }
        });

        mSortQuestionRecyclerView.post(new Runnable() {

            @Override
            public void run() {
                View view = mSortQuestionLinearLayoutManager.findViewByPosition(0);
                if (view != null) {
                    int[] questionFirstItemCoordinates = new int[2];
                    view.getLocationInWindow(questionFirstItemCoordinates);
                    mQuestionFirstItemY = questionFirstItemCoordinates[1];
                }
            }
        });
    }

    private void chooseResult(final int position, final SortData data, final TextView textView) {

        final List<SortData> resultData = mSortResultAdapter.getResultData();

        for (int i = 0; i < resultData.size(); i++) {
            if (mResultSet.add(i)) {
                final SortData contentBean = resultData.get(i);
                contentBean.setSelect(true);
                contentBean.setBgPosition(data.getBgPosition());
                contentBean.setContent(data.getContent());
                contentBean.setId(data.getId());
                contentBean.setSeq(data.getSeq());
                View view = mSortResultLinearLayoutManager.findViewByPosition(i);

                if (view != null) {
                    //点击的view的坐标
                    int[] ints = new int[2];
                    textView.getLocationInWindow(ints);
                    //将要移动到的位置
                    int[] targetCoordinate = new int[2];
                    view.getLocationInWindow(targetCoordinate);

                    int clickX = ints[0];
                    int clickY = ints[1];

                    int targetY = targetCoordinate[1];
                    int targetX = targetCoordinate[0];


                    mCopyView.setX(clickX);
                    mCopyView.setY(clickY);

                    mCopyView.setText(data.getContent());
                    mCopyView.setBackgroundResource(mAnnalsMaterialsBgDrawables[data.getBgPosition()]);
                    mCopyView.setVisibility(View.VISIBLE);

//                    Log.d(TAG, "目标:x " + targetX + " 目标y " + targetY + "  原始x " + clickX + " y  " + clickY);

                    int offX;
                    if (clickX == 0) {
                        offX = mChooseResultX;
                    } else {
                        offX = mChooseResultX - clickX;
                    }

                    int offY = targetY - clickY + 40;
                    if (offY < 300) {
                        offY = 300;
                        mScrollView.scrollTo(0, 0);
                    }

                    mSortQuestionAdapter.notifyItemRemovedData(position);
                    TranslateAnimation translateAnimation = new TranslateAnimation(0, offX, 0, offY);
                    translateAnimation.setDuration(DEFAULT_ANIMATION_RUNNING_TIME);
                    translateAnimation.setFillAfter(false);
                    mCopyView.startAnimation(translateAnimation);
                    isChooseResultAnimationRunning = true;
                    final int finalI = i;
                    translateAnimation.setAnimationListener(new AnimUtils.AnimEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mCopyView.setVisibility(View.GONE);
                            mSortResultAdapter.changeItemData(finalI, contentBean);
                            isChooseResultAnimationRunning = false;

                        }
                    });
                }
                break;
            }

        }
        if (mResultSet.size() >= mWebTrainResult.size()) {
            mConfirmAnnals.setVisibility(View.VISIBLE);
        } else {
            mConfirmAnnals.setVisibility(View.GONE);
        }
    }


    @OnClick(R.id.confirmAnnals)
    public void onViewClicked() {
        boolean isFinish = true;
        ArrayList<SortData> resultData = mSortResultAdapter.getResultData();
        for (SortData result : resultData) {
            if (!result.isSelect()) {
                isFinish = false;
                break;
            }
        }

        if (!isFinish) {
            return;
        }
        boolean isRight = true;
        if (mTrainingCountTime > mTrainTargetTime) {
            isRight = false;
        } else {
            for (int i = 0; i < mWebTrainResult.size(); i++) {
                SortData chooseResult = mWebTrainResult.get(i);
                SortData randRomResult = resultData.get(i);
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

        ScaleAnimation confirmBtnScaleAnimation = new ScaleAnimation(1, 0f, 1, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        confirmBtnScaleAnimation.setDuration(1000);
        confirmBtnScaleAnimation.setFillAfter(true);
        mConfirmAnnals.startAnimation(confirmBtnScaleAnimation);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.6f, 1, 0.6f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
        mSortResultLL.startAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(new AnimUtils.AnimEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                showResultDialog(isRight);
            }
        });
    }


    static class SortQuestionAdapter extends RecyclerView.Adapter<SortQuestionAdapter.SortQuestionViewHolder> {
        private List<SortData> mSortQuestionList;
        private OnItemClickListener mOnItemClickListener;
        private ArrayList<Integer> mItemBgDrawables;

        public SortQuestionAdapter(List<SortData> annalsMaterialsList) {
            this.mSortQuestionList = annalsMaterialsList;
        }

        public void addData(List<SortData> materialsList) {
            mSortQuestionList.clear();
            notifyItemRangeRemoved(0, mSortQuestionList.size());
            mSortQuestionList.addAll(materialsList);
            notifyItemRangeChanged(0, mSortQuestionList.size());
        }

        public void notifyItemRemovedData(int position) {

            if (position < mSortQuestionList.size()) {
                mSortQuestionList.remove(position);
                this.notifyItemRemoved(position);
                this.notifyItemRangeChanged(position, getItemCount());
            }

        }

        public void insert(int position, SortData data) {
            mSortQuestionList.add(0, data);
            notifyItemInserted(position);
            notifyItemRangeChanged(0, mSortQuestionList.size());
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        @Override
        public SortQuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sort_question, parent, false);
            return new SortQuestionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SortQuestionViewHolder holder, int position) {
            if (mSortQuestionList != null && !mSortQuestionList.isEmpty()) {
                holder.bindDataWithView(mSortQuestionList.get(position), position, mOnItemClickListener);
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

        class SortQuestionViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.materialsText)
            TextView mSortQuestionText;

            public SortQuestionViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindDataWithView(final SortData contentBean, final int position,
                                         final OnItemClickListener onItemClickListener) {
                if (contentBean == null) return;
                mSortQuestionText.setText(contentBean.getContent());
                mSortQuestionText.setBackgroundResource(mItemBgDrawables.get(contentBean.getBgPosition()));
                mSortQuestionText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(contentBean, position, mSortQuestionText);
                        }
                    }
                });
            }
        }
    }

    class SortResultAdapter extends RecyclerView.Adapter<SortResultAdapter.ViewHolder> {

        private ArrayList<SortData> mSortResultList;
        private Context mContext;
        private OnItemClickListener mOnItemClickListener;
        private ArrayList<int[]> mItemBgColors;

        public SortResultAdapter(ArrayList<SortData> sortResultList, Context context) {
            mSortResultList = sortResultList;
            mContext = context;
            mItemBgColors = new ArrayList<int[]>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sort_result, parent, false);
            return new ViewHolder(view);
        }

        public void addData(List<SortData> materialsList) {
            mSortResultList.clear();
            notifyItemRangeRemoved(0, mSortResultList.size());
            mSortResultList.addAll(materialsList);
            notifyItemRangeChanged(0, mSortResultList.size());
        }

        public void changeItemData(int position, SortData data) {
            if (position <= mSortResultList.size()) {
                notifyItemChanged(position, data);
            }
        }

        public void setItemBgColors(ArrayList<int[]> itemColors) {
            this.mItemBgColors = itemColors;
        }

        public ArrayList<SortData> getResultData() {
            return mSortResultList;
        }


        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (!mSortResultList.isEmpty()) {
                holder.bindDataWithView(mSortResultList.get(position), position, mContext, mOnItemClickListener, mItemBgColors, mSortResultList.size());
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
            TextView mResultText;

            @BindView(R.id.line)
            View mLine;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final SortData contentBean, final int position, Context context,
                                         final OnItemClickListener onItemClickListener,
                                         ArrayList<int[]> itemBgColors, int size) {
                if (contentBean == null) return;
                if (position == size - 1) {
                    mLine.setVisibility(View.GONE);
                } else {
                    mLine.setVisibility(View.VISIBLE);
                }
                TypefaceUtil.setHelveticaLTCompressedFont(mLineNumber);
                mLineNumber.setText(String.valueOf(position + 1));
                if (contentBean.isSelect()) {
                    mResultText.setText(contentBean.getContent());
                    if (itemBgColors != null && !itemBgColors.isEmpty()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mResultText.setBackground(createDrawable(itemBgColors.get(contentBean.getBgPosition()), context));
                        } else {
                            mResultText.setBackgroundDrawable(createDrawable(itemBgColors.get(contentBean.getBgPosition()), context));
                        }
                    }
                    mResultText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(contentBean, position, mResultText);
                            }
                        }
                    });
                } else {
                    mResultText.setBackgroundResource(R.drawable.bg_broken_line_oval);
                    mResultText.setText("");
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
