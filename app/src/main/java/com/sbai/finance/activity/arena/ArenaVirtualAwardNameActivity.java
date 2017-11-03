package com.sbai.finance.activity.arena;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.model.arena.ArenaVirtualAwardName;
import com.sbai.finance.utils.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArenaVirtualAwardNameActivity extends AppCompatActivity {

    public static final String CHOOSE_AWARD = "choose_award";

    @BindView(R.id.dialogDelete)
    ImageView mDialogDelete;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private VirtualAwardAdapter mVirtualAwardAdapter;
    private ArenaVirtualAwardName.VirtualAwardName mChooseVirtualAwardName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_exchange_virtual_name);
        ButterKnife.bind(this);
        ArenaVirtualAwardName arenaVirtualAwardName = getIntent().getParcelableExtra(ExtraKeys.ARENA_VIRTUAL_AWARD_NAME);
        mChooseVirtualAwardName = getIntent().getParcelableExtra(CHOOSE_AWARD);
        ArrayList<ArenaVirtualAwardName.VirtualAwardName> virtualAwardNameList = arenaVirtualAwardName.getVirtualAwardNameList();


        mVirtualAwardAdapter = new VirtualAwardAdapter(new ArrayList<ArenaVirtualAwardName.VirtualAwardName>());
        mRecyclerView.setAdapter(mVirtualAwardAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mVirtualAwardAdapter.setOnItemClickListener(new OnItemClickListener<ArenaVirtualAwardName.VirtualAwardName>() {
            @Override
            public void onItemClick(ArenaVirtualAwardName.VirtualAwardName virtualAwardName, int position) {
                if (mChooseVirtualAwardName != null) {
                    mChooseVirtualAwardName.setSelect(false);
                    mVirtualAwardAdapter.notifyDataSetChanged();
                }

                mChooseVirtualAwardName = virtualAwardName;
                mChooseVirtualAwardName.setSelect(true);
                mVirtualAwardAdapter.notifyDataSetChanged();
            }
        });

        if (virtualAwardNameList != null) {
            mVirtualAwardAdapter.addData(virtualAwardNameList);
            if (mChooseVirtualAwardName != null) {
                for (ArenaVirtualAwardName.VirtualAwardName result : virtualAwardNameList) {
                    if (result.getAwardName().equalsIgnoreCase(mChooseVirtualAwardName.getAwardName())) {
                        result.setSelect(true);
                        mVirtualAwardAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }


    class VirtualAwardAdapter extends RecyclerView.Adapter<VirtualAwardAdapter.ViewHolder> {

        private List<ArenaVirtualAwardName.VirtualAwardName> mVirtualAwardNameList;
        private OnItemClickListener mOnItemClickListener;

        public VirtualAwardAdapter(List<ArenaVirtualAwardName.VirtualAwardName> dara) {
            mVirtualAwardNameList = dara;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        public void addData(List<ArenaVirtualAwardName.VirtualAwardName> dara) {
            mVirtualAwardNameList.clear();
            mVirtualAwardNameList.addAll(dara);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_arena_virtual_award_name, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mVirtualAwardNameList.get(position), position, mOnItemClickListener);
        }

        @Override
        public int getItemCount() {
            return mVirtualAwardNameList != null ? mVirtualAwardNameList.size() : 0;
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.awardName)
            TextView mAwardName;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final ArenaVirtualAwardName.VirtualAwardName virtualAwardName,
                                         final int position, final OnItemClickListener onItemClickListener) {
                mAwardName.setText(virtualAwardName.getAwardName());
                mAwardName.setSelected(virtualAwardName.isSelect());
                mAwardName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            mOnItemClickListener.onItemClick(virtualAwardName, position);
                        }
                    }
                });
            }
        }
    }

}
