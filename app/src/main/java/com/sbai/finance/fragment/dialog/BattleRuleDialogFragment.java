package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/6/15.
 */

public class BattleRuleDialogFragment extends CenterDialogFragment {

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
    private String mTitleMsg;
    private String mPureHtml;

    public static BattleRuleDialogFragment newInstance(String title, String content) {
        Bundle args = new Bundle();
        BattleRuleDialogFragment fragment = new BattleRuleDialogFragment();
        args.putString(KEY_CONTENT_RES, content);
        args.putString(KEY_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitleMsg = getArguments().getString(KEY_TITLE);
            mPureHtml = getArguments().getString(KEY_CONTENT_RES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_battle_rule, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected float getWidthRatio() {
        return 0.65f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTitle.setText(mTitleMsg);
        if (!TextUtils.isEmpty(mPureHtml)){
            mContent.setText(Html.fromHtml(mPureHtml).toString().trim().replace("\n\n","\n"));
        }
    }

    @OnClick(R.id.dialogDelete)
    public void onViewClicked() {
        dismissAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBind != null) {
            mBind.unbind();
        }
    }
}
