package com.mooc.battle.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mooc.battle.R;
import com.mooc.battle.ui.activity.WebViewAnnouncementActivity;
import com.mooc.battle.model.GameNotice;
import com.mooc.battle.util.ConstantUtils;
import com.mooc.common.ktextends.IntExtentionKt;

import java.util.ArrayList;

public class GameNoticeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<GameNotice> currentDataList = new ArrayList<>();
    private int itemHeight = 0;
    Context mContext;

    public GameNoticeAdapter(Context context) {
        mContext = context;
//        itemHeight= Utils.getInstance().dip2px(context,20);
        itemHeight = IntExtentionKt.dp2px(20);
    }

    public void setCurrentDataList(ArrayList<GameNotice> currentDataList) {
        this.currentDataList = currentDataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_notice, parent, false);

        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        GameNotice dataBean = currentDataList.get(position % currentDataList.size());
        itemViewHolder.tvNotice.setText(dataBean.getTitle());
        itemViewHolder.parent.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, WebViewAnnouncementActivity.class);
            intent.putExtra(ConstantUtils.KEY_ID, dataBean.getId() + "");
//            intent.putExtra(ConstantUtils.KEY_FROM, LogEventConstants.PAGE_RUL_COMPETITION_NOTICE);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public int getItemHeight() {
        return itemHeight;
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {
        View parent;
        TextView tvNotice;

        private ItemViewHolder(View itemView) {
            super(itemView);
            parent = itemView;
            tvNotice = itemView.findViewById(R.id.tv_notice);
        }
    }


}
