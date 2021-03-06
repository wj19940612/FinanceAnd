package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.sbai.finance.R;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.WaveView;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StartMatchDialog extends BaseDialog {

    public interface OnCancelListener {
        void onCancel();
    }

    public StartMatchDialog(Activity activity) {
        super(activity);
    }

    public static BaseDialog get(final Activity activity, final OnCancelListener listener, boolean isShowMatchedAmount) {

        setCurrentDialog(DIALOG_START_MATCH);

        View customView = LayoutInflater.from(activity).inflate(R.layout.dialog_start_match, null);
        LinearLayout fourPkArea = customView.findViewById(R.id.fourPkArea);
        TextView message = customView.findViewById(R.id.message);
        if (isShowMatchedAmount) {
            fourPkArea.setVisibility(View.VISIBLE);
            message.setVisibility(View.GONE);
        } else {
            fourPkArea.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
        }

        final WaveView mMatchLoading = (WaveView) customView.findViewById(R.id.matchLoading);
        customView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(activity);
                listener.onCancel();
            }
        });
        final ImageView matchHead = (ImageView) customView.findViewById(R.id.matchHead);
        final List<Integer> heads = getHeads();
        final Handler mHandler = new Handler(Looper.getMainLooper());
        if (activity != null && activity.isFinishing()) return null;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMatchLoading.getTag() == null) {
                    mMatchLoading.setTag(0);
                }
                int count = (int) mMatchLoading.getTag();
                if (count % 20 == 0) {
                    Collections.shuffle(heads);
                }
                mMatchLoading.setTag(count + 1);
                if (activity != null && !activity.isFinishing()) {
                    GlideApp.with(activity).load(heads.get(count % 20))
                            .circleCrop()
                            //if use DataSource.MEMORY_CACHE,the DrawableCrossFadeFactory will not use transition
                            .skipMemoryCache(true)
                            .transition(DrawableTransitionOptions.withCrossFade(800))
                            .into(matchHead);
                    mHandler.postDelayed(this, 1000);
                }
            }
        }, 0);

        mMatchLoading.setStyle(Paint.Style.STROKE);
        mMatchLoading.setInterpolator(new AccelerateInterpolator());
        mMatchLoading.start();

        BaseDialog startMatchDialog = StartMatchDialog.single(activity);
        startMatchDialog
                .setCancelableOnBackPress(false)
                .setCancelableOnTouchOutside(false)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (mHandler != null) {
                            mHandler.removeCallbacksAndMessages(null);
                        }
                    }
                })
                .setCustomView(customView)
                .show();
        return startMatchDialog;
    }

    private static List<Integer> getHeads() {
        List<Integer> heads = new ArrayList<>();
        heads.add(R.drawable.ic_battle_matching_01);
        heads.add(R.drawable.ic_battle_matching_02);
        heads.add(R.drawable.ic_battle_matching_03);
        heads.add(R.drawable.ic_battle_matching_04);
        heads.add(R.drawable.ic_battle_matching_05);
        heads.add(R.drawable.ic_battle_matching_06);
        heads.add(R.drawable.ic_battle_matching_07);
        heads.add(R.drawable.ic_battle_matching_08);
        heads.add(R.drawable.ic_battle_matching_09);
        heads.add(R.drawable.ic_battle_matching_10);
        heads.add(R.drawable.ic_battle_matching_11);
        heads.add(R.drawable.ic_battle_matching_12);
        heads.add(R.drawable.ic_battle_matching_13);
        heads.add(R.drawable.ic_battle_matching_14);
        heads.add(R.drawable.ic_battle_matching_15);
        heads.add(R.drawable.ic_battle_matching_16);
        heads.add(R.drawable.ic_battle_matching_17);
        heads.add(R.drawable.ic_battle_matching_18);
        heads.add(R.drawable.ic_battle_matching_19);
        heads.add(R.drawable.ic_battle_matching_20);
        return heads;
    }
}
