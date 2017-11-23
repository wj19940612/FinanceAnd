package com.sbai.finance.model.local;

/**
 * Modified by john on 30/10/2017
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public interface BattleStatus {
    int CREATED_OWNER = 1;

    int STARTED_OWNER = 2;
    int STARTED_CHALLENGER = 3;
    int STARTED_OBSERVER = 4;

    int OVER_OWNER = 5;
    int OVER_CHALLENGER = 6;
    int OVER_OBSERVER = 7;

    int CANCELED = 8;
}
