package com.sbai.finance.activity.train;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.view.MyListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 更多训练反馈页
 */

public class MoreTrainFeedBackActivity extends BaseActivity {
    @BindView(R.id.changeTrain)
    TextView mChangeTrain;
    @BindView(R.id.listView)
    MyListView mListView;
    @BindView(R.id.content)
    EditText mContent;
    @BindView(R.id.textView)
    TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_feedback);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.changeTrain, R.id.textView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.changeTrain:
                break;
            case R.id.textView:
                break;
        }
    }
}
