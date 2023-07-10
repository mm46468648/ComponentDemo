package com.mooc.commonbusiness.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @ProjectName: GrideItemDecoration
 * @Package: com.example.commonbusiness.decoration
 * @ClassName: GrideItemDecoration
 * @Description: 网格布局装饰器
 * @Author: xym
 * @CreateDate: 2021/3/2 9:39 AM
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/3/2 9:39 AM
 * @UpdateRemark: 更新内容
 * @Version: 1.0
 */
class GrideItemDecoration(val space : Int) : RecyclerView.ItemDecoration(){

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager is GridLayoutManager) {
            outRect.bottom = space
            outRect.right = space
        }
    }

}