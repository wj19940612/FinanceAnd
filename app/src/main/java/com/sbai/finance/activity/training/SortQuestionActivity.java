package com.sbai.finance.activity.training;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.TrainingQuestion;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.training.TrainProgressBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 年报出炉界面  排序题
 */
public class SortQuestionActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.progressBar)
    TrainProgressBar mProgressBar;
    @BindView(R.id.sortQuestionRecyclerView)
    RecyclerView mSortQuestionRecyclerView;
    @BindView(R.id.sortResultRecycleView)
    RecyclerView mSortResultRecycleView;
    @BindView(R.id.confirmAnnals)
    ImageView mConfirmResult;

    //用來记录底部界面选择答案的索引
    private HashSet<Integer> mResultSet;
    private TrainingQuestion mTrainingQuestions;

    private int mAnnalsMaterialsBgDrawables[] = new int[]{R.drawable.bg_annals_materials_1, R.drawable.bg_annals_materials_2,
            R.drawable.bg_annals_materials_3, R.drawable.bg_annals_materials_4,
            R.drawable.bg_annals_materials_5, R.drawable.bg_annals_materials_6,
            R.drawable.bg_annals_materials_7, R.drawable.bg_annals_materials_8};

    private SortResultAdapter mSortResultAdapter;
    private SortQuestionAdapter mSortQuestionAdapter;
    private List<TrainingQuestion.ContentBean> mQuestionResultList;


    public interface OnItemClickListener {
        void onItemClick(TrainingQuestion.ContentBean data, int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annals_create);
        ButterKnife.bind(this);
        translucentStatusBar();
        Intent intent = getIntent();
        mResultSet = new HashSet<>();
        mTrainingQuestions = intent.getParcelableExtra(ExtraKeys.TRAIN_QUESTIONS);
        // TODO: 2017/8/16 倒计时时间
        long longExtra = intent.getIntExtra(ExtraKeys.TRAIN_TARGET_TIME, 0);

        mQuestionResultList = mTrainingQuestions.getContent();
        if (mQuestionResultList == null || mQuestionResultList.isEmpty()) return;
        initSortQuestionAdapter(mQuestionResultList);
        initSortResultAdapter(mQuestionResultList);
    }

    private void initSortResultAdapter(List<TrainingQuestion.ContentBean> content) {
        mSortResultAdapter = new SortResultAdapter(new ArrayList<TrainingQuestion.ContentBean>(), this);
        mSortResultRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mSortResultRecycleView.setAdapter(mSortResultAdapter);
        mSortResultAdapter.addData(content);
        mSortResultAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(TrainingQuestion.ContentBean data, int position) {
                updateResult(data, position);
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

        public SortResultAdapter(ArrayList<TrainingQuestion.ContentBean> sortResultList, Context context) {
            mSortResultList = sortResultList;
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_create_annals, parent, false);
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
                holder.bindDataWithView(mSortResultList.get(position), position, mContext, mOnItemClickListener);
            }
        }

        @Override
        public int getItemCount() {
            return mSortResultList != null ? mSortResultList.size() : 0;
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

            public void bindDataWithView(final TrainingQuestion.ContentBean contentBean, final int position, Context context, final OnItemClickListener onItemClickListener) {
                if (contentBean == null) return;
                mLineNumber.setText(String.valueOf(position + 1));
                if (contentBean.isSelect()) {
                    mMaterials.setText(contentBean.getContent());
                    mMaterials.setBackground(createDrawable(new int[]{Color.RED, Color.RED}, context));
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
