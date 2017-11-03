package com.sbai.finance.websocket.cmd;

import com.sbai.finance.net.Client;
import com.sbai.finance.websocket.WSCmd;

/**
 * Created by ${wangJie} on 2017/11/1.
 * {@link Client# /game/acti/quickSearch.do}
 * 竞技场快速匹配
 */

public class ArenaQuickMatchLauncher extends WSCmd {

    public static final int ARENA_MATCH_START = 1;  //加入匹配
    public static final int ARENA_MATCH_CANCEL = 2; //取消匹配


    public ArenaQuickMatchLauncher(int type) {
        super("/game/acti/quickSearch.do");
        setParameters(new Param(type));
    }

    class Param {

        private int type;

        public Param(int type) {
            this.type = type;
        }
    }
}

