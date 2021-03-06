package com.sbai.finance.game.cmd;

import com.sbai.finance.game.WSCmd;

/**
 *
 * 订阅房间
 *
 */
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
