package com.sbai.finance.websocket.cmd;

import com.sbai.finance.websocket.WSCmd;

public class SubscribeBattle extends WSCmd {

    public SubscribeBattle(int battleId) {
        super("/game/battle/subscribeBattle.do");
        setParameters(new Param(battleId));
    }

    class Param {

        private int battleId;

        public Param(int battleId) {
            this.battleId = battleId;
        }
    }
}
