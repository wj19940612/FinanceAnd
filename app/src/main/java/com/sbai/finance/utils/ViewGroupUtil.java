package com.sbai.finance.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 手动设置listView的高度
 */

public class ViewGroupUtil {
    private static final String TAG = "ViewGroupUtil";

    //设置listView高度
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);  //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    public static void setRecycleViewFullHeight(RecyclerView recycleView) {
        RecyclerView.LayoutManager layoutManager = recycleView.getLayoutManager();
        int totalHeight = 0;
        if (layoutManager != null) {
            for (int i = 0; i < layoutManager.getItemCount(); i++) {
                View childAt = layoutManager.getChildAt(i);
                childAt.measure(0, 0);
                totalHeight += childAt.getMeasuredHeight();
            }
            Log.d(TAG, "setRecycleViewFullHeight: " + totalHeight);
            ViewGroup.LayoutParams layoutParams = recycleView.getLayoutParams();
            layoutParams.height = totalHeight;
            recycleView.setLayoutParams(layoutParams);
        }

    }

}
