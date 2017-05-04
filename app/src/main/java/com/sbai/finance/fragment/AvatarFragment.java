package com.sbai.finance.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by lixiaokuan0819 on 2017/5/3.
 */

public class AvatarFragment extends DialogFragment {
	private String mUserPortrait;

	@BindView(R.id.avatar)
	ImageView mAvatar;
	Unbinder unbinder;

	public static AvatarFragment newInstance(String userPortrait) {
		AvatarFragment fragment = new AvatarFragment();
		Bundle args = new Bundle();
		args.putString(Launcher.EX_PAYLOAD, userPortrait);
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_fragment_avatar, container, false);
		unbinder = ButterKnife.bind(this, view);
		mUserPortrait = getArguments().getString(Launcher.EX_PAYLOAD);
		Glide.with(getActivity()).load(mUserPortrait)
				.placeholder(R.drawable.ic_default_avatar_big)
				.into(mAvatar);
		return view;
	}

	public void show(FragmentManager manager) {
		this.show(manager, AvatarFragment.class.getSimpleName());
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}
}
