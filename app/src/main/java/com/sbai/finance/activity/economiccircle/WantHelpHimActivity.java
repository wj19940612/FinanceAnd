package com.sbai.finance.activity.economiccircle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sbai.finance.R;
import com.sbai.finance.utils.Launcher;


public class WantHelpHimActivity extends AppCompatActivity {

	private int mDataId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_want_help_him);

		initData(getIntent());
	}

	private void initData(Intent intent) {
		mDataId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);

		requestWantHelpHimList();
	}

	private void requestWantHelpHimList() {

	}
}
