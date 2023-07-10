package com.mooc.commonbusiness.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.resource.widget.EmptyView

/**
 * 空的Fragment
 */
class EmptyFragment : BaseFragment() {

    var emptyTitle: String? = "暂无数据"
    var emptyImageRes: Int = R.mipmap.common_ic_empty
    var buttonStr: String = ""
    var onClickBack: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val emptyView = com.mooc.resource.widget.EmptyView(requireContext())

        //在学习室底部
        emptyView.setTitle(emptyTitle)
        emptyView.setIconOverride(150.dp2px(), 150.dp2px())
        emptyView.setEmptyIcon(R.drawable.common_gif_folder_empty)
        emptyView.setButton(buttonStr) {
            onClickBack?.invoke()
        }
        return emptyView
    }
}