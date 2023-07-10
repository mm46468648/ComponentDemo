package com.mooc.course.ui.widget

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.CoursePlatFormConstants
import com.mooc.course.R
import com.mooc.course.manager.db.CourseDbManger
import com.mooc.course.model.ChaptersBean
//import com.mooc.commonbusiness.model.search.ChaptersBean
import com.mooc.course.repository.CourseRepository
import com.mooc.download.db.DownloadDatabase
import com.mooc.download.util.DownloadConstants
import com.mooc.newdowload.DownloadInfo
import com.mooc.newdowload.DownloadManager
import com.mooc.newdowload.DownloadTaskObserve
import com.mooc.newdowload.State
import com.mooc.resource.widget.DownloadCircleProgressView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class XtCourseDownloadNewView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(mContext, attrs, defStyleAttr){

    //    private TextView tvChapterName;
    private var tvSectionName: TextView? = null
        //    var ivDownloadState: ImageView? = null
        private set

    //    private val ivDownloadMiddle: ImageView? = null
    var circleProgressBar: com.mooc.resource.widget.DownloadCircleProgressView? = null
        private set
    private var viewSpace: View? = null
    private var viewLine2: View? = null
    var chaptersBean: ChaptersBean? = null

    init {
        _init()
    }

    private fun _init() {
        LayoutInflater.from(mContext).inflate(R.layout.course_view_xt_dowload_item, this)

//        tvChapterName = findViewById(R.id.tvChapterName);
        tvSectionName = findViewById(R.id.tvSectionName)
//        ivDownloadState = findViewById(R.id.ivDownloadState)
        //        ivDownloadMiddle = findViewById(R.id.ivDownloadMiddle);
        circleProgressBar = findViewById(R.id.dcpView)
        viewSpace = findViewById(R.id.viewSpace)
        //        viewLine1 = findViewById(R.id.viewLine1);
        viewLine2 = findViewById(R.id.viewLine2)


    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    fun setCourseData(item: ChaptersBean) {
        chaptersBean = item
        val chapterColor =
            if (item.type != 0) Color.parseColor("#818181") else Color.parseColor(
                "#222222"
            )
        tvSectionName?.setTextColor(chapterColor)
        tvSectionName?.text = item.name



        //type == 0的代表可下载的视频章节,不为0不显示下载控件
        if(item.type != 0){
            circleProgressBar?.visibility = View.INVISIBLE
            return
        }

//        var stateVisibal = View.INVISIBLE
//        //如果leaf_list不为空，并且含有视频类型，再显示可下载
//        item.leaf_list?.forEach {
//            if (it.type == 0) { //包含视频链接
//                stateVisibal = View.VISIBLE
//            }
//        }

        circleProgressBar?.visibility = View.VISIBLE
        if(item.downloadModel?.downloadUrl?.isEmpty()==true){
            when(item.getDownloadUrlState){
                DownloadConstants.DOWNLOAD_EXTRA_STATE_NOT->{
                    circleProgressBar?.state = State.DOWNLOAD_NOT
                }
                DownloadConstants.DOWNLOAD_EXTRA_STATE_PREPARE->{
                    circleProgressBar?.state = State.DOWNLOAD_WAIT
                }
                DownloadConstants.DOWNLOAD_EXTRA_STATE_FAIL->{
                    circleProgressBar?.state = State.DOWNLOAD_ERROR
                }
            }
        }else{
            setDownloadState(item)
        }

    }


    fun setDownloadState(item: ChaptersBean) {
        changeUI(item.downloadModel)

    }

    private fun changeUI(downLoadInfo: DownloadInfo?) {
        val findDownloadMode = downLoadInfo
        if (findDownloadMode == null) {
            circleProgressBar?.state = State.DOWNLOAD_NOT
        } else {
            circleProgressBar?.state = findDownloadMode.state
            if (findDownloadMode.downloadSize != 0L) {
                val i = (100 * findDownloadMode.downloadSize / findDownloadMode.size).toInt()
                circleProgressBar?.setmCurrent(i)
            } else {
                circleProgressBar?.setmCurrent(0)
            }
        }
    }

    /**
     * 设置分割条显示
     */
    fun setSpaceShow(show: Boolean) {
        viewSpace?.visibility = if (show) View.VISIBLE else View.GONE
        viewLine2?.visibility = if (show) View.GONE else View.VISIBLE
    }

//    override fun update(id: Long) {
//        if (id == getDownloadId()) {
//            val downLoadInfo = DownloadManager.DOWNLOAD_INFO_HASHMAP[id]
//
//            downLoadInfo?.apply {
//                chaptersBean?.downloadModel = downLoadInfo
////                loge(downLoadInfo.toString())
//                changeUI(downLoadInfo)
//
//                //如果是已完成状态，更新课程数据库，hasDownload字段
//                if (downLoadInfo.state == State.DOWNLOAD_COMPLETED) {
//                    chaptersBean?.apply {
//                        val findCourseById =
//                            CourseDbManger.findCourseById(this.courseId, this.classRoomId)
//                        findCourseById?.let { courseDB ->
//                            courseDB.haveDownload = true
//                            CourseDbManger.updateCourse(courseDB)
//                        }
//                    }
//                }
//            }
//
//        }
//    }

//    override fun getDownloadId(): Long {
//        return chaptersBean?.let {
//            chaptersBean?.generateDownloadId(it.courseId, it.classRoomId)
//        }
//            ?: 0
//    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

//        DownloadManager.getInstance().removeObserve(this)

    }

}