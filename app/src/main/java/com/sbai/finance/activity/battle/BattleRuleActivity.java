package com.sbai.finance.activity.battle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BattleRuleActivity extends BaseActivity {

    @BindView(R.id.image)
    ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_rule);
        ButterKnife.bind(this);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        int bitmapWidth;
        int bitmapHeight;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_battle_rule, options);
        Log.d(TAG, "onCreate: " + options.outWidth + " " + options.outHeight);

        bitmapWidth = options.outWidth;
        bitmapHeight = options.outHeight;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        } else {
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }

        Log.d(TAG, "onPostResume: " + displayMetrics.widthPixels + " " + displayMetrics.heightPixels);

        options.inJustDecodeBounds = false;
        Bitmap oldBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_battle_rule);

        if (oldBitmap != null) {
            Bitmap bitmap1 = Bitmap.createBitmap(oldBitmap, bitmapWidth / 2, bitmapHeight / 2, displayMetrics.widthPixels, displayMetrics.heightPixels);
            if (bitmap1 != null) {
                mImage.setImageBitmap(bitmap1);
            }
        }
    }
}
