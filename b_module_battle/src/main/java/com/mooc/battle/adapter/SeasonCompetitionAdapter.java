package com.mooc.battle.adapter;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mooc.battle.R;
import com.mooc.battle.model.CompetitionDetails;
import com.mooc.common.ktextends.IntExtentionKt;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SeasonCompetitionAdapter extends BaseQuickAdapter<CompetitionDetails.SeasonInfo.Schedule, BaseViewHolder> implements LoadMoreModule {

    public SeasonCompetitionAdapter(@Nullable List<CompetitionDetails.SeasonInfo.Schedule> data) {
        super(R.layout.item_season_competition, data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder holder, CompetitionDetails.SeasonInfo.Schedule item) {
        holder.setText(R.id.tv_military, item.getLevelTitle());

        Glide.with(getContext()).load(item.getLevelImageBigIcon()).placeholder(R.mipmap.ic_game_rank_level_holder)
                .into((ImageView) holder.getView(R.id.img_military));

        //  "is_enter": 0,//是否能进入 1 能进入 0 不能进入
        //  "is_complete": 1//是否完成 1 完成 0 未完成
        if (item.getIsComplete() == 1) {//已完成，不加锁，不能进入详情
            holder.setGone(R.id.ll_lock, true);
            holder.setGone(R.id.img_status, false);
            holder.setImageResource(R.id.img_status, R.mipmap.icon_competition_complete);
        } else {//未完成
            holder.setImageResource(R.id.img_status, R.mipmap.icon_back_competition);
            if (item.getIsEnter() == 1) {//可以进入,不加锁
                holder.setGone(R.id.ll_lock, true);
                holder.setGone(R.id.img_status, false);
            } else {
                holder.setGone(R.id.ll_lock, false);
                holder.setGone(R.id.img_status, true);
            }
        }

        //最后一个等级单独显示
        if (item.getLevel() == getData().size()) {
            setLevel18(holder.getView(R.id.ll_rating), item.getStars());
        } else {
            setRatingNum(holder.getView(R.id.ll_rating), item.getStars(), item.getUpgradeStars());
        }


    }

    //level18  上将显示
    private void setLevel18(LinearLayout linearLayout, Integer stars) {
        linearLayout.removeAllViews();
        ImageView imageView = new ImageView(getContext());

        //0时用灰色的星星
        if (stars == 0) {
            imageView.setImageResource(R.mipmap.icon_star_white);
        } else {
            imageView.setImageResource(R.mipmap.ic_battle_season_rank_star);
        }

        linearLayout.addView(imageView);

        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(IntExtentionKt.dp2px(8), 0, IntExtentionKt.dp2px(2), 0);
        textView.setLayoutParams(layoutParams);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(getContext().getResources().getColor(R.color.white));
        textView.setText(String.valueOf(stars.intValue()));
        linearLayout.addView(textView);

    }

    private void setRatingNum(LinearLayout linearLayout, int num, int upGradeNum) {

        if (upGradeNum <= 0) {
            linearLayout.setVisibility(View.GONE);
            return;
        } else {
            linearLayout.setVisibility(View.VISIBLE);
        }

        linearLayout.removeAllViews();


        for (int i = 0; i < num; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.mipmap.ic_battle_season_rank_star);
            linearLayout.addView(imageView);
        }

        int whiteNum = upGradeNum - num;
        for (int j = 0; j < whiteNum; j++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.mipmap.icon_star_white);
            linearLayout.addView(imageView);
        }
    }


}
