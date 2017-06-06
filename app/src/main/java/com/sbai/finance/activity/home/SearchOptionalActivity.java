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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.Variety;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.TitleBar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-06-0.
 */

public class SearchOptionalActivity extends BaseActivity {
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
        } else if (type.equalsIgnoreCase(Variety.VAR_FUTURE)) {
            mTitleBar.setTitle(getString(R.string.future_optional));
            mSearch.setHint(getString(R.string.future_optional_hint));
        }
        mSearch.addTextChangedListener(mValidationWatcher);
        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    requestSearch(mSearch.getText().toString());
                }
                return true;
            }
        });
        mOptionalAdapter = new OptionalAdapter(this);
        mListView.setAdapter(mOptionalAdapter);
    }
    private void requestSearch(String key) {
        try {
            key = URLEncoder.encode(key, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSearch.removeTextChangedListener(mValidationWatcher);
    }

    static class OptionalAdapter extends ArrayAdapter<Variety> {
        private OnClickListener mOnClickListener;
        public OptionalAdapter(@NonNull Context context) {
            super(context, 0);
        }
        public  void setOnClickListener(OnClickListener onClickListener){
            mOnClickListener = onClickListener;
        }
        interface OnClickListener{
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
            viewHolder.bindingData(getItem(position),mOnClickListener);
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
            private void bindingData(final Variety variety, final OnClickListener onClickListener){
                mFutureName.setText(variety.getVarietyName());
                if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)){
                    mFutureCode.setText(variety.getContractsCode());
                }else if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)){
                    mFutureCode.setText(variety.getVarietyType());
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
