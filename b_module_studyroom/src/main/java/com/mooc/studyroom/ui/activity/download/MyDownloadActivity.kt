package com.mooc.studyroom.ui.activity.download

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.mooc.common.utils.FileMgr
import com.mooc.common.utils.SDUtils
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomActivityMydownloadBinding
import com.mooc.studyroom.db.OldDbSearchManager
import com.mooc.studyroom.ui.adapter.MyDownloadVpAdapter
import com.mooc.studyroom.viewmodel.MyDownloadActivityViewModel
//import kotlinx.android.synthetic.main.studyroom_activity_mydownload.*
import java.io.File

@Route(path = Paths.PAGE_MY_DOWNLOAD)
class MyDownloadActivity : BaseActivity() {


    val mVpAdapter by lazy {
        MyDownloadVpAdapter(this)
    }

    val mViewModel by viewModels<MyDownloadActivityViewModel>()


    val titleArray = arrayOf("课程","音频课","电子书")

    private lateinit var inflater: StudyroomActivityMydownloadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = StudyroomActivityMydownloadBinding.inflate(layoutInflater)
        setContentView(inflater.root)

//        LogUtil.addLoadLog(LogPageConstants.PID_DOWNLOAD)

        //adapter
        inflater.viewPager2.adapter = mVpAdapter
//        viewPager2.offscreenPageLimit = 3
        val tabLayoutMediator =
                TabLayoutMediator(inflater.tabLayout, inflater.viewPager2,true) { tab, position ->
                    tab.text = titleArray[position]
                }
        tabLayoutMediator.attach()
        //commonTitle
        inflater.commonTitle.setOnLeftClickListener { finish() }
        inflater.commonTitle.setOnRightTextClickListener(View.OnClickListener {
            mViewModel.editMode.value = !(mViewModel.editMode.value?:false)
            val editText = if(mViewModel.editMode.value == true) "完成" else "编辑"
            inflater.commonTitle.right_text = editText
        })


        //获取当前手机剩余空间
        val bytes: LongArray = SDUtils.getDirSize(DownloadConfig.defaultLocation)
        val freeValue = bytes[0]

        val courseFileSize = FileMgr.getFileSize(File(DownloadConfig.courseLocation))
        val audioFileSize = FileMgr.getFileSize(File(DownloadConfig.audioLocation))
        inflater.activityDownloadStorageUsed.setText("已用:${FileMgr.FormetFileSize(courseFileSize + audioFileSize)}")
        inflater.activityDownloadStorageFree.setText("剩余容量:${FileMgr.FormetFileSize(freeValue)}")

//        testDB()
    }

    fun testDB(){
        val dao = OldDbSearchManager(this)
//        dao.queryAllDwonloadCourseData()
//        dao.queryAllDwonloadTrackData()
        dao.queryAllDwonloadBookData()
    }


}