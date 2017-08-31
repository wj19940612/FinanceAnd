package com.sbai.finance.websocket.cmd;

import com.sbai.finance.websocket.WSCmd;

/**
 * 对战-游戏点赞
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
