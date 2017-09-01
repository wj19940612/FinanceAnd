package com.sbai.finance.websocket.cmd;

import com.sbai.finance.websocket.WSCmd;

/**
 *
 * 快速匹配，继续快速匹配并传送拒绝的房间 ids
 *
 */
public class QuickMatch extends WSCmd {

    public static final int TYPE_QUICK_MATCH = 1;
    public static final int TYPE_CANCEL = 0;
    public static final int TYPE_CONTINUE = 2;

    public QuickMatch(int type, String refuseIds) {
        super("/game/battle/quickSearchForAgainst.do");
        setParameters(new Param(type, refuseIds));
    }

    public QuickMatch(int type) {
        super("/game/battle/quickSearchForAgainst.do");
        setParameters(new Param(type));
    }

    class Param {

        private int type;
        private String refuseIds;

        public Param(int type) {
            this.type = type;
        }

        public Param(int type, String refuseIds) {
            this.type = type;
            this.refuseIds = refuseIds;
        }
    }
}
