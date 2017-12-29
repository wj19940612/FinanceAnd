package com.sbai.finance.fragment.anchor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.R;

/**
 * 主播观点
 */
public class AnchorPointFragment extends Fragment {

    public static final String POINT_TYPE_KEY = "point_type_key";

    public static final int POINT_TYPE_RECOMMEND = 0;  //米圈推荐的观点
    public static final int POINT_TYPE_ANCHOR = 1;  //主播的观点


    private int mPointType;

    public AnchorPointFragment() {
    }


    public static AnchorPointFragment newInstance(int pointType) {
        AnchorPointFragment fragment = new AnchorPointFragment();
        Bundle args = new Bundle();
        args.putInt(POINT_TYPE_KEY, pointType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPointType = getArguments().getInt(POINT_TYPE_KEY, POINT_TYPE_RECOMMEND);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_anchor_point, container, false);
    }


}
