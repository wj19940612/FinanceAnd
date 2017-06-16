package com.sbai.finance.activity.mutual;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.economiccircle.ContentImgActivity;
import com.sbai.finance.activity.mine.setting.LocationActivity;
import com.sbai.finance.fragment.dialog.UploadHelpImageDialogFragment;
import com.sbai.finance.model.mutual.ArticleProtocol;
import com.sbai.finance.net.API;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ImageUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.CustomToast;
import com.sbai.finance.view.GrapeGridView;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.TitleBar;
import com.sbai.httplib.CookieManger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BorrowActivity extends BaseActivity {
    public static final int REQ_CODE_ADDRESS = 100;
    @BindView(R.id.photoGv)
    GrapeGridView mPhotoGv;
    @BindView(R.id.borrowLimit)
    EditText mBorrowLimit;
    @BindView(R.id.borrowInterest)
    EditText mBorrowInterest;
    @BindView(R.id.borrowTimeLimit)
    EditText mBorrowTimeLimit;
    @BindView(R.id.borrowRemark)
    EditText mBorrowRemark;
    @BindView(R.id.publish)
    TextView mPublish;
    @BindView(R.id.agree)
    CheckBox mAgree;
    @BindView(R.id.location)
    IconTextRow mLocation;
    @BindView(R.id.contentDays)
    LinearLayout mContentDays;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.money)
    TextView mMoney;
    @BindView(R.id.interest)
    TextView mInterest;
    @BindView(R.id.days)
    TextView mDays;
    @BindView(R.id.protocol)
    TextView mProtocol;
    private LocalBroadcastManager mLocalBroadcastManager;
    private DelPhotoBroadcastReceiver mDelPhotoBroadcastReceiver;
    private IntentFilter mIntentFilter;
    private PhotoGridAdapter mPhotoGridAdapter;
    private String mImagePath;
    private Address mAddress;
    private String mProtocolUrl = API.getHost() + "/mobi/mutual/rules?nohead=1";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        SpannableString ss = new SpannableString(getString(R.string.borrow_remark_hint));//定义hint的值
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(12, true);//设置字体大小 true表示单位是sp
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBorrowRemark.setHint(new SpannedString(ss));
        mPhotoGridAdapter = new PhotoGridAdapter(this);
        mPhotoGridAdapter.add("");
        mPhotoGridAdapter.setOnItemClickListener(new PhotoGridAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (mPhotoGridAdapter.getCount() > 4) {
                    return;
                }
                UploadHelpImageDialogFragment.newInstance()
                        .setOnDismissListener(new UploadHelpImageDialogFragment.OnDismissListener() {
                            @Override
                            public void onGetImagePath(String path) {
                                mImagePath = path;
                                updateHelpImage(mImagePath);
                            }
                        })
                        .show(getSupportFragmentManager());
            }
        });
        mPhotoGv.setFocusable(false);
        mPhotoGv.setAdapter(mPhotoGridAdapter);
        mPhotoGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int photoCount = mPhotoGridAdapter.getCount() - 1;
                if (position < photoCount) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < photoCount; i++) {
                        builder.append(mPhotoGridAdapter.getItem(i)).append(",");
                    }
                    builder.deleteCharAt(builder.length() - 1);
                    Launcher.with(getActivity(), ContentImgActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, builder.toString().split(","))
                            .putExtra(Launcher.EX_PAYLOAD_1, position)
                            .putExtra(Launcher.EX_PAYLOAD_2, position)
                            .execute();
                }
            }
        });
        mBorrowLimit.requestFocus();
        mBorrowLimit.setFocusable(true);
        mBorrowLimit.addTextChangedListener(mBorrowMoneyValidationWatcher);
        mBorrowInterest.addTextChangedListener(mBorrowInterestValidationWatcher);
        mBorrowTimeLimit.addTextChangedListener(mBorrowTimeLimitValidationWatcher);
        mBorrowRemark.addTextChangedListener(mBorrowRemarkValidationWatcher);
        mAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPublishStatus();
            }
        });

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mDelPhotoBroadcastReceiver = new DelPhotoBroadcastReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ContentImgActivity.DEL_IMAGE);
        mLocalBroadcastManager.registerReceiver(mDelPhotoBroadcastReceiver, mIntentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @OnClick({R.id.publish, R.id.contentDays, R.id.protocol, R.id.location, R.id.titleBar, R.id.money, R.id.interest, R.id.days})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.publish:
                mPublish.setEnabled(false);
                String borrowMoney = mBorrowLimit.getText().toString().trim();
                if (borrowMoney.length() > 4 || Integer.parseInt(borrowMoney) > 2000) {
                    ToastUtil.curt(getString(R.string.money_more_2000));
                    showSoftWare(mBorrowLimit);
                    mBorrowLimit.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
                    mPublish.setEnabled(true);
                    return;
                } else if (Integer.parseInt(borrowMoney) < 500) {
                    ToastUtil.curt(getString(R.string.money_less_500));
                    showSoftWare(mBorrowLimit);
                    mBorrowLimit.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
                    mPublish.setEnabled(true);
                    return;
                }
                String borrowInterest = mBorrowInterest.getText().toString().trim();
                if (borrowInterest.length() > 3 || Integer.valueOf(borrowInterest) > 200) {
                    ToastUtil.curt(getString(R.string.interest_more_200));
                    showSoftWare(mBorrowInterest);
                    mBorrowInterest.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
                    mPublish.setEnabled(true);
                    return;
                } else if (Integer.valueOf(borrowInterest) < 1) {
                    ToastUtil.curt(getString(R.string.interest_less_1));
                    showSoftWare(mBorrowInterest);
                    mBorrowInterest.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
                    mPublish.setEnabled(true);
                    return;
                }

                String borrowTimeLimit = mBorrowTimeLimit.getText().toString().trim();
                if (borrowTimeLimit.length() > 3 || Integer.parseInt(borrowTimeLimit) > 60) {
                    ToastUtil.curt(getString(R.string.days_more_60));
                    showSoftWare(mBorrowTimeLimit);
                    mBorrowTimeLimit.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
                    mPublish.setEnabled(true);
                    return;
                } else if (Integer.parseInt(borrowTimeLimit) < 1) {
                    ToastUtil.curt(getString(R.string.days_less_1));
                    showSoftWare(mBorrowTimeLimit);
                    mBorrowTimeLimit.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
                    mPublish.setEnabled(true);
                    return;
                }
                if (TextUtils.isEmpty(mLocation.getSubText())) {
                    ToastUtil.curt(getString(R.string.no_address));
                    mPublish.setEnabled(true);
                    return;
                }
                String content = mBorrowRemark.getText().toString();
                if (content.length() >= 300) {
                    content = content.substring(0, 300);
                }
                while (content.startsWith("\n")) {
                    content = content.substring(1, content.length());
                }
                while (content.endsWith("\n")) {
                    content = content.substring(0, content.length() - 1);
                }
                StringBuilder contentImg = new StringBuilder();
                int photoAmount = mPhotoGridAdapter.getCount();
                for (int i = 0; i < photoAmount - 1; i++) {
                    String image = ImageUtils.compressImageToBase64(mPhotoGridAdapter.getItem(i), 600f);
                    Log.d(TAG, "image: " + image.length());
                    contentImg.append(image + ",");
                }
                if (contentImg.length() > 0) {
                    contentImg.deleteCharAt(contentImg.length() - 1);
                }
                if (mAddress == null) {
                    mPublish.setEnabled(true);
                    return;
                }
                String picture = contentImg.toString();
                if (!TextUtils.isEmpty(picture)) {
                    Client.uploadPicture(picture).setTag(TAG).setIndeterminate(this)
                            .setCallback(new Callback2D<Resp<List<String>>, List<String>>() {
                                @Override
                                protected void onRespSuccessData(List<String> data) {
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < data.size(); i++) {
                                        sb.append(data.get(i)).append(",");
                                    }
                                    if (sb.length() > 0) {
                                        sb.deleteCharAt(sb.length() - 1);
                                    }
                                    requestPublishBorrow(mBorrowRemark.getText().toString(), sb.toString(), mBorrowTimeLimit.getText().toString(),
                                            mBorrowInterest.getText().toString(), mBorrowLimit.getText().toString(),
                                            mLocation.getSubText(), mAddress.getLongitude(), mAddress.getLatitude());
                                }

                                @Override
                                public void onFailure(VolleyError volleyError) {
                                    super.onFailure(volleyError);
                                    mPublish.setEnabled(true);
                                }
                            }).fireSync();
                } else {
                    requestPublishBorrow(content, picture, mBorrowTimeLimit.getText().toString(),
                            mBorrowInterest.getText().toString(), mBorrowLimit.getText().toString(),
                            mLocation.getSubText(), mAddress.getLongitude(), mAddress.getLatitude());
                }
                break;
            case R.id.location:
                String location = null;
                if (mAddress!=null){
                    location = mAddress.getAdminArea()+" "+mAddress.getLocality()+" "+mAddress.getSubLocality();
                }
                Launcher.with(getActivity(), LocationActivity.class).putExtra(Launcher.EX_PAYLOAD_1, true).putExtra(Launcher.EX_PAYLOAD_2,location).executeForResult(REQ_CODE_ADDRESS);
                break;
            case R.id.protocol:
                Client.getArticleProtocol(2).setTag(TAG)
                        .setCallback(new Callback2D<Resp<ArticleProtocol>, ArticleProtocol>() {
                            @Override
                            protected void onRespSuccessData(ArticleProtocol data) {
                                Launcher.with(getActivity(), WebActivity.class)
                                        .putExtra(WebActivity.EX_TITLE, getString(R.string.protocol))
                                        .putExtra(WebActivity.EX_HTML, data.getContent())
                                        .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                        .execute();
                            }

                            @Override
                            public void onFailure(VolleyError volleyError) {
                                super.onFailure(volleyError);
                                Launcher.with(getActivity(), WebActivity.class)
                                        .putExtra(WebActivity.EX_TITLE, getString(R.string.protocol))
                                        .putExtra(WebActivity.EX_URL, mProtocolUrl)
                                        .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                        .execute();
                            }
                        }).fire();
                break;
            case R.id.titleBar:
                mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mScrollView.smoothScrollTo(0, 0);
                    }
                });
                break;
            case R.id.money:
                showSoftWare(mBorrowLimit);
                break;
            case R.id.interest:
                showSoftWare(mBorrowInterest);
                break;
            case R.id.days:
                showSoftWare(mBorrowTimeLimit);
                break;
            default:
                break;
        }
    }

    private void requestPublishBorrow(String content, String contentImg, String days, String interest, String money,
                                      String location, double locationLng, double locationLat) {
        Client.borrowIn(content, contentImg, days, interest, money, location, locationLng, locationLat).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            ToastUtil.curt(getString(R.string.publish_success));
//                            CustomToast.getInstance().showText(getActivity(), getString(R.string.publish_success));
                            Intent intent = new Intent(getActivity(), MutualActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            mPublish.setEnabled(true);
                            ToastUtil.curt(resp.getMsg());
                        }
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        mPublish.setEnabled(true);
                    }
                }).fire();
    }

    private void setPublishStatus() {
        boolean enable = checkPublishButtonEnable();
        if (enable != mPublish.isEnabled()) {
            mPublish.setEnabled(enable);
        }
    }

    private boolean checkPublishButtonEnable() {
        if (TextUtils.isEmpty(mBorrowLimit.getText())) {
            return false;
        }
        if (TextUtils.isEmpty(mBorrowInterest.getText())) {
            return false;
        }
        if (TextUtils.isEmpty(mBorrowTimeLimit.getText())) {
            return false;
        }
        if (TextUtils.isEmpty(mLocation.getSubText())) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBorrowLimit.removeTextChangedListener(mBorrowMoneyValidationWatcher);
        mBorrowInterest.removeTextChangedListener(mBorrowInterestValidationWatcher);
        mBorrowTimeLimit.removeTextChangedListener(mBorrowTimeLimitValidationWatcher);
        mBorrowRemark.removeTextChangedListener(mBorrowRemarkValidationWatcher);
        mLocalBroadcastManager.unregisterReceiver(mDelPhotoBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_ADDRESS && resultCode == RESULT_OK) {
            mAddress = data.getParcelableExtra(Launcher.EX_PAYLOAD_1);
            if (mAddress != null) {
                mLocation.setSubText(mAddress.getLocality() + "-" + mAddress.getSubLocality());
            }
            setPublishStatus();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftWare();
    }

    private void updateHelpImage(String helpImagePath) {
        if (!TextUtils.isEmpty(helpImagePath)) {
            mPhotoGridAdapter.insert(helpImagePath, mPhotoGridAdapter.getCount() - 1);
        }
    }
    private void showSoftWare(EditText editText){
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText,InputMethodManager.SHOW_FORCED);//强制显示
    }
    private void hideSoftWare(){
        InputMethodManager inputMethodManager =
                (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, 0);
    }
    private ValidationWatcher mBorrowMoneyValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mBorrowLimit.setTextColor(ContextCompat.getColor(getActivity(), R.color.blackAssist));
            setPublishStatus();
        }
    };
    private ValidationWatcher mBorrowInterestValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mBorrowInterest.setTextColor(ContextCompat.getColor(getActivity(), R.color.blackAssist));
            setPublishStatus();
        }
    };
    private ValidationWatcher mBorrowTimeLimitValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mBorrowTimeLimit.setTextColor(ContextCompat.getColor(getActivity(), R.color.blackAssist));
            setPublishStatus();
        }
    };
    private ValidationWatcher mBorrowRemarkValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            //setPublishStatus();
        }
    };

    class DelPhotoBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int index = intent.getExtras().getInt(ContentImgActivity.DEL_IMAGE);
            if (index >= 0) {
                mPhotoGridAdapter.remove(mPhotoGridAdapter.getItem(index));
            }
        }
    }

    static class PhotoGridAdapter extends ArrayAdapter<String> {

        public PhotoGridAdapter(@NonNull Context context) {
            super(context, 0);
        }

        interface OnItemClickListener {
            void onClick(int position);
        }

        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (position == getCount() - 1) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_add_photo, null);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.addPhoto);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onClick(position);
                    }
                });
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_photo, null);
                ImageView mPhotoImg = (ImageView) convertView.findViewById(R.id.photoImg);
                Glide.with(getContext()).load(getItem(position)).into(mPhotoImg);
            }
            return convertView;
        }
    }
}
