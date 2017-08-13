package com.sbai.finance.model.studyroom;

import java.io.Serializable;
import java.util.List;

/**
 * 自习室练习
 */

public class StudyOption implements Serializable {

    /**
     * analysis : 敢死队风格士大夫个
     * content : [{"content":"","id":892317136503042000,"right":false,"seq":1},{"content":"","id":892317136503042000,"right":false,"seq":2}]
     * id : 59802f1068fad7db045c8984
     * levelRatio : 1.1
     * modifyTime : 2017-08-01 17:32:38
     * option : false
     * status : 1
     * title : 妮妮你您
     * type : 2
     */

    private String analysis;
    private String id;
    private double levelRatio;
    private String modifyTime;
    private boolean option;
    private int status;
    private String title;
    private int type;
    private List<ContentBean> content;

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
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

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean implements Serializable {
        /**
         * content :
         * id : 892317136503042000
         * right : false
         * seq : 1
         */

        private String content;
        private long id;
        private boolean right;
        private int seq;

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
    }
}
