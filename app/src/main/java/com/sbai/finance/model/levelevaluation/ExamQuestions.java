package com.sbai.finance.model.levelevaluation;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ${wangJie} on 2017/7/31.
 * 水平测试题目   训练模块 只读图、选择题、图线题 model
 */

public class ExamQuestions implements Parcelable {

    /**
     * analysis : 打算大苏打打算大苏打
     * content : [{"content":"法撒旦发是的","id":892321037251899392,"right":false,"seq":1},{"content":"法撒旦发","id":892321037251899393,"right":true,"seq":2}]
     * digest : fdsaa
     * id : 5980309868fad7db045c8986
     * levelRatio : 2.1
     * modifyTime : 2017-08-01 17:48:08
     * option : false
     * status : 0
     * title : 阿斯顿ASD阿斯顿ASD
     * type : 2
     * createTime : 2017-08-02 09:19:40
     */

    private String analysis;     //题目解析
    private String digest;       //提干
    private String id;
    private double levelRatio;   //难度比例
    private String modifyTime;   //是否可跳过 （有的题是只读的）
    private boolean option;      //是否可跳过 （有的题是只读的）
    private int status;
    private String title;
    private int type;            // 1到6 分别为 排序，单选，多选，连连看，只读的题目（比如看漫画） ,K平均线
    private String createTime;
    private ArrayList<ContentBean> content;


    private String source;       // future 期货 stock 股票


    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLevelRatio() {
        return levelRatio;
    }

    public void setLevelRatio(double levelRatio) {
        this.levelRatio = levelRatio;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public boolean isOption() {
        return option;
    }

    public void setOption(boolean option) {
        this.option = option;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public ArrayList<ContentBean> getContent() {
        return content;
    }

    public void setContent(ArrayList<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean implements Parcelable {
        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        /**
         * content : 法撒旦发是的
         * id : 892321037251899392
         * right : false
         * seq : 1
         */
        private boolean isSelect;
        private String content;
        private long id;
        private boolean right;   //是否是正确答案
        private int seq;         //seq 排序

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public boolean isRight() {
            return right;
        }

        public void setRight(boolean right) {
            this.right = right;
        }

        public int getSeq() {
            return seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.content);
            dest.writeLong(this.id);
            dest.writeByte(this.right ? (byte) 1 : (byte) 0);
            dest.writeInt(this.seq);
        }

        public ContentBean() {
        }

        protected ContentBean(Parcel in) {
            this.content = in.readString();
            this.id = in.readLong();
            this.right = in.readByte() != 0;
            this.seq = in.readInt();
        }

        public static final Creator<ContentBean> CREATOR = new Creator<ContentBean>() {
            @Override
            public ContentBean createFromParcel(Parcel source) {
                return new ContentBean(source);
            }

            @Override
            public ContentBean[] newArray(int size) {
                return new ContentBean[size];
            }
        };

        @Override
        public String toString() {
            return "ContentBean{" +
                    "isSelect=" + isSelect +
                    ", content='" + content + '\'' +
                    ", id=" + id +
                    ", right=" + right +
                    ", seq=" + seq +
                    '}';
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.analysis);
        dest.writeString(this.digest);
        dest.writeString(this.id);
        dest.writeDouble(this.levelRatio);
        dest.writeString(this.modifyTime);
        dest.writeByte(this.option ? (byte) 1 : (byte) 0);
        dest.writeInt(this.status);
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeString(this.createTime);
        dest.writeTypedList(this.content);
    }

    public ExamQuestions() {
    }

    protected ExamQuestions(Parcel in) {
        this.analysis = in.readString();
        this.digest = in.readString();
        this.id = in.readString();
        this.levelRatio = in.readDouble();
        this.modifyTime = in.readString();
        this.option = in.readByte() != 0;
        this.status = in.readInt();
        this.title = in.readString();
        this.type = in.readInt();
        this.createTime = in.readString();
        this.content = in.createTypedArrayList(ContentBean.CREATOR);
    }

    public static final Creator<ExamQuestions> CREATOR = new Creator<ExamQuestions>() {
        @Override
        public ExamQuestions createFromParcel(Parcel source) {
            return new ExamQuestions(source);
        }

        @Override
        public ExamQuestions[] newArray(int size) {
            return new ExamQuestions[size];
        }
    };

    @Override
    public String toString() {
        return "ExamQuestionsModel{" +
                "analysis='" + analysis + '\'' +
                ", digest='" + digest + '\'' +
                ", id='" + id + '\'' +
                ", levelRatio=" + levelRatio +
                ", modifyTime='" + modifyTime + '\'' +
                ", option=" + option +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", createTime='" + createTime + '\'' +
                ", content=" + content +
                '}';
    }
}
