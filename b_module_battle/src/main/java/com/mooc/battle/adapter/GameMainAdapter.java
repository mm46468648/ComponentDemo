package com.mooc.battle.adapter;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mooc.battle.R;
import com.mooc.battle.model.GameMain;
import com.mooc.battle.model.SeasonInfo;
import com.mooc.common.utils.DateStyle;
import com.mooc.common.utils.DateUtil;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameMainAdapter extends BaseQuickAdapter<GameMain, BaseViewHolder> implements LoadMoreModule {
    SeasonIntent seasonIntent;

    public SeasonIntent getSeasonIntent() {
        return seasonIntent;
    }

    public void setSeasonIntent(SeasonIntent seasonIntent) {
        this.seasonIntent = seasonIntent;
    }

    public GameMainAdapter(@Nullable List<GameMain> data) {
        super(R.layout.item_game_main, data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder holder, GameMain item) {
        holder.setText(R.id.tv_title, item.getTitle());
        View view = holder.getView(R.id.to_season);
        SeasonInfo info = item.getSeasonInfo();

        if (info != null && !TextUtils.isEmpty(info.getTitle())) {
            holder.setText(R.id.tv_season, info.getTitle());
            String start = DateUtil.timeToString(info.getStartTimeStamp() * 1000, DateStyle.YYYY_MM_DD_CN.getValue());
            String end = DateUtil.timeToString(info.getEndTimeStamp() * 1000, DateStyle.YYYY_MM_DD_CN.getValue());
            holder.setText(R.id.tv_time, start + "~" + end);
            holder.setGone(R.id.tv_time, false);
        } else {
            holder.setText(R.id.tv_season, getContext().getResources().getString(R.string.str_no_season_in_progress));
            holder.setGone(R.id.tv_time, true);
        }

        view.setOnClickListener(view1 -> {
            if (seasonIntent != null) {
                seasonIntent.toSeason(item);
            }
        });

    }

    public interface SeasonIntent {
        void toSeason(GameMain seasonInfo);
    }

}
