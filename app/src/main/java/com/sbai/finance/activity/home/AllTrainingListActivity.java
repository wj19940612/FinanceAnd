package com.sbai.finance.activity.home;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.training.TrainingDetailActivity;
import com.sbai.finance.fragment.DiscoveryFragment;
import com.sbai.finance.model.training.MyTrainingRecord;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AllTrainingListActivity extends BaseActivity {

    @BindView(android.R.id.list)
    ListView mList;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private DiscoveryFragment.TrainAdapter mTrainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_training_list);
        ButterKnife.bind(this);

        mTrainAdapter = new DiscoveryFragment.TrainAdapter(this);
        mList.setEmptyView(mEmpty);
        mList.setAdapter(mTrainAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestAllTrainingList();
            }
        });
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyTrainingRecord myTrainingRecord = (MyTrainingRecord) parent.getAdapter().getItem(position);
                if(myTrainingRecord!=null){
                    Launcher.with(getActivity(), TrainingDetailActivity.class)
                            .putExtra(ExtraKeys.TRAINING, myTrainingRecord.getTrain())
                            .execute();
                }
            }
        });
        requestAllTrainingList();
    }

    private void requestAllTrainingList() {
        Client.getTrainingList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<MyTrainingRecord>>, List<MyTrainingRecord>>() {
                    @Override
                    protected void onRespSuccessData(List<MyTrainingRecord> data) {
                        updateTrainData(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }).fireFree();
    }

    private void updateTrainData(List<MyTrainingRecord> data) {
        mTrainAdapter.clear();
        for (MyTrainingRecord trainingRecord : data) {
            if (trainingRecord.getTrain() != null) {
                mTrainAdapter.add(trainingRecord);
            }
        }
    }
}
