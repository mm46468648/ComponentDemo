package com.mooc.studyroom.ui.fragment.collect

import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.pop.studyroom.StudyRoomResourceDeletePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyroom.model.CollectList
import com.mooc.studyroom.ui.adapter.CollectListAdapter
import com.mooc.studyroom.viewmodel.CollectStudyListViewModel

class CollectStudyListFragment : BaseListFragment<CollectList, CollectStudyListViewModel>() {
    override fun initAdapter(): BaseQuickAdapter<CollectList, BaseViewHolder>? {
        return mViewModel?.getPageData()?.value?.let {
            val collectListAdapter = CollectListAdapter(it)

            collectListAdapter.setOnItemClickListener { adapter, view, position ->


                    val collectList = it[position]

                    val sourceId = if(collectList.is_admin) collectList.folder_id else  collectList.id
                    ARouter.getInstance().build(Paths.PAGE_PUBLIC_STUDY_LIST)
                            .withString(IntentParamsConstants.STUDYROOM_FROM_FOLDER_ID, collectList.from_folder_id)
                            .withString(IntentParamsConstants.STUDYROOM_FOLDER_ID, sourceId)
                            .withBoolean(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_RECOMMEND, collectList.is_admin)
                            .withString(IntentParamsConstants.STUDY_ROOM_CHILD_FOLDER_ID, collectList.folder_id.toString())
                            .withString(IntentParamsConstants.STUDYROOM_FOLDER_NAME, collectList.name)
                            .navigation()
            }
            collectListAdapter.setOnItemLongClickListener { adapter, view, position ->

//                showEditPop(view,adapter.data[position] as CollectList)
                showDeletePop(adapter.data[position] as CollectList)
                return@setOnItemLongClickListener true
            }


            collectListAdapter
        }
    }

    /**
     * 展示长按弹窗
     */
    private fun showEditPop(view: View, any: CollectList) {
        XPopup.Builder(requireContext()).atView(view)
                .asAttachList(arrayOf("删除"), null
                ) { position, text ->
                    showDeletePop(any)
                }
                .show()
    }

    /**
     * 展示删除弹窗
     */
    fun showDeletePop(list: CollectList) {
        XPopup.Builder(context)
                .asCustom(StudyRoomResourceDeletePop(requireContext(), list) {
                    mViewModel?.deleteCollect(list.id)
                    mAdapter?.remove(list)
                }) // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .show()
    }
}