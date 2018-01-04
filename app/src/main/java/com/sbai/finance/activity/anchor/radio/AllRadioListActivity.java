package com.sbai.finance.activity.anchor.radio;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.anchor.RadioStationListActivity;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.glide.GlideApp;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllRadioListActivity extends BaseActivity {

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private HashSet<Integer> mSet;
    private RadioListAdapter mRadioListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_radio_list);
        ButterKnife.bind(this);

        mSet = new HashSet<>();
        mListView.setEmptyView(mEmpty);
        mRadioListAdapter = new RadioListAdapter(getActivity());
        mListView.setAdapter(mRadioListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Radio radio = (Radio) parent.getAdapter().getItem(position);
                if (radio != null)
                    Launcher.with(getActivity(), RadioStationListActivity.class)
                            .putExtra(ExtraKeys.RADIO, radio)
                            .execute();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                requestRadio();
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestRadio();
    }

    private void requestRadio() {
        Client.requestRadioListData()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Radio>>, List<Radio>>() {
                    @Override
                    protected void onRespSuccessData(List<Radio> data) {
                        updateRadioList(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                })
                .fireFree();
    }

    private void updateRadioList(List<Radio> radioList) {

        if (mSet.isEmpty()) mRadioListAdapter.clear();
        for (Radio data : radioList) {
            if (mSet.add(data.getId())) {
                mRadioListAdapter.add(data);
            }
        }
    }

    static class RadioListAdapter extends ArrayAdapter<Radio> {

        private Context mContext;

        public RadioListAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.view_miss_radio, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.view)
            View mView;
            @BindView(R.id.radioCover)
            ImageView mRadioCover;
            @BindView(R.id.radioOwnerName)
            TextView mRadioOwnerName;
            @BindView(R.id.spit)
            View mSpit;
            @BindView(R.id.radioName)
            TextView mRadioName;
            @BindView(R.id.radioLL)
            LinearLayout mRadioLL;
            @BindView(R.id.radioUpdateTime)
            TextView mRadioUpdateTime;
            @BindView(R.id.voiceName)
            TextView mVoiceName;
            @BindView(R.id.needPay)
            ImageView mNeedPay;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(Radio radio, Context context) {
                GlideApp.with(context)
                        .load(radio.getRadioCover())
                        .into(mRadioCover);
                mVoiceName.setText(radio.getAudioName());
                mRadioUpdateTime.setText(context.getString(R.string.time_update, DateUtil.formatDefaultStyleTime(radio.getReviewTime())));
                mRadioName.setText(radio.getRadioName());
                mRadioOwnerName.setText(radio.getRadioHostName());

                if (radio.getRadioPaid() == Radio.PRODUCT_RATE_CHARGE) {
                    mNeedPay.setVisibility(View.VISIBLE);
                    boolean alreadyPay = radio.getUserPayment() == Radio.PRODUCT_RECHARGE_STATUS_ALREADY_PAY;
                    mNeedPay.setSelected(alreadyPay);
                } else {
                    mNeedPay.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
