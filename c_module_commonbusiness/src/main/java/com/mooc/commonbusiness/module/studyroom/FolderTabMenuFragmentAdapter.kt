package com.mooc.commonbusiness.module.studyroom

import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.module.studyroom.ebook.StudyRoomEbookFragment
import com.mooc.commonbusiness.module.studyroom.note.StudyRoomNoteFragment
import com.mooc.commonbusiness.module.studyroom.collect.StudyRoomCollectFragment
import com.mooc.commonbusiness.module.studyroom.course.StudyRoomCourseFragment
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.base.EmptyFragment
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.RoomTabBean
import com.mooc.commonbusiness.module.studyroom.publication.StudyRoomPublicationFragment
import com.mooc.commonbusiness.route.Paths

/**
 * 学习室页面，和文件夹详情通用fragment适配器
 * 展示不同分类的学习资源
 *@param folderId 文件夹id，如果为""，代表根目录
 */
class FolderTabMenuFragmentAdapter : FragmentStateAdapter {

    var folderId: String? = ""
    var tabArray: MutableList<RoomTabBean>
    var lever2: MutableList<Int>?
    var isPublic: Boolean? = false;  //是否公开的

    //学习清单详情Activity页面用
    constructor(
            activity: FragmentActivity,
            folderId: String? = "",
            tabArray: MutableList<RoomTabBean>,
            lever2: MutableList<Int>?,
            isPub: Boolean?
    ) : super(activity) {
        this.isPublic = isPub
        this.folderId = folderId
        this.tabArray = tabArray
        this.lever2 = lever2

    }

    //fragment中用
    constructor(
            fragment: Fragment,
            folderId: String? = "",
            tabArray: MutableList<String>,
            lever2: MutableList<String>?
    ) : super(
            fragment
    ) {
        this.folderId = folderId
        this.tabArray = arrayListOf()
        this.lever2 = arrayListOf()
    }

    private var fragments: SparseArray<Fragment> = SparseArray()



    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle().put(IntentParamsConstants.STUDYROOM_FOLDER_ID, folderId)


        val tabBean = tabArray[position]

        return when (tabBean.type) {
            ResourceTypeConstans.TYPE_COURSE -> StudyRoomCourseFragment.getInstance(bundle)
            ResourceTypeConstans.TYPE_E_BOOK -> StudyRoomEbookFragment.getInstance(bundle)
            ResourceTypeConstans.TYPE_PUBLICATION -> StudyRoomPublicationFragment.getInstance(bundle)
            ResourceTypeConstans.TYPE_NOTE -> StudyRoomNoteFragment.getInstance(bundle)

            ResourceTypeConstans.TYPE_ROOM_TAB_OTHER -> {
                val arrayList = ArrayList<Int>()
                lever2?.forEach { arrayList.add(it)}
                bundle.putIntegerArrayList(StudyRoomCollectFragment.PARAMS_TYPES_ARRAY,arrayList)
                StudyRoomCollectFragment.getInstance(bundle)
            }
            else -> creatEmptyView()

        }
    }

    private fun creatEmptyView(): Fragment {

        val emptyFragment = EmptyFragment()
        if (isPublic != true) {
            emptyFragment.emptyTitle = "你还没有添加资源"
            emptyFragment.emptyImageRes = R.drawable.common_gif_folder_empty
            emptyFragment.buttonStr = "+添加学习资源"
            emptyFragment.onClickBack = {
                if (!GlobalsUserManager.isLogin())
                    ResourceTurnManager.turnToLogin()
                else
                //已登录，跳转发现页
                    ARouter.getInstance().build(Paths.PAGE_HOME)
                            .withInt(IntentParamsConstants.HOME_SELECT_POSITION, 0)
                            .withInt(IntentParamsConstants.HOME_SELECT_CHILD_POSITION, 0)
                            .navigation()
            }
        }
        return emptyFragment
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

}