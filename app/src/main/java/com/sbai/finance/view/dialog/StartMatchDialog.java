package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sbai.finance.R;
import com.sbai.glide.GlideApp;

/**
 * Created by linrongfang on 2017/7/10.
 */

public class StartMatchDialog extends BaseDialog {


    public interface OnCancelListener {
        void onCancel();
    }


    public StartMatchDialog(Activity activity) {
        super(activity);
    }

    public static void get(final Activity activity, final OnCancelListener listener) {

        setCurrentDialog(DIALOG_START_MATCH);

        View customView = LayoutInflater.from(activity).inflate(R.layout.dialog_fragment_start_match, null);

        customView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(activity);
                listener.onCancel();
            }
        });

        ImageView mMatchLoading = (ImageView) customView.findViewById(R.id.matchLoading);

        GlideApp.with(activity)
                .load(R.drawable.ic_future_svs_looking_for)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)//添加缓存
                .into(mMatchLoading);

        StartMatchDialog.single(activity)
                .setCancelableOnBackPress(false)
                .setCancelableOnTouchOutside(false)
                .setCustomView(customView)
                .show();
    }


}
