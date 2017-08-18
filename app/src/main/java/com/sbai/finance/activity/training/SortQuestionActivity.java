package com.sbai.finance.activity.training;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.levelevaluation.QuestionAnswer;
import com.sbai.finance.model.training.TrainingQuestion;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.TypefaceUtil;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.training.TrainHeaderView;

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


    @BindView(R.id.trainHeaderView)
    TrainHeaderView mTrainHeaderView;
    @BindView(R.id.sortQuestionRecyclerView)
    RecyclerView mSortQuestionRecyclerView;
    @BindView(R.id.sortResultRecycleView)
    RecyclerView mSortResultRecycleView;
    @BindView(R.id.confirmAnnals)
    ImageView mConfirmAnnals;
    @BindView(R.id.sortResultLL)
    LinearLayout mSortResultLL;
    //用來记录底部界面选择答案的索引
    private HashSet<Integer> mResultSet;
    private TrainingQuestion mTrainingQuestion;

    private int mAnnalsMaterialsBgDrawables[] = new int[]{R.drawable.bg_annals_materials_1, R.drawable.bg_annals_materials_2,
            R.drawable.bg_annals_materials_3, R.drawable.bg_annals_materials_4,
            R.drawable.bg_annals_materials_5, R.drawable.bg_annals_materials_6,
            R.drawable.bg_annals_materials_7, R.drawable.bg_annals_materials_8};


    private ArrayList<int[]> mResultBgColos;

    private SortResultAdapter mSortResultAdapter;
    private SortQuestionAdapter mSortQuestionAdapter;
    private List<TrainingQuestion.ContentBean> mQuestionResultList;

    public interface OnItemClickListener {
        void onItemClick(TrainingQuestion.ContentBean data, int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_question);
        ButterKnife.bind(this);
        translucentStatusBar();
        initHeaderView();
        Intent intent = getIntent();
        mResultSet = new HashSet<>();
        createResultBgColors();
        mTrainingQuestion = intent.getParcelableExtra(ExtraKeys.TRAIN_QUESTIONS);
        // TODO: 2017/8/16 倒计时时间
        long longExtra = intent.getIntExtra(ExtraKeys.TRAIN_TARGET_TIME, 0);

        mTrainHeaderView.setMinuteTime(1);

        mQuestionResultList = mTrainingQuestion.getRandRomResultList(mTrainingQuestion.getContent());

        if (mQuestionResultList == null || mQuestionResultList.isEmpty()) return;
        initSortQuestionAdapter(mQuestionResultList);
        initSortResultAdapter(mQuestionResultList);

    }

    private void createResultBgColors() {
        mResultBgColos = new ArrayList<>();
        int[] firstColors = {Color.parseColor("#13c336"), Color.parseColor("#13c336")};
        int[] secondColors = {Color.parseColor("#FE9722"), Color.parseColor("#FE9722")};
        int[] thirdColors = {Color.parseColor("#2561F6"), Color.parseColor("#2561F6")};
        int[] forthColors = {Color.parseColor("#55adFF"), Color.parseColor("#55adFF")};
        int[] fifthColors = {Color.parseColor("#F25B57"), Color.parseColor("#F25B57")};
        int[] sixthColors = {Color.parseColor("#af56ff"), Color.parseColor("#af56ff")};
        int[] seventhColors = {Color.parseColor("#FEC022"), Color.parseColor("#FEC022")};
        int[] eighthColors = {Color.parseColor("#EF6D6A"), Color.parseColor("#EF6D6A")};

        mResultBgColos.add(firstColors);
        mResultBgColos.add(secondColors);
        mResultBgColos.add(thirdColors);
        mResultBgColos.add(forthColors);
        mResultBgColos.add(fifthColors);
        mResultBgColos.add(sixthColors);
        mResultBgColos.add(seventhColors);
        mResultBgColos.add(eighthColors);
    }


    private void initHeaderView() {
        mTrainHeaderView.setCallback(new TrainHeaderView.Callback() {
            @Override
            public void onBackClick() {
                SmartDialog.with(getActivity(), R.string.is_sure_exit_train)
                        .setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                SortQuestionActivity.this.finish();
                            }
                        })
                        .show();
            }

            @Override
            public void onHowPlayClick() {

            }

            @Override
            public void onEndOfTimer() {
                ToastUtil.show("时间到了");
            }
        });
    }

    private void initSortResultAdapter(List<TrainingQuestion.ContentBean> content) {
        mSortResultAdapter = new SortResultAdapter(new ArrayList<TrainingQuestion.ContentBean>(), this);
        mSortResultRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mSortResultRecycleView.setAdapter(mSortResultAdapter);
        mSortResultRecycleView.setItemAnimator(new DefaultItemAnimator());
        mSortResultAdapter.addData(content);
        mSortResultAdapter.setItemBgColors(mResultBgColos);
        mSortResultAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(TrainingQuestion.ContentBean data, int position) {
                updateResult(data, position);
                mSortQuestionRecyclerView.scrollToPosition(0);
            }
        });
    }

    private void updateResult(TrainingQuestion.ContentBean data, int position) {
        data.setSelect(false);
        mResultSet.remove(position);
        mSortResultAdapter.notifyItemChanged(position, data);
        mSortQuestionAdapter.insert(0, data);
    }

    private void initSortQuestionAdapter(List<TrainingQuestion.ContentBean> content) {
        mSortQuestionAdapter = new SortQuestionAdapter(new ArrayList<TrainingQuestion.ContentBean>(), this);
        mSortQuestionAdapter.setItemBgDrawables(mAnnalsMaterialsBgDrawables);
        mSortQuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSortQuestionRecyclerView.setAdapter(mSortQuestionAdapter);
        mSortQuestionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSortQuestionAdapter.addData(content);
        mSortQuestionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(TrainingQuestion.ContentBean data, int position) {
                chooseResult(position);
            }
        });
    }

    private void chooseResult(int position) {
        mSortQuestionAdapter.notifyItemRemovedData(position);
        List<TrainingQuestion.ContentBean> resultData = mSortResultAdapter.getResultData();
        if (!resultData.isEmpty()) {
            for (int i = 0; i < resultData.size(); i++) {
                if (mResultSet.add(i)) {
                    mSortResultAdapter.changeItemData(i);
                    break;
                }
            }
        }
    }

    @OnClick(R.id.confirmAnnals)
    public void onViewClicked() {
        ArrayList<TrainingQuestion.ContentBean> resultData = mSortResultAdapter.getResultData();
        if (resultData != null && resultData.size() == mQuestionResultList.size()) {
            QuestionAnswer sortResult = createSortResult(resultData);
            Client.confirmQuestionResult(sortResult)
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback<Resp<String>>() {
                        @Override
                        protected void onRespSuccess(Resp<String> resp) {
                            Log.d(TAG, "onRespSuccess: " + resp.toString());
                        }
                    })
                    .fire();
            startResultListScaleAnimation();
        }
    }

    private void startResultListScaleAnimation() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0f, 1, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(5000);
        scaleAnimation.setFillAfter(true);
        mSortResultLL.startAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ToastUtil.show("动画结束");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //将答案数据组装成服务端需要的格式
    private QuestionAnswer createSortResult(ArrayList<TrainingQuestion.ContentBean> resultData) {
        QuestionAnswer questionAnswer = new QuestionAnswer();
        ArrayList<QuestionAnswer.AnswersBean> answersBeenList = new ArrayList<>();
        QuestionAnswer.AnswersBean answersBean = new QuestionAnswer.AnswersBean();

        answersBean.setTopicId(mTrainingQuestion.getId());
        //答案数组
        ArrayList<QuestionAnswer.AnswersBean.AnswerIdsBean> answerIdsBeenList = new ArrayList<>();
        for (int i = 0; i < resultData.size(); i++) {
            TrainingQuestion.ContentBean contentBean = resultData.get(i);
            QuestionAnswer.AnswersBean.AnswerIdsBean answerIdsBean = new QuestionAnswer.AnswersBean.AnswerIdsBean();
            answerIdsBean.setOptionId(contentBean.getId());
            answerIdsBeenList.add(answerIdsBean);
        }
        answersBean.setAnswerIds(answerIdsBeenList);
        answersBeenList.add(answersBean);
        questionAnswer.setAnswers(answersBeenList);
        return questionAnswer;
    }


    static class SortQuestionAdapter extends RecyclerView.Adapter<SortQuestionAdapter.AnnalsMaterialsViewHolder> {
        ArrayList<TrainingQuestion.ContentBean> mSortQuestionList;
        Context mContext;
        private int[] mItemBgDrawables;
        private OnItemClickListener mOnItemClickListener;

        public SortQuestionAdapter(ArrayList<TrainingQuestion.ContentBean> annalsMaterialsList, Context context) {
            this.mSortQuestionList = annalsMaterialsList;
            this.mContext = context;
        }

        public void addData(List<TrainingQuestion.ContentBean> materialsList) {
            mSortQuestionList.clear();
            notifyItemRangeRemoved(0, mSortQuestionList.size());
            mSortQuestionList.addAll(materialsList);
            notifyItemRangeChanged(0, mSortQuestionList.size());
        }

        public ArrayList<TrainingQuestion.ContentBean> getQuestionData() {
            return mSortQuestionList;
        }

        public void notifyItemRemovedData(int position) {
            this.notifyItemRemoved(position);
            mSortQuestionList.remove(position);
            notifyItemChanged(0, mSortQuestionList.size());
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
                holder.bindDataWithView(mSortQuestionList.get(position), mContext, position, mOnItemClickListener);
            }
        }

        @Override
        public int getItemCount() {
            return mSortQuestionList != null ? mSortQuestionList.size() : 0;
        }

        public void setItemBgDrawables(int[] annalsMaterialsBgDrawables) {
            this.mItemBgDrawables = annalsMaterialsBgDrawables;
        }


        class AnnalsMaterialsViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.materialsText)
            TextView mMaterialsText;

            public AnnalsMaterialsViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }


            public void bindDataWithView(final TrainingQuestion.ContentBean contentBean, Context context, final int position, final OnItemClickListener onItemClickListener) {
                if (contentBean == null) return;
                mMaterialsText.setText(contentBean.getContent());
                mMaterialsText.setBackgroundResource(mItemBgDrawables[position % mItemBgDrawables.length]);
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

    static class SortResultAdapter extends RecyclerView.Adapter<SortResultAdapter.ViewHolder> {

        private ArrayList<TrainingQuestion.ContentBean> mSortResultList;
        private Context mContext;
        private OnItemClickListener mOnItemClickListener;
        private ArrayList<int[]> mItemBgColors;

        public SortResultAdapter(ArrayList<TrainingQuestion.ContentBean> sortResultList, Context context) {
            mSortResultList = sortResultList;
            mContext = context;
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

        public void changeItemData(int position) {
            if (position <= mSortResultList.size()) {
                TrainingQuestion.ContentBean contentBean = mSortResultList.get(position);
                contentBean.setSelect(true);
                notifyItemChanged(position, contentBean);
            }
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
            return mSortResultList != null ? mSortResultList.size() : 0;
        }

        public void setItemBgColors(ArrayList<int[]> resultBgColors) {
            this.mItemBgColors = resultBgColors;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
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
                        mMaterials.setBackground(createDrawable(itemBgColors.get(position % 8), context));
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
