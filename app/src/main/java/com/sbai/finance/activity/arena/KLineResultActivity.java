package com.sbai.finance.activity.arena;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.KlineBattleResult;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.view.KLineTopResultView;
import com.sbai.finance.view.NoScrollListView;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017\12\12 0012.
 */

public class KLineResultActivity extends BaseActivity {
    public static final String GOIN_TYPE = "goin_type";
    public static final int TYPE_1V1 = 1;
    public static final int TYPE_4V4 = 2;


    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.top)
    KLineTopResultView mTop;
    @BindView(R.id.listView)
    NoScrollListView mListView;
    @BindView(R.id.moreOneBtn)
    TextView mMoreOneBtn;
    @BindView(R.id.topType)
    LinearLayout mTopType;

    private ResultAdapter mResultAdapter;
    private int goinType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline_result);
        ButterKnife.bind(this);
        initData();
        initView();
        refreshData();
    }

    private void initData() {
        goinType = getIntent().getIntExtra(GOIN_TYPE, 1);
    }

    private void initView() {
        mResultAdapter = new ResultAdapter(this);
        mResultAdapter.setOnMoreClickListener(new ResultAdapter.OnMoreClickListener() {
            @Override
            public void onMoreClick() {

            }
        });
        mListView.setAdapter(mResultAdapter);
    }

    private void refreshData() {
//        Client.requestKlineBattleResult().setTag(TAG).setCallback(new Callback2D<Resp<List<KlineBattleResult>>, List<KlineBattleResult>>() {
//            @Override
//            protected void onRespSuccessData(List<KlineBattleResult> data) {
//                updateData(data);
//            }
//        }).fireFree();
        List<KlineBattleResult> data = new ArrayList<>();
        KlineBattleResult result1 = new KlineBattleResult();
        result1.setUserName("美杜莎");
        result1.setMoney(2000);
        result1.setProfit(12.83);
        result1.setSort(1);
        result1.setUserId(1421);
        result1.setUserPortrait("https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20171124/1421i1511490760433.png");

        KlineBattleResult result2 = new KlineBattleResult();
        result2.setUserName("用户007");
        result2.setMoney(800);
        result2.setProfit(8.83);
        result2.setSort(2);
        result2.setUserId(1418);
        result2.setUserPortrait("https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20171124/1421i1511490760433.png");
        data.add(result1);
        data.add(result2);
        updateData(data);
    }

    private void updateData(List<KlineBattleResult> data) {
        //搜索自己的数据
        if (data != null && data.size() != 0 && LocalUser.getUser().getUserInfo() != null) {
            KlineBattleResult meResult = null;
            for (KlineBattleResult result : data) {
                if (result.getUserId() == LocalUser.getUser().getUserInfo().getId()) {
                    meResult = result;
                    break;
                }
            }

            if(meResult == null) return;
            mTop.setRankInfo(goinType, meResult.getSort());
            mResultAdapter.clear();
            mResultAdapter.addAll(data);
        }
    }

    @OnClick(R.id.moreOneBtn)
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.moreOneBtn:
                break;
        }
    }

    static class ResultAdapter extends ArrayAdapter<KlineBattleResult> {
        interface OnMoreClickListener {
            public void onMoreClick();
        }

        private Context mContext;
        private OnMoreClickListener mOnMoreClickListener;

        public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
            mOnMoreClickListener = onMoreClickListener;
        }

        public ResultAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_kline_result_rank, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext, position,getCount());
            return convertView;
        }


        static class ViewHolder {
            @BindView(R.id.rank)
            TextView mRank;
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.upDown)
            TextView mUpDown;
            @BindView(R.id.reward)
            TextView mReward;
            @BindView(R.id.moreBtn)
            ImageView mMoreBtn;
            @BindView(R.id.viewStub)
            View mViewStub;


            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(KlineBattleResult data, Context context, int position,int count) {
                if (data == null) return;
                mRank.setText(String.valueOf(position + 1));
                GlideApp.with(context).load(data.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop().into(mAvatar);
                mName.setText(data.getUserName());
                if (data.getProfit() >= 0) {
                    mUpDown.setTextColor(ContextCompat.getColor(context,R.color.redPrimary));
                    mUpDown.setText("+"+String.format("%.2f", data.getProfit())+"%");
                } else {
                    mUpDown.setTextColor(ContextCompat.getColor(context,R.color.greenAssist));
                    mUpDown.setText("-"+String.format("%.2f", data.getProfit())+"%");
                }
                mReward.setText(String.valueOf(data.getMoney())+"元宝");
                if(position == count - 1){
                    mViewStub.setVisibility(View.GONE);
                }else{
                    mViewStub.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
