package com.mooc.studyroom.ui.activity.download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.model.course.BaseChapter
import com.mooc.commonbusiness.model.studyroom.OldChapterBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.download.db.DownloadDatabase
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.DownloadActivityCourseListBinding
import com.mooc.studyroom.ui.adapter.mydownload.DownloadCourseChapterAdapter
import com.mooc.studyroom.viewmodel.DownloadCourseListViewModel
//import kotlinx.android.synthetic.main.download_activity_course_list.*
import java.io.File

/**
 * 离线课程列表页面
 */
@Route(path = Paths.PAGE_DOWNLOAD_COURSE_CHAPTER)
class DownloadCourseListActivity : BaseActivity(){

    val mViewModel by viewModels<DownloadCourseListViewModel>()

    val courseId by extraDelegate(IntentParamsConstants.COURSE_PARAMS_ID,"")
    val classRoomId by extraDelegate(IntentParamsConstants.COURSE_PARAMS_CLASSROOM_ID,"")
    val coursePlatForm by extraDelegate(IntentParamsConstants.COURSE_PARAMS_PLATFORM,"")
    val courseTitle by extraDelegate(IntentParamsConstants.COURSE_PARAMS_TITLE,"")
    var downloadCourseChapterAdapter = DownloadCourseChapterAdapter(arrayListOf())

    private lateinit var inflater: DownloadActivityCourseListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = DownloadActivityCourseListBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        //隐藏添加更多章节
        initView()

        //adapter
//        val loadData = mViewModel.loadData(courseId)
        initAdapter()

        loadData()
    }

    private fun initAdapter() {
        mViewModel.mChaptersLiveData.observe(this, Observer {
            downloadCourseChapterAdapter.setList(it)
        })

        inflater.rvCourseChapter.layoutManager = LinearLayoutManager(this)
        downloadCourseChapterAdapter.setOnItemClickListener { adapter, view, position ->
            setOnItemClick(adapter.data[position] as BaseChapter, position)
        }
        inflater.rvCourseChapter.adapter = downloadCourseChapterAdapter
    }

    fun loadData(){
        mViewModel.loadData(courseId,classRoomId)
    }

    private fun initView() {
        inflater.tvAddMore.visibility = View.GONE
        inflater.viewLine.visibility = View.GONE
        inflater.commonTitle.middle_text = courseTitle
        inflater.commonTitle.setOnLeftClickListener { finish() }
        inflater.commonTitle.setOnRightTextClickListener(View.OnClickListener {
            downloadCourseChapterAdapter.editMode = !(downloadCourseChapterAdapter?.editMode?:false)
            val editStr = if(downloadCourseChapterAdapter.editMode == true) "完成" else "编辑"
            inflater.commonTitle.right_text = editStr
            //底部显示隐藏
            val bottomVisibal = if(downloadCourseChapterAdapter.editMode == true)View.VISIBLE else View.GONE
            inflater.rlBottom.visibility = bottomVisibal
        })

        inflater.cbSelectAll.setOnCheckedChangeListener { compoundButton, b ->
            downloadCourseChapterAdapter.let {
                if(b){ //全部选中
                    it.selectChapters.clear()
                    it.selectChapters.addAll(it.data)
                    it.notifyDataSetChanged()
                }else{ //全部移除
                    it.selectChapters.clear()
                    it.notifyDataSetChanged()
                }
            }

        }

        inflater.btDelete.setOnClickListener {
            //todo删除全部，并刷新
            downloadCourseChapterAdapter.let {
                deleteChapters(it.selectChapters)
                loadData()
            }

        }
    }

    /**
     * chapters并没有村到数据库，直接删除缓存数据库即可
     */
    fun deleteChapters(chapters: List<BaseChapter>){

        chapters.forEach{

            var filePath = DownloadConfig.courseLocation + "/${coursePlatForm}" + "/${courseId}"
            if(classRoomId.isNotEmpty()){
                filePath += "/${classRoomId}"
            }
            //删除文件
            try {
                //老版本迁移过来的数据，路径直接获取
                if(it is OldChapterBean){
                    val file = File(it.path)
                    file.delete()
                }else{
                    val file = File(filePath,it.name)
                    file.delete()
                }
            }catch (e:Exception){
                loge(e.toString())
            }

            //删除缓存数据
            val downloadModle = DownloadDatabase.database?.getDownloadDao()
                ?.getDownloadModle(it.generateDownloadId(courseId, classRoomId))
            downloadModle?.let {
                DownloadDatabase.database?.getDownloadDao()?.delete(it)
            }
        }
    }

    private fun setOnItemClick(chapter : BaseChapter, position: Int) {
//        val chapter = loadData?.get(position)
        downloadCourseChapterAdapter.let {
            if(it.editMode){
                val selectTracks = it.selectChapters
                if(selectTracks.contains(chapter)){
                    selectTracks.remove(chapter)
                }else{
                    chapter.let { it1 -> selectTracks.add(it1) }
                }
                it.notifyItemChanged(position)
                return
            }

        }

        chapter.generateDownloadId(courseId,classRoomId).let {
//            val findDownloadModeByUrl = DownloadManager.findDownloadModeByUrl(it)
            val downloadInfo = DownloadDatabase.database?.getDownloadDao()?.getDownloadModle(it)

            downloadInfo?.apply {
                var path = downloadInfo.downloadPath + "/"
//                if(classRoomId.isNotEmpty()){
//                    path += "${courseId}/"
//                }
                path += downloadInfo.fileName

                //如果是老项目同步过来的，path，直接就是path
                if(chapter is OldChapterBean){
                    path = downloadInfo.downloadPath
                }
                ARouter.getInstance().build(Paths.PAGE_DOWNLOAD_COURSE_PLAY)
                    .withString(DownloadCoursePlayActivity.PARAMS_COURSE_TITLE, chapter.name)
                    .withString(DownloadCoursePlayActivity.PARAMS_COURSE_PLAY_PATH, path)
                    .navigation()
            }

        }
    }
}