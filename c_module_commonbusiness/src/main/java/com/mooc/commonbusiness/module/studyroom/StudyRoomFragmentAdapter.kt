package com.mooc.commonbusiness.module.studyroom

import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.module.studyroom.collect.StudyRoomCollectFragment
import com.mooc.commonbusiness.module.studyroom.course.StudyRoomCourseFragment
import com.mooc.commonbusiness.module.studyroom.ebook.StudyRoomEbookFragment
import com.mooc.commonbusiness.module.studyroom.note.StudyRoomNoteFragment

/**
 * 学习室页面，和文件夹详情通用fragment适配器
 * 展示不同分类的学习资源
 *@param folderId 文件夹id，如果为""，代表根目录
 */
class StudyRoomFragmentAdapter : FragmentStateAdapter {

    var folderId: String? = ""

    //学习清单详情Activity页面用
    constructor(activity: FragmentActivity, folderId: String? = "") : super(activity) {
        this.folderId = folderId
        initFragments()
    }

    //fragment中用
    constructor(fragment: Fragment, folderId: String? = "") : super(fragment) {
        this.folderId = folderId
        initFragments()
    }

    private val fragments: SparseArray<Fragment> = SparseArray()

    companion object {
        const val PAGE_COURSE = 0
        const val PAGE_EBOOK = 1

        //        const val PAGE_PUBLICATION = 2
        const val PAGE_NOTE = 2
        const val PAGE_COLLECT = 3

        //        val tabArray = arrayOf("课程", "电子书","刊物", "笔记", "其他")
        val tabArray = arrayOf("课程", "电子书", "笔记", "其他")

    }

    fun initFragments() {
        val bundle = Bundle().put(IntentParamsConstants.STUDYROOM_FOLDER_ID, folderId)
        fragments.put(PAGE_COURSE, StudyRoomCourseFragment.getInstance(bundle))
        fragments.put(PAGE_EBOOK, StudyRoomEbookFragment.getInstance(bundle))
//        fragments.put(PAGE_PUBLICATION, StudyRoomPublicationFragment.getInstance(bundle))
        fragments.put(PAGE_NOTE, StudyRoomNoteFragment.getInstance(bundle))
        fragments.put(PAGE_COLLECT, StudyRoomCollectFragment.getInstance(bundle))
    }


    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size()
    }

    /**
     * 刷新当前显示数据
     */
    fun refreshCurrentData(position: Int) {
        val get = fragments.get(position)
        if (get != null && get is BaseListFragment<*, *>) {
//            get.loadDataWithRrefresh()
            when (get) {
                is StudyRoomCourseFragment -> {
                    get.loadDataWithRrefresh()
                }
                is StudyRoomEbookFragment -> {
                    get.loadDataWithRrefresh()
                }
                is StudyRoomNoteFragment -> {
                    get.loadDataWithRrefresh()
                }
                is StudyRoomCollectFragment -> {
                    get.loadDataWithRrefresh()
                }
            }
        }
    }


}