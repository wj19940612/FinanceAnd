package com.sbai.finance.fragment.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sbai.finance.R;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by linrongfang on 2017/6/21.
 */

public class StartMatchDialogFragment extends BaseDialogFragment {
    public static final String TAG = "StartMatchDialogFragment";

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.matchHead)
    ImageView mMatchHead;
    @BindView(R.id.matchLoading)
    ImageView mMatchLoading;
    @BindView(R.id.message)
    TextView mMessage;
    @BindView(R.id.cancel)
    TextView mCancel;

    private OnCancelListener mOnCancelListener;

    Unbinder unbinder;

    public interface OnCancelListener {
        void onCancel();
    }

    public StartMatchDialogFragment setOnCancelListener(OnCancelListener listener) {
        mOnCancelListener = listener;
        return this;
    }

    public static StartMatchDialogFragment newInstance() {
        StartMatchDialogFragment fragment = new StartMatchDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_start_match, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
        }
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        init();
    }

    private void init() {
        GlideApp.with(getContext())
                .load(R.drawable.ic_future_svs_looking_for)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)//添加缓存
                .into(mMatchLoading);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnCancelListener.onCancel();
            }
        });
    }

    public StartMatchDialogFragment setMatchSuccess(String imageUrl) {
        mTitle.setText(getString(R.string.title_match_success));
        mMatchLoading.setVisibility(View.GONE);
        GlideApp.with(getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_default_avatar_big)
                .into(mMatchHead);
        mCancel.setText("");
        mCancel.setBackgroundResource(android.R.color.white);
        timer.start();
        return this;
    }

    public void updateDeadline(int count) {
        if (count == 0) return;
        mMessage.setText(getString(R.string.desc_match_success, count));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void show(FragmentManager manager) {
        if (manager == null) return;
        if (!this.isAdded()) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, TAG);
            ft.commitAllowingStateLoss();
        }
    }

    private CountDownTimer timer = new CountDownTimer(4000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            updateDeadline((int) (millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            StartMatchDialogFragment.this.dismiss();
        }
    };
}
