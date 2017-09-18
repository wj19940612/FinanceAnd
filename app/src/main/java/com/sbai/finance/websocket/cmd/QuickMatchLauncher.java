package com.sbai.finance.websocket.cmd;

import com.sbai.finance.websocket.WSCmd;

/**
 * 房主快速匹配
 */
public class QuickMatchLauncher extends WSCmd {

    public static final int TYPE_QUICK_MATCH = 1;  //开始快速匹配
    public static final int TYPE_CANCEL = 0;        //取消匹配
    public static final int TYPE_CONTINUE = 2;      //继续匹配

    public QuickMatchLauncher(int type, int battleId) {
        super("/game/battle/quickSearchForLaunch.do");
        setParameters(new Param(type, battleId));
    }

    class Param {

        private int type;
        private int battleId;

        public Param(int type, int battleId) {
            this.type = type;
            this.battleId = battleId;
        }
    }
}
