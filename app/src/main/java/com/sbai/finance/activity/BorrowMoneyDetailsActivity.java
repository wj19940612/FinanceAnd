package com.sbai.finance.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BorrowMoneyDetailsActivity extends AppCompatActivity {

    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.publishTime)
    TextView mPublishTime;
    @BindView(R.id.address)
    TextView mAddress;
    @BindView(R.id.needAmount)
    TextView mNeedAmount;
    @BindView(R.id.borrowTime)
    TextView mBorrowTime;
    @BindView(R.id.borrowInterest)
    TextView mBorrowInterest;
    @BindView(R.id.opinion)
    TextView mOpinion;
    @BindView(R.id.peopleNum)
    TextView mPeopleNum;
    @BindView(R.id.giveHelp)
    Button mGiveHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_money_details);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mUserName.setText("刘亦菲");
        mAddress.setText("温州 苍南");
        mPublishTime.setText("战国");
        mOpinion.setText("话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。");
        mNeedAmount.setText(getString(R.string.RMB, "8888"));
        mBorrowTime.setText(getString(R.string.day, "88"));
        mBorrowInterest.setText(getString(R.string.RMB, "8888"));
        mPeopleNum.setText(getString(R.string.people_want_help_him_number, "88"));

    }
}
