package com.mooc.battle.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mooc.battle.R;
import com.mooc.battle.model.Rank;
import com.mooc.common.utils.DateStyle;
import com.mooc.common.utils.DateUtil;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CompetitionHistoryAdapter extends BaseQuickAdapter<Rank, BaseViewHolder> implements LoadMoreModule {

    public CompetitionHistoryAdapter(@Nullable List<Rank> data) {
        super(R.layout.item_competition_rank, data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder holder, Rank item) {
        holder.setText(R.id.title, item.getTitle());
        String time;
        if (item.getStartTime() > 0) {
            time = DateUtil.timeToString(item.getStartTime() * 1000, DateStyle.YYYY_MM_DD_CN.getValue());
            if (item.getEndTime() > 0) {
                time = time + "~" + DateUtil.timeToString(item.getEndTime() * 1000, DateStyle.YYYY_MM_DD_CN.getValue());
            } else {
                time = time + "~";
            }
        } else {
            if (item.getEndTime() > 0) {
                time = "~" + DateUtil.timeToString(item.getEndTime() * 1000, DateStyle.YYYY_MM_DD_CN.getValue());
            } else {
                time = "~";
            }
        }

        holder.setText(R.id.time, time);
        holder.setText(R.id.military, item.getLevelTitle());
        holder.setText(R.id.star_num, item.getStars().toString());
    }


}
