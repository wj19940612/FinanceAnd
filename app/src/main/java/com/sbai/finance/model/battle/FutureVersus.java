package com.sbai.finance.model.battle;

import java.util.List;

/**
 * Created by Administrator on 2017-06-19.
 */

public class FutureVersus {
    public static final int HAS_MORE=0;
    private int end;
    private List<VersusGaming> list;
    public boolean hasMore(){
        return end==HAS_MORE;
    }
    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public List<VersusGaming> getList() {
        return list;
    }

    public void setList(List<VersusGaming> list) {
        this.list = list;
    }
}
