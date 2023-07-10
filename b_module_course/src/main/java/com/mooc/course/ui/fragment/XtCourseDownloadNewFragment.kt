package com.mooc.course.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.course.R
//import com.mooc.commonbusiness.model.search.ChaptersBean
import com.mooc.course.ui.adapter.XtCourseDownloadListAdapter
import com.mooc.course.viewmodel.NewXtCourseDownloadActivityViewModel
import com.mooc.course.viewmodel.NewXtCourseDownloadViewModel
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.CoursePlatFormConstants
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.utils.DownloadUtils
import com.mooc.course.manager.db.CourseDbManger
import com.mooc.course.model.ChaptersBean
import com.mooc.course.repository.CourseRepository
import com.mooc.course.viewmodel.XtCourseDownloadViewModel
import com.mooc.download.util.DownloadConstants
import com.mooc.newdowload.DownloadInfo
import com.mooc.newdowload.DownloadManager
import com.mooc.newdowload.DownloadTaskObserve
import com.mooc.newdowload.State
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class XtCourseDownloadNewFragment : BaseListFragment<ChaptersBean, XtCourseDownloadViewModel>(),
    DownloadTaskObserve {

    val classRoomId by extraDelegate(IntentParamsConstants.COURSE_PARAMS_CLASSROOM_ID, "")
    val courseBean by extraDelegate<CourseBean?>(IntentParamsConstants.COURSE_PARAMS_DATA, null)

    val parentViewModel by viewModels<NewXtCourseDownloadActivityViewModel>(ownerProducer = { requireActivity() })


    //检查下载任务是否都已完成
    companion object {

        const val MSG_CHECK_DOWNLOAD_COMPLETE = 1
        const val CHECK_DOWNLOAD_COMPLETE_TIMES = 500L
    }

    val mDownlaodCompleteObserver: Mhandler by lazy {
        Mhandler(parentViewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DownloadManager.getInstance().addObserve(this)
        parentViewModel.allStartDownload.observe(viewLifecycleOwner, Observer {
            if (it) { //全部开始
                startAllTask(mAdapter)
                return@Observer
            }
            //全部暂停
            pauseAllDownloadingTask()
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        DownloadManager.getInstance().removeObserve(this)
        mDownlaodCompleteObserver.removeCallbacksAndMessages(null)
        //移除缓存，移除所有下载中的任务
        pauseAllDownloadingTask()
        //清除缓存
        DownloadManager.DOWNLOAD_INFO_HASHMAP.clear()

    }

    override fun initAdapter(): BaseQuickAdapter<ChaptersBean, BaseViewHolder>? {
        mViewModel?.classRoomId = classRoomId ?: ""
        mViewModel?.courseBean = courseBean
        return mViewModel?.getPageData()?.value?.let {
            val xtCourseDownloadListAdapter = XtCourseDownloadListAdapter(it)
            xtCourseDownloadListAdapter.addChildClickViewIds(R.id.dcpView)
            xtCourseDownloadListAdapter.setOnItemChildClickListener { adapter, view, position ->

                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
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
                } else {
                    handlerClick(it.get(position))
                    startDownloadCutdown()
                }

            }
            xtCourseDownloadListAdapter
        }
    }


    /**
     * 开始所有任务未开始的任务
     */
    fun startAllTask(mAdapter: BaseQuickAdapter<ChaptersBean, BaseViewHolder>?) {
        mAdapter?.data?.forEach { chaptersBean ->
            if (chaptersBean.leaf_list?.isNotEmpty() == true) {
                val downloadModel = chaptersBean.downloadModel
                downloadModel?.apply {
                    if (this.state != State.DOWNLOADING && this.state != State.DOWNLOAD_COMPLETED) {
                        handlerClick(chaptersBean)
                    }
                }
            }
        }

        startDownloadCutdown()
    }


    /**
     *开启倒计时检查是否全部下载完成
     */
    private fun startDownloadCutdown() {
        mDownlaodCompleteObserver.removeCallbacksAndMessages(null)
        mDownlaodCompleteObserver.sendEmptyMessageDelayed(
            MSG_CHECK_DOWNLOAD_COMPLETE,
            CHECK_DOWNLOAD_COMPLETE_TIMES
        )
    }


    /**
     * 暂停所有进行中的任务
     */
    fun pauseAllDownloadingTask() {

        DownloadManager.DOWNLOAD_INFO_HASHMAP.forEach {
            val d = it.value
            if (d.state == State.DOWNLOADING) {
                d.state = State.DOWNLOAD_STOP
            }
            DownloadManager.getInstance().deleteQueueTask(d.id)

        }
    }


    /**
     * 检查是否还有正在下载中的任务
     * 改变全部下载或者暂停状态
     */
    class Mhandler(parentViewModel: NewXtCourseDownloadActivityViewModel) : Handler() {
        var weakReference = WeakReference<NewXtCourseDownloadActivityViewModel>(parentViewModel)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if (msg.what == MSG_CHECK_DOWNLOAD_COMPLETE) {
                var have = false
                DownloadManager.DOWNLOAD_INFO_HASHMAP.forEach {
                    val d = it.value
                    if (d.state == State.DOWNLOADING || d.state == State.DOWNLOAD_WAIT) {
                        have = true
                        return@forEach
                    }
                }

                //改变开始或者暂停状态
                val value = weakReference.get()?.changeAllTvShowState?.value
                if (value != have) {
                    weakReference.get()?.changeAllTvShowState?.value = have
                }
                if (have) {
                    sendEmptyMessageDelayed(
                        MSG_CHECK_DOWNLOAD_COMPLETE,
                        CHECK_DOWNLOAD_COMPLETE_TIMES
                    )
                }

//                loge("倒计时进行中,${have}")
            }

        }
    }


    private fun handlerClick(item: ChaptersBean) {
        if (!DownloadUtils.checkNetStateCanDownload(requireContext())) return

        val generateDownloadId = item.generateDownloadId(item.courseId, item.classRoomId)
        var get = DownloadManager.DOWNLOAD_INFO_HASHMAP.get(generateDownloadId)
        if (get == null) {
            get = item.downloadModel
            get?.let { DownloadManager.DOWNLOAD_INFO_HASHMAP.put(generateDownloadId, it) }
        }

        get?.let {
            if (get.downloadUrl?.isNotEmpty() == true) {  //如果已经有了下载地址
                DownloadManager.getInstance().handleDownload(generateDownloadId)
            } else {
                val leafId = item.id
                getCourseVideoPath(item, item.classRoomId, leafId)
            }
        }
    }


    fun getCourseVideoPath(item: ChaptersBean, classRoomId: String, videoId: String) {
        lifecycleScope.launchWhenCreated {
            try {
                item.getDownloadUrlState = DownloadConstants.DOWNLOAD_EXTRA_STATE_PREPARE
                updateItemData(item)
                val xtCourseVideoMessage =
                    CourseRepository.getXtCourseVideoMessage(classRoomId, videoId)
                item.getDownloadUrlState = DownloadConstants.DOWNLOAD_EXTRA_STATE_SUCCESS
                item.downloadModel?.apply {
                    this.downloadUrl = xtCourseVideoMessage.data.sources?.quality10?.get(0)
                    val key = item.generateDownloadId(item.courseId, item.classRoomId)
                    if (!DownloadManager.DOWNLOAD_INFO_HASHMAP.containsKey(key)) {
                        DownloadManager.DOWNLOAD_INFO_HASHMAP.put(key, this)
                        DownloadManager.getInstance().handleDownload(key)
                    }
                }

            } catch (e: Exception) {
                item.getDownloadUrlState = DownloadConstants.DOWNLOAD_EXTRA_STATE_FAIL
                updateItemData(item)
            }
        }
    }

    fun updateItemData(item: ChaptersBean) {
        (mAdapter as XtCourseDownloadListAdapter).data.forEachIndexed { index, chaptersBean ->
            if (chaptersBean == item) {
                mAdapter?.notifyItemChanged(index)
            }
        }
    }

    override fun update(id: Long) {
        val downLoadInfo = DownloadManager.DOWNLOAD_INFO_HASHMAP[id]

        downLoadInfo?.apply {
            mViewModel?.newChapterList?.forEachIndexed { index, chaptersBean ->
                if (chaptersBean.downloadModel?.id == downLoadInfo.id) {
                    chaptersBean.downloadModel = downLoadInfo
                    mAdapter?.notifyItemChanged(index)
                    //如果是已完成状态，更新课程数据库，hasDownload字段
                    if (downLoadInfo.state == State.DOWNLOAD_COMPLETED) {
                        chaptersBean.apply {
                            val findCourseById =
                                CourseDbManger.findCourseById(this.courseId, this.classRoomId)
                            findCourseById?.let { courseDB ->
                                courseDB.haveDownload = true
                                CourseDbManger.updateCourse(courseDB)
                            }
                        }
                    }
                }
            }
        }

    }

    override fun getDownloadId(): Long {
        return -1
    }


}