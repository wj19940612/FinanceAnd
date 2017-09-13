package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.sbai.finance.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by linrongfang on 2017/7/10.
 */

public class StartMatchDialog extends BaseDialog {

    private static GifDrawable mGifFromResource;

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
                mGifFromResource.recycle();
            }
        });

        GifImageView mMatchLoading = (GifImageView) customView.findViewById(R.id.matchLoading);

        try {
            mGifFromResource = new GifDrawable(activity.getResources(), R.drawable.ic_future_svs_looking_for);
            mMatchLoading.setImageDrawable(mGifFromResource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StartMatchDialog.single(activity)
                .setCancelableOnBackPress(false)
                .setCancelableOnTouchOutside(false)
                .setCustomView(customView)
                .show();
    }
}
