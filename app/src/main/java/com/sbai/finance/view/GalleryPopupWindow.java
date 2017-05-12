package com.sbai.finance.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.model.ImageData;
import com.sbai.finance.utils.Display;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by linrongfang on 2017/5/10.
 */
public class GalleryPopupWindow extends PopupWindow {
    private static final String TAG = "GalleryPopupWindow";

    RecyclerView mRecyclerView;

    private Activity activity;
    private GalleryPopupWindow.OnItemClickListener onItemClickListener;
    private List<ImageData> list;
    private GalleryAdapter adapter;


    public GalleryPopupWindow(Activity context, List<ImageData> list) {
        super(context);
        this.activity = context;
        this.list = list;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_recyclerview, null);
        initView(contentView);

        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        this.setContentView(contentView);
        this.setWidth(w);
        this.setHeight((int) Display.dp2Px(350, context.getResources()));
        this.setFocusable(false);
        this.setOutsideTouchable(true);
        this.update();

        setBackgroundDrawable(new ColorDrawable(000000000));
    }

    public void notifyDataChanged() {
        adapter.notifyDataSetChanged();
    }

    private void initView(View contentView) {
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new GalleryAdapter(list, activity);
        adapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String fileName) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(fileName);
                    dismiss();
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    //暴露点击的接口
    public interface OnItemClickListener {
        /**
         * @param keyValue
         */
        void onItemClick(String keyValue);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.NViewHolder> {

        private Context context;
        private List<ImageData> list;
        private OnItemClickListener onItemClickListener;
        //用于记录是选中的哪一个文件夹
        private int selectedPos;

        public GalleryAdapter(List<ImageData> list, Context context) {
            this.list = list;
            this.context = context;
        }

        public interface OnItemClickListener {
            void onItemClick(String fileName);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public NViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NViewHolder(LayoutInflater.from(context).inflate(R.layout.row_gallery, parent, false));
        }

        @Override
        public void onBindViewHolder(NViewHolder holder, final int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPos = position;
                    notifyDataSetChanged();
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(list.get(position).getFileName());
                    }
                }
            });
            if (position == selectedPos) {
                holder.ivCheck.setVisibility(View.VISIBLE);
            } else {
                holder.ivCheck.setVisibility(View.GONE);
            }
            holder.tvCount.setText(list.get(position).getCount() + "张");
            holder.tvName.setText(list.get(position).getFileName());
            Glide.with(context).load("file://" + list.get(position).getFirstPicPath()).into(holder.iv);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class NViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.itemGallery)
            ImageView iv;
            @BindView(R.id.galleryName)
            TextView tvName;
            @BindView(R.id.galleryCount)
            TextView tvCount;
            @BindView(R.id.galleryCheck)
            ImageView ivCheck;

            public NViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
