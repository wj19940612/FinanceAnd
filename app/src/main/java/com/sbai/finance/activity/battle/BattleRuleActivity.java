package com.sbai.finance.activity.battle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.FinanceUtil;

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


        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        } else {
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        Bitmap oldBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_battle_rule);
        if (oldBitmap != null) {
            Matrix matrix = new Matrix();
            float scale = FinanceUtil.divide(displayMetrics.widthPixels, oldBitmap.getWidth()).floatValue();
            matrix.postScale(scale, scale);
            Bitmap bitmap1 = Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, true);
            if (bitmap1 != null) {
                mImage.setImageBitmap(bitmap1);
                oldBitmap.recycle();
            } else {
                mImage.setScaleType(ImageView.ScaleType.FIT_XY);
                mImage.setImageBitmap(oldBitmap);
            }
        }
    }
}
