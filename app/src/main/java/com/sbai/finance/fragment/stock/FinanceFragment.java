package com.sbai.finance.fragment.stock;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.view.IconTextRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FinanceFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.company)
    IconTextRow mCompany;
    @BindView(R.id.companyInfoList)
    ExpandableListView mCompanyInfoList;

    private Unbinder mBind;
    private CompanyFinanceAdapter mCompanyFinanceAdapter;


    private Map<String, List<String>> dataset = new HashMap<>();
    private String[] parentList = new String[]{"2017年第4季度，(截止日期:2016-12-12)", "2017年第3季度，(截止日期:2016-9-12)", "2017年第4季度，(截止日期:2016-6-12)"};
    private List<String> childrenList1 = new ArrayList<>();
    private List<String> childrenList2 = new ArrayList<>();
    private List<String> childrenList3 = new ArrayList<>();

    public FinanceFragment() {

    }

    public static FinanceFragment newInstance(String param1, String param2) {
        FinanceFragment fragment = new FinanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finance, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialData();
        mCompanyFinanceAdapter = new CompanyFinanceAdapter();
        mCompanyInfoList.setAdapter(mCompanyFinanceAdapter);
        mCompanyFinanceAdapter.addCompanyFinanceData(dataset);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    private void initialData() {
        childrenList1.add(parentList[0] + "-" + "first");
        childrenList1.add(parentList[0] + "-" + "second");
        childrenList1.add(parentList[0] + "-" + "third");
        childrenList2.add(parentList[1] + "-" + "first");
        childrenList2.add(parentList[1] + "-" + "second");
        childrenList2.add(parentList[1] + "-" + "third");
        childrenList3.add(parentList[2] + "-" + "first");
        childrenList3.add(parentList[2] + "-" + "second");
        childrenList3.add(parentList[2] + "-" + "third");
        dataset.put(parentList[0], childrenList1);
        dataset.put(parentList[1], childrenList2);
        dataset.put(parentList[2], childrenList3);
    }

    class CompanyFinanceAdapter extends BaseExpandableListAdapter {

        private Map<String, List<String>> mCompanyFinanceData;

        public void addCompanyFinanceData(Map<String, List<String>> data) {
            this.mCompanyFinanceData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return mCompanyFinanceData == null ? 0 : mCompanyFinanceData.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mCompanyFinanceData == null ? null : mCompanyFinanceData.get(parentList[groupPosition]);
        }

        // 获得某个父项的某个子项
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mCompanyFinanceData == null ? null :mCompanyFinanceData.get(parentList[groupPosition]).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_stock_company_faiance_title, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(parentList[groupPosition]);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        class ViewHolder {
            @BindView(R.id.companyFinancePublishTime)
            AppCompatTextView mCompanyFinancePublishTime;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(String financePublishTime) {
                mCompanyFinancePublishTime.setText(financePublishTime);
            }
        }
    }
}
