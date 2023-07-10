package com.mooc.battle.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.loadmore.BaseLoadMoreView;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mooc.battle.R;

public class GameLoadMoreView extends BaseLoadMoreView {


    @NonNull
    @Override
    public View getLoadComplete(@NonNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.getView(R.id.course_load_more_load_end_view);
    }

    @NonNull
    @Override
    public View getLoadEndView(@NonNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.getView(R.id.course_load_more_load_end_view);
    }

    @NonNull
    @Override
    public View getLoadFailView(@NonNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.getView(R.id.load_more_load_fail_view);
    }


    @NonNull
    @Override
    public View getLoadingView(@NonNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.getView(R.id.load_more_loading_view);
    }

    @NonNull
    @Override
    public View getRootView(@NonNull ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.course_load_more, parent, false);
    }
}
