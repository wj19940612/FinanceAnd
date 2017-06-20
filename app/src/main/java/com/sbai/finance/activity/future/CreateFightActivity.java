package com.sbai.finance.activity.future;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.R.id.chooseFutures;

public class CreateFightActivity extends BaseActivity {

	public static final int REQ_CODE_CHOOSE_FUTURES = 1001;

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(chooseFutures)
	TextView mChooseFutures;
	@BindView(R.id.integralWar)
	TextView mIntegralWar;
	@BindView(R.id.ingotWar)
	TextView mIngotWar;
	@BindView(R.id.launch_fight)
	ImageView mLaunchFight;
	@BindView(R.id.xxx)
	TextView mXxx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_fight);
		ButterKnife.bind(this);
	}

	@OnClick(chooseFutures)
	public void onViewClicked() {
		Launcher.with(getActivity(), ChooseFuturesActivity.class).executeForResult(REQ_CODE_CHOOSE_FUTURES);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_CODE_CHOOSE_FUTURES && resultCode == RESULT_OK) {
			if (data != null) {
				String futureName = data.getStringExtra(Launcher.EX_PAYLOAD);
				mChooseFutures.setText(futureName);
				mChooseFutures.setSelected(true);
			}
		}
	}
}
