package com.sbai.finance.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.Launcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CatchCrashActivity extends BaseActivity {

    @BindView(R.id.restart)
    AppCompatButton mRestart;
    @BindView(R.id.cut)
    AppCompatButton mCut;
    @BindView(R.id.crashData)
    TextView mCrashData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catch_crash);
        ButterKnife.bind(this);

        Throwable throwable = (Throwable) getIntent().getSerializableExtra(Launcher.EX_PAYLOAD);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        throwable.printStackTrace(pw);

        try {
            sw.close();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCrashData.setText(sw.toString());
    }

    @OnClick({R.id.restart, R.id.cut})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.restart:
                Launcher.with(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .execute();
                break;
            case R.id.cut:
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData text = ClipData.newPlainText("text", mCrashData.getText().toString().trim());
                    clipboardManager.setPrimaryClip(text);
                }
                break;
        }
    }
}
