package com.mooc.commonbusiness.module.studyroom

import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.base.EmptyFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.folder.FolderTab
import com.mooc.commonbusiness.module.studyroom.collect.StudyRoomCollectFragment
import com.mooc.commonbusiness.module.studyroom.course.StudyRoomCourseFragment
import com.mooc.commonbusiness.module.studyroom.ebook.StudyRoomEbookFragment
import com.mooc.commonbusiness.module.studyroom.note.StudyRoomNoteFragment
import com.mooc.commonbusiness.module.studyroom.publication.StudyRoomPublicationFragment

/**
 * 学习室页面，和文件夹详情通用fragment适配器
 * 展示不同分类的学习资源
 *@param folderId 文件夹id，如果为""，代表根目录
 */
class FolderStudyRoomTabMenuFragmentAdapter : FragmentStateAdapter {

    var folderId: String? = ""
    var tabArray: MutableList<FolderTab>
    var lever2: MutableList<Int>?


    //fragment中用
    constructor(fragment: Fragment, folderId: String? = "", tabArray: MutableList<FolderTab>, lever2: MutableList<Int>?) : super(
            fragment
    ) {
        this.folderId = folderId
        this.tabArray = tabArray
        this.lever2 = lever2
    }

    private var fragments: SparseArray<Fragment> = SparseArray()


    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle().put(IntentParamsConstants.STUDYROOM_FOLDER_ID, "")

        val tabBean = tabArray[position]

        return when (tabBean.type) {
            ResourceTypeConstans.TYPE_COURSE -> StudyRoomCourseFragment.getInstance(bundle)
            ResourceTypeConstans.TYPE_E_BOOK -> StudyRoomEbookFragment.getInstance(bundle)
            ResourceTypeConstans.TYPE_PUBLICATION -> StudyRoomPublicationFragment.getInstance(bundle)
            ResourceTypeConstans.TYPE_NOTE -> StudyRoomNoteFragment.getInstance(bundle)
            ResourceTypeConstans.TYPE_ROOM_TAB_OTHER ->
            {

                val arrayList = ArrayList<Int>()
                lever2?.forEach { arrayList.add(it)}
                bundle.putIntegerArrayList(StudyRoomCollectFragment.PARAMS_TYPES_ARRAY,arrayList)
                StudyRoomCollectFragment.getInstance(bundle)
            }
            else -> EmptyFragment()
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return tabArray.size
    }

    /**
     * 刷新当前显示数据
     */
    fun refreshCurrentData(fragmentManager: FragmentManager, position: Int) {
        val get = fragmentManager.findFragmentByTag("f" + getItemId(position))
        if (get != null && get is BaseListFragment<*, *>) {
            loge("刷新${get} position: ${position}")
            when (get) {
                is StudyRoomCourseFragment -> {
                    get.loadDataWithNoRrefresh()
                }
                is StudyRoomEbookFragment -> {
                    get.loadDataWithNoRrefresh()
                }
                is StudyRoomNoteFragment -> {
                    get.loadDataWithNoRrefresh()
                }
                is StudyRoomCollectFragment -> {
                    get.loadDataWithNoRrefresh()
                }
            }
        }
    }

    /**
     * 更新收藏分类
     */
    fun setLevel2(fragmentManager: FragmentManager, lever2: ArrayList<Int>?){
        if(lever2 != this.lever2){ //两个数组不一样，则通知收藏更新
            for (i in tabArray.indices){
                val get = fragmentManager.findFragmentByTag("f" + getItemId(i))
                if (get != null && get is StudyRoomCollectFragment) {
                    get.updateTabResource(lever2)
                }
            }
        }
        this.lever2 = lever2
    }


}