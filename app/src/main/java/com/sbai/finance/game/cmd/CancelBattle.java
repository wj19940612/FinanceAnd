package com.sbai.finance.game.cmd;

import com.sbai.finance.game.WSCmd;

/**
 *
 * 取消对战命令，关闭房间
 *
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
