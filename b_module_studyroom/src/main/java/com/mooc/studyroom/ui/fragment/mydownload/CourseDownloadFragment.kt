package com.mooc.studyroom.ui.fragment.mydownload

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.model.db.CourseDB
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.CourseDownloadService
import com.mooc.studyroom.R
import com.mooc.studyroom.ui.adapter.mydownload.DownloadCourseAdapter
import com.mooc.studyroom.viewmodel.CourseDwonloadViewModel
import com.mooc.studyroom.viewmodel.MyDownloadActivityViewModel

class CourseDownloadFragment : BaseListFragment2<CourseDB, CourseDwonloadViewModel>() {

    val mActivityViewModel : MyDownloadActivityViewModel by lazy {
        ViewModelProviders.of(requireActivity())[MyDownloadActivityViewModel::class.java]
    }
//    val mActivityViewModel : MyDownloadActivityViewModel by viewModels()
    override fun initAdapter(): BaseQuickAdapter<CourseDB, BaseViewHolder> {
        return mViewModel?.getPageData()?.value.let {
            val downloadCourseAdapter = DownloadCourseAdapter(it)
            downloadCourseAdapter.setOnItemClickListener { adapter, view, position ->
                it?.get(position)?.let {
                    turnToDownloadChapterPage(it)
                }
            }
            downloadCourseAdapter.addChildClickViewIds(R.id.btnDelete)
            downloadCourseAdapter.setOnItemChildClickListener { adapter, view, position ->
                if(view.id == R.id.btnDelete){
                    //删除课程记录
                    it?.get(position)?.let {albumBean ->
                            val navigation = ARouter.getInstance().navigation(CourseDownloadService::class.java)
                            navigation.deleteCourse(albumBean)
                        adapter.remove(albumBean)

                    }

                    //如果adapter空需要显示空状态
                    if(adapter.data.isEmpty()){
                        loadDataWithRrefresh()
                    }
                }
            }

            //监听编辑模式
            mActivityViewModel.editMode.observe(this, Observer {
                downloadCourseAdapter.editMode = it
            })
            downloadCourseAdapter
        }
    }



    private fun turnToDownloadChapterPage(courseBean: CourseDB) {
        ARouter.getInstance().build(Paths.PAGE_DOWNLOAD_COURSE_CHAPTER)
                .withString(IntentParamsConstants.COURSE_PARAMS_ID,courseBean.courseId)
                .withString(IntentParamsConstants.COURSE_PARAMS_TITLE,courseBean.name)
                .withString(IntentParamsConstants.COURSE_PARAMS_CLASSROOM_ID,courseBean.classRoomID)
                .withString(IntentParamsConstants.COURSE_PARAMS_PLATFORM,courseBean.platform)
                .navigation()
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(requireContext(),2)
    }


    override fun initEmptyView() {
        emptyView.setTitle("你还没有完成下载的课程")
//        emptyView.setEmptyIcon(R.mipmap.studyroom_ic_download_empty)
        emptyView.setGravityTop(40.dp2px())
    }


}