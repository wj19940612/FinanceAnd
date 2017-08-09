package com.sbai.finance.model.train;

/**
 * 训练反馈获取的信息
 */

public class TrainFeedback {

    /**
     * question : 想要训练更多项目15
     * id : 76
     * checked   本地用来区分是否选择
     */
    private boolean checked;

    private String question;
    private int id;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
