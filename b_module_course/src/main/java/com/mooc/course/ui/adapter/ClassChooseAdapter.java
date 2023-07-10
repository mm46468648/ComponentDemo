package com.mooc.course.ui.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mooc.commonbusiness.utils.ServerTimeManager;
import com.mooc.commonbusiness.utils.format.TimeFormatUtil;
import com.mooc.course.R;
import com.mooc.course.binding.XtCourseDetialBindings;
import com.mooc.course.model.BaseClassInfo;
import com.mooc.course.model.ClassroomInfo;
import com.mooc.common.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * 班级选择适配器
 */
public class ClassChooseAdapter extends BaseQuickAdapter<BaseClassInfo, BaseViewHolder> {

    private int selectPosition; //选中位置

    public ClassChooseAdapter(@Nullable List<BaseClassInfo> data, int selectPosition) {
        super(R.layout.course_item_xt_class, data);
        this.selectPosition = selectPosition;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BaseClassInfo item) {


        helper.setText(R.id.tvClassName, item.getName());
        int classNameColor = selectPosition == helper.getAdapterPosition()
                ? getContext().getResources().getColor(R.color.colorPrimary) : getContext().getResources().getColor(R.color.color_2);
        helper.setTextColor(R.id.tvClassName, classNameColor);


        helper.setVisible(R.id.ivSelect, selectPosition == helper.getAdapterPosition());
        try {
            String class_start = item.getClass_start();
            String class_end = item.getClass_end();

//            long startParse = simpleDateFormat.parse(class_start).getTime();
//            long startParse = Long.parseLong(class_start) * 1000;   //后端返回的时候要*1000
            long startParse = 0;   //开始时间有可能没有,所以默认为0
            long currentTimeMillis = ServerTimeManager.getInstance().getServiceTime();
            long endParse = 0;  //结束时间有可能没有,所以默认为0
            String classStateStr = "";
            StringBuilder stringBuilder = new StringBuilder("开课: ");
            //中国大学mooc有可能返回"0" 或者null
            if (!TextUtils.isEmpty(class_start) && !"0".equals(class_start)) {
                String startStr = TimeFormatUtil.formatDate(Long.parseLong(class_start) * 1000, TimeFormatUtil.yyyy_MM_dd);
                stringBuilder.append(startStr);

                startParse = Long.parseLong(class_start) * 1000;
                if (currentTimeMillis < startParse) {
                    classStateStr = "即将开课";
                }

            } else {
                stringBuilder.append("-");
            }
            //连接结束时间
            stringBuilder.append("-");
            if (!TextUtils.isEmpty(class_end) && !"0".equals(class_end)) {    //如果end字段不为空，再拼接结束时间
                String endStr = TimeFormatUtil.formatDate(Long.parseLong(class_end) * 1000, TimeFormatUtil.yyyy_MM_dd);
                stringBuilder.append(endStr);

                endParse = Long.parseLong(class_end) * 1000;
                if (currentTimeMillis > startParse && currentTimeMillis < endParse) {
                    classStateStr = "开课中";
                }
                if (currentTimeMillis > endParse) {
                    classStateStr = "已结课";
                }
            } else {
                //开始不为空，结束为空
                if (startParse != 0) {
                    classStateStr = "永久开课";
                } else {
                    //开始和结束都为空
                    classStateStr = "已结课";
                }
            }
            helper.setText(R.id.tvClassState, classStateStr);
            helper.setText(R.id.tvClassTime, stringBuilder.toString());
//            stringBuilder.replace(0, stringBuilder.length(), "永久开课");

            //如果开课时间是3000-01-01,设置成暂无
            if (!TextUtils.isEmpty(class_start) && !"0".equals(class_start)) {
                String startStr = TimeFormatUtil.formatDate(Long.parseLong(class_start) * 1000, TimeFormatUtil.yyyy_MM_dd);
                if(startStr.equals(XtCourseDetialBindings.CLASS_NOT_START_TIME)){
                    helper.setText(R.id.tvClassTime, XtCourseDetialBindings.CLASS_TIME_NOT_SURE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
