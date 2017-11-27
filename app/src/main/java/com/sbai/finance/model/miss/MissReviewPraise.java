package com.sbai.finance.model.miss;

import com.sbai.finance.net.Client;

/**
 * Created by ${wangJie} on 2017/11/27.
 * {@link Client# /user/comment/priseReply.do}
 * 姐说问题详情页点赞
 */

public class MissReviewPraise {
    private int priseCount;
    private boolean isPrise;

    public int getPriseCount() {
        return priseCount;
    }

    public void setPriseCount(int priseCount) {
        this.priseCount = priseCount;
    }

    public boolean isPrise() {
        return isPrise;
    }

    public void setPrise(boolean prise) {
        isPrise = prise;
    }
}
