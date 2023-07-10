package com.mooc.course.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.utils.DownloadUtils
import com.mooc.course.R
import com.mooc.course.manager.db.CourseDbManger
import com.mooc.course.model.LessonInfo
import com.mooc.course.ui.adapter.ZHSCourseDownloadAdapter
import com.mooc.course.viewmodel.ZHSDownloadActivityViewModel
import com.mooc.course.viewmodel.ZHSDownloadViewModel
import com.mooc.newdowload.DownloadInfo
import com.mooc.newdowload.DownloadManager
import com.mooc.newdowload.DownloadTaskObserve
import com.mooc.newdowload.State


class ZHSDownloadFragment : BaseListFragment<LessonInfo, ZHSDownloadViewModel>(),
    DownloadTaskObserve {

    val mParentViewModel: ZHSDownloadActivityViewModel by lazy {
        ViewModelProviders.of(requireActivity())[ZHSDownloadActivityViewModel::class.java]
    }


    companion object {
        fun getInstence(bundle: Bundle): ZHSDownloadFragment {
            val zhsDownloadFragment = ZHSDownloadFragment()
            zhsDownloadFragment.arguments = bundle
            return zhsDownloadFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DownloadManager.getInstance().addObserve(this)
    }

    override fun onDetach() {
        super.onDetach()

        loge("fragment onDetach")


    }

    override fun onDestroyView() {
        super.onDestroyView()

        loge("fragment onDestroyView")

        allPauseDownload()
        DownloadManager.getInstance().removeObserve(this)
        DownloadManager.DOWNLOAD_INFO_HASHMAP.clear()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()

    }

    /**
     * 初始化观察者
     */
    private fun initObserver() {
        //编辑模式
        mParentViewModel.editMode.observe(viewLifecycleOwner, Observer {
            (mAdapter as ZHSCourseDownloadAdapter).isEditMode = it
            //编辑模式禁止下啦刷新
            refresh_layout?.isEnabled = !it
        })

        //选中全部
        mParentViewModel.isAllSelect.observe(viewLifecycleOwner, Observer {
            (mAdapter as ZHSCourseDownloadAdapter).isAllSelect = it
        })

        //全部开始监听
        mParentViewModel.allStartDownload.observe(viewLifecycleOwner, Observer {
            if (it) allStartDownload() else allPauseDownload()
        })

        mParentViewModel.deleteSelectDownload.observe(viewLifecycleOwner, Observer {
            mViewModel?.deleteSelectDownloadChapter(mAdapter)
        })
    }


    override fun initAdapter(): BaseQuickAdapter<LessonInfo, BaseViewHolder>? {
        mViewModel?.courseId = (arguments?.get(IntentParamsConstants.COURSE_PARAMS_ID)
            ?: "") as String
        mViewModel?.courseBean = arguments?.getParcelable(IntentParamsConstants.COURSE_PARAMS_DATA)

        return mViewModel?.getPageData()?.value?.let {
//            val zhsCourseDownloadAdapter = ZHSCourseDownloadAdapter(it)
            val zhsCourseDownloadAdapter = ZHSCourseDownloadAdapter(it)
            zhsCourseDownloadAdapter.setHasStableIds(true);
            //点击下载事件
            zhsCourseDownloadAdapter.addChildClickViewIds(R.id.dcpView)
            zhsCourseDownloadAdapter.setOnItemChildClickListener { baseQuickAdapter, view, i ->
                if (view.id == R.id.dcpView) {
//                    downlaodAudio(it[i], i)
                    it[i].downloadInfo?.let { it1 -> handleDownlaod(it[i], it1) }
                }
            }

            zhsCourseDownloadAdapter.setOnItemClickListener { adapter, view, position ->
                if (mParentViewModel.editMode.value == true) {
                    it[position].deleteDownloadSelect = !it[position].deleteDownloadSelect
                    adapter.notifyItemChanged(position)
                }
            }

            //更新有下载
            zhsCourseDownloadAdapter.onDownloadComplete = {
                val findCourseById =
                    CourseDbManger.findCourseById(mParentViewModel.courseId, "")
                findCourseById?.let { courseDB ->
                    courseDB.haveDownload = true
                    CourseDbManger.updateCourse(courseDB)
                }
            }
            zhsCourseDownloadAdapter
        }
    }

    /**
     * 暂停所有任务
     */
    fun allPauseDownload() {
        mAdapter?.data?.forEachIndexed { index, lessonInfo ->
            if (lessonInfo.videoUrl?.isNotEmpty() == true && lessonInfo.downloadInfo != null) {
                if (lessonInfo.downloadInfo!!.state == State.DOWNLOADING) {
//                    handleDownlaod(lessonInfo, lessonInfo.downloadInfo!!)
                    DownloadManager.getInstance().handleDownload(lessonInfo.downloadInfo!!.id)
                }
                //清空等待任务栈
                DownloadManager.getInstance().deleteQueueTask(lessonInfo.downloadInfo!!.id)
            }
        }


    }

    /**
     * 开始所有任务
     */
    fun allStartDownload() {
        mAdapter?.data?.forEachIndexed { index, lessonInfo ->
            if (lessonInfo.videoUrl?.isNotEmpty() == true) {
//                downlaodAudio(lessonInfo, index)
                lessonInfo.downloadInfo?.let {
                    //开始所有未开始任务，已开始不受影戏
                    if (it.state != State.DOWNLOADING) {
                        handleDownlaod(lessonInfo, it)
                    }
                }
            }
        }
    }

    /**
     * 点击这时候保证所有章节
     * 已经添加到监听容器中
     */
    fun handleDownlaod(lessonInfo: LessonInfo, downloadInfo: DownloadInfo) {
//        loge("currentDownloadId: ${downloadInfo.id}  downloadState: ${downloadInfo.state}")
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), PermissionApplyActivity.REQUEST_CODE_DEFAULT
            )
            return
        }

        if (!DownloadUtils.checkNetStateCanDownload(requireContext())) return
        //添加到缓存队列
        val key = lessonInfo.generateDownloadId(mParentViewModel.courseId, "")
        if (!DownloadManager.DOWNLOAD_INFO_HASHMAP.containsKey(key)) {
            DownloadManager.DOWNLOAD_INFO_HASHMAP.put(key, downloadInfo)
        }

        DownloadManager.getInstance().handleDownload(downloadInfo.id)
    }

    override fun update(id: Long) {
        checkDownloadingState()
        mAdapter?.notifyDataSetChanged()
    }

    override fun getDownloadId(): Long {
        return 0
    }

    /**
     * 检查是有正在进行中的任务
     * 并通知Activity改变状态
     */
    fun checkDownloadingState(){
        var have = false
        DownloadManager.DOWNLOAD_INFO_HASHMAP.forEach {
            val d = it.value
            if (d.state == State.DOWNLOADING || d.state == State.DOWNLOAD_WAIT) {
                have = true
                return@forEach
            }
        }

        val value = mParentViewModel?.downloadStatsFromFragment?.value
        if(value!=have){
            mParentViewModel?.downloadStatsFromFragment?.value = have
        }
    }
}