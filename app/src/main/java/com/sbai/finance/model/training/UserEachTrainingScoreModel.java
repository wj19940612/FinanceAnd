package com.sbai.finance.model.training;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbai.finance.model.leveltest.TestResultModel;

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

    private double rank;
    private List<ScoresBean> scores;


    public double getUserTotalScore() {
        double totalScore = 0;
        if (getScores() != null && !getScores().isEmpty()) {
            for (ScoresBean data : getScores()) {
                totalScore += data.getSocre();
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
        //因子得分
        private double socre;

        public double getSocre() {
            return socre;
        }

        public void setSocre(double socre) {
            this.socre = socre;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.classifyId);
            dest.writeString(this.classifyName);
            dest.writeDouble(this.socre);
        }

        public ScoresBean() {
        }

        protected ScoresBean(Parcel in) {
            this.classifyId = in.readInt();
            this.classifyName = in.readString();
            this.socre = in.readDouble();
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

        @Override
        public String toString() {
            return "ScoresBean{" +
                    "classifyId=" + classifyId +
                    ", classifyName='" + classifyName + '\'' +
                    ", socre=" + socre +
                    '}';
        }
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

    @Override
    public String toString() {
        return "UserEachTrainingScoreModel{" +
                "rank=" + rank +
                ", scores=" + scores +
                '}';
    }

    public TestResultModel getTestResultModel() {
        TestResultModel testResultModel = new TestResultModel();
        for (ScoresBean data : getScores()) {
            switch (data.getClassifyName()) {
                case "盈利能力":
                    testResultModel.setProfitAccuracy(data.getSocre());
                    break;
                case "风险控制":
                    testResultModel.setRiskAccuracy(data.getSocre());
                    break;
                case "基本面分析":
                    testResultModel.setBaseAccuracy(data.getSocre());
                    break;
                case "指标分析":
                    testResultModel.setSkillAccuracy(data.getSocre());
                    break;
                case "理论掌握":
                    testResultModel.setTheoryAccuracy(data.getSocre());
                    break;
            }
        }

        return testResultModel;
    }
}
