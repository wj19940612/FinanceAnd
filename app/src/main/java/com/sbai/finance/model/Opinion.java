package com.sbai.finance.model;

/**
 * Created by lixiaokuan0819 on 2017/4/24.
 */

public class Opinion {
    private String name;

    public Opinion (String name) {
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
