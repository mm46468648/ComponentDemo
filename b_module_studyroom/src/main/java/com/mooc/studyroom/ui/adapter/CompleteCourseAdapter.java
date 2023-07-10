package com.mooc.studyroom.ui.adapter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mooc.commonbusiness.constants.CoursePlatFormConstants;
import com.mooc.commonbusiness.model.search.CourseBean;
import com.mooc.common.ktextends.IntExtentionKt;
import com.mooc.resource.widget.MoocImageView;
import com.mooc.studyroom.R;

import java.util.ArrayList;

/**
 * 已完成课程适配器
 */
public class CompleteCourseAdapter extends BaseQuickAdapter<CourseBean, BaseViewHolder> implements LoadMoreModule {
    public CompleteCourseAdapter(@Nullable ArrayList<CourseBean> data) {
        super(R.layout.studyroom_item_course_complete, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CourseBean item) {
        helper.setText(R.id.tvCourseTitle, item.getTitle());
        MoocImageView ivCourseCover = helper.getView(R.id.ivCourseCover);
        ivCourseCover.setImageUrl(item.getPicture(), (int) IntExtentionKt.dp2px(2));
        helper.setText(R.id.tvPlatform, item.getPlatform_zh());
        helper.setText(R.id.tvOrg, item.getOrg());


        //设置底部得分，已学，作业，富文本样式
        TextView tvCourseScore = helper.getView(R.id.tvCourseScore);
        TextView tvStudyed = helper.getView(R.id.tvStudyed);
        TextView tvHomeWork = helper.getView(R.id.tvHomeWork);
        String scoreStr = "得分: " + item.getScore();
        //如果是26，将得分改为成绩评定
        if (CoursePlatFormConstants.COURSE_EXPEDITION == item.getPlatform()) {
            scoreStr = "成绩评定:" + item.getScore_level();
            setSpanText(tvCourseScore, scoreStr,item.getScore_level());
        }else {
            setSpanText(tvCourseScore, scoreStr, item.getScore());
        }
        String studyProgress = "";
        try {
            studyProgress = String.format("%.2f", Float.parseFloat(item.getLearned_process()) * 100);
        } catch (Exception e) {

        }
        String studyStr = "已学: " + studyProgress + "%";
        String homeworkStr = "作业: " + item.getExercise_process();



        setSpanText(tvStudyed, studyStr, studyProgress);
        setSpanText(tvHomeWork, homeworkStr, item.getExercise_process());

        //如果作业字段为空要用invisible隐藏
        helper.setVisible(R.id.tvHomeWork, !TextUtils.isEmpty(item.getExercise_process()));
        helper.setVisible(R.id.tvStudyed, !TextUtils.isEmpty(studyProgress));

        //设置证书状态，以及停服状态提醒
        TextView tvVerifiedStatus = helper.getView(R.id.tvVerifyStatus);
        if ("1".equals(item.getVerified_status())) {
            //可申请，但是未申请状态
            tvVerifiedStatus.setVisibility(View.VISIBLE);
            tvVerifiedStatus.setBackgroundResource(R.drawable.shape_solid_e0f0e9_cornor2);
            tvVerifiedStatus.setText("未申请证书");
            tvVerifiedStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else if ("2".equals(item.getVerified_status())) {
            tvVerifiedStatus.setVisibility(View.VISIBLE);
            tvVerifiedStatus.setBackgroundResource(R.drawable.shape_radius2_solid_e0e0e0);
            tvVerifiedStatus.setText("已申请证书");
            tvVerifiedStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.color_9));
        } else {
            //0无证书，获取其他情况，隐藏状态提示
            tvVerifiedStatus.setVisibility(View.INVISIBLE);
        }

        helper.setVisible(R.id.tvCourseState, "0".equals(item.getCourse_status()));
    }

    private void setSpanText(TextView textView, String totleStr, String spanStr) {
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        SpannableString spannableString = new SpannableString(totleStr);
        spannableString.setSpan(foregroundColorSpan, totleStr.indexOf(spanStr), totleStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);

    }
}
