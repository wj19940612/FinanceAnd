package com.sbai.finance.model.arena;

import com.sbai.finance.net.Client;

import java.util.List;

/**
 * Created by ${wangJie} on 2017/11/2.
 * {@link Client# activity/activity/getActivityScore.do}
 */

public class ArenaActivityAndUserStatus {

    public static final String DEFAULT_ACTIVITY_CODE = "stocktrade01";

    private static final int ARENA_ACTIVITY_STATUS_NOT_STARTED = 1;
    private static final int ARENA_ACTIVITY_STATUS_EXCHANGEING = 2;
    public static final int ARENA_ACTIVITY_STATUS_OVER = 3;


    public boolean arenaIsOver() {
        return activityModel != null && activityModel.getActivityStatus() == ARENA_ACTIVITY_STATUS_OVER;
    }

    public boolean userCanExchangeAward() {
        return activityModel != null && activityModel.getActivityStatus() == ARENA_ACTIVITY_STATUS_OVER;
    }


    private ArenaInfo activityModel;
    private List<ArenaActivityAwardInfo> prizeModels;
    private UserActivityScore myScoreVO;
    private boolean applyed;  //是否报名

    public ArenaInfo getActivityModel() {
        return activityModel;
    }

    public void setActivityModel(ArenaInfo activityModel) {
        this.activityModel = activityModel;
    }

    public List<ArenaActivityAwardInfo> getPrizeModels() {
        return prizeModels;
    }

    public void setPrizeModels(List<ArenaActivityAwardInfo> prizeModels) {
        this.prizeModels = prizeModels;
    }

    public UserActivityScore getMyScoreVO() {
        return myScoreVO;
    }

    public void setMyScoreVO(UserActivityScore myScoreVO) {
        this.myScoreVO = myScoreVO;
    }

    public boolean isApplyed() {
        return applyed;
    }

    public void setApplyed(boolean applyed) {
        this.applyed = applyed;
    }

    @Override
    public String toString() {
        return "ArenaActivityAndUserStatus{" +
                "activityModel=" + activityModel +
                ", prizeModels=" + prizeModels +
                ", myScoreVO=" + myScoreVO +
                ", applyed=" + applyed +
                '}';
    }
}
