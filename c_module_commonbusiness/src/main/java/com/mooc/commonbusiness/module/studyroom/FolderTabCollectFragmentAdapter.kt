package com.mooc.commonbusiness.module.studyroom

import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.module.studyroom.ebook.StudyRoomEbookFragment
import com.mooc.commonbusiness.module.studyroom.note.StudyRoomNoteFragment
import com.mooc.commonbusiness.module.studyroom.collect.StudyRoomCollectFragment
import com.mooc.commonbusiness.module.studyroom.course.StudyRoomCourseFragment
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.base.EmptyFragment
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.RoomTabBean
import com.mooc.commonbusiness.module.studyroom.collect.PublicListCollectFragment
import com.mooc.commonbusiness.module.studyroom.course.PublicStudyListCourseFragment
import com.mooc.commonbusiness.module.studyroom.ebook.PublicListEbookFragment
import com.mooc.commonbusiness.module.studyroom.note.PublicListNoteFragment
import com.mooc.commonbusiness.module.studyroom.publication.PublicListPublicationkFragment
import java.util.ArrayList

/**
 * 学习室页面，和文件夹详情通用fragment适配器
 * 展示不同分类的学习资源
 *@param folderId 文件夹id，如果为""，代表根目录
 */
class FolderTabCollectFragmentAdapter : FragmentStateAdapter {

    var folderId: String? = ""
    var tabArray: MutableList<RoomTabBean>
    var lever2: MutableList<Int>?
    var userId: String? = ""
    var fromTaskId: String = ""
    var fromRecommend:Boolean = false      //是否是运营推荐
    var fromTask:Boolean = false      //是否是从任务页面进入

    //学习清单详情Activity页面用
//    constructor(
//            activity: FragmentActivity,
//            folderId: String? = "",
//            tabArray: MutableList<RoomTabBean>,
//            lever2: MutableList<Int>?,
//            userId: String?,
//            fromRecommedn: Boolean = false,
//            fromTask: Boolean = false,
//
//    ) : super(activity) {
//        this.folderId = folderId
//        this.tabArray = tabArray
//        this.lever2 = lever2
//        this.userId = userId
//        this.fromRecommend = fromRecommedn
//        this.fromTask = fromTask
//    }

    //学习清单详情Activity页面用
    constructor(
        activity: FragmentActivity,
        folderId: String? = "",
        tabArray: MutableList<RoomTabBean>,
        lever2: MutableList<Int>?,
        userId: String?,
        fromRecommedn: Boolean = false,
        fromTaskId: String = "",

        ) : super(activity) {
        this.folderId = folderId
        this.tabArray = tabArray
        this.lever2 = lever2
        this.userId = userId
        this.fromRecommend = fromRecommedn
        this.fromTaskId = fromTaskId
    }


    private var fragments: SparseArray<Fragment> = SparseArray()



    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        bundle.put(IntentParamsConstants.STUDYROOM_FOLDER_ID, folderId)
        if (userId?.isNotEmpty() == true) {  //自己公开的传自己的uid
            bundle.put(IntentParamsConstants.MY_USER_ID, userId)
        }

        if(fromRecommend){
            bundle.put(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_RECOMMEND, true)
        }

        fromTask = fromTaskId.isNotEmpty()
        if(fromTask){
            bundle.put(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_TASK, true)
            bundle.put(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_TASK_ID, fromTaskId)
        }

        bundle.putIntegerArrayList(IntentParamsConstants.STUDYROOM_COLLECT_TABLIST, lever2 as ArrayList<Int>?)
        val roomTabBean = tabArray[position]

        return when (roomTabBean.type) {
            ResourceTypeConstans.TYPE_COURSE -> PublicStudyListCourseFragment.getInstance(bundle)
            ResourceTypeConstans.TYPE_E_BOOK -> PublicListEbookFragment.getInstance(bundle)
            ResourceTypeConstans.TYPE_PUBLICATION -> PublicListPublicationkFragment.getInstance(bundle)
            ResourceTypeConstans.TYPE_NOTE -> PublicListNoteFragment.getInstance(bundle)
            ResourceTypeConstans.TYPE_ROOM_TAB_OTHER -> PublicListCollectFragment.getInstance(bundle)
            else -> EmptyFragment()
        }
    }

    override fun getItemCount(): Int {
        return tabArray.size
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