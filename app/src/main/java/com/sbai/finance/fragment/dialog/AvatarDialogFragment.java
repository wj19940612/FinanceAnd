package com.sbai.finance.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.sbai.finance.R;
import com.sbai.finance.utils.Launcher;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by lixiaokuan0819 on 2017/5/3.
 */

public class AvatarDialogFragment extends DialogFragment {

	private String mUserPortrait;

	@BindView(R.id.avatar)
	ImageView mAvatar;
	Unbinder unbinder;
	private Dialog mDialog;

	public static AvatarDialogFragment newInstance(String userPortrait) {
		AvatarDialogFragment fragment = new AvatarDialogFragment();
		Bundle args = new Bundle();
		args.putString(Launcher.EX_PAYLOAD, userPortrait);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_fragment_avatar, container, false);
		unbinder = ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mUserPortrait = getArguments().getString(Launcher.EX_PAYLOAD);
		GlideApp.with(getActivity()).load(mUserPortrait)
				.placeholder(R.drawable.ic_default_avatar_big)
				.into(mAvatar);
		mDialog = getDialog();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Window window = mDialog.getWindow();
		if (window != null) {
			window.setLayout((int) (displayMetrics.widthPixels * 0.95), (int) (displayMetrics.widthPixels * 0.95));
		}
	}

	public void show(FragmentManager manager) {
		this.show(manager, AvatarDialogFragment.class.getSimpleName());
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	@OnClick(R.id.avatar)
	public void onViewClicked() {
		mDialog.dismiss();
	}
}
