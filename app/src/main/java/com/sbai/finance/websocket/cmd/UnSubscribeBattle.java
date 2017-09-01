package com.sbai.finance.websocket.cmd;

import com.sbai.finance.websocket.WSCmd;

/**
 *
 * 反订阅房间信息
 *
 */
public class UnSubscribeBattle extends WSCmd {

    public UnSubscribeBattle(int battleId) {
        super("/game/battle/unsubscribeBattle.do");
        setParameters(new Param(battleId));
    }

    class Param {

        private int battleId;

        public Param(int battleId) {
            this.battleId = battleId;
        }
    }
}
