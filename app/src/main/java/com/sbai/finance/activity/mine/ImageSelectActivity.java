package com.sbai.finance.activity.mine;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.ImageData;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.GalleryPopupWindow;
import com.sbai.finance.view.TitleBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by linrongfang on 2017/5/10.
 */

public class ImageSelectActivity extends BaseActivity {

    /**
     * 打开预览的请求码
     */
    private static final int REQ_CODE_TAKE_PHONE_FROM_PREVIEW = 444;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.allPic)
    TextView mAllPic;
    @BindView(R.id.preview)
    Button mPreview;
    private GalleryPopupWindow mPopupWindow;

    ImageSelectAdapter mImageSelectAdapter;

    //存储每个目录下的图片路径,key是文件名
    private Map<String, List<String>> mGroupMap = new HashMap<>();
    private List<ImageData> list = new ArrayList<>();
    //当前文件夹显示的图片路径
    private List<String> listPath = new ArrayList<>();
    //所选择的图片路径集合
    private ArrayList<String> listSelectedPath = new ArrayList<>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //扫描完成后
            getGalleryList();
            listPath.clear();
            listPath.addAll(mGroupMap.get("所有图片"));
            mImageSelectAdapter.update(listPath);
            mRecyclerView.scrollToPosition(mImageSelectAdapter.getItemCount() - 1);
            if (mPopupWindow != null)
                mPopupWindow.notifyDataChanged();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        getImages();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mImageSelectAdapter = new ImageSelectAdapter(this, listPath);
        mRecyclerView.setAdapter(mImageSelectAdapter);
        mImageSelectAdapter.setOnCheckedChangedListener(onCheckedChangedListener);
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyRefresh();
            }
        });
    }

    //选择图片变化的监听
    private ImageSelectAdapter.OnCheckedChangedListener onCheckedChangedListener = new ImageSelectAdapter.OnCheckedChangedListener() {
        @Override
        public void onChanged(boolean isChecked, String path, CheckBox cb, int position) {
            if (isChecked) {
                //选中
                if (listSelectedPath.size() == 1) {
                    //把点击变为checked的图片变为没有checked
                    cb.setChecked(false);
                    mImageSelectAdapter.setCheckedBoxFalse(position);
                    return;
                }
                //选中的图片路径加入集合
                listSelectedPath.add(path);

            } else {//取消选中
                //从集合中移除
                if (listSelectedPath.contains(path))
                    listSelectedPath.remove(path);
            }
            //如果没有选中的按钮不可点击
            if (listSelectedPath.size() == 0) {
                setRightButtonEnable(false);
            } else {
                setRightButtonEnable(true);
            }
        }
    };


    private void setRightButtonEnable(boolean enable) {
        mTitleBar.setRightVisible(enable);
        mTitleBar.setRightViewEnable(enable);
    }

    @OnClick({R.id.preview, R.id.allPic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.preview:
                if (listSelectedPath.size() > 0) {
                    //跳转预览
                    Launcher.with(ImageSelectActivity.this,ImagePreViewActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD,listSelectedPath.get(0))
                            .executeForResult(REQ_CODE_TAKE_PHONE_FROM_PREVIEW);
                }
                break;
            case R.id.allPic:
                chooseOtherGallery();
                break;
        }
    }

    //选择其他图片文件夹
    private void chooseOtherGallery() {
        if (mPopupWindow == null) {
            //把文件夹列表的集合传入显示
            mPopupWindow = new GalleryPopupWindow(this, list);
            mPopupWindow.setOnItemClickListener(new GalleryPopupWindow.OnItemClickListener() {
                @Override
                public void onItemClick(String fileName) {
                    //切换了文件夹，清除之前的选择的信息
                    setRightButtonEnable(false);
                    listPath.clear();
                    listSelectedPath.clear();
                    //把当前选择的文件夹内图片的路径放入listPath，更新界面
                    listPath.addAll(mGroupMap.get(fileName));
                    mImageSelectAdapter.update(listPath);
                    mAllPic.setText(fileName);
                }
            });
        }
        mPopupWindow.showAtLocation(mRecyclerView, Gravity.BOTTOM, 0, (int) Display.dp2Px(50, getResources()));
    }


    /**
     * 利用ContentProvider扫描手机中的图片
     */
    private void getImages() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = getContentResolver();

                Cursor mCursor = mContentResolver.query(mImageUri, null, null, null,
                        MediaStore.Images.Media.DATE_MODIFIED);
                if (mCursor == null) {
                    return;
                }
                //存放所有图片的路径
                List<String> listAllPic = new ArrayList<String>();
                while (mCursor.moveToNext()) {
                    //获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    //获取该图片的父路径名
                    String parentName = new File(path).getParentFile().getName();
                    listAllPic.add(path);

                    //根据父路径名将图片放入到mGruopMap中
                    if (!mGroupMap.containsKey(parentName)) {
                        List<String> chileList = new ArrayList<String>();
                        chileList.add(path);
                        mGroupMap.put(parentName, chileList);
                    } else {
                        mGroupMap.get(parentName).add(path);
                    }
                }
                //添加所有图片
                mGroupMap.put("所有图片", listAllPic);
                //通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0);
                mCursor.close();
            }
        }).start();

    }

    //获取相册文件夹列表
    private void getGalleryList() {
        Iterator<Map.Entry<String, List<String>>> iterator = mGroupMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> next = iterator.next();
            ImageData imageData = new ImageData();
            imageData.setFileName(next.getKey());
            imageData.setFirstPicPath(next.getValue().get(0));
            imageData.setCount(next.getValue().size());
            if (next.getKey().equals("所有图片"))
                list.add(0, imageData);
            else
                list.add(imageData);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == FragmentActivity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_TAKE_PHONE_FROM_PREVIEW:
                    notifyRefresh();
                    break;
            }
        }
    }

    //通知前一个页面刷新
    private void notifyRefresh() {
        Intent intent = new Intent();
        intent.putExtra(Launcher.EX_PAYLOAD, listSelectedPath.get(0));
        setResult(RESULT_OK, intent);
        finish();
    }


    static class ImageSelectAdapter extends RecyclerView.Adapter<ImageSelectAdapter.NViewHolder> {
        private Context context;
        private List<String> list = new ArrayList<>();
        private OnCheckedChangedListener onCheckedChangedListener;
        private List<Boolean> listChecked = new ArrayList<>();

        public ImageSelectAdapter(Context context, List<String> list) {
            this.context = context;
            this.list.addAll(list);
            setListCheched(list);
        }

        public void update(List<String> list) {
            this.list.clear();
            this.list.addAll(list);
            setListCheched(list);
            notifyDataSetChanged();

        }

        /**
         * 设置listChecked的初始值
         *
         * @param list
         */
        private void setListCheched(List<String> list) {
            listChecked.clear();
            for (int i = 0; i < list.size(); i++) {
                listChecked.add(false);
            }
        }

        //当点击超过了1张图片，再点击的设置为false
        public void setCheckedBoxFalse(int pos) {
            listChecked.set(pos, false);
        }

        public interface OnCheckedChangedListener {
            /**
             * @param isChecked 是否选中
             * @param path      点击的图片路径
             * @param cb        点击的CheckBox
             * @param pos       点击的位置
             */
            void onChanged(boolean isChecked, String path, CheckBox cb, int pos);
        }

        public void setOnCheckedChangedListener(OnCheckedChangedListener onCheckedChangedListener) {
            this.onCheckedChangedListener = onCheckedChangedListener;
        }

        @Override
        public NViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NViewHolder(LayoutInflater.from(context).inflate(R.layout.row_gallery_image, parent, false));
        }

        @Override
        public void onBindViewHolder(final NViewHolder holder, final int position) {
            Glide.with(context).load("file://" + list.get(position)).into(holder.iv);
            holder.cb.setChecked(listChecked.get(position));
            //不能手动选
            holder.cb.setEnabled(false);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.cb.setChecked(!holder.cb.isChecked());
                    if (holder.cb.isChecked()) {
                        listChecked.set(position, true);
                    } else {
                        listChecked.set(position, false);
                    }
                    if (onCheckedChangedListener != null) {
                        onCheckedChangedListener.onChanged(holder.cb.isChecked(), list.get(position), holder.cb, position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class NViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.itemImage)
            ImageView iv;
            @BindView(R.id.itemCheckBox)
            CheckBox cb;

            public NViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
