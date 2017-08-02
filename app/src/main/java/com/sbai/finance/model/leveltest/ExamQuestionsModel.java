package com.sbai.finance.model.leveltest;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by ${wangJie} on 2017/7/31.
 * 水平测试题目model
 */

public class ExamQuestionsModel implements Parcelable {

    public static final int RESULT_A = 0;
    public static final int RESULT_B = 1;
    public static final int RESULT_C = 2;
    public static final int RESULT_D = 3;

    private String topic;
    private int id;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DataBean> getDataList() {
        return mDataList;
    }

    public void setDataList(List<DataBean> dataList) {
        mDataList = dataList;
    }

    private List<DataBean> mDataList;


    public static class DataBean implements Parcelable {

        private boolean isSelect;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public DataBean(String result) {
            this.result = result;
        }

        private String result;

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public DataBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
            dest.writeString(this.result);
        }

        protected DataBean(Parcel in) {
            this.isSelect = in.readByte() != 0;
            this.result = in.readString();
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel source) {
                return new DataBean(source);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.topic);
        dest.writeInt(this.id);
        dest.writeTypedList(this.mDataList);
    }

    public ExamQuestionsModel() {
    }

    protected ExamQuestionsModel(Parcel in) {
        this.topic = in.readString();
        this.id = in.readInt();
        this.mDataList = in.createTypedArrayList(DataBean.CREATOR);
    }

    public static final Creator<ExamQuestionsModel> CREATOR = new Creator<ExamQuestionsModel>() {
        @Override
        public ExamQuestionsModel createFromParcel(Parcel source) {
            return new ExamQuestionsModel(source);
        }

        @Override
        public ExamQuestionsModel[] newArray(int size) {
            return new ExamQuestionsModel[size];
        }
    };
}
