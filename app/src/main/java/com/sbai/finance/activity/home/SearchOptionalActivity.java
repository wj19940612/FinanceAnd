package com.sbai.finance.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.stock.StockDetailActivity;
import com.sbai.finance.activity.stock.StockIndexActivity;
import com.sbai.finance.fragment.optional.OptionalListFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.stock.Stock;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.TitleBar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.umeng.socialize.utils.ContextUtil.getContext;

/**
 * 自选搜索
 */

public class SearchOptionalActivity extends BaseActivity {
    public static final String TYPE_STOCK_ONLY = "stock_only";
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.search)
    EditText mSearch;
    @BindView(R.id.listView)
    ListView mListView;
    private String mKey;
    private OptionalAdapter mOptionalAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optional_search);
        ButterKnife.bind(this);
        initView();
        requestStockData();
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            requestSearch(mSearch.getText().toString());
        }
    };

    private void initView() {
        mSearch.addTextChangedListener(mValidationWatcher);
        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    requestSearch(mSearch.getText().toString());
                }
                return true;
            }
        });
        mOptionalAdapter = new OptionalAdapter(this);
        mOptionalAdapter.setOnClickListener(new OptionalAdapter.OnClickListener() {
            @Override
            public void onClick(final Stock stock) {
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.DISCOVERY_ADD_SELF_OPTIONAL);
                    Client.addOption(stock.getVarietyCode(), Stock.OPTIONAL_TYPE_STOCK)
                            .setTag(TAG)
                            .setCallback(new Callback<Resp<JsonObject>>() {
                                @Override
                                protected void onRespSuccess(Resp<JsonObject> resp) {
                                    if (resp.isSuccess()) {
                                        updateOptionalData(stock.getId());
                                        sendAddOptionalBroadCast();
                                    } else {
                                        ToastUtil.show(resp.getMsg());
                                    }
                                }
                            }).fireFree();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
        mListView.setAdapter(mOptionalAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Stock stock = (Stock) parent.getItemAtPosition(position);
                if (stock != null) {
                    if (stock.getType().equalsIgnoreCase(Stock.OPTIONAL_TYPE_INDEX)) {
                        Launcher.with(getActivity(), StockIndexActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, stock).execute();
                        finish();
                    } else {
                        Launcher.with(getActivity(), StockDetailActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, stock).execute();
                        finish();
                    }
                }
            }
        });
    }

    private void updateOptionalData(int varietyId) {
        for (int i = 0; i < mOptionalAdapter.getCount(); i++) {
            Stock stock = mOptionalAdapter.getItem(i);
            if (stock != null && stock.getId() == varietyId) {
                stock.setOption(Stock.OPTIONAL);
                mOptionalAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private void requestSearch(String key) {
        try {
            mKey = key = URLEncoder.encode(key, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mKey = key;
        Client.searchStocks(key).setTag(TAG)
                .setId(key)
                .setCallback(new Callback2D<Resp<List<Stock>>, List<Stock>>() {
                    @Override
                    protected void onRespSuccessData(List<Stock> data) {
                        if (mKey.equals(getId())) {
                            updateSearchData(data);
                        }
                    }
                }).fire();
    }

    private void requestStockData() {
        Client.searchStocks("0000").setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Stock>>, List<Stock>>() {
                    @Override
                    protected void onRespSuccessData(List<Stock> data) {
                        if (data.size() >= 10) {
                            updateSearchData(data.subList(0, 10));
                        } else {
                            updateSearchData(data);
                        }
                    }
                }).fire();
    }

    private void updateSearchData(List<Stock> data) {
        mOptionalAdapter.clear();
        mOptionalAdapter.addAll(data);
        mOptionalAdapter.notifyDataSetChanged();
    }

    private void sendAddOptionalBroadCast() {
        Intent intent = new Intent();
        intent.setAction(OptionalListFragment.OPTIONAL_CHANGE_ACTION);
        intent.putExtra(Launcher.EX_PAYLOAD_1, true);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcastSync(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSearch.removeTextChangedListener(mValidationWatcher);
    }

    @OnClick(R.id.titleBar)
    public void OnClick(View view) {
        mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.smoothScrollToPosition(0);
            }
        });

    }

    static class OptionalAdapter extends ArrayAdapter<Stock> {
        private OnClickListener mOnClickListener;

        public OptionalAdapter(@NonNull Context context) {
            super(context, 0);
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            mOnClickListener = onClickListener;
        }

        interface OnClickListener {
            void onClick(Stock stock);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_optional_search, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(getItem(position), mOnClickListener);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.futureName)
            TextView mFutureName;
            @BindView(R.id.futureCode)
            TextView mFutureCode;
            @BindView(R.id.status)
            TextView mStatus;
            @BindView(R.id.addOptional)
            ImageView mAddOptional;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(final Stock stock, final OnClickListener onClickListener) {
                mFutureName.setText(stock.getVarietyName());
                mFutureCode.setText(stock.getVarietyCode());
                if (stock.isOptional()) {
                    mAddOptional.setVisibility(View.GONE);
                    mStatus.setVisibility(View.VISIBLE);
                } else {
                    mStatus.setVisibility(View.GONE);
                    mAddOptional.setVisibility(View.VISIBLE);
                }
                mAddOptional.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.onClick(stock);
                    }
                });
            }
        }
    }
}
