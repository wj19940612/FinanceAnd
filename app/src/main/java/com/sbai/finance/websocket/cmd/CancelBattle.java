package com.sbai.finance.websocket.cmd;

import com.sbai.finance.websocket.WSCmd;

/**
 * Created by linrongfang on 2017/6/30.
 */

public class CancelBattle extends WSCmd {
    public CancelBattle(int battleId) {
        super("/game/battle/cancelBattle.do");
        setParameters(new Param(battleId));
    }

    class Param {

        private int battleId;

        public Param(int battleId) {
            this.battleId = battleId;
        }
    }
}
