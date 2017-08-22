package com.sbai.finance.activity.training;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.BuildConfig;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.Question;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingDetail;
import com.sbai.finance.model.training.TrainingSubmit;
import com.sbai.finance.model.training.question.SortData;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.RenderScriptGaussianBlur;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.TypefaceUtil;
import com.sbai.finance.utils.ViewPlaceUtils;
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
    @BindView(R.id.bg)
    ImageView mBg;
    @BindView(R.id.content)
    RelativeLayout mContent;

    @BindView(R.id.view)
    ImageView mView;
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

    private boolean isConfirmResult;

    private RenderScriptGaussianBlur mRenderScriptGaussianBlur;
    private LinearLayoutManager mSortResultLinearLayoutManager;
    private float mTargetX;
    private float mTargetY;

    public interface OnItemClickListener {
        /**
         * @param data         数据
         * @param position     索引
         * @param drawingCache 复制的图层
         * @param originalX    点击的view 的x坐标
         * @param originalY    点击的view 的y坐标
         * @param
         */
        void onItemClick(SortData data, int position, Bitmap drawingCache, float originalX, float originalY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_question);
        ButterKnife.bind(this);
        translucentStatusBar();
        Intent intent = getIntent();
        mTrainingQuestion = intent.getParcelableExtra(ExtraKeys.QUESTION);
        mTrainingDetail = intent.getParcelableExtra(ExtraKeys.TRAINING_DETAIL);
        mRenderScriptGaussianBlur = new RenderScriptGaussianBlur(this);
        initHeaderView();

        mResultSet = new HashSet<>();
        createResultBgColors();
        createSortQuestionBgDrawables();
        initQuestionData();
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
                mTitleBar.setTitle(DateUtil.format(mTrainingCountTime, "mm:ss.SS"));
            }

            @Override
            public void onFinish() {
                mTitleBar.setTitle(DateUtil.format(mTrainTargetTime, "mm:ss.SS"));
                if (!isConfirmResult) {
                    showResultDialog(false);
                }
            }
        });
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), HowPlayActivity.class)
                        .putExtra(ExtraKeys.TRAINING, mTrainingDetail.getTrain())
                        .execute();
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

        SortTrainResultDialog sortTrainResultDialog = new SortTrainResultDialog(this);
        sortTrainResultDialog.show();
        sortTrainResultDialog.setResult(isRight);
        sortTrainResultDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBg.setVisibility(View.GONE);
                mContent.setVisibility(View.VISIBLE);
                dialog.dismiss();
                openTrainingResultPage(isRight);
            }
        });
    }

    private void openTrainingResultPage(boolean isRight) {
        Training training = mTrainingDetail.getTrain();
        if (training != null) {
            TrainingSubmit trainingSubmit = new TrainingSubmit(training.getId());
            trainingSubmit.setFinish(isRight);
            trainingSubmit.setTime(mTrainTargetTime / 1000);

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
        mSortResultRecycleView.setItemAnimator(null);
        mSortResultAdapter.setItemBgColors(mResultBgColors);
        mSortResultAdapter.addData(content);
        mSortResultAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(SortData data, int position, Bitmap drawingCache, float originalX, float originalY) {
                if (data.isSelect()) {
                    updateResult(data, position);

                    mView.setImageBitmap(drawingCache);
                    ViewPlaceUtils.setLayout(mView, (int) originalX, (int) originalY);
//                    mView.setX(200);
//                    mView.setY(600);
                    Log.d(TAG, "onItemClick: " + originalX + " " + originalY + " " + mTargetX + " " + mTargetY);
                    TranslateAnimation translateAnimation = new TranslateAnimation(0, mTargetX, 0, mTargetY);
                    translateAnimation.setDuration(1000);
                    mView.startAnimation(translateAnimation);

                }
            }

        });

    }

    private void updateResult(SortData data, int position) {
        data.setSelect(false);
        mResultSet.remove(position);
        mSortResultAdapter.notifyItemChanged(position, data);

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
        mSortQuestionAdapter = new SortQuestionAdapter(new ArrayList<SortData>(), this);
        mSortQuestionAdapter.setItemBgDrawables(mSortQuestionBgDrawables);
        mSortQuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSortQuestionRecyclerView.setAdapter(mSortQuestionAdapter);
        mSortQuestionRecyclerView.setItemAnimator(null);
        mSortQuestionAdapter.addData(content);

        mSortQuestionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(SortData data, int position, Bitmap drawingCache, float originalX, float originalY) {
                if (drawingCache != null) {
                    chooseResult(position, data);
                }
            }
        });

        mSortQuestionRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Log.d(TAG, "onChildViewDetachedFromWindow: " + view);


                int[] ints = new int[2];
                view.getLocationInWindow(ints); //获取在当前窗口内的绝对坐标
                view.getLocationOnScreen(ints);
                Log.d("wangjie  小时的", "onClick: " + ints[0] + "  " + ints[1]);

            }
        });

    }

    private void chooseResult(int position, SortData data) {

        List<SortData> resultData = mSortResultAdapter.getResultData();
        for (int i = 0; i < resultData.size(); i++) {
            if (mResultSet.add(i)) {

//                View view = mSortResultLinearLayoutManager.findViewByPosition(i);
//                mTargetX = view.getX();
//                mTargetY = view.getY();


                SortData contentBean = resultData.get(i);
                contentBean.setSelect(true);
                contentBean.setBgPosition(data.getBgPosition());
                contentBean.setContent(data.getContent());
                contentBean.setId(data.getId());
                contentBean.setSeq(data.getSeq());
                mSortResultAdapter.changeItemData(i, contentBean);
                break;
            }
        }
        mSortQuestionAdapter.notifyItemRemovedData(position);
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


    static class SortQuestionAdapter extends RecyclerView.Adapter<SortQuestionAdapter.SortQuestionViewHolder> {
        private List<SortData> mSortQuestionList;
        private Context mContext;
        private OnItemClickListener mOnItemClickListener;
        private ArrayList<Integer> mItemBgDrawables;

        public SortQuestionAdapter(List<SortData> annalsMaterialsList, Context context) {
            this.mSortQuestionList = annalsMaterialsList;
            this.mContext = context;
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

        public List<SortData> getQuestionData() {
            return mSortQuestionList;
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

        class SortQuestionViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.materialsText)
            TextView mSortQuestionText;

            public SortQuestionViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }


            public void bindDataWithView(final SortData contentBean, Context context,
                                         final int position, final OnItemClickListener onItemClickListener,
                                         int size) {
                if (contentBean == null) return;
                mSortQuestionText.setText(contentBean.getContent());
                mSortQuestionText.setBackgroundResource(mItemBgDrawables.get(contentBean.getBgPosition()));
                mSortQuestionText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mSortQuestionText.setDrawingCacheEnabled(true);
                        mSortQuestionText.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

                        Bitmap drawingCache = mSortQuestionText.getDrawingCache();

                        float x = mSortQuestionText.getX();
                        float y = mSortQuestionText.getY();
                        Log.d("----", "onClick: " + x + "  " + y);
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(contentBean, position, drawingCache, x, y, mSortQuestionText);
                        }
                        int[] ints = new int[2];
                        mSortQuestionText.getLocationInWindow(ints); //获取在当前窗口内的绝对坐标
                        mSortQuestionText.getLocationOnScreen(ints);
                        Log.d("wangjie", "onClick: " + ints[0] + "  " + ints[1]);


                        Log.d("wangjie", "onClick: x " + x + "  y " + y);

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
            TextView mResultText;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final SortData contentBean, final int position, Context context, final OnItemClickListener onItemClickListener, ArrayList<int[]> itemBgColors) {
                if (contentBean == null) return;
                TypefaceUtil.setHelveticaLTCompressedFont(mLineNumber);
                mLineNumber.setText(String.valueOf(position + 1));
                if (contentBean.isSelect()) {
                    mResultText.setText(contentBean.getContent());
                    if (itemBgColors != null && !itemBgColors.isEmpty()) {
                        mResultText.setBackground(createDrawable(itemBgColors.get(contentBean.getBgPosition()), context));
                    }
                    mResultText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            float x = mResultText.getX();
                            float y = mResultText.getY();

                            mResultText.setDrawingCacheEnabled(true);
                            mResultText.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

                            Bitmap drawingCache = mResultText.getDrawingCache();

                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(contentBean, position, drawingCache, x, y, null);
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
