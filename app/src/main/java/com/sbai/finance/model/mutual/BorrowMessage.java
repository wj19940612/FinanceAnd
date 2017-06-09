package com.sbai.finance.model.mutual;


public class BorrowMessage {
    private int loanId;
    private int userId;
    private String content;
    private String userName;
    private int id;

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BorrowMessage{" +
                "loanId=" + loanId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", userName='" + userName + '\'' +
                ", id=" + id +
                '}';
    }
}
