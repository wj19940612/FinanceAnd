package com.sbai.finance.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.battle.FutureBattleActivity;
import com.sbai.finance.activity.discovery.DailyReportDetailActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.activity.studyroom.StudyRoomActivity;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.push.PushMessageModel;
import com.sbai.finance.utils.Launcher;

/**
 * Created by ${wangJie} on 2017/5/3.
 */

public class PushIntentService extends GTIntentService {
    private static final String TAG = "PushIntentService";

    private static final String PHONE_BRAND_SAMSUNG = "samsung";

    @Override
    public void onReceiveServicePid(Context context, int i) {

    }

    @Override
    public void onReceiveClientId(Context context, String s) {
        Log.d(TAG, "onReceiveClientId: " + s);
        Preference.get().setPushClientId(s);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);

        if (payload == null) {
            Log.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            Log.d(TAG, "receiver payload = " + data);
            handleMessage(context, data);
        }
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean b) {

    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
//            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    private void handleMessage(Context context, String data) {
        Log.d(TAG, "===data  " + data);
        try {
            PushMessageModel pushMessageModel = new Gson().fromJson(data, PushMessageModel.class);
            createNotification(context, pushMessageModel);
        } catch (JsonSyntaxException e) {
            Log.d(TAG, "handleMessage: " + e.toString());
        }

    }

    private void createNotification(Context context, PushMessageModel pushMessageModel) {
        if (pushMessageModel.isBattleMatchSuccess() && Preference.get().isForeground()) {
            return;
        }
        if (pushMessageModel.isMissAnswer()) {
            pushMessageModel.setTitle(null);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(pushMessageModel.getTitle());
        builder.setContentText(pushMessageModel.getMsg());
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        String brand = Build.BRAND;
        PendingIntent intent = setPendingIntent(context, pushMessageModel);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        if (!TextUtils.isEmpty(brand) && brand.equalsIgnoreCase(PHONE_BRAND_SAMSUNG)) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            builder.setLargeIcon(bitmap);
        }
        builder.setContentIntent(intent);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.string.app_name, builder.build());
    }

    @NonNull
    private PendingIntent setPendingIntent(Context context, PushMessageModel data) {
        Intent intent = null;
        if (data.isDailyReportDetail()) {
            intent = new Intent(context, DailyReportDetailActivity.class);
            intent.putExtra(DailyReportDetailActivity.EX_ID, data.getDataId());
        } else if (data.isBattleMatchSuccess()) {
            if (!Preference.get().isForeground() && data.getData() != null) {
                intent = new Intent(context, FutureBattleActivity.class);
                Battle battle = new Battle();
                battle.setId(data.getData().getId());
                battle.setBatchCode(data.getData().getBatchCode());
                intent.putExtra(ExtraKeys.BATTLE, battle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        } else if (data.isMissAnswer()) {
            intent = new Intent(context, QuestionDetailActivity.class);
            intent.putExtra(Launcher.EX_PAYLOAD, Integer.valueOf(data.getDataId()));
        } else if (data.isStudy()) {
            intent = new Intent(context, StudyRoomActivity.class);
        } else if (data.isFeedBackInfo()) {
            intent = new Intent(context, FeedbackActivity.class);
            if (data.getData() != null && data.getData().getId() > 0) {
                intent.putExtra(ExtraKeys.TRAINING, data.getData().getId());
            }
        }

        switch (data.getType()) {
            case PushMessageModel.PUSH_TYPE_ATTENTION_MISS_ANSWERED:
                break;
            case PushMessageModel.PUSH_TYPE_ACTIVITY:
                break;
            case PushMessageModel.PUSH_TYPE_FEED_BVACK_REPLY:
                break;
            case PushMessageModel.PUSH_TYPE_SELF_STUDY_ROOM:
                break;
            case PushMessageModel.PUSH_TYPE_TRAINING:
                break;
        }


        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();

        String text = "设置标签失败, 未知异常";
        switch (Integer.valueOf(code)) {
            case PushConsts.SETTAG_SUCCESS:
                text = "设置标签成功";
                break;

            case PushConsts.SETTAG_ERROR_COUNT:
                text = "设置标签失败, tag数量过大, 最大不能超过200个";
                break;

            case PushConsts.SETTAG_ERROR_FREQUENCY:
                text = "设置标签失败, 频率过快, 两次间隔应大于1s且一天只能成功调用一次";
                break;

            case PushConsts.SETTAG_ERROR_REPEAT:
                text = "设置标签失败, 标签重复";
                break;

            case PushConsts.SETTAG_ERROR_UNBIND:
                text = "设置标签失败, 服务未初始化成功";
                break;

            case PushConsts.SETTAG_ERROR_EXCEPTION:
                text = "设置标签失败, 未知异常";
                break;

            case PushConsts.SETTAG_ERROR_NULL:
                text = "设置标签失败, tag 为空";
                break;

            case PushConsts.SETTAG_NOTONLINE:
                text = "还未登陆成功";
                break;

            case PushConsts.SETTAG_IN_BLACKLIST:
                text = "该应用已经在黑名单中,请联系售后支持!";
                break;

            case PushConsts.SETTAG_NUM_EXCEED:
                text = "已存 tag 超过限制";
                break;

            default:
                break;
        }

        Log.d(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + text);
    }

}
