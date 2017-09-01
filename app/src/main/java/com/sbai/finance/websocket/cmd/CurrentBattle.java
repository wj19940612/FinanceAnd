package com.sbai.finance.websocket.cmd;

import com.sbai.finance.websocket.WSCmd;

/**
 *
 * 获取当前对战信息
 *
 */
public class CurrentBattle extends WSCmd {

    public CurrentBattle(int battleId, String batchCode) {
        super("/game/battle/findBattle.do");
        setParameters(new Param(battleId, batchCode));
    }

    class Param {

        private int battleId;
        private String batchCode;

        public Param(int battleId, String batchCode) {
            this.battleId = battleId;
            this.batchCode = batchCode;
        }
    }
}
