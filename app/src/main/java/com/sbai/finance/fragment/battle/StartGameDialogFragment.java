package com.sbai.finance.fragment.battle;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.fragment.dialog.BaseDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by linrongfang on 2017/6/21.
 */

public class StartGameDialogFragment extends BaseDialogFragment {

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

    Unbinder unbinder;

    private String mImageUrl;


    public static StartGameDialogFragment newInstance(String imageUrl) {
        StartGameDialogFragment fragment = new StartGameDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", imageUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageUrl = (String) getArguments().get("url");
        }
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
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        init();
    }

    private void init() {
        mTitle.setText(getString(R.string.title_quick_join_battle));
        mMatchLoading.setVisibility(View.GONE);
        Glide.with(getContext())
                .load(mImageUrl)
                .placeholder(R.drawable.ic_default_avatar_big)
                .into(mMatchHead);
        mCancel.setText("");
        mCancel.setBackgroundResource(android.R.color.white);
        timer.start();
    }


    public void updateDeadline(int count) {
        mMessage.setText(getString(R.string.desc_match_success, count));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void show(FragmentManager manager) {
        this.show(manager, StartGameDialogFragment.class.getSimpleName());
    }

    private CountDownTimer timer = new CountDownTimer(4000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            updateDeadline((int) (millisUntilFinished / 1000) - 1);
        }

        @Override
        public void onFinish() {
            StartGameDialogFragment.this.dismiss();
        }
    };
}
