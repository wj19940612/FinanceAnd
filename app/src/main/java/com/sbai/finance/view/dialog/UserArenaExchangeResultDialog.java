package com.sbai.finance.view.dialog;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        userArenaExchangeResultDialog.mSmartDialog = SmartDialog.single(activity);
        userArenaExchangeResultDialog.mUserExchangeAwardInfo = userExchangeAwardInfo;
        userArenaExchangeResultDialog.mView = LayoutInflater.from(activity).inflate(R.layout.dialog_user_arena_exchange_result, null);
        userArenaExchangeResultDialog.mSmartDialog.setCustomView(userArenaExchangeResultDialog.mView);
        userArenaExchangeResultDialog.init();
        return userArenaExchangeResultDialog;
    }

    private void init() {
        ImageView dialogDelete = mView.findViewById(R.id.dialogDelete);
        TextView gameNumber = mView.findViewById(R.id.gameNumber);
        TextView skinName = mView.findViewById(R.id.skinName);
        TextView gameNickName = mView.findViewById(R.id.gameNickName);
        TextView gameArea = mView.findViewById(R.id.gameArea);
        TextView exchangeStatus = mView.findViewById(R.id.exchangeStatus);


        TextView wechatOrReceiver = mView.findViewById(R.id.weixinOrReceiver);
        TextView addressOrSkin = mView.findViewById(R.id.addressOrSkin);
        TextView phoneOrNick = mView.findViewById(R.id.phoneOrNick);
        LinearLayout gameAreaLL = mView.findViewById(R.id.gameAreaLL);

        dialogDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmartDialog.dismiss();
            }
        });

        UserGameInfo userGameInfo = mUserExchangeAwardInfo.getUserGameInfo();
        if (mUserExchangeAwardInfo.getPrizeType() == UserExchangeAwardInfo.AWARD_TYPE_ENTITY) {
            gameAreaLL.setVisibility(View.GONE);
            if (userGameInfo != null) {
                gameNumber.setText(userGameInfo.getReceiver());
                skinName.setText(userGameInfo.getAddress());
                gameNickName.setText(userGameInfo.getPhone());
            }
            wechatOrReceiver.setText(R.string.receive_user);
            addressOrSkin.setText(R.string.receive_user_address);
            phoneOrNick.setText(R.string.phone_);
        } else {
            if (userGameInfo != null) {
                gameArea.setText(userGameInfo.getGameZone());
                gameNumber.setText(userGameInfo.getWxqq());
                skinName.setText(userGameInfo.getSkin());
                gameNickName.setText(userGameInfo.getGameNickName());
            }

            wechatOrReceiver.setText(R.string.game_number);
            addressOrSkin.setText(R.string.skin_name);
            phoneOrNick.setText(R.string.game_nickname);
            gameAreaLL.setVisibility(View.VISIBLE);
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

    public void show() {
        mSmartDialog.show();
    }
}
