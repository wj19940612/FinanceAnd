package com.sbai.finance.websocket.cmd;

import com.sbai.finance.websocket.WSCmd;

public class QuickMatch extends WSCmd {

    public static final int TYPE_QUICK_MATCH = 1;
    public static final int TYPE_CANCEL = 0;
    public static final int TYPE_CONTINUE = 2;

    public QuickMatch(int type, String refuseIds) {
        super("/game/battle/quickSearchForAgainst.do");
        setParameters(new Param(type, refuseIds));
    }

    class Param {

        private int type;
        private String refuseIds;

        public Param(int type, String refuseIds) {
            this.type = type;
            this.refuseIds = refuseIds;
        }
    }
}
