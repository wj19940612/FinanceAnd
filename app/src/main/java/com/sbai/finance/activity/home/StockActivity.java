package com.sbai.finance.activity.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017-04-19.
 */

public class StockActivity extends BaseActivity {
	@BindView(R.id.shangHai)
	TextView mShangHai;
	@BindView(R.id.shenZhen)
	TextView mShenZhen;
	@BindView(R.id.board)
	TextView mBoard;
	@BindView(R.id.listView)
	ListView mListView;
	@BindView(R.id.empty)
	TextView mEmpty;
	@BindView(R.id.stock)
	EditText mStock;
	@BindView(R.id.search)
	ImageView mSearch;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock);
		ButterKnife.bind(this);
		initView();
	}

	private void initView() {
		mStock.setFocusable(false);
		mListView.setEmptyView(mEmpty);

		SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor("上证",
				"\n"+"24396.26","\n50.39 +0.21%", 1.133f, 0.667f,
				ContextCompat.getColor(this,R.color.redPrimary),
				ContextCompat.getColor(this,R.color.redPrimary));

		mShangHai.setText(attentionSpannableString);
		mShenZhen.setText(attentionSpannableString);
		mBoard.setText(attentionSpannableString);
	}
	@OnClick({R.id.search,R.id.stock})
	public void onClick(View view){
		Launcher.with(getActivity(),SearchStockActivity.class).execute();
	}
}
