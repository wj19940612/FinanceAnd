package com.sbai.finance.model;

/**
 * Created by lixiaokuan0819 on 2017/4/19.
 */

public class Comment {
    private String name;

    public Comment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "name='" + name + '\'' +
                '}';
    }
}
