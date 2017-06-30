package com.sbai.finance.websocket.cmd;

import com.sbai.finance.websocket.WSCmd;

/**
 * Created by linrongfang on 2017/6/30.
 */

public class UserPraise extends WSCmd {

    public UserPraise(int battleId, int praiseId) {
        super("/game/battle/userPraise.do");
        setParameters(new Param(battleId, praiseId));
    }

    class Param {

        private int battleId;
        private int praiseId;

        public Param(int battleId, int praiseId) {
            this.battleId = battleId;
            this.praiseId = praiseId;
        }
    }
}
