package com.mooc.course.ui.pop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mooc.course.R;
import com.mooc.course.model.BaseClassInfo;
import com.mooc.course.model.ClassroomInfo;
import com.mooc.course.ui.adapter.ClassChooseAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

/**
 * 学堂课程，班级选择
 */
public class XTCourseClassChooseDialog extends BottomSheetDialogFragment {

    private List<BaseClassInfo> classroom_info;
    private int selectPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.course_dialog_xt_class_choose,null);
    }

    RecyclerView recyclerView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        view.findViewById(R.id.tvCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });


        if(classroom_info.isEmpty()){
            return;
        }
        ClassChooseAdapter adapter = new ClassChooseAdapter(classroom_info,selectPosition);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                dismissAllowingStateLoss();
                if(classChooseListener!=null){
                    classChooseListener.onClassChoose(classroom_info.get(position),position);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    public void setData(List<BaseClassInfo> classroom_info,int selectPosition){
        this.classroom_info = classroom_info;
        this.selectPosition = selectPosition;
    }
    onClassChooseListener  classChooseListener;
    public interface onClassChooseListener{
        void onClassChoose(BaseClassInfo classroomInfoBean, int position);
    }

    public onClassChooseListener getClassChooseListener() {
        return classChooseListener;
    }

    public void setClassChooseListener(onClassChooseListener classChooseListener) {
        this.classChooseListener = classChooseListener;
    }
}
