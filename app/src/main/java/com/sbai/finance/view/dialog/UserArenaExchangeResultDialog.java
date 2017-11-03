package com.sbai.finance.view.dialog;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.arena.UserExchangeAwardInfo;
import com.sbai.finance.model.arena.UserGameInfo;
import com.sbai.finance.view.SmartDialog;

/**
 * Created by ${wangJie} on 2017/11/2.
 */

public class UserArenaExchangeResultDialog {

    private Activity mActivity;
    private UserExchangeAwardInfo mUserExchangeAwardInfo;
    private SmartDialog mSmartDialog;
    private View mView;

    public static UserArenaExchangeResultDialog single(Activity activity, UserExchangeAwardInfo userExchangeAwardInfo) {
        UserArenaExchangeResultDialog userArenaExchangeResultDialog = new UserArenaExchangeResultDialog();
        userArenaExchangeResultDialog.mActivity = activity;
        userArenaExchangeResultDialog.mSmartDialog = SmartDialog.withSingleMap(activity);
        userArenaExchangeResultDialog.mUserExchangeAwardInfo = userExchangeAwardInfo;
        userArenaExchangeResultDialog.mView = LayoutInflater.from(activity).inflate(R.layout.dialog_user_arena_exchange_result, null);
        userArenaExchangeResultDialog.mSmartDialog.setCustomView(userArenaExchangeResultDialog.mView);
        userArenaExchangeResultDialog.init();
        return userArenaExchangeResultDialog;
    }

    private void init() {
        ImageView dialogDelete = (ImageView) mView.findViewById(R.id.dialogDelete);
        TextView gameNumber = (TextView) mView.findViewById(R.id.gameNumber);
        TextView skinName = (TextView) mView.findViewById(R.id.skinName);
        TextView gameNickName = (TextView) mView.findViewById(R.id.gameNickName);
        TextView gameArea = (TextView) mView.findViewById(R.id.gameArea);
        TextView exchangeStatus = (TextView) mView.findViewById(R.id.exchangeStatus);
        dialogDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmartDialog.dismiss();
            }
        });

        UserGameInfo userGameInfo = mUserExchangeAwardInfo.getUserGameInfo();
        if (userGameInfo != null) {
            gameArea.setText(userGameInfo.getGameZone());
            gameNumber.setText(userGameInfo.getWxqq());
            skinName.setText(userGameInfo.getSkin());
            gameNickName.setText(userGameInfo.getGameNickName());
        }

        switch (mUserExchangeAwardInfo.getStatus()) {
            case UserExchangeAwardInfo.EXCHANGE_STATUS_APPLY_FOR:
                exchangeStatus.setText(R.string.checking);
                break;
            case UserExchangeAwardInfo.EXCHANGE_STATUS_SUCCESS:
                exchangeStatus.setText(R.string.check_success);
                break;
            case UserExchangeAwardInfo.EXCHANGE_STATUS_FAIL:
                exchangeStatus.setText(R.string.check_fail);
                break;
        }
    }

    public void show(){
        mSmartDialog.show();
    }
}
