package com.sbai.finance.fragment.dialog.system;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sbai.finance.R;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.ActivityModel;
import com.sbai.finance.utils.Launcher;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/9/12.
 * 用户注册即送300元宝
 */

public class StartDialogFragment extends DialogFragment {
    public static final String ACTIVITY = "activity";
    @BindView(R.id.dialogDelete)
    AppCompatImageView mDialogDelete;
    @BindView(R.id.button)
    ImageView mButton;
    @BindView(R.id.window)
    ImageView mWindow;
    private Unbinder mBind;
    private ActivityModel mActivityModel;

    public static StartDialogFragment newInstance(ActivityModel activityModel) {
        StartDialogFragment startDialogFragment = new StartDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("activity", activityModel);
        startDialogFragment.setArguments(bundle);
        return startDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_start, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.BaseDialog);
        if (getArguments() != null) {
            mActivityModel = getArguments().getParcelable("activity");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels * 0.8), WindowManager.LayoutParams.WRAP_CONTENT);
        }
        initView();
    }

    private void initView() {
        if (mActivityModel != null) {
            GlideApp.with(getActivity()).load(mActivityModel.getWindowUrl()).into(mWindow);
            GlideApp.with(getActivity()).load(mActivityModel.getButtonUrl()).into(mButton);
        }
    }

    public void show(FragmentManager manager) {
        if (!isAdded()) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, this.getClass().getSimpleName());
            ft.commitAllowingStateLoss();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.dialogDelete, R.id.button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialogDelete:
                dismissAllowingStateLoss();
                break;
            case R.id.button:
                dismissAllowingStateLoss();
                if (mActivityModel != null) {
                    if (mActivityModel.getLinkType().equalsIgnoreCase(ActivityModel.LINK_TYPE_MODEL)) {
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    } else if (mActivityModel.getLinkType().equalsIgnoreCase(ActivityModel.LINK_TYPE_BANNER)) {
                        if (mActivityModel.isH5Style()) {
                            Launcher.with(getActivity(), WebActivity.class)
                                    .putExtra(WebActivity.EX_URL, mActivityModel.getContent())
                                    .execute();
                        } else {
                            Launcher.with(getActivity(), WebActivity.class)
                                    .putExtra(WebActivity.EX_HTML, mActivityModel.getContent())
                                    .putExtra(WebActivity.EX_TITLE, mActivityModel.getTitle())
                                    .execute();
                        }
                    } else {
                        Launcher.with(getActivity(), WebActivity.class)
                                .putExtra(WebActivity.EX_URL, mActivityModel.getLink())
                                .execute();
                    }
                }
                break;
        }
    }
}
