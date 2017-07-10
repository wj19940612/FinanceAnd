package com.sbai.finance.model.battle;

import static com.sbai.finance.model.battle.Battle.GAME_STATUS_END;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_STARTED;

/**
 * Created by linrongfang on 2017/7/3.
 */

public class BattleRoom {

    public static final int ROOM_STATE_CREATE = 1;
    public static final int ROOM_STATE_START = 2;
    public static final int ROOM_STATE_END = 3;

    public static final int USER_STATE_OBSERVER = 4;
    public static final int USER_STATE_FIGHTING = 5;

    private int mRoomState;  //房间状态  1发起对战 2 对战中  3.对战结束
    private int mUserState;  //用户状态  1观战  2对战

    public int getRoomState() {
        return mRoomState;
    }

    public void setRoomState(int roomState) {
        mRoomState = roomState;
    }

    public int getUserState() {
        return mUserState;
    }

    public void setUserState(int userState) {
        mUserState = userState;
    }

    public static BattleRoom getInstance(Battle battle, int userId) {
        BattleRoom battleRoom = new BattleRoom();

        if (battle.getAgainstUser() != userId && battle.getLaunchUser() != userId) {
            battleRoom.setUserState(USER_STATE_OBSERVER);
        } else {
            battleRoom.setUserState(USER_STATE_FIGHTING);
        }

        int gameStatus = battle.getGameStatus();
        if (gameStatus == Battle.GAME_STATUS_CREATED) {
            battleRoom.setRoomState(ROOM_STATE_CREATE);
        } else if (gameStatus == GAME_STATUS_STARTED) {
            battleRoom.setRoomState(ROOM_STATE_START);
        } else if (gameStatus == GAME_STATUS_END) {
            battleRoom.setRoomState(ROOM_STATE_END);
        }
        return battleRoom;
    }
}
