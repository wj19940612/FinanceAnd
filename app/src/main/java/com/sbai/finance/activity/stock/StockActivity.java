package com.sbai.finance.activity.stock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.future.FutureFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;

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
    private FutureFragment.FutureListAdapter mListAdapter;
    private Integer mPage = 0;
    private Integer mPageSize = 15;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestStockData();
    }

    private void requestStockData() {
        //获取股票列表
        Client.getStockVariety(mPage,mPageSize).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>,List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        updateStockData((ArrayList<Variety>) data);
                    }
                }).fire();
        //获取股票指数
        Client.getStockIndexVariety().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>,List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        updateStockIndexData((ArrayList<Variety>) data);
                    }
                }).fire();
    }

    private void updateStockIndexData(ArrayList<Variety> data) {
        data.size();
    }

    private void updateStockData(ArrayList<Variety> data) {
        if (data == null){
            return;
        }
        mListAdapter.clear();
        mListAdapter.addAll(data);
        mListAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mStock.setFocusable(false);
        mListAdapter = new FutureFragment.FutureListAdapter(this);
        mListView.setAdapter(mListAdapter);
        mListView.setEmptyView(mEmpty);

        SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor("上证",
                "\n" + "24396.26", "\n50.39 +0.21%", 1.133f, 0.667f,
                ContextCompat.getColor(this, R.color.redPrimary),
                ContextCompat.getColor(this, R.color.redPrimary));
        mShangHai.setText(attentionSpannableString);
        mShenZhen.setText(attentionSpannableString);
        mBoard.setText(attentionSpannableString);
    }
    @OnClick({R.id.stock,R.id.search})
    public void onClick(View view){
        Launcher.with(getActivity(), SearchStockActivity.class).execute();
    }
}
