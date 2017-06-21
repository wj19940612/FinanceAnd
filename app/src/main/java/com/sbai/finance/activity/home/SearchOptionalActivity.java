package com.sbai.finance.activity.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.sbai.finance.activity.future.FutureTradeActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.stock.StockDetailActivity;
import com.sbai.finance.activity.stock.StockIndexActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.TitleBar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017-06-0.
 */

public class SearchOptionalActivity extends BaseActivity {
    public static final String TYPE_STOCK_ONLY = "stock_only";
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.search)
    EditText mSearch;
    @BindView(R.id.listView)
    ListView mListView;
    private String type;
    private OptionalAdapter mOptionalAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optional_search);
        type = getIntent().getStringExtra("type");
        ButterKnife.bind(this);
        initView();
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            requestSearch(mSearch.getText().toString());
        }
    };

    private void initView() {
        if (type.equalsIgnoreCase(Variety.VAR_STOCK)) {
            mTitleBar.setTitle(getString(R.string.stock_optional));
            mSearch.setHint(getString(R.string.stock_optional_hint));
        } else if (type.equalsIgnoreCase(TYPE_STOCK_ONLY)) {
            mTitleBar.setTitle(getString(R.string.stock));
            mSearch.setHint(getString(R.string.stock_optional_hint));
        } else if (type.equalsIgnoreCase(Variety.VAR_FUTURE)) {
            mTitleBar.setTitle(getString(R.string.future_optional));
            mSearch.setHint(getString(R.string.future_optional_hint));
        }
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
            public void onClick(Variety variety) {
                if (LocalUser.getUser().isLogin()) {
                    Client.addOption(variety.getVarietyId())
                            .setTag(TAG)
                            .setCallback(new Callback<Resp<JsonObject>>() {
                                @Override
                                protected void onRespSuccess(Resp<JsonObject> resp) {
                                    if (resp.isSuccess()) {
                                        requestSearch(mSearch.getText().toString());
                                    } else {
                                        ToastUtil.curt(resp.getMsg());
                                    }
                                }
                            }).fireSync();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
        mListView.setAdapter(mOptionalAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Variety variety = (Variety) parent.getItemAtPosition(position);
                if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
                    Launcher.with(getActivity(), FutureTradeActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                    finish();
                }
                if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                    if (variety.getSmallVarietyTypeCode().equalsIgnoreCase(Variety.STOCK_EXPONENT)) {
                        Launcher.with(getActivity(), StockIndexActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                        finish();
                    } else {
                        Launcher.with(getActivity(), StockDetailActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                        finish();
                    }
                }
            }
        });
    }

    private void requestSearch(String key) {
        try {
            key = URLEncoder.encode(key, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (type.equalsIgnoreCase(Variety.VAR_STOCK) || type.equalsIgnoreCase(TYPE_STOCK_ONLY)) {
            Client.searchStock(key).setTag(TAG)
                    .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                        @Override
                        protected void onRespSuccessData(List<Variety> data) {
                            updateSearchData(data);
                        }
                    }).fire();
        } else if (type.equalsIgnoreCase(Variety.VAR_FUTURE)) {
            Client.searchFuture(key).setTag(TAG)
                    .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                        @Override
                        protected void onRespSuccessData(List<Variety> data) {
                            updateSearchData(data);
                        }
                    }).fire();
        }

    }

    private void updateSearchData(List<Variety> data) {
        mOptionalAdapter.clear();
        mOptionalAdapter.addAll(data);
        mOptionalAdapter.notifyDataSetChanged();
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

    static class OptionalAdapter extends ArrayAdapter<Variety> {
        private OnClickListener mOnClickListener;

        public OptionalAdapter(@NonNull Context context) {
            super(context, 0);
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            mOnClickListener = onClickListener;
        }

        interface OnClickListener {
            void onClick(Variety variety);
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

            private void bindingData(final Variety variety, final OnClickListener onClickListener) {
                mFutureName.setText(variety.getVarietyName());
                if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
                    mFutureCode.setText(variety.getContractsCode());
                } else if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                    mFutureCode.setText(variety.getVarietyType());
                }
                if (variety.getCheckOptional() == Variety.OPTIONAL) {
                    mAddOptional.setVisibility(View.GONE);
                    mStatus.setVisibility(View.VISIBLE);
                } else {
                    mStatus.setVisibility(View.GONE);
                    mAddOptional.setVisibility(View.VISIBLE);
                }
                mAddOptional.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.onClick(variety);
                    }
                });
            }
        }
    }
}
