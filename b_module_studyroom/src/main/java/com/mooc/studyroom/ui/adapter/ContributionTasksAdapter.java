package com.mooc.studyroom.ui.adapter;


import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mooc.studyroom.R;
import com.mooc.studyroom.model.ContributionTaskBean;

import java.util.List;

/***
 * 贡献任务adapter
 */
public class ContributionTasksAdapter extends BaseQuickAdapter<ContributionTaskBean, BaseViewHolder> {


    public ContributionTasksAdapter(@Nullable List<ContributionTaskBean> data) {
        super(R.layout.studyroom_item_contribution_task, data);
//        this.data = data;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ContributionTaskBean item) {
        int adapterPosition = helper.getAdapterPosition();
        List<ContributionTaskBean> data = getData();
        ContributionTaskBean currentContributionTaskBean = data.get(adapterPosition);
        //隐藏头部
        if(adapterPosition>0){
            ContributionTaskBean lastContributionTaskBean = data.get(adapterPosition-1);
            helper.setGone(R.id.con_parent_title,currentContributionTaskBean.task_type.equals(lastContributionTaskBean.task_type));
        }else{
            helper.setGone(R.id.con_parent_title, false);
        }

        //隐藏分割线
        helper.setGone(R.id.viewBottomLine,true);
        if(adapterPosition < data.size()-1){
            ContributionTaskBean nextContributionTaskBean = data.get(adapterPosition+1);
            helper.setGone(R.id.viewBottomLine,!currentContributionTaskBean.task_type.equals(nextContributionTaskBean.task_type));
        }

        helper.setText(R.id.tv_parent_title, "1".equals(item.task_type)?"今日任务":"持续任务");
        helper.setText(R.id.tvTaskName, item.name);
        helper.setText(R.id.tvGoTask, item.button);
//        helper.setText(R.id.tvTaskProgress, item.show_word);
        //隐藏进度
        if(!TextUtils.isEmpty(item.show_word)){
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            SpannableString spannableString = new SpannableString(item.show_word);
            if(!TextUtils.isEmpty(item.count) && item.show_word.contains(item.count)){
                spannableString.setSpan(foregroundColorSpan,item.show_word.indexOf(item.count),item.show_word.indexOf(item.count)+item.count.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            helper.setText(R.id.tvTaskProgress,spannableString);
            helper.setGone(R.id.tvTaskProgress,false);
        }else {
            helper.setGone(R.id.tvTaskProgress,true);
        }
        //隐藏提示
        helper.setGone(R.id.tvTaskTip, TextUtils.isEmpty(item.show_word_short));
        helper.setText(R.id.tvTaskTip, item.show_word_short);

//        helper.addOnClickListener(R.id.tvGoTask);

        //设置文案
        int goTaskBgRes = "已完成".equals(item.button) ? R.drawable.shape_radius20_stoke1primary_solidno : R.drawable.shape_green_gradient25c794_primary_cornor15;
        int goTaskTextColor = "已完成".equals(item.button) ? ContextCompat.getColor(getContext(), R.color.colorPrimary) :ContextCompat.getColor(getContext(), R.color.white);
        helper.setBackgroundResource(R.id.tvGoTask,goTaskBgRes);
        helper.setTextColor(R.id.tvGoTask,goTaskTextColor);
    }
}
