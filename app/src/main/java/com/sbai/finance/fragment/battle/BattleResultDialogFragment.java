package com.sbai.finance.fragment.battle;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatImageView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.dialog.BaseDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.sbai.finance.utils.Launcher.EX_PAYLOAD;
import static com.sbai.finance.utils.Launcher.EX_PAYLOAD_1;

/**
 * Created by linrongfang on 2017/6/29.
 */

public class BattleResultDialogFragment extends BaseDialogFragment {

    public static final int GAME_RESULT_DRAW = 0;
    public static final int GAME_RESULT_WIN = 1;
    public static final int GAME_RESULT_LOSE = 2;

    @BindView(R.id.dialogDelete)
    AppCompatImageView mDialogDelete;
    @BindView(R.id.winResult)
    TextView mWinResult;
    @BindView(R.id.winDescribe)
    TextView mWinDescribe;
    @BindView(R.id.background)
    RelativeLayout mBackground;

    private OnCloseListener mListener;

    private int  mResult;
    private String mContent;

    Unbinder unbinder;

    public interface OnCloseListener {
        void onClose();
    }

    public BattleResultDialogFragment setOnCloseListener(OnCloseListener listener) {
        mListener = listener;
        return this;
    }

    public static BattleResultDialogFragment newInstance(int result, String content) {
        Bundle args = new Bundle();
        BattleResultDialogFragment fragment = new BattleResultDialogFragment();
        args.putInt(EX_PAYLOAD, result);
        args.putString(EX_PAYLOAD_1, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mResult = getArguments().getInt(EX_PAYLOAD);
            mContent = getArguments().getString(EX_PAYLOAD_1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_win_result, container, false);
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

        initViews();
    }

    private void initViews() {
        if (mResult == GAME_RESULT_DRAW) {
            mWinDescribe.setText(getString(R.string.game_result_draw));
            mBackground.setBackgroundResource(R.drawable.bg_future_battle_alerts_adraw);
        } else if (mResult == GAME_RESULT_WIN) {
            mWinDescribe.setText(getString(R.string.game_result_win));
            mBackground.setBackgroundResource(R.drawable.bg_future_battle_alerts_win);
        } else if (mResult == GAME_RESULT_LOSE) {
            mWinDescribe.setText(getString(R.string.game_result_lose));
            mBackground.setBackgroundResource(R.drawable.bg_futuresvs_battle_lose);
        }
        mWinResult.setText(mContent);

    }

    @OnClick(R.id.dialogDelete)
    public void onViewClicked() {
        if (mListener != null) {
            mListener.onClose();
        }
    }

    public void show(FragmentManager manager) {
        this.show(manager, BattleResultDialogFragment.class.getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
