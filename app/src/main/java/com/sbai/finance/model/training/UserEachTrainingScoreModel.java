package com.sbai.finance.model.training;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbai.finance.BuildConfig;
import com.sbai.finance.model.levelevaluation.EvaluationResult;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.ToastUtil;

import java.util.List;


/**
 * Created by ${wangJie} on 2017/8/8.
 * 用户的 分数 包含各个分数段
 */

public class UserEachTrainingScoreModel implements Parcelable {


    /**
     * scores : [{"classifyId":1,"classifyName":"test1"}]
     * rank : 0
     */

    private double rank;           //百分率
    private List<ScoresBean> scores;
    private int evaluate;         // 是否测评过  1 测评过 0 没有

    public boolean isEvaluated() {
        return getEvaluate() == 1;
    }

    public int getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(int evaluate) {
        this.evaluate = evaluate;
    }

    public double getUserTotalScore() {
        double totalScore = 0;
        if (getScores() != null && !getScores().isEmpty()) {
            for (ScoresBean data : getScores()) {
                totalScore += data.getScore();
            }
        }
        return totalScore;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public List<ScoresBean> getScores() {
        return scores;
    }

    public void setScores(List<ScoresBean> scores) {
        this.scores = scores;
    }

    public static class ScoresBean implements Parcelable {
        /**
         * classifyId : 1
         * classifyName : test1
         */

        private int classifyId;
        //评价因子名称
        private String classifyName;
        //因子得分  正确率
        private double score;
        //每一项的总分
        private double totalScore;

        public double getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(double totalScore) {
            this.totalScore = totalScore;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getClassifyId() {
            return classifyId;
        }

        public void setClassifyId(int classifyId) {
            this.classifyId = classifyId;
        }

        public String getClassifyName() {
            return classifyName;
        }

        public void setClassifyName(String classifyName) {
            this.classifyName = classifyName;
        }

        public ScoresBean() {
        }

        @Override
        public String toString() {
            return "ScoresBean{" +
                    "classifyId=" + classifyId +
                    ", classifyName='" + classifyName + '\'' +
                    ", score=" + score +
                    ", totalScore=" + totalScore +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.classifyId);
            dest.writeString(this.classifyName);
            dest.writeDouble(this.score);
            dest.writeDouble(this.totalScore);
        }

        protected ScoresBean(Parcel in) {
            this.classifyId = in.readInt();
            this.classifyName = in.readString();
            this.score = in.readDouble();
            this.totalScore = in.readDouble();
        }

        public static final Creator<ScoresBean> CREATOR = new Creator<ScoresBean>() {
            @Override
            public ScoresBean createFromParcel(Parcel source) {
                return new ScoresBean(source);
            }

            @Override
            public ScoresBean[] newArray(int size) {
                return new ScoresBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.rank);
        dest.writeTypedList(this.scores);
    }

    public UserEachTrainingScoreModel() {
    }

    protected UserEachTrainingScoreModel(Parcel in) {
        this.rank = in.readDouble();
        this.scores = in.createTypedArrayList(ScoresBean.CREATOR);
    }

    public static final Creator<UserEachTrainingScoreModel> CREATOR = new Creator<UserEachTrainingScoreModel>() {
        @Override
        public UserEachTrainingScoreModel createFromParcel(Parcel source) {
            return new UserEachTrainingScoreModel(source);
        }

        @Override
        public UserEachTrainingScoreModel[] newArray(int size) {
            return new UserEachTrainingScoreModel[size];
        }
    };

    // 计算每一项的分数所占各个总分的比例
    public EvaluationResult getTestResultModel() {
        EvaluationResult evaluationResult = new EvaluationResult();
        evaluationResult.setTotalCredit(getUserTotalScore());
        for (ScoresBean data : getScores()) {
            double scale = 0;
            if (data.getTotalScore() != 0) {
                if (data.getScore() > data.getTotalScore()) {
                    scale = 1;
                    if (BuildConfig.DEBUG) {
                        ToastUtil.show("数据不对 超出限制了");
                    }
                } else {
                    scale = FinanceUtil.divide(data.getScore(), data.getTotalScore()).doubleValue();
                }
            }
            switch (data.getClassifyName()) {
                case "盈利能力":
                    evaluationResult.setProfitAccuracy(scale);
                    break;
                case "风险控制":
                    evaluationResult.setRiskAccuracy(scale);
                    break;
                case "基本面分析":
                    evaluationResult.setBaseAccuracy(scale);
                    break;
                case "指标分析":
                    evaluationResult.setSkillAccuracy(scale);
                    break;
                case "理论掌握":
                    evaluationResult.setTheoryAccuracy(scale);
                    break;
            }
        }
        return evaluationResult;
    }

    @Override
    public String toString() {
        return "UserEachTrainingScoreModel{" +
                "rank=" + rank +
                ", scores=" + scores +
                ", evaluate=" + evaluate +
                '}';
    }
}
