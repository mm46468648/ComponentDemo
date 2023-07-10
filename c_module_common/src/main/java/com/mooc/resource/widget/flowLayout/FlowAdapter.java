package com.mooc.resource.widget.flowLayout;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ProjectName: FlowAdapter
 * @Package: com.moocxuetang.c_module_resource.widget.flowLayout
 * @ClassName: FlowAdapter
 * @Description: 流式布局适配器
 * @Author: xym
 * @CreateDate: 2020/8/26 8:08 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/8/26 8:08 PM
 * @UpdateRemark: 更新内容
 * @Version: 1.0
 */

public abstract class FlowAdapter<T> {
    private List<T> mList;

    public FlowAdapter(List<T> datas) {
        mList = datas;
    }
    public FlowAdapter(T[] datas) {
        mList = new ArrayList<T>(Arrays.asList(datas));
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    public abstract View getView(int position);
}
