package com.sbai.finance.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
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
import com.sbai.finance.BuildConfig;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.battle.BattleActivity;
import com.sbai.finance.activity.mine.WaitForMeAnswerActivity;
import com.sbai.finance.activity.miss.MissRecordAudioReplyActivity;
import com.sbai.finance.activity.miss.MissProfileDetailActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.activity.miss.radio.RadioStationPlayActivity;
import com.sbai.finance.activity.studyroom.StudyRoomActivity;
import com.sbai.finance.activity.web.DailyReportDetailActivity;
import com.sbai.finance.model.Banner;
import com.sbai.finance.model.push.PushMessageModel;
import com.sbai.finance.net.Client;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.umeng.analytics.MobclickAgent;

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

        String channelId = getString(R.string.app_name);
        boolean b = !TextUtils.isEmpty(pushMessageModel.getTitle());
        String notificationTitle;
        if (b) {
            notificationTitle = pushMessageModel.getTitle();
        } else {
            notificationTitle = channelId;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setContentTitle(notificationTitle);
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
//        notificationManager.notify(R.string.app_name, builder.build());
        int notificationId = (int) pushMessageModel.getCreateTime();
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = createNotificationChannel(channelId, notificationManager);
            }
            notificationManager.notify(notificationId, builder.build());
        }
    }

    @SuppressLint("InlinedApi")
    private NotificationChannel createNotificationChannel(String channelId, NotificationManager notificationManager) {
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);   //开启指示灯，如果设备有的话。
        notificationChannel.enableVibration(true); //开启震动
        notificationChannel.setLightColor(Color.RED); // 设置指示灯颜色
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);//设置是否应在锁定屏幕上显示此频道的通知
        notificationChannel.setShowBadge(true);  //设置是否显示角标
        notificationChannel.setBypassDnd(true);  // 设置绕过免打扰模式
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400}); //设置震动频率
        notificationChannel.setDescription(channelId);
        notificationManager.createNotificationChannel(notificationChannel);
        return notificationChannel;
    }

    @NonNull
    private PendingIntent setPendingIntent(Context context, PushMessageModel data) {
        Intent intent = null;
        if (data.isDailyReportDetail()) {
            MobclickAgent.onEvent(context, UmengCountEventId.PUSH_FOCUS_NEWS);
            intent = new Intent(context, DailyReportDetailActivity.class);
            intent.putExtra(DailyReportDetailActivity.EX_ID, data.getDataId());
        } else if (data.isBattleMatchSuccess()) {
            MobclickAgent.onEvent(context, UmengCountEventId.PUSH_FUTURE_PK);
            if (!Preference.get().isForeground() && data.getData() != null) {
                intent = new Intent(context, BattleActivity.class);
                intent.putExtra(ExtraKeys.BATTLE, data.getData());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        } else if (data.isMissAnswer()) {
            intent = new Intent(context, QuestionDetailActivity.class);
            intent.putExtra(Launcher.EX_PAYLOAD, Integer.valueOf(data.getDataId()));
        }

        switch (data.getType()) {
            case PushMessageModel.QUESTION_DETAIL:
                MobclickAgent.onEvent(context, UmengCountEventId.PUSH_FOLLOW_MANUAL);
                intent = new Intent(context, QuestionDetailActivity.class);
                try {
                    intent.putExtra(Launcher.EX_PAYLOAD, Integer.valueOf(data.getDataId()));
                } catch (NumberFormatException e) {
                    if (!BuildConfig.IS_PROD) {
                        ToastUtil.show("web data is error" + data.getDataId());
                    }
                }
                break;
            case PushMessageModel.ACTIVITY:
                MobclickAgent.onEvent(context, UmengCountEventId.PUSH_ACTIVITY);
                intent = new Intent(context, MainActivity.class);
                Banner banner = new Banner();
                //返回id 去查询banner
                banner.setId(data.getDataId());
                banner.setContent(data.getUrl());
                intent.putExtra(ExtraKeys.ACTIVITY, banner);
                break;
            case PushMessageModel.FEED_BACK_REPLY:
                MobclickAgent.onEvent(context, UmengCountEventId.PUSH_FEEDBACK);
                intent = new Intent(context, MainActivity.class);
                intent.putExtra(ExtraKeys.TRAINING, data.getDataId());
                intent.putExtra(ExtraKeys.MAIN_PAGE_CURRENT_ITEM, MainActivity.PAGE_POSITION_MINE);
                intent.putExtra(ExtraKeys.PUSH_FEEDBACK, true);
                break;
            case PushMessageModel.SELF_STUDY_ROOM:
                MobclickAgent.onEvent(context, UmengCountEventId.PUSH_STUDY_ROOM);
                intent = new Intent(context, StudyRoomActivity.class);
                break;
            case PushMessageModel.TRAINING:
                MobclickAgent.onEvent(context, UmengCountEventId.PUSH_TRAINING);
                intent = new Intent(context, MainActivity.class);
                break;
            case PushMessageModel.MISS_HOME_PAGE:
                MobclickAgent.onEvent(context, UmengCountEventId.PUSH_FOLLOW_AUTOMATIC);
                intent = new Intent(context, MissProfileDetailActivity.class);
                try {
                    intent.putExtra(Launcher.EX_PAYLOAD, Integer.valueOf(data.getDataId()));
                } catch (NumberFormatException e) {
                    if (!BuildConfig.IS_PROD) {
                        ToastUtil.show("web data is error" + data.getDataId());
                    }
                }
                break;
            case PushMessageModel.MODULE:
                MobclickAgent.onEvent(context, UmengCountEventId.PUSH_MODULE);
                // 暂时保留，先不处理
                break;
            case PushMessageModel.H5_LINK:
                intent = new Intent(context, MainActivity.class);
                intent.putExtra(ExtraKeys.WEB_PAGE_URL, data.getUrl());
                break;
            case PushMessageModel.ASK_QUESTION_DESIGNATED_MISS:
                intent = new Intent(context, MissRecordAudioReplyActivity.class);
                try {
                    intent.putExtra(ExtraKeys.QUESTION_ID, Integer.valueOf(data.getDataId()));
                    intent.putExtra(ExtraKeys.QUESTION_TYPE, MissRecordAudioReplyActivity.QUESTION_TYPE_IS_NOT_SPECIFIED_MISS);
                } catch (NumberFormatException e) {
                    if (!BuildConfig.IS_PROD) {
                        ToastUtil.show("web data is error" + data.getDataId());
                    }
                }
                break;
            case PushMessageModel.ASK_QUESTION_UNASSIGNED_MISS:
                intent = new Intent(context, WaitForMeAnswerActivity.class);
                intent.putExtra(ExtraKeys.MAIN_PAGE_CURRENT_ITEM, 1);
                break;
            case PushMessageModel.TOPIC:
                intent = new Intent(context, WebActivity.class);
                intent.putExtra(WebActivity.EX_URL, String.format(Client.MISS_TOP_DETAILS_H5_URL, data.getDataId()));
                break;
            case PushMessageModel.MISS_AUDIO_IS_PASS_AUDIT:
                intent = new Intent(context, MissProfileDetailActivity.class);
                intent.putExtra(ExtraKeys.MAIN_PAGE_CURRENT_ITEM, 1);
                try {
                    intent.putExtra(Launcher.EX_PAYLOAD, Integer.valueOf(data.getDataId()));
                } catch (NumberFormatException e) {
                    if (!BuildConfig.IS_PROD) {
                        ToastUtil.show("web data is error" + data.getDataId());
                    }
                }
                break;
            case PushMessageModel.AUDIO_DETAIL:
                intent = new Intent(context, RadioStationPlayActivity.class);
                try {
                    intent.putExtra(ExtraKeys.IAudio, Integer.valueOf(data.getDataId()));
                } catch (NumberFormatException e) {
                    if (!BuildConfig.IS_PROD) {
                        ToastUtil.show("web data is error" + data.getDataId());
                    }
                }
                break;

        }
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return PendingIntent.getActivity(context, (int) data.getCreateTime(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
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
