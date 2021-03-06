package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/6/15.
 */

public class BindBankHintDialogFragment extends DialogFragment {

    private static final String KEY_CONTENT_RES = "content_resId";
    private static final String KEY_content = "content";
    private static final String KEY_TITLE = "TITLE";
    @BindView(R.id.dialogDelete)
    AppCompatImageView mDialogDelete;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.rootView)
    TextView mContent;


    private Unbinder mBind;
    private int mContentRes;
    private int mTitleResId;
    private String mContentMsg;

    public BindBankHintDialogFragment() {
    }

    public static BindBankHintDialogFragment newInstance(int title, int content) {
        Bundle args = new Bundle();
        BindBankHintDialogFragment fragment = new BindBankHintDialogFragment();
        args.putInt(KEY_CONTENT_RES, content);
        args.putInt(KEY_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public static BindBankHintDialogFragment newInstance(int title, String content) {
        Bundle args = new Bundle();
        BindBankHintDialogFragment fragment = new BindBankHintDialogFragment();
        args.putString(KEY_content, content);
        args.putInt(KEY_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.BaseDialog);
        if (getArguments() != null) {
            mContentRes = getArguments().getInt(KEY_CONTENT_RES);
            mTitleResId = getArguments().getInt(KEY_TITLE);
            mContentMsg = getArguments().getString(KEY_content);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_bind_bank_hint, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels * 0.65), WindowManager.LayoutParams.WRAP_CONTENT);
        }


        if (mContentRes != 0) {
            mContent.setText(mContentRes);
        } else if (!TextUtils.isEmpty(mContentMsg)) {
            mContent.setText(mContentMsg);
        }

        if (mTitleResId != 0) {
            mTitle.setText(mTitleResId);
        }
    }


    @OnClick(R.id.dialogDelete)
    public void onViewClicked() {
        dismissAllowingStateLoss();
    }

    public void show(FragmentManager manager) {
        this.show(manager, this.getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBind != null) {
            mBind.unbind();
        }
    }
}
