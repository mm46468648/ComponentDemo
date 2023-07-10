package com.mooc.studyroom.ui.fragment.record;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mooc.commonbusiness.base.BaseListFragment;
import com.mooc.commonbusiness.model.search.CourseBean;
import com.mooc.studyroom.manager.CourseItemClickPresenter;
import com.mooc.studyroom.ui.adapter.CompleteCourseAdapter;
import com.mooc.studyroom.viewmodel.CompleteCourseViewModel;

/**
 * 已完成课程
 */
public class StudyCompleteCourseFragment extends BaseListFragment<CourseBean, CompleteCourseViewModel> {
    @Nullable
    @Override
    public BaseQuickAdapter<CourseBean, BaseViewHolder> initAdapter() {
        CompleteCourseAdapter completeCourseAdapter = new CompleteCourseAdapter(getMViewModel().getPageData().getValue());
        completeCourseAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                CourseBean userCourseStudyBean = completeCourseAdapter.getData().get(position);
//                Intent intent;
//                if ("45".equals(userCourseStudyBean.getPlatform())) {    //等于45跳新学堂页面，其他跳老课程
//                    intent = new Intent(getActivity(), XTCourseDetailActivity.class);
//                } else {
//                    intent = new Intent(getActivity(), CourseDetailActivity.class);
//                    intent.putExtra(ConstantUtils.INTENT_KEY_COURSE_ID, String.valueOf(userCourseStudyBean.getId()));
//                    intent.putExtra(ConstantUtils.INTENT_KEY_ROOM_ID, userCourseStudyBean.getClassroom_id());
//                }
//                startActivity(intent);
                CourseItemClickPresenter.onClickCourse(getActivity(),userCourseStudyBean);
            }
        });
        return completeCourseAdapter;
    }
}
